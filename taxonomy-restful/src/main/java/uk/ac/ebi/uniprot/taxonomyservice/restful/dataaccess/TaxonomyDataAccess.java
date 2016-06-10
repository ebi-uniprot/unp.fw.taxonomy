package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.util.Optional;

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
    Optional<TaxonomyNode> getTaxonomyDetailsById(long taxonomyId,String basePath);

    /**
     * This method return list of siblings of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy siblings
     * @return details about searched taxonomy
     */
    Optional<Taxonomies> getTaxonomySiblingsById(long taxonomyId);

    /**
     * This method return the parent of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy
     * @return details about searched taxonomy parent
     */
    Optional<TaxonomyNode> getTaxonomyParentById(long taxonomyId);

    /**
     * This method return list of children of a searched {@param taxonomyId}.
     *
     * @param taxonomyId identification of taxonomy
     * @return details about searched taxonomy children
     */
    Optional<Taxonomies> getTaxonomyChildrenById(long taxonomyId);

    /**
     * This method return the details of all taxonomies names that contains {@param nameRequestParams.taxonomyName}
     * based on {@param nameRequestParams.searchType}
     *
     * @param nameRequestParams contains taxonomyName and searchType that will be executed the filter
     * @param basePath base path to build taxonomy parent, children and sibling links
     * @return List of taxonomies
     */
    Optional<Taxonomies> getTaxonomyDetailsByName(NameRequestParams nameRequestParams, String basePath);

    /**
     * This method return all nodes that are between  {@param from} and {@param to}
     *
     * @param from identification of taxonomy
     * @param to identification of taxonomy
     * @return taxonomy nodes between {@param from} and {@param to}
     */
    Optional<TaxonomyNode> getTaxonomiesRelationship(long from, long to);

    /**
     * This method return all nodes that has relationship with taxonomyId in a specific direction
     * and only depth levels.
     *
     * @param nodePathParams {@link PathRequestParams} are taxonomyId, direction and depth
     * @return taxonomy path
     */
    Optional<TaxonomyNode> getTaxonomyPath(PathRequestParams nodePathParams);

    /**
     * This method check if exist any historical change for {@param id} and it there is, it will return the the
     * new identification of taxonomy and if there is not, it will return -1.
     *
     * @param id identification of taxonomy
     * @return new identification of taxonomy if exist
     */
    Optional<Long> getTaxonomyHistoricalChange(long id);

}
