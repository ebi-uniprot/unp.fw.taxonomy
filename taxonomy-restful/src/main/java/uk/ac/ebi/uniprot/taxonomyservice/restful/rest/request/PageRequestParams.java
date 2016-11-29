package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.Maximum;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.Minimum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.*;

/**
 * This class contains request parameters for paginated requests
 *
 * Created by lgonzales on 25/11/16.
 */
@RequestScoped
public class PageRequestParams {

    @ApiParam(value = TAXONOMY_PAGE_NUMBER_PARAM,defaultValue = "1")
    @Pattern(regexp = "[0-9]+", message = PAGE_NUMBER_PARAMETER_VALID_NUMBER)
    @Minimum(min = 1, message = PAGE_NUMBER_PARAMETER_MIN_VALUE)
    @QueryParam("pageNumber")
    private String pageNumber;

    @ApiParam(value = TAXONOMY_PAGE_SIZE_PARAM,defaultValue = "100")
    @Pattern(regexp = "[0-9]+", message = PAGE_SIZE_PARAMETER_VALID_NUMBER)
    @Maximum(max = 200, message = PAGE_SIZE_PARAMETER_MAX_VALUE)
    @QueryParam("pageSize")
    private String pageSize;

    public String getPageNumber() {
        if(this.pageNumber == null){
            this.pageNumber = "1";
        }
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonIgnore
    public int getPageNumberInt() {
        return Integer.parseInt(getPageNumber());
    }

    public String getPageSize() {
        if(this.pageSize == null){
            this.pageSize = "100";
        }
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @JsonIgnore
    public int getPageSizeInt() {
        return Integer.parseInt(getPageSize());
    }

    @JsonIgnore
    public int getSkip() {
        int localPageSize = Integer.parseInt(getPageSize());
        int localPageNumber = Integer.parseInt(getPageNumber());
        return ((localPageNumber * localPageSize) - localPageSize);
    }

    @Override
    public String toString() {
        return "PageRequestParams{" +
                "pageNumber='" + pageNumber + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", skip='" + getSkip() + '\'' +
                '}';
    }
}
