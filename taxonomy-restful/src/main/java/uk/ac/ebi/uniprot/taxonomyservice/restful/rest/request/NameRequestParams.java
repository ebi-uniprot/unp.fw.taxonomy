package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsEnumValue;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.NameMinSizeForPartialSearches;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant.*;

/**
 * This class contains request parameters for /taxonomy/name requests
 *
 * Created by lgonzales on 09/06/16.
 */
@RequestScoped
@NameMinSizeForPartialSearches(message = NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES, min = 4)
public class NameRequestParams {

    @ApiParam(value = TAXONOMY_NAME_PARAM, required = true)
    @NotNull(message = NAME_PARAMETER_IS_REQUIRED)
    @PathParam("name")
    private String taxonomyName;

    @ApiParam(value = TAXONOMY_SEARCH_TYPE_PARAM,defaultValue = "EQUALSTO")
    @IsEnumValue(enumClass = SearchType.class,message = SEARCH_TYPE_VALID_VALUES, ignoreCase = true)
    @QueryParam("searchType")
    private String searchType;

    public String getTaxonomyName() {
        return taxonomyName;
    }

    public void setTaxonomyName(String taxonomyName) {
        this.taxonomyName = taxonomyName;
    }

    public String getSearchType() {
        if(searchType == null){
            this.searchType = SearchType.EQUALSTO.toString();
        }
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    @JsonIgnore
    public String getSearchTypeQueryKeyword(){
        SearchType searchType = SearchType.valueOf(this.searchType.toUpperCase());
        String searchQueryKeyword = null;
        switch (searchType){
            case CONTAINS:
                searchQueryKeyword = "CONTAINS";
                break;
            case ENDSWITH:
                searchQueryKeyword = "ENDS WITH";
                break;
            case STARTSWITH:
                searchQueryKeyword = "STARTS WITH";
                break;
            case EQUALSTO:
                searchQueryKeyword = "=";
                break;
        }
        return searchQueryKeyword;
    }

    @Override public String toString() {
        return "NameRequestParams{" +
                "taxonomyName='" + taxonomyName + '\'' +
                ", searchType='" + searchType + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NameRequestParams that = (NameRequestParams) o;

        if (getTaxonomyName() != null ? !getTaxonomyName().equals(that.getTaxonomyName()) :
                that.getTaxonomyName() != null) {
            return false;
        }
        return getSearchType().equals(that.getSearchType());

    }

    @Override public int hashCode() {
        int result = getTaxonomyName() != null ? getTaxonomyName().hashCode() : 0;
        result = 31 * result + getSearchType().hashCode();
        return result;
    }
}