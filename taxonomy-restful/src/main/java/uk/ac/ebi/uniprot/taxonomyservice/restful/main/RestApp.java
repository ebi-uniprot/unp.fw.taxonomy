package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.JacksonFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.*;
import com.google.inject.Module;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.core.Context;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.server.validation.ValidationFeature;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.Neo4jTaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.GeneralExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ParamExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.ValidationExceptionMapper;
import uk.ac.ebi.uniprot.taxonomyservice.restful.main.TaxonomyProperties.APP_PROPERTY_NAME;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.CORSFilter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter.FilterResourceURL;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.listener.StartupListener;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.ValidationConfigurationContextResolver;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Scopes;
import org.glassfish.hk2.api.PerLookup;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Context
    ServletConfig servletConfig;

    public RestApp() {
        this(null);
    }

    /**
     * This constructor inject all necessary services and also register jackson response provider for the application
     * @param serviceLocator Hk2 service Locator
     */
    @Inject
    public RestApp(ServiceLocator serviceLocator) {
        logger.info("Starting up RestApp");
        
        // Create ServiceLocator if null
        if (serviceLocator == null) {
            logger.warn("ServiceLocator is null");
            serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        }

        // Setup Guice and HK2 bridge first
        Properties configProperties = TaxonomyProperties.getConfigProperties();
        Module abstractModule = configGuice(configProperties);
        Injector injector = Guice.createInjector(Stage.PRODUCTION, abstractModule);
        
        // Initialize Guice-HK2 bridge
        bindingGuice(serviceLocator, injector);

        // Register HK2 bindings explicitly
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Bind Neo4jTaxonomyDataAccess from Guice
                bind(injector.getInstance(Neo4jTaxonomyDataAccess.class))
                    .to(TaxonomyDataAccess.class);
            }
        });

        // Start Neo4jTaxonomyDataAccess
        Neo4jTaxonomyDataAccess taxonomyDataAccess = injector.getInstance(Neo4jTaxonomyDataAccess.class);
        taxonomyDataAccess.start();

        // Setup server properties
        configureServerProperties();
        
        // Register components
        registerComponents();
        
        try {
            setupSwagger();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        logger.info("Starting of RestApp Done");
    }

    private void configureServerProperties() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        property(ServerProperties.MONITORING_STATISTICS_MBEANS_ENABLED, true);
        property(ServerProperties.APPLICATION_NAME, "Taxonomy");
        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
    }

    private void registerComponents() {
        register(OpenApiResource.class);
        register(SwaggerSerializers.class);
        register(AcceptHeaderOpenApiResource.class);
        register(JacksonFeature.class);
        register(JacksonXmlBindJsonProvider.class);
        register(JacksonJsonProvider.class);
        register(ParamExceptionMapper.class);
        register(GeneralExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(FilterResourceURL.class);
        register(CORSFilter.class);
        register(StartupListener.class);  // Register this after HK2 bindings are set up
        register(ValidationConfigurationContextResolver.class);
        register(ValidationFeature.class);
    }

    private void setupSwagger() throws JsonProcessingException {
        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title(TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_SERVICE_TITLE))
                .description(TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_SERVICE_DESCRIPTION))
                .version(TaxonomyProperties.getProperty(APP_PROPERTY_NAME.SWAGGER_VERSION))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
        oas.info(info);
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Stream.of("uk.ac.ebi.uniprot.taxonomyservice.restful").collect(Collectors.toSet()))
                .readAllResources(true);

        try {
            new JaxrsOpenApiContextBuilder()
                    .servletConfig(servletConfig)
                    .application(this)
                    .openApiConfiguration(oasConfig)
                    .buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
