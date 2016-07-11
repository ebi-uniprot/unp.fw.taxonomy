package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to unit test EnumValueValidator methods
 *
 * Created by lgonzales on 06/07/16.
 */
public class EnumValueValidatorTest {

    @Test
    public void isValidWithEmptyValueReturnFalse(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, true);
        String valueForValidation = "";
            boolean result = enumValueValidator.isValid(valueForValidation,null);
            assertThat(result, is(false));
    }

    @Test
    public void isValidWithCaseInsensitiveValueReturnTrue(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, true);
        String valueForValidation = "conTains";
        boolean result = enumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithCaseSensitiveValueReturnFalse(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, false);
        String valueForValidation = "conTains";
        boolean result = enumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithValidValueReturnTrue(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, true);
        String valueForValidation = "EQUALSTO";
        boolean result = enumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithInvalidValueReturnFalse(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, true);
        String valueForValidation = "INVALID";
        boolean result = enumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithNullValueForOptionalFieldScenarioReturnTrue(){
        MockedEnumValueValidator enumValueValidator = new MockedEnumValueValidator(SearchType.class, true);
        String valueForValidation = null;
        boolean result = enumValueValidator.isValid(valueForValidation,null);
        assertThat(result, is(true));
    }

    private class MockedEnumValueValidator extends IsEnumValue.EnumValueValidator{

        public MockedEnumValueValidator(Class<? extends Enum<?>> enumClass,boolean ignoreCase){
            IsEnumValue isEnumValue = new IsEnumValue() {

                @Override public Class<? extends Enum<?>> enumClass() {
                    return enumClass;
                }

                @Override public boolean ignoreCase() {
                    return ignoreCase;
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
            initialize(isEnumValue);
        }
    }

}