package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

/**
 * Created by lgonzales on 10/03/16.
 */
public class NodeRelationshipParams {

    private long from;
    private long to;

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    @Override public String toString() {
        return "NodeRelationshipParams{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NodeRelationshipParams that = (NodeRelationshipParams) o;

        if (getFrom() != that.getFrom()) {
            return false;
        }
        return getTo() == that.getTo();

    }

    @Override public int hashCode() {
        int result = (int) (getFrom() ^ (getFrom() >>> 32));
        result = 31 * result + (int) (getTo() ^ (getTo() >>> 32));
        return result;
    }
}
