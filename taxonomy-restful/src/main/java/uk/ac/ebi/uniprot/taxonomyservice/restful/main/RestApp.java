package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.GeneralExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ParamExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ValidationExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.main.TaxonomyProperties.APP_PROPERTY_NAME;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.CORSFilter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.FilterResourceURL;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.listener.StartupListener;
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
import java.util.Properties;
import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
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

        AbstractModule abstractModule = configGuice(TaxonomyProperties.getConfigProperties());
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CloseableModule(), new Jsr250Module(),
                abstractModule);
        bindingGuice(serviceLocator, injector);
        register(new ServiceLifecycleManager(injector));

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        property(ServerProperties.MONITORING_STATISTICS_MBEANS_ENABLED,true);
        property(ServerProperties.APPLICATION_NAME,"Taxonomy");

        JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider();
        register(jacksonJaxbJsonProvider);

        register(ValidationExceptionMapper.class);
        register(ParamExceptionMapper.class);
        register(GeneralExceptionMapper.class);

        BeanConfig beanConfig = setupSwagger();

        packages("uk.ac.ebi.uniprot.taxonomyservice.restful.rest",
                "uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request");

        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        register(FilterResourceURL.class);
        register(CORSFilter.class);
        register(ValidationConfigurationContextResolver.class);
        register(StartupListener.class);

        logger.info("Starting of RestApp Done");

    }

    private BeanConfig setupSwagger(){
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setSchemes(new String[]{"http","https"});

        String version = TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_VERSION);
        if(version == null || version.isEmpty()){
            logger.error(APP_PROPERTY_NAME.SWAGGER_VERSION+" property must have a valid value " +
                    "at config.properties");
            throw new IllegalArgumentException(APP_PROPERTY_NAME.SWAGGER_VERSION+" property must have a valid value " +
                    "at config.properties");
        }
        beanConfig.setVersion(version);

        String description = TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_SERVICE_DESCRIPTION);
        beanConfig.setDescription(description);

        String title =  TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_SERVICE_TITLE);
        beanConfig.setTitle(title);

        String basePath = TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_BASE_PATH);
        beanConfig.setBasePath(basePath);

        String resPackage = TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_RESOURCE_PACKAGE);
        beanConfig.setResourcePackage(resPackage);

        beanConfig.setScan(true);
        return beanConfig;
    }

    /**
     * Return an instance of {@link GuiceModule}
     * @return {@link GuiceModule}
     */
    protected AbstractModule configGuice(Properties configProperties) {
        return new GuiceModule(this, configProperties);
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

}
