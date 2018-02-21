package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * This class is used to test NameRequestParams methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class NameRequestParamsTest {

    @Test
    public void getSkipWithNullValuesReturnCorrectValue() {
        NameRequestParams param = getAncestorRequestParam(null,null,null,null);

        int id = param.getSkip();

        assertThat(id, is(0));
    }

    @Test
    public void getSkipToSkipTwoPagesReturnCorrectValue() {
        NameRequestParams param = getAncestorRequestParam(null,"3","10",null);

        int id = param.getSkip();

        assertThat(id, is(20));
    }

    @Test
    public void getSearchTypeQueryKeywordWithNullReturnDefaultEqualsValue() {
        NameRequestParams param = getAncestorRequestParam(null,null,null,null);

        String searchType = param.getSearchTypeQueryKeyword();
        assertThat(searchType, is("="));

    }

    @Test
    public void getSearchTypeQueryKeywordWithValidValueReturnValue() {
        NameRequestParams param = getAncestorRequestParam(null,null,null,"contains");

        String searchType = param.getSearchTypeQueryKeyword();
        assertThat(searchType, is("CONTAINS"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void getSearchTypeQueryKeywordWithInvalidValueThrowsException() {
        NameRequestParams param = getAncestorRequestParam(null,null,null,"invalid");

        String searchType = param.getSearchTypeQueryKeyword();

    }

    @Test
    public void getFieldNameQueryKeywordWithNullReturnDefaultScientificName() {
        NameRequestParams param = getAncestorRequestParam(null,null,null,null);

        String[] fieldName = param.getFieldNameQueryKeyword();

        assertThat(fieldName, notNullValue());
        assertThat(fieldName.length, is(1));
        assertThat(fieldName[0],is("scientificNameLowerCase"));
    }

    @Test
    public void getFieldNameQueryKeywordWithValidValueReturnValue() {
        NameRequestParams param = getAncestorRequestParam("commonname",null,null,null);

        String[] fieldName = param.getFieldNameQueryKeyword();

        assertThat(fieldName, notNullValue());
        assertThat(fieldName.length, is(1));
        assertThat(fieldName[0],is("commonNameLowerCase"));
    }

    @Test
    public void getFieldNameQueryKeywordWithValidNameValueReturnValue() {
        NameRequestParams param = getAncestorRequestParam("name",null,null,null);

        String[] fieldName = param.getFieldNameQueryKeyword();

        assertThat(fieldName, notNullValue());
        assertThat(fieldName.length, is(2));
        assertThat(fieldName[0],is("commonNameLowerCase"));
        assertThat(fieldName[1],is("scientificNameLowerCase"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFieldNameQueryKeywordWithInvalidValueThrowsException() {
        NameRequestParams param = getAncestorRequestParam("invalid",null,null,null);

        String[] fieldName = param.getFieldNameQueryKeyword();
    }

    private NameRequestParams getAncestorRequestParam(String fieldName, String pageNumber, String pageSize,
            String searchType) {
        NameRequestParams param = new NameRequestParams();
        param.setFieldName(fieldName);
        param.setPageNumber(pageNumber);
        param.setPageSize(pageSize);
        param.setTaxonomyName("");
        param.setSearchType(searchType);
        return param;
    }
}