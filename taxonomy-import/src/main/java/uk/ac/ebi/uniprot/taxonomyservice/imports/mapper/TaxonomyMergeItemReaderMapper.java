package uk.ac.ebi.uniprot.taxonomyservice.imports.mapper;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportMerge;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * This class is mapping @link{java.sql.ResultSet} returned by SQL executed in @link{TaxonomyImportConfig
 * .itemMergedReader} to @link{TaxonomyImportMerge} object that will be used to save Merged Taxonomy Nodes
 *
 * Created by lgonzales on 29/04/16.
 */
public class TaxonomyMergeItemReaderMapper implements RowMapper<TaxonomyImportMerge> {

    @Override public TaxonomyImportMerge mapRow(ResultSet resultSet, int i) throws SQLException {
        TaxonomyImportMerge mergedNode = new TaxonomyImportMerge();
        mergedNode.setNewTaxonomyId(resultSet.getLong("NEW_TAX_ID"));
        mergedNode.setOldTaxonomyId(resultSet.getLong("OLD_TAX_ID"));
        return mergedNode;
    }


}
