package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import org.glassfish.jersey.server.ParamException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is used to test FilterResourceURL methods
 *
 * Created by lgonzales on 31/03/16.
 */
public class FilterResourceURLTest {

    private FilterResourceURL filterResourceURL;

    @Before
    public void initialiseMocks() {
        filterResourceURL = new FilterResourceURL();
    }

    @Test
    public void assertJsonFormatQueryParameterReturnJsonAcceptHeader() throws IOException{
        ContainerRequestContext requestContext = getMockedContainerRequestContext("json",null);

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0),is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertXmlFormatQueryParameterReturnXmlAcceptHeader() throws IOException{
        ContainerRequestContext requestContext = getMockedContainerRequestContext("xml",null);

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0),is(MediaType.APPLICATION_XML));
    }

    @Test
    public void assertWithoutFormatAndAcceptHeaderParametersReturnDefaultJsonAcceptHeader()
            throws IOException, URISyntaxException {
        ContainerRequestContext requestContext = getMockedContainerRequestContext(null,null);

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0),is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertValidJsonAcceptHeaderHasPriorityOverXmlFormatParamReturnJsonAcceptHeader() throws IOException {
        ContainerRequestContext requestContext = getMockedContainerRequestContext("xml",MediaType.APPLICATION_JSON);

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0),is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertXmlFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() throws
                                                                                               IOException{
        ContainerRequestContext requestContext = getMockedContainerRequestContext("xml",MediaType.APPLICATION_ATOM_XML);

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0),is(MediaType.APPLICATION_XML));
    }

    @Test(expected = ParamException.QueryParamException.class)
    public void assertInvalidFormatParamThrowsQueryParamException() throws IOException {
        ContainerRequestContext requestContext = getMockedContainerRequestContext("invalid",null);

        filterResourceURL.filter(requestContext);
    }


    private ContainerRequestContext getMockedContainerRequestContext(String queryParameterFormat,String mediaType) {
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

        UriInfo mockedInfo = mock(UriInfo.class);
        when(requestContext.getUriInfo()).thenReturn(mockedInfo);

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        if(mediaType != null){
            headers.add(HttpHeaders.ACCEPT,mediaType);
        }
        when(requestContext.getHeaders()).thenReturn(headers);

        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        if(queryParameterFormat != null) {
            queryParameters.add("format", queryParameterFormat);
            when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format="+queryParameterFormat);
        }else{
            when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345");
        }
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);

        return requestContext;
    }


}