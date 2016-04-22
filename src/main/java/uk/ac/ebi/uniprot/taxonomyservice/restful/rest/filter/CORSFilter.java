package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;



import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * This filter class is used to add CORS (Cross Origin Resource Sharing) response headers, this way external services
 * will be able to access taxonomy service
 *
 * Created by lgonzales on 11/04/16.
 */

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD");
    }
}
