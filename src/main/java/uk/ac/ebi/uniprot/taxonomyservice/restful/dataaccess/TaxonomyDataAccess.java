package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNamesResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyPathResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyRelationshipResponse;

/**
 * Interface used to retrieve a Taxonomy entry from a datasource using search criteria
 *
 * Created by lgonzales on 19/02/16.
 */
public interface TaxonomyDataAccess {
    /**
     *
     * @param taxonomyId
     * @return
     */
    TaxonomyDetailResponse getTaxonomyDetailsById(long taxonomyId);

    /**
     *
     * @param taxonomyName
     * @return
     */
    TaxonomyNamesResponse getTaxonomyDetailsByName(String taxonomyName);

    /**
     *
     * @param taxonomyId1
     * @param taxonomyId2
     * @return
     */
    TaxonomyRelationshipResponse checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2);

    /**
     *
     * @param taxonomyId
     * @param direction
     * @param depth
     * @return
     */
    TaxonomyPathResponse getTaxonomyPath(long taxonomyId, String direction, int depth);

}
