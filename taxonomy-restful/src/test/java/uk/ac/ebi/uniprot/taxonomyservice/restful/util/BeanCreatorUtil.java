package uk.ac.ebi.uniprot.taxonomyservice.restful.util;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is able to create Model objects that will be used by unit tests or integration tests.
 *
 * Created by lgonzales on 27/09/16.
 */
public class BeanCreatorUtil {

    public static Optional<TaxonomyNode> getOptionalTaxonomyNode(long taxonomyId){
        return Optional.of(getTaxonomyNode(taxonomyId));
    }

    public static TaxonomyNode getTaxonomyNode(long taxonomyId){
        TaxonomyNode node = new TaxonomyNode();
        node.setTaxonomyId(taxonomyId);
        node.setCommonName("common Name "+taxonomyId);
        node.setMnemonic("Mnemonic "+taxonomyId);
        node.setRank("Rank "+taxonomyId);
        node.setSuperregnum("Superregnum "+taxonomyId);
        node.setScientificName("ScientificName "+taxonomyId);
        node.setSynonym("Synonym "+taxonomyId);
        return node;
    }

    public static Optional<Taxonomies> getOptionalTaxonomies(long ... ids) {
        return Optional.ofNullable(getTaxonomies(ids));
    }

    public static Taxonomies getTaxonomies(long ... ids) {
        List<TaxonomyNode> nodes = new ArrayList<>();
        for (long id : ids) {
            nodes.add(getTaxonomyNode(id));
        }
        Taxonomies taxonomies = null;
        if(!nodes.isEmpty()){
            taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(nodes);
        }
        return taxonomies;
    }
}
