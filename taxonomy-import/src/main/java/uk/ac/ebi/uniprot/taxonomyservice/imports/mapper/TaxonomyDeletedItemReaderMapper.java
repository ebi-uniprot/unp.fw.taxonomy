package uk.ac.ebi.uniprot.taxonomyservice.imports.mapper;

import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportDelete;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * This class is mapping @link{java.sql.ResultSet} returned by SQL executed in @link{TaxonomyImportConfig
 * .itemDeletedReader} to @link{TaxonomyImportDelete} object that will be used to save Deleted Taxonomy Nodes
 *
 * Created by lgonzales on 29/04/16.
 */
public class TaxonomyDeletedItemReaderMapper implements RowMapper<TaxonomyImportDelete> {

    @Override public TaxonomyImportDelete mapRow(ResultSet resultSet, int i) throws SQLException {
        TaxonomyImportDelete deletedNode = new TaxonomyImportDelete();
        deletedNode.setTaxonomyId(resultSet.getLong("TAX_ID"));
        return deletedNode;
    }
}

