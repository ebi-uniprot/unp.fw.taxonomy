package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

/**
 * Interface used to retrieve Taxonomy information from a datasource using search criteria
 *
 * Created by lgonzales on 19/02/16.
 */
public interface TaxonomyDataAccess {
    /**
     * This method return details about searched {@param taxonomyId}, including it siblings and children
     *
     * @param taxonomyId identification of taxonomy
     * @param basePath base path to build taxonomy parent, children and sibling links
     * @return details about searched taxonomy
     */
    TaxonomyNode getTaxonomyDetailsById(long taxonomyId,String basePath);

    /**
     * This method return list of siblings of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy siblings
     * @return details about searched taxonomy
     */
    Taxonomies getTaxonomySiblingsById(long taxonomyId);

    /**
     * This method return the parent of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy
     * @return details about searched taxonomy parent
     */
    TaxonomyNode getTaxonomyParentById(long taxonomyId);

    /**
     * This method return list of children of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy
     * @return details about searched taxonomy children
     */
    Taxonomies getTaxonomyChildrenById(long taxonomyId);

    /**
     * This method return the details of all taxonomies names that contains {@param taxonomyName}
     *
     * @param taxonomyName Name given to a taxonomy element
     * @param basePath base path to build taxonomy parent, children and sibling links
     * @return List of taxonomies
     */
    Taxonomies getTaxonomyDetailsByName(String taxonomyName, String basePath);

    /**
     * This method return all nodes that are between  {@param taxonomyId1} and {@param taxonomyId2}
     *
     * @param taxonomyId1 identification of taxonomy
     * @param taxonomyId2 identification of taxonomy
     * @return List of taxonomies
     */
    TaxonomyNode checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2);

    /**
     * This method return all nodes that has relationship with taxonomyId in a specific direction
     * and only depth levels.
     *
     * @param nodePathParams {@link PathRequestParams} are taxonomyId, direction and depth
     * @return List of taxonomies
     */
    TaxonomyNode getTaxonomyPath(PathRequestParams nodePathParams);

    /**
     * This method check if exist any historical change for {@param id} and it there is, it will return the the
     * new identification of taxonomy and if there is not, it will return -1.
     *
     * @param id identification of taxonomy
     * @return new identification of taxonomy if exist
     */
    long checkTaxonomyIdHistoricalChange(long id);

}
