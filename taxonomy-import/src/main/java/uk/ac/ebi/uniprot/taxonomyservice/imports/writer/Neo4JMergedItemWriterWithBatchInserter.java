package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportMerge;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyFields;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyLabels;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyRelationships;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded {@link TaxonomyImportMerge} "MERGED_TO" relationship into Neo4J using
 * {@link BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JMergedItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportMerge> {
    private static final Logger logger = LoggerFactory.getLogger(Neo4JDeletedItemWriterWithBatchInserter.class);

    private final BatchInserter batchInserter;

    private int pageCount = 0;

    private final RelationshipType mergedRelationship;

    private final Label mergeLabel;


    public Neo4JMergedItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        mergedRelationship = RelationshipType.withName(TaxonomyRelationships.MERGED_TO.name());
        mergeLabel = Label.label(TaxonomyLabels.Merged.name());
    }

    @Override
    public void write(List<? extends TaxonomyImportMerge> list) {
        for (TaxonomyImportMerge nodeModel : list) {
            Map<String, Object> properties = createPropertyMapWithTaxonomyId(nodeModel);
            batchInserter.createNode(nodeModel.getOldTaxonomyId(),properties,mergeLabel);
            batchInserter.createRelationship(nodeModel.getOldTaxonomyId(),nodeModel.getNewTaxonomyId(),
                    mergedRelationship,null);
        }
        pageCount++;
        logger.info("chunck write for Neo4JMergedItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    private Map<String, Object> createPropertyMapWithTaxonomyId(TaxonomyImportMerge nodeModel) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(TaxonomyFields.taxonomyId.name(), String.valueOf(nodeModel.getOldTaxonomyId()));
        return properties;
    }

    @AfterStep
    public void afterStep(){
        logger.info("completed Neo4JMergedItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void beforeStep() throws IOException {
        logger.info("starting  Neo4JMergedItemWriterWithBatchInserter");
    }
}

