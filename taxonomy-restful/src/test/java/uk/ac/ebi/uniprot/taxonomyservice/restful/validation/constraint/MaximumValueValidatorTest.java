package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to test MaximumValueValidator methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class MaximumValueValidatorTest {


    @Test
    public void isValidWithInvalidValueReturnFalse(){
        MockedMaximumValueValidator maximumValueValidator = new MockedMaximumValueValidator(5L);
        String valueForValidation = "6";
        boolean result = maximumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithValidValueReturnTrue(){
        MockedMaximumValueValidator maximumValueValidator = new MockedMaximumValueValidator(5L);
        String valueForValidation = "5";
        boolean result = maximumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithNullValueForOptionalFieldScenarioReturnTrue(){
        MockedMaximumValueValidator maximumValueValidator = new MockedMaximumValueValidator(5L);
        String valueForValidation = null;
        boolean result = maximumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithEmptyValueReturnTrue(){
        MockedMaximumValueValidator maximumValueValidator = new MockedMaximumValueValidator(5L);
        String valueForValidation = "";
        boolean result = maximumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    private class MockedMaximumValueValidator extends Maximum.MaximumValueValidator{

        public MockedMaximumValueValidator(long max){
            Maximum maximum = new Maximum() {

                @Override public long max() {
                    return max;
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
            initialize(maximum);
        }
    }

}