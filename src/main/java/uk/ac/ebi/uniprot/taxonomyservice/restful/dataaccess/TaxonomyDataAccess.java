package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;

/**
 * Interface used to retrieve a Taxonomy entry from a datasource using search criteria
 *
 * Created by lgonzales on 19/02/16.
 */
public interface TaxonomyDataAccess {

    TaxonomyDetailResponse getTaxonomyDetailsById(long taxonomyId);

}
