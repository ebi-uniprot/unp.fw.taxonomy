package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.accesslog.AccessLogBuilder;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
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

    public static final Logger logger = LoggerFactory.getLogger(RestAppMain.class);

    public static final String baseUri =
            "http://0.0.0.0:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9090");

    public static final String DEFAULT_TAXONOMY_SERVICE_CONTEXT_PATH = "/uniprot/services/restful";

    /**
     * Main method that start the application
     * @param args main method argument
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        final Map<String, String> initParams = new HashMap<>();

        initParams.put("javax.ws.rs.Application", "uk.ac.ebi.uniprot.taxonomyservice.restful.main.RestApp");

        String taxonomyServiceContextPath = getContextPathFromMainArgument(args);

        HttpServer httpServer =
                create(URI.create(baseUri), ServletContainer.class, null, initParams, null, taxonomyServiceContextPath);
        httpServer.start();
    }

    /*
     * This method check if received an argument[0] from main method for application context and return it, if does not
     * receive, it return de default context path from {@link #DEFAULT_TAXONOMY_SERVICE_CONTEXT_PATH}
     *
     * @param args Arguments received in main method
     * @return  application ContextPath
     */
    private static String getContextPathFromMainArgument(String[] args) {
        String taxonomyServiceContextPath = DEFAULT_TAXONOMY_SERVICE_CONTEXT_PATH;
        if (args != null && args.length > 0 && !args[0].isEmpty()) {
            taxonomyServiceContextPath = args[0];
        }
        return taxonomyServiceContextPath;
    }

    /**
     * This method create a Grizzly HTTPServer and register(configure) Rest Service servlet in it
     *
     * @param baseUri Application server baseURI
     * @param servletClass a servlet class
     * @param servlet a servlet object
     * @param initParams Grizzly server initial parameters
     * @param contextInitParams Grizzly server initial context parameters
     * @param taxonomyServiceContextPath Application context path
     * @return Grizzly http server
     * @throws IOException
     */
    public static HttpServer create(URI baseUri, Class<? extends Servlet> servletClass, Servlet servlet,
            Map<String, String> initParams, Map<String, String> contextInitParams, String taxonomyServiceContextPath)
            throws IOException {

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri);

        // adding services
        WebappContext context = new WebappContext("GrizzlyContext", taxonomyServiceContextPath);
        ServletRegistration registration;
        if (servletClass != null) {
            registration = context.addServlet(servletClass.getName(), servletClass);
        } else {
            registration = context.addServlet(servlet.getClass().getName(), servlet);
        }

        registration.addMapping("/*");

        //adding mapping for docs.
        HttpHandlerRegistration docHandler = new HttpHandlerRegistration.Builder().contextPath("/uniprot/services/docs")
                .urlPattern("/*").build();
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(RestAppMain.class.getClassLoader(),
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

        enableAccessLog(server);

        return server;

    }

    /** This method configure Grizzly HttpServer accessLog
     *
     * @param httpServer GrizzlyHttpServer
     */
    //@TODO: This is not working locally, I kept it, may it works in the server... need to check logs in the server
    private static void enableAccessLog(HttpServer httpServer) {
        //final AccessLogBuilder builder = new AccessLogBuilder(new File("/tmp/access.log"));./logs/access.log
        final AccessLogBuilder builder = new AccessLogBuilder("./logs/access.log");
        builder.synchronous(true);
        builder.rotatedHourly();
        builder.instrument(httpServer.getServerConfiguration());

/*        try {
            AccessLogAppender appender = new StreamAppender(new FileOutputStream(new File("/tmp/access.log")));
            //AccessLogAppender appender = new StreamAppender(System.out);
            AccessLogFormat format = ApacheLogFormat.COMBINED;
            int statusThreshold = AccessLogProbe.DEFAULT_STATUS_THRESHOLD;
            AccessLogProbe alp = new AccessLogProbe(appender, format, statusThreshold);
            ServerConfiguration sc = httpServer.getServerConfiguration();
            sc.getMonitoringConfig().getWebServerConfig().addProbes(alp);
            logger.info("created access log at /tmp/access.log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

    }

}
