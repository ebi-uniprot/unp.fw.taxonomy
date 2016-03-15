package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to intercept requests URLs that ends ".xml" or ".json", remove it from URL, by doing it,
 * Jersey will be able to map it. It also add the request header "Accept" to application/xml for request URL that ends
 * with .xml or application/json for request URL that ends with .json, this way Jackson will be able to return the
 * correct Content-Type
 *
 * Created by lgonzales on 11/03/16.
 */
@PreMatching
public class FilterResourceURL implements ContainerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(FilterResourceURL.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.debug("FilterResourceURL: " + requestContext.getUriInfo().getRequestUri().toString());
        if (requestContext.getUriInfo() != null && requestContext.getUriInfo().getPath() != null) {
            String path = requestContext.getUriInfo().getRequestUri().toString();
            try {
                if (requestContext.getUriInfo().getPath().endsWith(".xml")) {
                    path = path.substring(0, path.indexOf(".xml"));
                    logger.debug("new path: " + path);
                    requestContext.getHeaders().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
                    requestContext.setRequestUri(new URI(path));
                } else if (requestContext.getUriInfo().getPath().endsWith(".json")) {
                    path = path.substring(0, path.indexOf(".json"));
                    logger.debug("new path: " + path);
                    requestContext.getHeaders().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
                    requestContext.setRequestUri(new URI(path));
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
