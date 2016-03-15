package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
public class RelationshipRequestParams {

    @NotNull
    @QueryParam(value = "from")
    @ApiParam(value = "from", required = true)
    private long from;

    @NotNull
    @QueryParam(value = "to")
    @ApiParam(value = "to", required = true)
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

        RelationshipRequestParams that = (RelationshipRequestParams) o;

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
