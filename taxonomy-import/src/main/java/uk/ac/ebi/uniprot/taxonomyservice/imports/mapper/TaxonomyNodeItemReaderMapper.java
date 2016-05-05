package uk.ac.ebi.uniprot.taxonomyservice.imports.mapper;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * This class is mapping @link{java.sql.ResultSet} returned by SQL executed in @link{TaxonomyImportConfig
 * .itemNodeReader} to @link{TaxonomyImportNode} object that will be used to save Taxonomy Nodes
 *
 * Created by lgonzales on 25/04/16.
 */
public class TaxonomyNodeItemReaderMapper implements RowMapper<TaxonomyImportNode>{

    @Override public TaxonomyImportNode mapRow(ResultSet resultSet, int i) throws SQLException {
        TaxonomyImportNode node = new TaxonomyImportNode();
        node.setCommonName(resultSet.getString("SPTR_COMMON"));
        node.setMnemonic(resultSet.getString("SPTR_CODE"));
        node.setParentId(resultSet.getLong("PARENT_ID"));
        node.setRank(resultSet.getString("RANK"));
        node.setScientificName(resultSet.getString("SPTR_SCIENTIFIC"));
        node.setSynonym(resultSet.getString("SPTR_SYNONYM"));
        node.setTaxonomyId(resultSet.getLong("TAX_ID"));
        return node;
    }
}
