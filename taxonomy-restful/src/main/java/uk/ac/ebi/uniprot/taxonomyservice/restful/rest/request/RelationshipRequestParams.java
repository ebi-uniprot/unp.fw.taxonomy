package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant.*;

/**
 * This class contains request parameter for /taxonomy/relationship requests
 *
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
public class RelationshipRequestParams {

    @NotNull(message = FROM_PARAMETER_IS_REQUIRED)
    @Pattern(regexp = "[0-9]+", message = FROM_PARAMETER_VALID_NUMBER)
    @QueryParam(value = "from")
    @ApiParam(value = TAXONOMY_FROM_PARAM, required = true)
    private String from;

    @NotNull(message = TO_PARAMETER_IS_REQUIRED)
    @Pattern(regexp = "[0-9]+", message = TO_PARAMETER_VALID_NUMBER)
    @QueryParam(value = "to")
    @ApiParam(value = TAXONOMY_TO_PARAM, required = true)
    private String to;


    public String getFrom() {
        return from;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
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
