package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * This class contains request parameter for /taxonomy/relationship requests
 *
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
public class RelationshipRequestParams {

    @NotNull
    @QueryParam(value = "from")
    @ApiParam(value = "from", required = true)
    private Long from;

    @NotNull
    @QueryParam(value = "to")
    @ApiParam(value = "to", required = true)
    private Long to;


    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
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

        if (getFrom() != null ? !getFrom().equals(that.getFrom()) : that.getFrom() != null) {
            return false;
        }
        return getTo() != null ? getTo().equals(that.getTo()) : that.getTo() == null;

    }

    @Override public int hashCode() {
        int result = getFrom() != null ? getFrom().hashCode() : 0;
        result = 31 * result + (getTo() != null ? getTo().hashCode() : 0);
        return result;
    }
}
