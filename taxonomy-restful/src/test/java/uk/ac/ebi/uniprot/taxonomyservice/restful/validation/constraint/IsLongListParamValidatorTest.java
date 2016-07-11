package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to test IsLongListParamValidator methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class IsLongListParamValidatorTest {

    @Test
    public void isValidWithValidListReturnTrue(){
        MockedIsLongListParamValidator isLongListParamValidator = new MockedIsLongListParamValidator();
        String valueForValidation = "10,100,200,300";
        boolean result = isLongListParamValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithValidOneElementReturnTrue(){
        MockedIsLongListParamValidator isLongListParamValidator = new MockedIsLongListParamValidator();
        String valueForValidation = "10";
        boolean result = isLongListParamValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }


    @Test
    public void isValidWithInvalidValueReturnFalse(){
        MockedIsLongListParamValidator isLongListParamValidator = new MockedIsLongListParamValidator();
        String valueForValidation = "10,INVALID,200";
        boolean result = isLongListParamValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithNullValueForOptionalFieldScenarioReturnTrue(){
        MockedIsLongListParamValidator isLongListParamValidator = new MockedIsLongListParamValidator();
        String valueForValidation = null;
        boolean result = isLongListParamValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    private class MockedIsLongListParamValidator extends IsLongListParam.IsLongListParamValidator{

        public MockedIsLongListParamValidator(){
            IsLongListParam isLongListParam = new IsLongListParam() {

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
            initialize(isLongListParam);
        }
    }


}