package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.net.URI;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
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
    public void assertNullQueryInfoFilterParameterExceptionReturnNull() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        when(requestContext.getUriInfo()).thenReturn(null);

        filterResourceURL.filter(requestContext);

        assertNull(requestContext.getHeaders());
    }

    @Test
    public void assertNullQueryPathFilterParameterExceptionReturnNull() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn(null);

        filterResourceURL.filter(requestContext);

        assertNull(requestContext.getHeaders());
    }

    @Test
    public void assertJsonQueryPathFilterParameterExceptionReturnJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/12345.json");
        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getUriInfo().getRequestUri()).thenReturn(new URI("/12345.json"));

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT), notNullValue());
        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT).get(0), is(equalTo(MediaType.APPLICATION_JSON)));
    }

    @Test
    public void assertXmlQueryPathFilterParameterExceptionReturnJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/12345.xml");
        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getUriInfo().getRequestUri()).thenReturn(new URI("/12345.xml"));

        filterResourceURL.filter(requestContext);

        assertThat(requestContext.getHeaders().get(HttpHeaders.ACCEPT), notNullValue());
    }

    @Test
    public void assertWithoutExtensionQueryPathFilterParameterExceptionReturnJsonAcceptHeader() throws Exception{
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        UriInfo mockedInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(requestContext.getUriInfo().getPath()).thenReturn("/12345");
        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getUriInfo().getRequestUri()).thenReturn(new URI("/12345"));

        filterResourceURL.filter(requestContext);

        assertNull(requestContext.getHeaders().get(HttpHeaders.ACCEPT));
    }



}