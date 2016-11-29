package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.ID_PARAMETER_IS_REQUIRED;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.ID_PARAMETER_VALID_NUMBER;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.TAXONOMY_ID_PARAM;

/**
 * This class contains request parameters for id requests with paginated  request parameters
 *
 * Created by lgonzales on 25/11/16.
 */
@RequestScoped
public class TaxonomyIdWithPageRequestParams extends PageRequestParams{

    @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
    @NotNull(message = ID_PARAMETER_IS_REQUIRED)
    @PathParam("id")
    @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
    private String id;

    public String getId() {
        return id;
    }

    @JsonIgnore
    public long getLongId() {
        return new Long(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TaxonomyIdWithPageRequestParams{" +
                "id='" + id + '\'' +
                '}';
    }
}
