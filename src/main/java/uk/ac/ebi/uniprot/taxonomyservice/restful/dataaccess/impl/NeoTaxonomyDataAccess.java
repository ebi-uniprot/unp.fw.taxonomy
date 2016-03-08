package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks up Taxonomy entries from Neo4J Graph data store based on search criteria.
 *
 * Created by lgonzales on 19/02/16.
 */
public class NeoTaxonomyDataAccess implements TaxonomyDataAccess {

    @Override public TaxonomyDetailResponse getTaxonomyDetailsById(long taxonomyId) {
        //TODO: Remove mock method content
        TaxonomyNode node = getTaxonomyMockedNode(taxonomyId, "node");

        TaxonomyDetailResponse response = new TaxonomyDetailResponse();
        response.setNode(node);

        List<TaxonomyNode> childList = new ArrayList<>();
        childList.add(getTaxonomyMockedNode(1, "child 1"));
        childList.add(getTaxonomyMockedNode(2, "child 2"));
        childList.add(getTaxonomyMockedNode(3, "child 3"));

        response.setChildren(childList);

        List<TaxonomyNode> siblingsList = new ArrayList<>();
        siblingsList.add(getTaxonomyMockedNode(4, "sibling 1"));
        siblingsList.add(getTaxonomyMockedNode(5, "sibling 2"));
        siblingsList.add(getTaxonomyMockedNode(6, "sibling 3"));

        response.setSiblings(siblingsList);
        response.setParentLink("parent Link");

        return response;
    }

    @Override public TaxonomyNamesResponse getTaxonomyDetailsByName(String taxonomyName) {
        //TODO: Remove mock method content
        List<TaxonomyDetailResponse> detailList = new ArrayList<>();
        detailList.add(getTaxonomyDetailsById(1));
        detailList.add(getTaxonomyDetailsById(2));
        detailList.add(getTaxonomyDetailsById(3));

        TaxonomyNamesResponse response = new TaxonomyNamesResponse(detailList);

        return response;
    }

    @Override
    public TaxonomyRelationshipResponse checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2) {
        //TODO: Remove mock method content
        TaxonomyRelationshipResponse response = new TaxonomyRelationshipResponse();
        response.setHasRelationship(true);

        List<TaxonomyNode> nodeList = new ArrayList<>();
        nodeList.add(getTaxonomyMockedNode(1, "relationship 1"));
        nodeList.add(getTaxonomyMockedNode(2, "relationship 2"));
        nodeList.add(getTaxonomyMockedNode(3, "relationship 3"));

        response.setRelationshipPath(nodeList);
        return response;
    }

    @Override public TaxonomyPathResponse getTaxonomyPath(long taxonomyId, String direction, int depth) {
        //TODO: Remove mock method content

        List<TaxonomyNode> pathList = new ArrayList<>();
        pathList.add(getTaxonomyMockedNode(1, "path 1"));
        pathList.add(getTaxonomyMockedNode(2, "path 2"));
        pathList.add(getTaxonomyMockedNode(3, "path 3"));

        TaxonomyPathResponse response = new TaxonomyPathResponse(pathList);

        return response;
    }

    //TODO: Remove this mock method
    private TaxonomyNode getTaxonomyMockedNode(long id, String nodeName) {
        TaxonomyNode node = new TaxonomyNode();
        node.setCommonName(nodeName + " common Name");
        node.setTaxonomyId(id);
        node.setMnemonic(nodeName + " mnemonic name");
        node.setParentId(67890);
        node.setRank("Rank name");
        node.setScientificName(nodeName + "Scientific Name");
        node.setSynonym("Synonym Name");
        return node;
    }

}
