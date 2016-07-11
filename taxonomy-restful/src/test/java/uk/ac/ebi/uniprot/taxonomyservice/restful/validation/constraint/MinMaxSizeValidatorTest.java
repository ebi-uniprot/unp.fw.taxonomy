package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to test MinMaxSizeValidator methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class MinMaxSizeValidatorTest {

    @Test
    public void isValidWithValidListSizeMaxReturnTrue(){
        MockedMinMaxSizeValidator minMaxSizeValidator = new MockedMinMaxSizeValidator(2,4);
        String valueForValidation = "1,2,3,4";
        boolean result = minMaxSizeValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }


    @Test
    public void isValidWithValidListSizeMinReturnTrue(){
        MockedMinMaxSizeValidator minMaxSizeValidator = new MockedMinMaxSizeValidator(2,4);
        String valueForValidation = "1,2";
        boolean result = minMaxSizeValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithInValidListSizeMaxReturnFalse(){
        MockedMinMaxSizeValidator minMaxSizeValidator = new MockedMinMaxSizeValidator(2,4);
        String valueForValidation = "1,2,3,4,5";
        boolean result = minMaxSizeValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithInValidListSizeMinReturnFalse(){
        MockedMinMaxSizeValidator minMaxSizeValidator = new MockedMinMaxSizeValidator(2,4);
        String valueForValidation = "1";
        boolean result = minMaxSizeValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithNullValueForOptionalFieldScenarioReturnTrue(){
        MockedMinMaxSizeValidator minMaxSizeValidator = new MockedMinMaxSizeValidator(2,4);
        String valueForValidation = null;
        boolean result = minMaxSizeValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    private class MockedMinMaxSizeValidator extends ListParamMinMaxSize.MinMaxSizeValidator{

        public MockedMinMaxSizeValidator(int minSize, int maxSize){
            ListParamMinMaxSize isLongListParam = new ListParamMinMaxSize() {

                @Override public int minSize() {
                    return minSize;
                }

                @Override public int maxSize() {
                    return maxSize;
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
            initialize(isLongListParam);
        }
    }


}