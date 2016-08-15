package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This annotation validate if a value for validation is more than min() value
 *
 * Created by lgonzales on 22/06/16.
 */
@Documented
@Constraint(validatedBy = {Minimum.MinimumValueValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Minimum {

    String message() default "Invalid value. This is not permitted.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long min();

    class MinimumValueValidator implements ConstraintValidator<Minimum, String>
    {
        private static final Logger logger = LoggerFactory.getLogger(MinimumValueValidator.class);
        private Minimum annotation;

        @Override
        public void initialize(Minimum annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext)
        {
            boolean result = true;

            if(valueForValidation != null) {
                try {
                    long longValue = Long.parseLong(valueForValidation);
                    if(longValue <  annotation.min()){
                        result = false;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Minimum NumberFormatException for: "+valueForValidation);
                }
            }
            return result;
        }
    }
}