package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyRelationshipResponse;

import java.util.List;

/**
 * Interface used to retrieve a Taxonomy entry from a datasource using search criteria
 *
 * Created by lgonzales on 19/02/16.
 */
public interface TaxonomyDataAccess {

    TaxonomyDetailResponse getTaxonomyDetailsById(long taxonomyId);

    List<TaxonomyDetailResponse> getTaxonomyDetailsByName(String taxonomyName);

    TaxonomyRelationshipResponse checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2);

    List<TaxonomyNode> getTaxonomyPath(long taxonomyId, String direction, int depth);

}
