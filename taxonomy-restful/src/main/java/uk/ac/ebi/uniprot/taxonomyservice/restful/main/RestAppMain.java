package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import uk.ac.ebi.uniprot.taxonomyservice.restful.main.TaxonomyProperties.APP_PROPERTY_NAME;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.accesslog.AccessLogBuilder;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.grizzly.utils.Charsets;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the Main project class and is responsible to initialize the application Grizzly Server,setup
 * Rest Service and start it.
 *
 * Created by lgonzales on 19/02/16.
 */
public class RestAppMain {

    private static final Logger logger = LoggerFactory.getLogger(RestAppMain.class);

    public static final String baseUri =
            "http://0.0.0.0:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9090");

    /**
     * Main method that start the application
     * @param args main method argument
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        final Map<String, String> initParams = new HashMap<>();

        initParams.put("javax.ws.rs.Application", "uk.ac.ebi.uniprot.taxonomyservice.restful.main.RestApp");

        HttpServer httpServer = create(URI.create(baseUri), ServletContainer.class, null, initParams, null);

        enableAccessLog(httpServer);

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
    public static HttpServer create(URI baseUri, Class<? extends Servlet> servletClass, Servlet servlet,
            Map<String, String> initParams, Map<String, String> contextInitParams)
            throws IOException {

        String host = baseUri.getHost() == null?"0.0.0.0":baseUri.getHost();
        int port = baseUri.getPort() == -1?(80):baseUri.getPort(); // SSL
        NetworkListener listener = new NetworkListener("grizzly", host, port);

        ThreadPoolConfig threadPoolConfig = listener.getTransport().getWorkerThreadPoolConfig();
        threadPoolConfig.setThreadFactory((new ThreadFactoryBuilder()).setNameFormat("grizzly-http-server-%d")
                .setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler()).build());
        threadPoolConfig.setCorePoolSize(TaxonomyProperties.getIntProperty(APP_PROPERTY_NAME
                .TAXONOMY_GRIZZLY_CORE_THREAD_POOL_SIZE));
        threadPoolConfig.setMaxPoolSize(TaxonomyProperties.getIntProperty(APP_PROPERTY_NAME
                .TAXONOMY_GRIZZLY_MAX_THREAD_POOL_SIZE));
        listener.setSecure(false);

        HttpServer server = new HttpServer();
        server.addListener(listener);
        ServerConfiguration config = server.getServerConfiguration();
        config.setPassTraceRequest(true);
        config.setDefaultQueryEncoding(Charsets.UTF8_CHARSET);



        // adding services
        WebappContext context = new WebappContext("GrizzlyContext", TaxonomyProperties.getProperty(
                APP_PROPERTY_NAME.TAXONOMY_SERVICE_CONTEXT_PATH));
        ServletRegistration registration;
        if (servletClass != null) {
            registration = context.addServlet(servletClass.getName(), servletClass);
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

        if (contextInitParams != null) {
            for (Map.Entry<String, String> e : contextInitParams.entrySet()) {
                context.setInitParameter(e.getKey(), e.getValue());
            }
        }

        if (initParams != null) {
            registration.setInitParameters(initParams);
        }
        context.deploy(server);

        return server;

    }

    /** This method configure Grizzly HttpServer accessLog
     *
     * @param httpServer GrizzlyHttpServer
     */
    private static void enableAccessLog(HttpServer httpServer) {
        final AccessLogBuilder builder = new AccessLogBuilder(TaxonomyProperties.getProperty(
                APP_PROPERTY_NAME.TAXONOMY_LOGS_PATH));
        builder.synchronous(true);
        builder.rotatedHourly();
        builder.instrument(httpServer.getServerConfiguration());
    }

}
