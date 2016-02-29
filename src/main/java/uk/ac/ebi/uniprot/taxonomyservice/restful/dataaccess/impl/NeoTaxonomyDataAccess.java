package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks up Taxonomy entries from Neo4J Graph data store based on search criteria.
 *
 * Created by lgonzales on 19/02/16.
 */
public class NeoTaxonomyDataAccess implements TaxonomyDataAccess{

    @Override public TaxonomyDetailResponse getTaxonomyDetailsById(long taxonomyId) {
        TaxonomyNode node = new TaxonomyNode();
        node.setCommonName("common Name");
        node.setTaxonomyId(12345);
        node.setMnemonic("mnemonic name");
        node.setParentId(67890);
        node.setRank("Rank name");
        node.setScientificName("Scientific Name");
        node.setSynonym("Synonym Name");

        TaxonomyDetailResponse response = new TaxonomyDetailResponse();
        response.setNode(node);

        List<TaxonomyNode> list = new ArrayList<>();
        list.add(node);
        list.add(node);
        list.add(node);

        response.setChildren(list);
        response.setSiblings(list);
        response.setParentLink("parent Link");

        return response;
    }
}
