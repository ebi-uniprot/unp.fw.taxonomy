package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsLongListParam;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.ListParamMinMaxSize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.IDS_PARAMETER_IS_REQUIRED;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.IDS_PARAMETER_VALID_NUMBER;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.TAXONOMY_IDS_PARAM;

/**
 * This class contains request parameters for /taxonomy/ancestor requests
 *
 * Created by lgonzales on 05/07/16.
 */
@RequestScoped
public class AncestorRequestParams {

    @ApiParam(value = TAXONOMY_IDS_PARAM, required = true)
    @NotNull(message = IDS_PARAMETER_IS_REQUIRED)
    @IsLongListParam(message = IDS_PARAMETER_VALID_NUMBER)
    @ListParamMinMaxSize(maxSize = 50, minSize = 2, message = IDS_PARAMETER_MIN_MAX_SIZE)
    @PathParam("ids")
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @JsonIgnore
    public List<Long> getIdList(){
        ArrayList<Long> taxonomyIds = new ArrayList<>();
        if(ids != null && !ids.isEmpty()) {
            String[] taxonomyIdsStr = ids.split(",");
            Arrays.stream(taxonomyIdsStr).forEach(id -> taxonomyIds.add(Long.parseLong(id)));
        }
        return taxonomyIds;
    }

    @Override public String toString() {
        return "AncestorRequestParams{" +
                "ids='" + ids + '\'' +
                '}';
    }
}
