package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * This validation class will validate for BOTTOM Direction path if path depth is not bigger than {@code max()}
 *
 * Created by lgonzales on 08/07/16.
 */
@Documented
@Constraint(validatedBy = {MaxRequiredDepthForBottomPath.MaxDepthForBottomPathValidator.class})
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxRequiredDepthForBottomPath {

    String message() default "Invalid value. This is not permitted.";

    int max() default 5;

    String requiredMessage();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};



    class MaxDepthForBottomPathValidator implements ConstraintValidator<MaxRequiredDepthForBottomPath, PathRequestParams>
    {
        private MaxRequiredDepthForBottomPath annotation;

        @Override
        public void initialize(MaxRequiredDepthForBottomPath annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(PathRequestParams pathParam, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = true;
            if (pathParam.getDirection() != null && PathDirections.BOTTOM.equals(pathParam.getPathDirection())) {
                if(pathParam.getDepth() != null){
                    // if path depth parameter value is bigger than annotation max
                    if(pathParam.getDepth() > this.annotation.max()){
                        result = false;
                    }
                }else{
                    // direction is a required parameter for BOTTOM path direction.
                    result = false;
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate(this.annotation.requiredMessage()
                    ).addBeanNode().addConstraintViolation();
                }
            }
            return result;
        }
    }
}