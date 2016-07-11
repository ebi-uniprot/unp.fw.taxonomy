package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to unit test NameMinSizeValidator methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class NameMinSizeValidatorTest {

    @Test
    public void isValidWithContainsAndInvalidSizeReturnFalse(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.CONTAINS,"1");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithEndsWithAndInvalidSizeReturnFalse(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.ENDSWITH,"1");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithStartWithAndInvalidSizeReturnFalse(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.STARTSWITH,"1");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithEqualsToAndInvalidSizeReturnTrue(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.EQUALSTO,"1");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithContainsAndValidSizeReturnTrue(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.CONTAINS,"12");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithEndsWithAndValidSizeReturnTrue(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.ENDSWITH,"123");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithStartWithAndValidSizeReturnTrue(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.STARTSWITH,"123");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithEqualsToAndValidSizeReturnTrue(){
        MockedNameMinSizeValidator nameMinSizeValidator = new MockedNameMinSizeValidator(2);
        NameRequestParams nameParams = getNameRequestParams(SearchType.EQUALSTO,"123");
        boolean result = nameMinSizeValidator.isValid(nameParams,null);
        assertThat(result, is(true));
    }

    private NameRequestParams getNameRequestParams(SearchType searchType,String taxonomyName){
        NameRequestParams nameRequestParams = new NameRequestParams();
        nameRequestParams.setSearchType(searchType.toString());
        nameRequestParams.setTaxonomyName(taxonomyName);
        nameRequestParams.setPageSize("100");
        nameRequestParams.setPageNumber("1");
        nameRequestParams.setFieldName("");
        return nameRequestParams;
    }

    private class MockedNameMinSizeValidator extends NameMinSizeForPartialSearches.NameMinSizeValidator{

        public MockedNameMinSizeValidator(int min){
            NameMinSizeForPartialSearches nameMinSizeForPartialSearches = new NameMinSizeForPartialSearches() {

                @Override public int min() {
                    return min;
                }

                @Override public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override public String message() {
                    return null;
                }

                @Override public Class<?>[] groups() {
                    return null;
                }

                @Override public Class<? extends Payload>[] payload() {
                    return null;
                }
            };
            initialize(nameMinSizeForPartialSearches);
        }
    }


}