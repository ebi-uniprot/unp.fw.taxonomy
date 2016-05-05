package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * Class responsible to save/write loaded @link{TaxonomyImportNode} into Neo4J using @{BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JNodeItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportNode> {
    protected static final Log logger = LogFactory.getLog(Neo4JNodeItemWriterWithBatchInserter.class);

    private BatchInserter batchInserter;

    private int pageCount = 0;

    protected Label nodeLabel;


    public Neo4JNodeItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        nodeLabel = DynamicLabel.label("Node");
    }

    @Override
    public void write(List<? extends TaxonomyImportNode> list) {
        for (TaxonomyImportNode nodeModel : list) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("taxonomyId", "" + nodeModel.getTaxonomyId());
            if (nodeModel.getMnemonic() != null) {
                properties.put("mnemonic", nodeModel.getMnemonic());
            }
            if (nodeModel.getScientificName() != null) {
                properties.put("scientificName", nodeModel.getScientificName());
            }
            if (nodeModel.getCommonName() != null) {
                properties.put("commonName", nodeModel.getCommonName());
            }
            if (nodeModel.getSynonym() != null) {
                properties.put("synonym", nodeModel.getSynonym());
            }
            if (nodeModel.getRank() != null) {
                properties.put("rank", nodeModel.getRank());
            }
            batchInserter.createNode(nodeModel.getTaxonomyId(),properties,nodeLabel);
        }
        pageCount++;
        logger.info("chunck write for Neo4JNodeItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    @AfterStep
    public void shutdownInserter(){
        logger.info("completed Neo4JNodeItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void createInserter() throws IOException{
        logger.info("starting  Neo4JNodeItemWriterWithBatchInserter");
    }


}
