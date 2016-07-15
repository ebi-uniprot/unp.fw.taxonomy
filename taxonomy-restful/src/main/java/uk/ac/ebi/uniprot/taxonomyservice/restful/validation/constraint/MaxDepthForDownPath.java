package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * This validation class will validate for BOTTOM Direction path if path depth is not bigger than {@code max()}
 *
 * Created by lgonzales on 08/07/16.
 */
@Documented
@Constraint(validatedBy = {MaxDepthForDownPath.MaxDepthForDownPathValidator.class})
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxDepthForDownPath {

    public abstract String message() default "Invalid value. This is not permitted.";

    public abstract int max() default 5;

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};



    public class MaxDepthForDownPathValidator implements ConstraintValidator<MaxDepthForDownPath, PathRequestParams>
    {
        private MaxDepthForDownPath annotation;

        @Override
        public void initialize(MaxDepthForDownPath annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(PathRequestParams pathParam, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = true;
            if (pathParam.getDirection() != null && PathDirections.BOTTOM.equals(pathParam.getPathDirection()) &&
                    pathParam.getDepth() > this.annotation.max()) {
                result = false;
            }

            return result;
        }
    }
}