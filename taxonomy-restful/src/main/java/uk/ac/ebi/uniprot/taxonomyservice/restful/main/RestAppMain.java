package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import ch.qos.logback.classic.ViewStatusMessagesServlet;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.accesslog.AccessLogBuilder;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import uk.ac.ebi.uniprot.taxonomyservice.restful.main.TaxonomyProperties.APP_PROPERTY_NAME;

import jakarta.servlet.Servlet;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

/**
 * This class is the Main project class and is responsible to initialize the application Grizzly Server,setup
 * Rest Service and start it.
 *
 * Created by lgonzales on 19/02/16.
 */
public class RestAppMain {

    private static final Logger logger = LoggerFactory.getLogger(RestAppMain.class);

    public static final String baseUri = TaxonomyProperties.getProperty(APP_PROPERTY_NAME.TAXONOMY_BASE_URI) +
            ":" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9090");

    /**
     * Main method that start the application
     * @param args main method argument
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        initLogs();

        final Map<String, String> initParams = new HashMap<>();

        initParams.put("jakarta.ws.rs.Application", "uk.ac.ebi.uniprot.taxonomyservice.restful.main.RestApp");
        initParams.put("jersey.config.server.provider.packages", "com.fasterxml.jackson.jaxrs.json");

        HttpServer httpServer = create(URI.create(baseUri), ServletContainer.class, null, initParams, null);

        setupAccessLog(httpServer);

        httpServer.start();
    }

    /**
     * This method create a Grizzly HTTPServer and register(configure) Rest Service servlet in it
     *
     * @param baseUri Application server baseURI
     * @param servletClass a servlet class
     * @param servlet a servlet object
     * @param initParams Grizzly server initial parameters
     * @param contextInitParams Grizzly server initial context parameters
     * @return Grizzly http server
     * @throws IOException
     */
    public static HttpServer create(URI u, Class<? extends ServletContainer> c, Servlet servlet,
                                    Map<String, String> initParams, Map<String, String> contextInitParams)
            throws IOException {

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(u,false);
        server.getServerConfiguration().setJmxEnabled(true);
        registerRestServlet(c, servlet, initParams, contextInitParams, server);
        setupAccessLog(server);
        return server;
    }

    private static void registerRestServlet(Class<? extends ServletContainer> c, Servlet servlet,
                                            Map<String, String> initParams, Map<String, String> contextInitParams,
                                            HttpServer server) {
        registerRestServlet(c, servlet, initParams, contextInitParams, server, TaxonomyProperties.getProperty(APP_PROPERTY_NAME.TAXONOMY_DOCS_CONTEXT_PATH));
    }

    private static void registerRestServlet(Class<? extends ServletContainer> c, Servlet servlet,
                                            Map<String, String> initParams, Map<String, String> contextInitParams,
                                            HttpServer server, String root) {
        WebappContext context = new WebappContext("GrizzlyContext", TaxonomyProperties.getProperty(
                APP_PROPERTY_NAME.TAXONOMY_SERVICE_CONTEXT_PATH));
        ServletRegistration registration;
        if (c != null) {
            registration = context.addServlet(ServletContainer.class.getName(), c.getName());
        } else {
            registration = context.addServlet(servlet.getClass().getName(), servlet);
        }

        registration.addMapping("/*");
        //adding mapping for docs.
        HttpHandlerRegistration docHandler = new HttpHandlerRegistration.Builder().contextPath
                        (TaxonomyProperties.getProperty(APP_PROPERTY_NAME.TAXONOMY_DOCS_CONTEXT_PATH))
                .urlPattern("/*").build();
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandlerWithCORS(RestAppMain.class.getClassLoader(),
                "staticContent/"), docHandler);

        if (initParams != null) {
            registration.setInitParameters(initParams);
        }
//        registration = context.addServlet("ViewStatusMessagesServlet", (Class<? extends Servlet>) ViewStatusMessagesServlet.class);
        registration.addMapping("/logBackStatus");

        if (contextInitParams != null) {
            for (Map.Entry<String, String> e : contextInitParams.entrySet()) {
                context.setInitParameter(e.getKey(), e.getValue());
            }
        }
        context.deploy(server);
    }

    /**
     * This method reset common logging and setup SLF4J redirection to LogBack
     *
     */
    private static void initLogs() {
        LogManager.getLogManager().reset();
        java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);
        SLF4JBridgeHandler.install();
    }

    /** This method configure Grizzly HttpServer accessLog
     *
     * @param httpServer GrizzlyHttpServer
     */
    private static void setupAccessLog(HttpServer httpServer) {
        final AccessLogBuilder builder = new AccessLogBuilder(TaxonomyProperties.getProperty(
                APP_PROPERTY_NAME.TAXONOMY_ACCESS_LOG_PATH));
        builder.synchronous(true);
        builder.rotationPattern("yyyyMMdd");
        builder.instrument(httpServer.getServerConfiguration());
    }

}
