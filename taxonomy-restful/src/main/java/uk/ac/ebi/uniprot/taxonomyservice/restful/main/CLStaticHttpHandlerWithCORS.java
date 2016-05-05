package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * This filter class is used to add CORS (Cross Origin Resource Sharing) response headers for taxonomy static content,
 * this way external services will be able to access it.
 *
 * Created by lgonzales on 12/04/16.
 */
public class CLStaticHttpHandlerWithCORS extends CLStaticHttpHandler {

    public CLStaticHttpHandlerWithCORS(ClassLoader classLoader, String... docRoots) {
        super(classLoader, docRoots);
    }

    @Override public void service(Request request, Response response) throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
        response.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD");
        super.service(request, response);
    }
}
