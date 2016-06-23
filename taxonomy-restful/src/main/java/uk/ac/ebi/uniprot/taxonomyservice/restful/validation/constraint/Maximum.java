package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This annotation validate if a value for validation is less than max() value
 *
 * Created by lgonzales on 22/06/16.
 */
@Documented
@Constraint(validatedBy = {Maximum.MaximumValueValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Maximum {

    public abstract String message() default "Invalid value. This is not permitted.";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};

    public abstract long max();

    public class MaximumValueValidator implements ConstraintValidator<Maximum, String>
    {
        private static final Logger logger = LoggerFactory.getLogger(MaximumValueValidator.class);
        private Maximum annotation;

        @Override
        public void initialize(Maximum annotation)
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
                    if(longValue >  annotation.max()){
                        result = false;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Maximum NumberFormatException for: "+valueForValidation);
                }
            }
            return result;
        }
    }
}

