package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * Checks if list of comma separated taxonomy ids size is between @link{minSize} and @link{maxSize}
 *
 * Created by lgonzales on 05/07/16.
 */
@Documented
@Constraint(validatedBy = {ListParamMinMaxSize.MinMaxSizeValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListParamMinMaxSize {

    public abstract String message() default "Invalid value. This is not permitted.";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};

    public abstract int minSize();

    public abstract int maxSize();


    public class MinMaxSizeValidator implements ConstraintValidator<ListParamMinMaxSize, String> {

        private ListParamMinMaxSize annotation;

        @Override
        public void initialize(ListParamMinMaxSize annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = false;

            if (valueForValidation != null) {
                String[] taxonomyIdsStr = valueForValidation.split(",");
                if(taxonomyIdsStr.length >= this.annotation.minSize() && taxonomyIdsStr.length <= this.annotation
                        .maxSize()) {
                    result = true;
                }
            } else {
                result = true;
            }

            return result;
        }

    }
}
