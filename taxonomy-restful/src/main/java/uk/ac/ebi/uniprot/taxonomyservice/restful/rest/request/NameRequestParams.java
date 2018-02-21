package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import org.glassfish.jersey.process.internal.RequestScoped;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.FieldNames;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsEnumValue;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.NameMinSizeForPartialSearches;

import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.*;

/**
 * This class contains request parameters for /taxonomy/name requests
 *
 * Created by lgonzales on 09/06/16.
 */
@RequestScoped
@NameMinSizeForPartialSearches(message = NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES, min = 3)
public class NameRequestParams extends PageRequestParams{

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

    @JsonIgnore
    public String getSearchTypeQueryKeyword(){
        SearchType searchTypeEnum = SearchType.valueOf(getSearchType().toUpperCase());
        return searchTypeEnum.getSearchQueryKeyWord();
    }

    @JsonIgnore
    public String[] getFieldNameQueryKeyword(){
        FieldNames fieldNameEnum = FieldNames.valueOf(getFieldName().toUpperCase());
        return fieldNameEnum.getSearchFieldName();
    }

    @Override public String toString() {
        return "NameRequestParams{" +
                "taxonomyName='" + taxonomyName + '\'' +
                ", searchType='" + searchType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
