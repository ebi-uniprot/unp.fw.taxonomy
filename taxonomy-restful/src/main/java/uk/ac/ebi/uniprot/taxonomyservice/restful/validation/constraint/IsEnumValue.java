package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * Checks if direction request parameter contains correct correct @link{PathDirections} values. if it does not
 * contains it will not pass in isValid method.
 *
 * Created by lgonzales on 21/03/16.
 */
@Documented
@Constraint(validatedBy = {IsEnumValue.EnumValueValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsEnumValue {

    String message() default "Invalid value. This is not permitted.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends java.lang.Enum<?>> enumClass();

    boolean ignoreCase() default false;



    class EnumValueValidator implements ConstraintValidator<IsEnumValue, String>
    {
        private IsEnumValue annotation;

        @Override
        public void initialize(IsEnumValue annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext)
        {
            boolean result = false;

            if(valueForValidation != null) {
                Object[] enumValues = this.annotation.enumClass().getEnumConstants();

                if (enumValues != null) {
                    for (Object enumValue : enumValues) {
                        if (valueForValidation.equals(enumValue.toString())
                                || (this.annotation.ignoreCase() &&
                                            valueForValidation.equalsIgnoreCase(enumValue.toString()))) {
                            result = true;
                            break;
                        }
                    }
                }
            }else{
                result = true;
            }

            return result;
        }
    }

}
