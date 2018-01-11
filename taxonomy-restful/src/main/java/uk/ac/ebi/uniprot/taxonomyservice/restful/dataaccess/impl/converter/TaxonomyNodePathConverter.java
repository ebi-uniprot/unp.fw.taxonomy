package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter;

import org.neo4j.graphdb.Relationship;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;

import java.util.Map;
import java.util.Optional;

/**
 * This converter is responsible to convert cypher query path responses into a TaxonomyNode path
 */
public class TaxonomyNodePathConverter implements Neo4JQueryResulConverter<TaxonomyNode>{

    private long initialId;

    private TaxonomyNode currentPath;

    public TaxonomyNodePathConverter(long initialId,TaxonomyNode currentPath) {
        this.initialId = initialId;
        this.currentPath = currentPath;
    }

    @Override
    public Optional<TaxonomyNode> convert(Map<String, Object> row) {
        Optional<TaxonomyNode> result = Optional.empty();
        Optional<Object> value = getProperty(row,"r");
        if (value.isPresent()) {
            Iterable<Relationship> r = (Iterable<Relationship>) value.get();
            result = Optional.ofNullable(getTaxonomyNodePath(initialId,r,currentPath));
        }
        return result;
    }

    private TaxonomyNode getTaxonomyNodePath(long initialId,Iterable<Relationship> relationships, TaxonomyNode result) {
        TaxonomyNode lastEndNode = null;
        for (Relationship relationship :relationships) {
            TaxonomyNode startNode = TaxonomyNodeConverter.getTaxonomyBaseNode(relationship.getStartNode());
            TaxonomyNode endNode = TaxonomyNodeConverter.getTaxonomyBaseNode(relationship.getEndNode());
            if(result == null){
                if(startNode.getTaxonomyId() == initialId){
                    result = startNode;
                    lastEndNode = startNode.mergeParent(endNode);
                }else{
                    result = endNode;
                    lastEndNode = endNode.mergeChildren(startNode);
                }
            }else{
                if(lastEndNode == null){
                    if(endNode.getTaxonomyId() == initialId){
                        lastEndNode = result.mergeChildren(startNode);
                    }else{
                        lastEndNode = result.mergeParent(endNode);
                    }
                }else{
                    if (lastEndNode.getTaxonomyId() == startNode.getTaxonomyId()) {
                        lastEndNode = lastEndNode.mergeParent(endNode);
                    } else {
                        lastEndNode = lastEndNode.mergeChildren(startNode);
                    }
                }
            }
        }
        return result;
    }

}
