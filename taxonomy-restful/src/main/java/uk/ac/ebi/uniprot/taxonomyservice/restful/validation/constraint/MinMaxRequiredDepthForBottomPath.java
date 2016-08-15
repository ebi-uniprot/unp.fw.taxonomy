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
@Constraint(validatedBy = {MinMaxRequiredDepthForBottomPath.MinMaxDepthForBottomPathValidator.class})
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinMaxRequiredDepthForBottomPath {

    String message() default "Invalid value. This is not permitted.";

    int max() default 5;

    int min() default 1;

    String requiredMessage();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};



    class MinMaxDepthForBottomPathValidator implements ConstraintValidator<MinMaxRequiredDepthForBottomPath, PathRequestParams>
    {
        private MinMaxRequiredDepthForBottomPath annotation;

        @Override
        public void initialize(MinMaxRequiredDepthForBottomPath annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(PathRequestParams pathParam, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = true;
            if (pathParam.getDirection() != null && PathDirections.BOTTOM.equals(pathParam.getPathDirection())) {
                if(pathParam.getDepth() != null){
                    if(pathParam.getDepth() > this.annotation.max() || pathParam.getDepth() < this.annotation.min()){
                        result = false;
                    }
                }else{
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