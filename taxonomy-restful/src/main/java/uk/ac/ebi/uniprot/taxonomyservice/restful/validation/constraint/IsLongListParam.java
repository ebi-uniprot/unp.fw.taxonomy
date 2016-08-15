
package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * Checks if values in the list of comma separated taxonomy ids are valid long values.
 *
 * Created by lgonzales on 05/07/16.
 */
@Documented
@Constraint(validatedBy = {IsLongListParam.IsLongListParamValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsLongListParam {

    String message() default "Invalid value. This is not permitted.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    class IsLongListParamValidator implements ConstraintValidator<IsLongListParam, String> {
        private IsLongListParam annotation;

        @Override
        public void initialize(IsLongListParam annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = false;

            if (valueForValidation != null) {
                String[] taxonomyIdsStr = valueForValidation.split(",");
                if(isLongArray(taxonomyIdsStr)){
                    result = true;
                }
            } else {
                result = true;
            }

            return result;
        }

        private static boolean isLongArray(String[] taxonomyIdsStr){
            boolean result = true;
            for (String item:taxonomyIdsStr) {
                if(!isLong(item)){
                    result = false;
                    break;
                }
            }
            return result;
        }

        private static boolean isLong(String str)
        {
            try {
                long d = Long.parseLong(str);
            } catch(NumberFormatException nfe) {
                return false;
            }
            return true;
        }

    }



}
