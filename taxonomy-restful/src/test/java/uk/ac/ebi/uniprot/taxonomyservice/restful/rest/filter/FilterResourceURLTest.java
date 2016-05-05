package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.net.URI;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import org.glassfish.jersey.server.ParamException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
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
    public void assertJsonFormatQueryParameterReturnJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","json");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format=json");
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON);
        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT),is(equalTo(headers.get(HttpHeaders.ACCEPT))));
    }


    @Test
    public void assertXmlFormatQueryParameterReturnXmlAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_ATOM_XML);
        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT),is(equalTo(headers.get(HttpHeaders.ACCEPT))));
    }

    @Test
    public void assertWithoutFormatAndAcceptHeaderParametersReturnDefaultJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345");
        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getUriInfo().getRequestUri()).thenReturn(new URI("/taxonomy/id/12345"));

        filterResourceURL.filter(requestContext);

        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON);
        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT),is(equalTo(headers.get(HttpHeaders.ACCEPT))));
    }

    @Test
    public void assertValidJsonAcceptHeaderHasPriorityOverXmlFormatParamReturnJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON);
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT),is(equalTo(headers.get(HttpHeaders.ACCEPT))));
    }

    @Test
    public void assertXmlFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() throws
                                                                                                        Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_ATOM_XML);
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_XML);
        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT),is(equalTo(headers.get(HttpHeaders.ACCEPT))));
    }

    @Test(expected = ParamException.QueryParamException.class)
    public void assertInvalidFormatParamThrowsQueryParamException() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","invalid");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/taxonomy/id/12345?format=invalid");
        when(requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);

        filterResourceURL.filter(requestContext);
    }


}