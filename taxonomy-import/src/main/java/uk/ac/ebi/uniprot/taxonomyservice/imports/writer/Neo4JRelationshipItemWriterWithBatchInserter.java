package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded @link{TaxonomyImportNode} "CHILD_OF" relationship from node to it parent
 * into Neo4J using @{BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JRelationshipItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportNode> {
    protected static final Log logger = LogFactory.getLog(Neo4JRelationshipItemWriterWithBatchInserter.class);

    private BatchInserter batchInserter;

    private int pageCount = 0;

    protected RelationshipType childOfRelationship;

    public Neo4JRelationshipItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        this.childOfRelationship = DynamicRelationshipType.withName("CHILD_OF");
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
    public void shutdownInserter(){
        logger.info("completed Neo4JRelationshipItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void createInserter() throws IOException{
        logger.info("starting  Neo4JRelationshipItemWriterWithBatchInserter");
    }


}

