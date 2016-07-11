package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to unit test MinimumValueValidator methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class MinimumValueValidatorTest {


    @Test
    public void isValidWithInvalidValueReturnFalse(){
        MockedMinimumValueValidator minimumValueValidator = new MockedMinimumValueValidator(5L);
        String valueForValidation = "4";
        boolean result = minimumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithValidValueReturnTrue(){
        MockedMinimumValueValidator minimumValueValidator = new MockedMinimumValueValidator(5L);
        String valueForValidation = "5";
        boolean result = minimumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithNullValueForOptionalFieldScenarioReturnTrue(){
        MockedMinimumValueValidator minimumValueValidator = new MockedMinimumValueValidator(5L);
        String valueForValidation = null;
        boolean result = minimumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithEmptyValueReturnTrue(){
        MockedMinimumValueValidator minimumValueValidator = new MockedMinimumValueValidator(5L);
        String valueForValidation = "";
        boolean result = minimumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    private class MockedMinimumValueValidator extends Minimum.MinimumValueValidator{

        public MockedMinimumValueValidator(long min){
            Minimum minEmum = new Minimum() {

                @Override public long min() {
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
            initialize(minEmum);
        }
    }

}