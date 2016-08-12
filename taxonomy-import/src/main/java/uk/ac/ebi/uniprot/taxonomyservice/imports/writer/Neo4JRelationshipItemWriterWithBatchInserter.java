package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyRelationships;

import java.io.IOException;
import java.util.List;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded {@link TaxonomyImportNode} "CHILD_OF" relationship from node to it parent
 * into Neo4J using {@link BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JRelationshipItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportNode> {
    private static final Logger logger = LoggerFactory.getLogger(Neo4JRelationshipItemWriterWithBatchInserter.class);

    private final BatchInserter batchInserter;

    private int pageCount = 0;

    private final RelationshipType childOfRelationship;

    public Neo4JRelationshipItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        this.childOfRelationship = RelationshipType.withName(TaxonomyRelationships.CHILD_OF.name());
    }

    @Override
    public void write(List<? extends TaxonomyImportNode> list) {
        for (TaxonomyImportNode nodeModel : list) {
            if (nodeModel.getParentId() != null && nodeModel.getParentId() > 0) {
                batchInserter.createRelationship(nodeModel.getTaxonomyId(),nodeModel.getParentId(),
                        childOfRelationship,null);
            }
        }
        pageCount++;
        logger.info("chunck write for Neo4JRelationshipItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    @AfterStep
    public void afterStep(){
        logger.info("completed Neo4JRelationshipItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void beforeStep() throws IOException{
        logger.info("starting  Neo4JRelationshipItemWriterWithBatchInserter");
    }


}

