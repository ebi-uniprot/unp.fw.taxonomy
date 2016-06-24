package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.ParamException;
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

    private static final Logger logger = LoggerFactory.getLogger(FilterResourceURL.class);

    private static final String[] acceptedContentType = {MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON};

    private static final String defaultContentType = MediaType.APPLICATION_JSON;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String currentAcceptHeader = requestContext.getHeaders().getFirst(HttpHeaders.ACCEPT);
        if (currentAcceptHeader == null || !isCurrentAcceptHeaderInAcceptedContentTypeList(currentAcceptHeader)) {
            Optional<String> formatParamValue = getFormatParamFromRequestContext(requestContext.getUriInfo());
            if(formatParamValue.isPresent()){
                if(formatParamValue.get().equalsIgnoreCase("xml")){
                    requestContext.getHeaders().addFirst(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
                }else if(formatParamValue.get().equalsIgnoreCase("json")){
                    requestContext.getHeaders().addFirst(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
                }else{
                   throw new ParamException.QueryParamException(new IllegalArgumentException( SwaggerConstant
                           .REQUEST_PARAMETER_INVALID_VALUE),"format","");
                }
            }else{
                requestContext.getHeaders().addFirst(HttpHeaders.ACCEPT, defaultContentType);
            }
        }else{
            logger.debug("FilterResourceURL, we already have a valid accept header: "+currentAcceptHeader);
        }
    }

    private Optional<String> getFormatParamFromRequestContext(UriInfo uriInfo) {
        Optional<String> format;
        if(uriInfo != null && uriInfo.getQueryParameters() != null){
            format = Optional.ofNullable(uriInfo.getQueryParameters().getFirst("format"));
        }else{
            format = Optional.empty();
        }
        return format;
    }

    private boolean isCurrentAcceptHeaderInAcceptedContentTypeList(String currentAcceptHeader) {
        return Stream.of(acceptedContentType).anyMatch(x -> x.equalsIgnoreCase(currentAcceptHeader));
    }

}
