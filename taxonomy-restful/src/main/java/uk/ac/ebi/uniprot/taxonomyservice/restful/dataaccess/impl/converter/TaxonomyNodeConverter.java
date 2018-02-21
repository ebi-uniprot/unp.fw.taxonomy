package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter;

import org.neo4j.graphdb.Node;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;

import java.util.*;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode.TAXONOMY_NODE_FIELDS.*;

/**
 * This converter is responsible to convert cypher query into a TaxonomyNode
 */
public class TaxonomyNodeConverter implements Neo4JQueryResulConverter<TaxonomyNode> {

    private String basePath;

    private boolean hasDetail;

    public TaxonomyNodeConverter(String basePath, boolean hasDetail) {
        this.basePath = basePath;
        this.hasDetail = hasDetail;
    }

    @Override
    public Optional<TaxonomyNode> convert(Map<String, Object> row){
        TaxonomyNode taxonomyNode = null;
        if(row.containsKey("node")) {
            Node node = (Node) row.get("node");
            taxonomyNode = getTaxonomyBaseNode(node);
            if(taxonomyNode != null && hasDetail){
                Long taxId = taxonomyNode.getTaxonomyId();
                taxonomyNode.setParentLink(getTaxonomyParentLink(basePath, row));
                taxonomyNode.setChildrenLinks(getLinkList("children", row, taxId, basePath));
                taxonomyNode.setSiblingsLinks(getLinkList("siblings", row, taxId, basePath));
            }
        }
        return Optional.ofNullable(taxonomyNode);
    }

    static TaxonomyNode getTaxonomyBaseNode(Node node) {
        TaxonomyNode result = null;

        if(node.hasProperty(taxonomyId.name())) {
            String taxId = ""+node.getProperty(taxonomyId.name());
            if(!taxId.isEmpty()) {
                result = new TaxonomyNode();
                result.setTaxonomyId(Long.parseLong(taxId));

                if(node.hasProperty(commonName.name())) {
                    result.setCommonName("" + node.getProperty(commonName.name()));
                }
                if(node.hasProperty(mnemonic.name())) {
                    result.setMnemonic("" + node.getProperty(mnemonic.name()));
                }
                if(node.hasProperty(rank.name())) {
                    result.setRank("" + node.getProperty(rank.name()));
                }
                if(node.hasProperty(superregnum.name())) {
                    result.setSuperregnum("" + node.getProperty(superregnum.name()));
                }
                if(node.hasProperty(scientificName.name())) {
                    result.setScientificName("" + node.getProperty(scientificName.name()));
                }
                if(node.hasProperty(synonym.name())) {
                    result.setSynonym("" + node.getProperty(synonym.name()));
                }
            }
        }

        return result;
    }

    private ArrayList<String> getLinkList(String propertyName, Map<String,Object> row, long id, String basePath) {
        ArrayList<String> result = null;
        Optional<Object> value = getProperty(row, propertyName);
        if (value.isPresent()) {
            Set<String> list = new HashSet<>();
            Iterable<Iterable<String>> pathList = (Iterable<Iterable<String>>) value.get();
            pathList.forEach(wrapper -> wrapper.forEach(item -> list.add(basePath + item)));
            list.remove(basePath + id);
            if (!list.isEmpty()){
                result = new ArrayList<>(list);
            }
        }
        return result;
    }

    private String getTaxonomyParentLink(String basePath, Map<String, Object> row) {
        String result = null;
        Optional<Object> parentIdValue = getProperty(row, "parentId");
        if (parentIdValue.isPresent()) {
            result = basePath + parentIdValue.get();
        }
        return result;
    }

}
