package uk.ac.ebi.uniprot.taxonomyservice.imports.config;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyLabels;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyRelationships;
import uk.ac.ebi.uniprot.taxonomyservice.imports.setup.InstanceTestClassListener;
import uk.ac.ebi.uniprot.taxonomyservice.imports.setup.JobTestRunnerConfig;
import uk.ac.ebi.uniprot.taxonomyservice.imports.setup.Neo4JGraphDatabase;
import uk.ac.ebi.uniprot.taxonomyservice.imports.setup.SpringInstanceTestClassRunner;
import uk.ac.ebi.uniprot.taxonomyservice.imports.utils.TaxonomyImportTestUtils;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.helpers.collection.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.neo4j.graphdb.Direction.OUTGOING;
import static uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyFields.*;

/**
 * This class execute the integration test for taxonomy import and validate if the inserted information in Neo4J is
 * correct
 *
 * Created by lgonzales on 05/08/16.
 */
@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringInstanceTestClassRunner.class)
@IntegrationTest
@ContextConfiguration(
        initializers = ConfigFileApplicationContextInitializer.class,
        classes = {FakeTaxonomyImportConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ValidateImportedDataIT implements InstanceTestClassListener{

    private static final Logger logger = LoggerFactory.getLogger(ValidateImportedDataIT.class);

    private static Neo4JGraphDatabase neo4JGraphDatabase;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private FakeTaxonomyImportConfig config;

    @Override
    public void beforeClassSetup() throws Exception {
        jobLauncherTestUtils.launchJob();
        config.getBatchInserter().shutdown();
        neo4JGraphDatabase = new Neo4JGraphDatabase(config.getNeo4jDatabasePath());
    }

    @Test
    public void assertTaxonomyIndexes() throws  IOException{
        GraphDatabaseService neo4jDb = neo4JGraphDatabase.getNeo4jDb();
        try(Transaction tx = neo4jDb.beginTx()){
            Iterable<IndexDefinition> indexes = neo4jDb.schema().getIndexes();
            assertThat(indexes, notNullValue());
            assertThat(Iterables.count(indexes),is(4L));

            assertThat(hasIndexInPropertyName(indexes,taxonomyId.name()),is(true));
            assertThat(hasIndexInPropertyName(indexes,scientificNameLowerCase.name()),is(true));
            assertThat(hasIndexInPropertyName(indexes,commonNameLowerCase.name()),is(true));
            assertThat(hasIndexInPropertyName(indexes,mnemonicLowerCase.name()),is(true));

            tx.success();
        }
    }

    @Test
    public void assertTaxonomyDeletedNodes() throws  IOException{
        logger.info("Validating Deleted Nodes Loaded in Neo4J");
        GraphDatabaseService neo4jDb = neo4JGraphDatabase.getNeo4jDb();
        try(Transaction tx = neo4jDb.beginTx()){
            ResourceIterator<Node> nodes = neo4jDb.findNodes(Label.label(TaxonomyLabels.Deleted.name()));
            assertThat(nodes, notNullValue());

            nodes.forEachRemaining(node -> assertValidDeletedNode(node));
            tx.success();
        }
    }

    @Test
    public void assertTaxonomyMergedNodes() throws  IOException{
        logger.info("Validating Merged Nodes Loaded in Neo4J");
        GraphDatabaseService neo4jDb = neo4JGraphDatabase.getNeo4jDb();
        try(Transaction tx = neo4jDb.beginTx()){
            ResourceIterator<Node> nodes = neo4jDb.findNodes(Label.label(TaxonomyLabels.Merged.name()));
            assertThat(nodes, notNullValue());

            long size = nodes.stream().count();
            assertThat(size,is(12L));

            nodes.forEachRemaining(node -> assertValidMergedNode(node));
            tx.success();
        }
    }

    @Test
    public void assertTaxonomyNodes() throws  IOException{
        logger.info("Validating Node Loaded in Neo4J");
        GraphDatabaseService neo4jDb = neo4JGraphDatabase.getNeo4jDb();
        try(Transaction tx = neo4jDb.beginTx()){
            ResourceIterator<Node> nodes = neo4jDb.findNodes(Label.label(TaxonomyLabels.Node.name()));
            assertThat(nodes, notNullValue());

            Node result = nodes.stream().filter(node -> node.getProperty(taxonomyId.name()).equals("1"))
                    .findAny()
                    .orElse(null);
            assertThat(result, notNullValue());
            assertValidTaxonomyRootNode(result);

            result = nodes.stream().filter(node -> node.getProperty(taxonomyId.name()).equals("2"))
                    .findAny()
                    .orElse(null);
            assertValidNCBINode(result);
            tx.success();
        }
    }

    private void assertValidTaxonomyRootNode(Node node){
        Object taxId = node.getProperty(taxonomyId.name());
        assertThat(taxId, notNullValue());

        int id = Integer.parseInt(""+taxId);
        assertThat(id,is(1));

        assertNodeProperty(mnemonic.name(), "Tax_Code_1", node);
        assertNodeProperty(mnemonicLowerCase.name(), "tax_code_1",node);
        assertNodeProperty(scientificName.name(), "Sptr_Scientific_1",node);
        assertNodeProperty(scientificNameLowerCase.name(),"sptr_scientific_1",node);
        assertNodeProperty(commonName.name(),"Sptr_Common_1", node);
        assertNodeProperty(commonNameLowerCase.name(),"sptr_common_1", node);
        assertNodeProperty(synonym.name(), "sptr_synonym_1",node);
        assertNodeProperty(rank.name(),"rank_1",node);
        assertChildOfRelationsShip(true,node);

    }

    private boolean hasIndexInPropertyName(Iterable<IndexDefinition> indexes, String propertyName) {
        Boolean result = null;
        for (IndexDefinition index: indexes) {
            for (String key: index.getPropertyKeys()) {
                if(key.equals(propertyName)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private void assertValidNCBINode(Node node){
        Object taxId = node.getProperty(taxonomyId.name());
        assertThat(taxId, notNullValue());

        int id = Integer.parseInt(""+taxId);
        assertThat(id,is(2));

        assertNodeProperty(mnemonic.name(), "Tax_Code_2", node);
        assertNodeProperty(mnemonicLowerCase.name(), "tax_code_2",node);
        assertNodeProperty(scientificName.name(), "Ncbi_Scientific_2",node);
        assertNodeProperty(scientificNameLowerCase.name(),"ncbi_scientific_2",node);
        assertNodeProperty(commonName.name(),"Ncbi_Common_2", node);
        assertNodeProperty(commonNameLowerCase.name(),"ncbi_common_2", node);
        assertNodeProperty(synonym.name(), "sptr_synonym_2",node);
        assertNodeProperty(rank.name(),"rank_2",node);
        assertChildOfRelationsShip(false,node);
    }

    private void assertChildOfRelationsShip(boolean root,Node node){
        Iterable<Relationship> childOf = node.getRelationships(OUTGOING, RelationshipType.withName
                (TaxonomyRelationships.CHILD_OF.name()));
        assertThat(childOf, notNullValue());
        if(root) {
            assertThat(Iterables.count(childOf),is(0L));
        }else{
            assertThat(Iterables.count(childOf),is(1L));
        }
    }

    private void assertNodeProperty(String propertyName, String expectedValue, Node node) {
        Object property = node.getProperty(propertyName);
        assertThat(property, notNullValue());

        String propertyValue = String.valueOf(property);
        assertThat(propertyValue,is(expectedValue));

    }

    private void assertValidMergedNode(Node node){
        Object taxId = node.getProperty(taxonomyId.name());
        assertThat(taxId, notNullValue());

        int id = Integer.parseInt(""+taxId);
        assertThat(id,allOf(greaterThanOrEqualTo(30), lessThanOrEqualTo(140)));

        Iterable<Relationship> mergedTo = node.getRelationships(OUTGOING,RelationshipType.withName
                (TaxonomyRelationships.MERGED_TO.name()));
        assertThat(mergedTo,notNullValue());
        assertThat(Iterables.count(mergedTo),is(1));
    }

    private void assertValidDeletedNode(Node node){
        Long taxonomyId = node.getId();
        assertThat(taxonomyId, notNullValue());

        int id = Integer.parseInt(""+taxonomyId);
        assertThat(id,allOf(greaterThanOrEqualTo(101), lessThanOrEqualTo(1021)));
    }

    @Override
    public void afterClassSetup() throws IOException{
        neo4JGraphDatabase.shutdown();
        TaxonomyImportTestUtils.cleanNeo4JTemporaryData(config.getNeo4jDatabasePath());
    }
}
