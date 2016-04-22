package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mock taxonomy data access that create and return mock results based on {@values #validIds},  {@values
 * validNames} and {@values changedIds} attributes.
 *
 * Created by lgonzales on 19/02/16.
 */
public class MockTaxonomyDataAccess implements TaxonomyDataAccess {

    private static final long[] validIds = {11111,12121, 12345, 22222, 55555};

    private static final long[] changedIds = {23232,33333,34343, 44444};

    private static final String[] validNames = {"gnathostomata", "human", "metazoa"};

    private static final String TAXONOMY_BASE_LINK = "https://localhost:9090/uniprot/services/restful/taxonomy/id/";

    @Override
    public TaxonomyNode getTaxonomyDetailsById(long taxonomyId,String basePath) {
        TaxonomyNode node = null;
        if (Arrays.binarySearch(validIds, taxonomyId) > 0) {
            node = getTaxonomyMockedNodeBase(taxonomyId, "node");

            node.setParentLink(basePath + "56789");

            List<String> siblingsLinkList = Arrays.asList(basePath + (taxonomyId + 1), basePath +
                    (taxonomyId + 2), basePath + (taxonomyId + 3));
            node.setSiblingsLinks(siblingsLinkList);

            List<String> childrenLinkList = Arrays.asList(basePath + (taxonomyId + 10), basePath +
                    (taxonomyId + 11), basePath + (taxonomyId + 12));
            node.setChildrenLinks(childrenLinkList);
        }
        return node;
    }

    @Override
    public Taxonomies getTaxonomySiblingsById(long taxonomyId) {
        Taxonomies response = null;
        if (Arrays.binarySearch(validIds, taxonomyId) > 0) {
            List<TaxonomyNode> detailList = new ArrayList<>();
            detailList.add(getTaxonomyMockedNodeBase(1000, " Sibling 1"));
            detailList.add(getTaxonomyMockedNodeBase(2000, "Sibling 2"));
            detailList.add(getTaxonomyMockedNodeBase(3000, "Sibling 3"));

            response = new Taxonomies(detailList);
        }
        return response;
    }

    @Override
    public TaxonomyNode getTaxonomyParentById(long taxonomyId) {
        if (Arrays.binarySearch(validIds, taxonomyId) > 0) {
            return getTaxonomyMockedNodeBase(999, "parent");
        } else {
            return null;
        }
    }

    @Override
    public Taxonomies getTaxonomyChildrenById(long taxonomyId) {
        Taxonomies response = null;
        if (Arrays.binarySearch(validIds, taxonomyId) > 0) {
            List<TaxonomyNode> detailList = new ArrayList<>();
            detailList.add(getTaxonomyMockedNodeBase(1000, " Children 1"));
            detailList.add(getTaxonomyMockedNodeBase(2000, "Children 2"));
            detailList.add(getTaxonomyMockedNodeBase(3000, "Children 3"));

            response = new Taxonomies(detailList);
        }
        return response;
    }

    @Override
    public Taxonomies getTaxonomyDetailsByName(String taxonomyName, String basePath) {
        Taxonomies response = null;
        if (Arrays.binarySearch(validNames, taxonomyName.toLowerCase()) > 0) {
            List<TaxonomyNode> detailList = new ArrayList<>();
            detailList.add(getTaxonomyDetailsById(validIds[1],basePath));
            detailList.add(getTaxonomyDetailsById(validIds[2],basePath));
            detailList.add(getTaxonomyDetailsById(validIds[3],basePath));

            response = new Taxonomies(detailList);
        }
        return response;
    }

    @Override
    public TaxonomyNode checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2) {
        if (Arrays.binarySearch(validIds, taxonomyId1) > 0 && Arrays.binarySearch(validIds, taxonomyId2) > 0) {
            TaxonomyNode taxonomy1 = getTaxonomyMockedNodeBase(taxonomyId1, "Taxonomy " + taxonomyId1);
            TaxonomyNode taxonomy2 = getTaxonomyMockedNodeBase(taxonomyId1 + 10, "Taxonomy 2");
            TaxonomyNode taxonomy3 = getTaxonomyMockedNodeBase(taxonomyId1 + 20, "Taxonomy 3");
            TaxonomyNode taxonomy4 = getTaxonomyMockedNodeBase(taxonomyId1 + 30, "Taxonomy 4");
            TaxonomyNode taxonomy5 = getTaxonomyMockedNodeBase(taxonomyId1 + 40, "Taxonomy 5");
            TaxonomyNode taxonomy6 = getTaxonomyMockedNodeBase(taxonomyId2, "Taxonomy " + taxonomyId2);

            taxonomy5.setChildren(Arrays.asList(taxonomy6));
            taxonomy4.setChildren(Arrays.asList(taxonomy5));
            taxonomy3.setSiblings(Arrays.asList(taxonomy4));
            taxonomy2.setParent(taxonomy3);
            taxonomy1.setParent(taxonomy2);

            return taxonomy1;
        } else {
            return null;
        }
    }

    @Override
    public TaxonomyNode getTaxonomyPath(PathRequestParams nodePathParams) {
        if (Arrays.binarySearch(validIds, Long.valueOf(nodePathParams.getId())) > 0) {
            switch (nodePathParams.getPathDirection()) {
                case TOP:
                    TaxonomyNode levelNode =
                            getTaxonomyMockedNodeBase(Long.valueOf(nodePathParams.getId()) + nodePathParams.getDepth(),
                                    "Level " + nodePathParams.getDepth());
                    for (int currentLevel = nodePathParams.getDepth() - 1; currentLevel >= 0; currentLevel--) {
                        TaxonomyNode parentNode =
                                getTaxonomyMockedNodeBase(Long.valueOf(nodePathParams.getId()) + currentLevel, "Level " +
                                        "" + currentLevel);
                        parentNode.setParent(levelNode);

                        levelNode = parentNode;
                    }
                    return levelNode;
                case BOTTOM:
                    //TODO: Improve it to enable more deep levels (recursively build tree depth levels)
                    TaxonomyNode rootNode = getTaxonomyMockedNodeBase(Long.valueOf(nodePathParams.getId()), "Root");

                    List<TaxonomyNode> childrenLevel = Arrays.asList(getTaxonomyMockedNodeBase(1, "Level 1"),
                            getTaxonomyMockedNodeBase(1, "Level 1"));
                    rootNode.setChildren(childrenLevel);
                    for (TaxonomyNode nodeItem : rootNode.getChildren()) {
                        childrenLevel = Arrays.asList(getTaxonomyMockedNodeBase(1, "Level 2"),
                                getTaxonomyMockedNodeBase(1, "Level 2"));
                        nodeItem.setChildren(childrenLevel);
                    }
                    return rootNode;

            }
        }
        return null;
    }

    @Override
    public long checkTaxonomyIdHistoricalChange(long id) {
        if (Arrays.binarySearch(changedIds,id) > 0) {
            return 55555;
        }else{
            return -1;
        }
    }

    private TaxonomyNode getTaxonomyMockedNodeBase(long id, String nodeName) {
        TaxonomyNode node = new TaxonomyNode();
        node.setTaxonomyId(id);
        node.setCommonName(nodeName + " common Name");
        node.setMnemonic(nodeName + " mnemonic name");
        node.setRank("Rank name");
        node.setScientificName(nodeName + " Scientific Name");
        node.setSynonym("Synonym Name");
        return node;
    }

}
