package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

/**
 * Created by lgonzales on 10/03/16.
 */
public class NodePathParams {

    private long id;
    private int depth;
    private NodePathDirections direction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public NodePathDirections getDirection() {
        return direction;
    }

    public void setDirection(NodePathDirections direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        this.direction = NodePathDirections.valueOf(direction);
    }


}
