package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportDelete;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyLabels;

import java.io.IOException;
import java.util.List;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded {@link TaxonomyImportDelete} into Neo4J using {@link BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JDeletedItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportDelete> {
    private static final Logger logger = LoggerFactory.getLogger(Neo4JDeletedItemWriterWithBatchInserter.class);

    private final BatchInserter batchInserter;

    private int pageCount = 0;

    private final Label deleteLabel;


    public Neo4JDeletedItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        deleteLabel = Label.label(TaxonomyLabels.Deleted.name());
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
    public void afterStep(){
        logger.info("completed Neo4JDeletedItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void beforeStep() throws IOException {
        logger.info("starting  Neo4JDeletedItemWriterWithBatchInserter");
    }
}
