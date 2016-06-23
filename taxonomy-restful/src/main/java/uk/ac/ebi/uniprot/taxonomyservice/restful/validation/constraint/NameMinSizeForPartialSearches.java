package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * This validation class will validate if taxonomy name search value has a minimum of {@code min()} characters for
 * partial searche types (CONTAINS, ENDSWITH or STARTSWITH)
 *
 * Created by lgonzales on 09/06/16.
 */
@Documented
@Constraint(validatedBy = {NameMinSizeForPartialSearches.NameMinSizeValidator.class})
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NameMinSizeForPartialSearches {

    public abstract String message() default "Invalid value. This is not permitted.";

    public abstract int min() default 4;

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};



    public class NameMinSizeValidator implements ConstraintValidator<NameMinSizeForPartialSearches, NameRequestParams>
    {
        private NameMinSizeForPartialSearches annotation;

        @Override
        public void initialize(NameMinSizeForPartialSearches annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(NameRequestParams nameParams, ConstraintValidatorContext constraintValidatorContext)
        {
            boolean result =  true;
            if(nameParams.getSearchType().equalsIgnoreCase(SearchType.CONTAINS.toString()) ||
                    nameParams.getSearchType().equalsIgnoreCase(SearchType.ENDSWITH.toString()) ||
                    nameParams.getSearchType().equalsIgnoreCase(SearchType.STARTSWITH.toString())
                    ){
                if(nameParams.getTaxonomyName() != null && nameParams.getTaxonomyName().length() <
                        this.annotation.min()){
                    result = false;
                }
            }

            return result;
        }
    }
}
