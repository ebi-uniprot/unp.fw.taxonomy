package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.FakeTaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.main.GuiceModule;
import uk.ac.ebi.uniprot.taxonomyservice.restful.main.RestApp;

import com.google.inject.AbstractModule;
import com.jayway.restassured.RestAssured;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.junit.rules.ExternalResource;

/**
 * External resource that starts up and shuts down a grizzly
 *
 * @author Leonardo Gonzales
 */
public class RestContainer extends ExternalResource {
    private final String baseURI;
    private final int port;
    private final String basePath;
    private HttpServer httpServer;

    public final String baseURL;

    public RestContainer() {
        baseURI = "http://localhost";
        port = 12345;
        basePath = "/rest";
        baseURL = "https://localhost:"+port+basePath;
    }

    @Override protected void before() throws Throwable {
        RestAssured.baseURI = baseURI;
        RestAssured.port = 12345;
        RestAssured.basePath = basePath;

        httpServer = GrizzlyWebContainerFactory.create(getServerURL(), containerInitParams());
        httpServer.start();
    }

    @Override protected void after() {
        if (httpServer != null) {
            httpServer.shutdownNow();
        }
    }

    public String getServerURL() {
        return baseURI + ":" + port + basePath;
    }

    private Map<String, String> containerInitParams() {
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("javax.ws.rs.Application", TaxonomyAppMock.class.getName());
        return objectObjectHashMap;
    }

    /**
     * Class used to configure the Guice dependencies necessary for the Integration tests for the S4 REST service.
     */
    private static class TaxonomyAppMock extends RestApp {
        @Inject
        public TaxonomyAppMock(ServiceLocator serviceLocator) {
            super(serviceLocator);

            registerInstances(new LoggingFilter(Logger.getLogger(TaxonomyAppMock.class.getName()), true));
        }

        @Override
        protected AbstractModule configGuice(Properties configProperties) {
            return new AbstractModule() {
                @Override
                protected void configure() {
                    bind(TaxonomyDataAccess.class).to(FakeTaxonomyDataAccess.class).asEagerSingleton();
                    packages(GuiceModule.PACKAGE_SCAN);
                }
            };
        }
    }

}