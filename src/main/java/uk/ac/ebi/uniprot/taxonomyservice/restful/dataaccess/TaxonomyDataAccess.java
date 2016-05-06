package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

/**
 * Interface used to retrieve Taxonomy information from the Taxonomy datasource.
 *
 * Created by lgonzales on 19/02/16.
 */
public interface TaxonomyDataAccess {
    /**
     * Retrieves the taxonomy node details, including its siblings and children, for the given {@param taxonomyId}.
     *
     * @param taxonomyId taxonomic node identifier
     * @return the taxonomic node that maps to the taxonomyId, or null if the id does not mpa to a node
     */
    TaxonomyNode getTaxonomyDetailsById(long taxonomyId);

    /**
     * Lists all siblings for the taxonomic node that maps to {@param taxonomyId}.
     *
     * @param taxonomyId id of the node the siblings are associated to
     * @return siblings of the node associated to the taxonomyId
     */
    Taxonomies getTaxonomySiblingsById(long taxonomyId);

    /**
     * Retrieves the parent of the node associated to the identifier {@param taxonomyId}.
     *
     * @param taxonomyId taxonomy identifier
     * @return parent taxonomic node
     */
    TaxonomyNode getTaxonomyParentById(long taxonomyId);

    /**
     * Retrieves all of the direct child nodes for the given {@param taxonomyId}.
     *
     * @param taxonomyId taxonomy identifier
     * @return returns all direct child nodes
     */
    Taxonomies getTaxonomyChildrenById(long taxonomyId);

    /**
     * Retrieves all nodes that directly map to the {@param taxonomyName}
     *
     * Note: Taxonomy names do not always map 1-to-1 to a node. A single name can map to more than one node.
     *
     * @param taxonomyName Name of the taxonomy node to retrieve
     * @return Set of matching taxonomy nodes
     */
    Taxonomies getTaxonomyDetailsByName(String taxonomyName);

    /**
     * Provides a direct path between the node that maps to {@param taxonomyId1} and {@param taxonomyId2}.
     *
     * @param startId taxonomy identifier of the start node
     * @param stopId taxonomy identifier of the stop node
     * @return start node with a string of nodes that eventually lead to the stop node
     */
    TaxonomyNode checkRelationshipBetweenTaxonomies(long startId, long stopId);

    /**
     * Retrieves all nodes associated to a given taxonomy id, for the given direction {up, down}, up to the specified
     * level.
     *
     * @param nodePathParams {@link PathRequestParams} are taxonomyId, direction and depth
     * @return Taxonomic nodes that fulfill the search criteria
     */
    TaxonomyNode getTaxonomyPath(PathRequestParams nodePathParams);

    /**
     * Checks to see if the provided {@param taxonomyId} is still current, or if the node associated to the id has
     * been changed and associated to a newer id. If the id is historical the most up to date id for the node will be
     * returned, if the id is the current then -1 is returned.
     *
     * @param taxonomyId taxonomic node identifier
     * @return a more recent taxonomic node identifier, or -1 if the provided id is the most current
     */
    long checkTaxonomyIdHistoricalChange(long taxonomyId);
}