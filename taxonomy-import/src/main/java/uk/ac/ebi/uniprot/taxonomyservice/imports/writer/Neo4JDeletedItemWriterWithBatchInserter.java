package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportDelete;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded @link{TaxonomyImportDelete} into Neo4J using @{BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JDeletedItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportDelete> {
    protected static final Log logger = LogFactory.getLog(Neo4JDeletedItemWriterWithBatchInserter.class);

    private BatchInserter batchInserter;

    private int pageCount = 0;

    protected Label deleteLabel;


    public Neo4JDeletedItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        deleteLabel = DynamicLabel.label("Deleted");
    }

    @Override
    public void write(List<? extends TaxonomyImportDelete> list) {
        for (TaxonomyImportDelete nodeModel : list) {
            batchInserter.createNode(nodeModel.getTaxonomyId(),null,deleteLabel);
        }
        pageCount++;
        logger.info("chunck write for Neo4JDeletedItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    @AfterStep
    public void shutdownInserter(){
        logger.info("completed Neo4JDeletedItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void createInserter() throws IOException {
        logger.info("starting  Neo4JDeletedItemWriterWithBatchInserter");
    }
}
