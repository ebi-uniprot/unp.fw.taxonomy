package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.GeneralExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ParamExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ValidationExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.CORSFilter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.FilterResourceURL;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String CONFIG_PROPERTY_FILE = "config.properties";
    private static final String DEFAULT_API_VERSION = "1.0.0";
    private static final String DEFAULT_SWAGGER_DESCRIPTION = "UniProt Rest Services.";
    private static final String DEFAULT_SWAGGER_TITLE = "UniProt Services";
    private static final String DEFAULT_SWAGGER_BASE_PATH = "/uniprot/api";
    private static final String DEFAULT_SWAGGER_RESOURCE_PACKAGE = "uk.ac.ebi.uniprot.dataservice.restful";

    /**
     * This constructor inject all necessary services and also register jackson response provider for the application
     * @param serviceLocator Hk2 service Locator
     */
    @Inject
    public RestApp(ServiceLocator serviceLocator) {
        logger.info("Starting up RestApp");
        Properties configProperties = loadProperties();

        AbstractModule abstractModule = configGuice(configProperties);
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

        BeanConfig beanConfig = setupSwagger(configProperties);

        packages("uk.ac.ebi.uniprot.taxonomyservice.restful.rest",
                "uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request");

        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        register(FilterResourceURL.class);
        register(CORSFilter.class);
        register(ValidationConfigurationContextResolver.class);

        logger.info("Starting of RestApp Done");

    }

    private BeanConfig setupSwagger(Properties configProperties){
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setSchemes(new String[]{"http"}); //TODO: Enable HTTPS

        String version = configProperties.getProperty("ServiceVersion", DEFAULT_API_VERSION);
        beanConfig.setVersion(version);

        String description = configProperties.getProperty("ServiceDescription", DEFAULT_SWAGGER_DESCRIPTION);
        beanConfig.setDescription(description);

        String title =  configProperties.getProperty("ServiceTitle", DEFAULT_SWAGGER_TITLE);
        beanConfig.setTitle(title);

        String basePath = configProperties.getProperty("BasePath", DEFAULT_SWAGGER_BASE_PATH);
        beanConfig.setBasePath(basePath);

        String resPackage = configProperties.getProperty("ResourcePackage", DEFAULT_SWAGGER_RESOURCE_PACKAGE);
        beanConfig.setResourcePackage(resPackage);

        beanConfig.setScan(true);
        return beanConfig;
    }

    /**
     * Load application properties from {@link #CONFIG_PROPERTY_FILE}
     * @return loaded properties
     */
    protected Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream propertyInputStream = getClass().getResourceAsStream("/" + CONFIG_PROPERTY_FILE)) {
            properties.load(propertyInputStream);
        } catch (IOException e) {
            logger.warn("unable to load " + CONFIG_PROPERTY_FILE + " with getResourceAsStream");
        }
        if (properties.isEmpty()) {
            try (InputStream propertyInputStream = new FileInputStream(CONFIG_PROPERTY_FILE)) {
                properties.load(propertyInputStream);
            } catch (IOException e) {
                logger.warn("unable to load " + CONFIG_PROPERTY_FILE + " with FileInputStream");
            }
        }
        return properties;
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
