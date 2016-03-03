package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import uk.ac.ebi.uniprot.taxonomyservice.restful.exception.mapper.RestExceptionMapper;

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
import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
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

        JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider();
        register(jacksonJaxbJsonProvider);

        RestExceptionMapper exceptionMapper = new RestExceptionMapper();
        register(exceptionMapper);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setDescription("Taxonomy Rest Services.");
        beanConfig.setTitle("Taxonomy Service");
        beanConfig.setHost("localhost:9090"); //TODO
        beanConfig.setBasePath(RestAppMain.DEFAULT_TAXONOMY_SERVICE_CONTEXT_PATH);
        beanConfig.setResourcePackage("uk.ac.ebi.uniprot.taxonomyservice.restful.rest");
        beanConfig.setScan(true);

        packages("uk.ac.ebi.uniprot.taxonomyservice.restful.rest");
        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

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

}
