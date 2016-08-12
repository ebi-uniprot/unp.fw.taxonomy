package uk.ac.ebi.uniprot.taxonomyservice.imports.writer;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyLabels;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

import static uk.ac.ebi.uniprot.taxonomyservice.imports.model.constants.TaxonomyFields.*;

/**
 * Class responsible to save/write loaded {@link TaxonomyImportNode} into Neo4J using {@link BatchInserter} tool
 *
 * Created by lgonzales on 29/04/16.
 */
public class Neo4JNodeItemWriterWithBatchInserter implements ItemWriter<TaxonomyImportNode> {
    private static final Logger logger = LoggerFactory.getLogger(Neo4JNodeItemWriterWithBatchInserter.class);

    private final BatchInserter batchInserter;

    private int pageCount = 0;

    private final Label nodeLabel;


    public Neo4JNodeItemWriterWithBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
        nodeLabel = Label.label(TaxonomyLabels.Node.name());
    }

    @Override
    public void write(List<? extends TaxonomyImportNode> list) {
        for (TaxonomyImportNode nodeModel : list) {
            Map<String, Object> properties = new HashMap<>();
            properties.put(taxonomyId.name(), String.valueOf(nodeModel.getTaxonomyId()));
            if (nodeModel.getMnemonic() != null) {
                properties.put(mnemonic.name(), nodeModel.getMnemonic());
                properties.put(mnemonicLowerCase.name(), nodeModel.getMnemonic().toLowerCase());
            }
            if (nodeModel.getScientificName() != null) {
                properties.put(scientificName.name(), nodeModel.getScientificName());
                properties.put(scientificNameLowerCase.name(), nodeModel.getScientificName().toLowerCase());
            }
            if (nodeModel.getCommonName() != null) {
                properties.put(commonName.name(), nodeModel.getCommonName());
                properties.put(commonNameLowerCase.name(), nodeModel.getCommonName().toLowerCase());
            }
            if (nodeModel.getSynonym() != null) {
                properties.put(synonym.name(), nodeModel.getSynonym());
            }
            if (nodeModel.getRank() != null) {
                properties.put(rank.name(), nodeModel.getRank());
            }
            batchInserter.createNode(nodeModel.getTaxonomyId(),properties,nodeLabel);
        }
        pageCount++;
        logger.info("chunck write for Neo4JNodeItemWriterWithBatchInserter completed: "+
                (pageCount * list.size()));
    }

    @AfterStep
    public void afterStep(){
        logger.info("completed Neo4JNodeItemWriterWithBatchInserter");
    }

    @BeforeStep
    public void beforeStep() throws IOException{
        logger.info("starting  Neo4JNodeItemWriterWithBatchInserter");
    }


}
