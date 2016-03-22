package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.GeneralExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ParamExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ValidationExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.FilterResourceURL;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.MyInjectingConstraintValidatorFactory;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.ValidationConfigurationContextResolver;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import javax.inject.Inject;
import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class in responsible to initialize and configure application Rest Service. As part of initial setup, it
 * inject all necessary services and also register jackson response provider for the application.
 *
 * Created by lgonzales on 19/02/16.
 */
public class RestApp extends ResourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestApp.class);

    /**
     * This constructor inject all necessary services and also register jackson response provider for the application
     * @param serviceLocator Hk2 service Locator
     */
    @Inject
    public RestApp(ServiceLocator serviceLocator) {
        logger.info("Starting up RestApp");

        AbstractModule abstractModule = configGuice();
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CloseableModule(), new Jsr250Module(),
                abstractModule);
        bindingGuice(serviceLocator, injector);
        register(new ServiceLifecycleManager(injector));

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);

        JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider();
        register(jacksonJaxbJsonProvider);

        register(ValidationExceptionMapper.class);
        register(ParamExceptionMapper.class);
        register(GeneralExceptionMapper.class);

        String apiVersion = System.getenv("TAXONOMY_VERSION") != null ? System.getenv("TAXONOMY_VERSION") : "1.0.0";
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(apiVersion);
        beanConfig.setSchemes(new String[]{"http"}); //TODO: Enable HTTPS
        beanConfig.setDescription("Taxonomy Rest Services.");
        beanConfig.setTitle("Taxonomy Service");
        beanConfig.setBasePath(RestAppMain.DEFAULT_TAXONOMY_SERVICE_CONTEXT_PATH);
        beanConfig.setResourcePackage("uk.ac.ebi.uniprot.taxonomyservice.restful.rest");
        beanConfig.setScan(true);

        packages("uk.ac.ebi.uniprot.taxonomyservice.restful.rest",
                "uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request");

        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        register(FilterResourceURL.class);
        register(ValidationConfigurationContextResolver.class);

        logger.info("Starting of RestApp Done");

    }

    /**
     * Return an instance of {@link GuiceModule}
     * @return {@link GuiceModule}
     */
    protected AbstractModule configGuice() {

        return new GuiceModule(this);
    }

    /**
     * Initialize guice with Hk2 bridge.
     * @param serviceLocator Hk2 service Locator
     * @param injector Guice Injector
     */
    public static void bindingGuice(ServiceLocator serviceLocator, Injector injector) {
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }


    public static class ValidationConfigurationContextResolverLocal implements ContextResolver<ValidationConfig> {

        @Context
        private ResourceContext resourceContext;

        @Override
        public ValidationConfig getContext(final Class<?> type) {
            return new ValidationConfig().constraintValidatorFactory(resourceContext.getResource
                    (MyInjectingConstraintValidatorFactory.class)).parameterNameProvider(new
                    CustomParameterNameProvider());
        }

        private class CustomParameterNameProvider implements ParameterNameProvider {

            private final ParameterNameProvider nameProvider;

            public CustomParameterNameProvider() {
                nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
            }

            @Override
            public List<String> getParameterNames(final Constructor<?> constructor) {
                return nameProvider.getParameterNames(constructor);
            }

            @Override
            public List<String> getParameterNames(final Method method) {
                return nameProvider.getParameterNames(method);
            }
        }

    }

}
