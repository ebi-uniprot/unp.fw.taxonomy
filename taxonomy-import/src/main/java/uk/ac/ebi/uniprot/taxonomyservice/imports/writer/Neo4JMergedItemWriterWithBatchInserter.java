package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportMerge;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded @link{TaxonomyImportMerge} "MERGED_TO" relationship into Neo4J using
 * @{BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JMergedItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportMerge> {
    protected static final Log logger = LogFactory.getLog(Neo4JDeletedItemWriterWithBatchInserter.class);

    private BatchInserter batchInserter;

    private int pageCount = 0;

    protected RelationshipType mergedRelationship;

    protected Label mergeLabel;


    public Neo4JMergedItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        mergedRelationship = RelationshipType.withName("MERGED_TO");
        mergeLabel = Label.label("Merged");
    }

    @Override
    public void write(List<? extends TaxonomyImportMerge> list) {
        for (TaxonomyImportMerge nodeModel : list) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("taxonomyId", "" + nodeModel.getOldTaxonomyId());
            batchInserter.createNode(nodeModel.getOldTaxonomyId(),properties,mergeLabel);
            batchInserter.createRelationship(nodeModel.getOldTaxonomyId(),nodeModel.getNewTaxonomyId(),
                    mergedRelationship,null);
        }
        pageCount++;
        logger.info("chunck write for Neo4JMergedItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    @AfterStep
    public void shutdownInserter(){
        logger.info("completed Neo4JMergedItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void createInserter() throws IOException {
        logger.info("starting  Neo4JMergedItemWriterWithBatchInserter");
    }
}

