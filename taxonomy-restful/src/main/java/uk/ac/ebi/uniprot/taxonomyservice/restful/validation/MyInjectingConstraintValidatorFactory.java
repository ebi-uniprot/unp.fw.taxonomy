package uk.ac.ebi.uniprot.taxonomyservice.restful.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

/**
 * A {@link ConstraintValidatorFactory} that is used to create ConstraintValidator objects from ResourceContext object.
 *
 * Created by lgonzales on 17/03/16.
 */
public class MyInjectingConstraintValidatorFactory implements ConstraintValidatorFactory {

    @Context
    private ResourceContext resourceContext;

    public MyInjectingConstraintValidatorFactory() {
    }

    /*
     * Work around to create the Factory with the resource....
     */
    public MyInjectingConstraintValidatorFactory(ResourceContext resourceContext) {
        this.resourceContext = resourceContext;
    }

    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return resourceContext.getResource(key);
    }

    public void releaseInstance(ConstraintValidator<?, ?> instance) {
    }
}
