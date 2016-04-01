package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
        if (requestContext.getUriInfo() != null && requestContext.getUriInfo().getPath() != null) {
            logger.debug("FilterResourceURL: " + requestContext.getUriInfo().getRequestUri().toString());
            String path = requestContext.getUriInfo().getRequestUri().toString();
            MultivaluedMap<String, String> queryString = requestContext.getUriInfo().getQueryParameters();
            try {
                if (requestContext.getUriInfo().getPath().endsWith(".xml")) {
                    path = path.substring(0, path.indexOf(".xml"));
                    logger.debug("new path: " + path);
                    requestContext.getHeaders().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
                    requestContext.setRequestUri(getURI(path,queryString));
                } else if (requestContext.getUriInfo().getPath().endsWith(".json")) {
                    path = path.substring(0, path.indexOf(".json"));
                    logger.debug("new path: " + path);
                    requestContext.getHeaders().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
                    requestContext.setRequestUri(getURI(path,queryString));
                }
            } catch (URISyntaxException e) {
                logger.error("Error at FilterResourceURL: ",e);
            }
        }else{
            logger.warn("FilterResourceURL: requestContext.getUriInfo is null");
        }
    }

    private URI getURI(String path,  MultivaluedMap<String, String> queryString) throws URISyntaxException {
        String absolutePath = path;
        if(queryString!= null && !queryString.isEmpty()){
            absolutePath += "?"+queryString.entrySet().stream()
                    .flatMap(entry -> encodeMultiParameter(entry.getKey(), entry.getValue(), StandardCharsets.UTF_8))
                    .reduce((param1, param2) -> param1 + "&" + param2)
                    .orElse("");
        }
        return new URI(absolutePath);
    }

    private Stream<String> encodeMultiParameter(String key, List<String> values, Charset encoding) {
        String[] arrayValues = values.toArray(new String[values.size()]);
        return Stream.of(arrayValues).map(value -> encodeSingleParameter(key, value,encoding));
    }

    private String encodeSingleParameter(String key, String value, Charset encoding) {
        return urlEncode(key, encoding) + "=" + urlEncode(value, encoding);
    }

    private String urlEncode(String value, Charset encoding) {
        try {
            return URLEncoder.encode(value, encoding.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot url encode " + value, e);
        }
    }
}
