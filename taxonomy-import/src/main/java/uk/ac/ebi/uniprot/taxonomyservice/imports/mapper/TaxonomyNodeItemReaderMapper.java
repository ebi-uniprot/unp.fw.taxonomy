package uk.ac.ebi.uniprot.taxonomyservice.imports.mapper;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is mapping {@link java.sql.ResultSet} returned by SQL executed in
 *  {@link TaxonomyImportConfig#itemNodeReader()} to {@link TaxonomyImportNode} object that will be used to save
 *  Taxonomy Nodes
 *
 * Created by lgonzales on 25/04/16.
 */
public class TaxonomyNodeItemReaderMapper implements RowMapper<TaxonomyImportNode>{

    @Override public TaxonomyImportNode mapRow(ResultSet resultSet, int i) throws SQLException {
        TaxonomyImportNode node = new TaxonomyImportNode();
        String common = resultSet.getString("SPTR_COMMON");
        if(common == null){
            common = resultSet.getString("NCBI_COMMON");
        }
        node.setCommonName(common);

        node.setMnemonic(resultSet.getString("TAX_CODE"));
        node.setParentId(resultSet.getLong("PARENT_ID"));
        node.setRank(resultSet.getString("RANK"));
        String scientificName = resultSet.getString("SPTR_SCIENTIFIC");
        if(scientificName == null){
            scientificName = resultSet.getString("NCBI_SCIENTIFIC");
        }
        node.setScientificName(scientificName);
        node.setSynonym(resultSet.getString("SPTR_SYNONYM"));
        node.setTaxonomyId(resultSet.getLong("TAX_ID"));
        node.setSuperregnum(resultSet.getString("SUPERREGNUM"));
        node.setHidden(resultSet.getBoolean("HIDDEN"));

        return node;
    }
}
