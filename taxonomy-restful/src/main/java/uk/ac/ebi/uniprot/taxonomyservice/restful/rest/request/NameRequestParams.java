package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.FieldNames;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsEnumValue;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.Maximum;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.Minimum;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.NameMinSizeForPartialSearches;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @ApiParam(value = TAXONOMY_FIELD_NAME_PARAM,defaultValue = "SCIENTIFICNAME")
    @IsEnumValue(enumClass = FieldNames.class,message = FIELD_NAME_VALID_VALUES, ignoreCase = true)
    @QueryParam("fieldName")
    private String fieldName;

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

    public String getTaxonomyName() {
        return taxonomyName;
    }

    public void setTaxonomyName(String taxonomyName) {
        this.taxonomyName = taxonomyName;
    }

    public String getSearchType() {
        if(this.searchType == null){
            this.searchType = SearchType.EQUALSTO.toString();
        }
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getFieldName() {
        if(this.fieldName == null){
            this.fieldName = FieldNames.SCIENTIFICNAME.toString();
        }
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPageNumber() {
        if(this.pageNumber == null){
            this.pageNumber = "1";
        }
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
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

    @JsonIgnore
    public String getSearchTypeQueryKeyword(){
        SearchType searchTypeEnum = SearchType.valueOf(getSearchType().toUpperCase());
        return searchTypeEnum.getSearchQueryKeyWord();
    }

    @JsonIgnore
    public String getFieldNameQueryKeyword(){
        FieldNames fieldNameEnum = FieldNames.valueOf(getFieldName().toUpperCase());
        return fieldNameEnum.getSearchFieldName();
    }

    @Override public String toString() {
        return "NameRequestParams{" +
                "taxonomyName='" + taxonomyName + '\'' +
                ", searchType='" + searchType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", pageNumber='" + pageNumber + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", skip='" + getSkip() + '\'' +
                '}';
    }
}
