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

    private static ContainerRequestContext requestContext;

    private static UriInfo mockedInfo;

    @Before
    public void setup() {
        filterResourceURL = new FilterResourceURL();
        requestContext = mock(ContainerRequestContext.class);
        mockedInfo = mock(UriInfo.class);
    }

    @Test
    public void jsonFormatQueryParameterReturnJsonAcceptHeader() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","json");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345?format=json");
        when(mockedInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        assertThat(headers.containsKey(HttpHeaders.ACCEPT), is(true));
        assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(equalTo(MediaType.APPLICATION_JSON)));
    }


    @Test
    public void xmlFormatQueryParameterReturnXmlAcceptHeader() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(mockedInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        assertThat(headers.containsKey(HttpHeaders.ACCEPT), is(true));
        assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(equalTo(MediaType.APPLICATION_XML)));
    }

    @Test
    public void withoutFormatAndAcceptHeaderParametersReturnDefaultJsonAcceptHeader() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345");
        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getUriInfo().getRequestUri()).thenReturn(new URI("/taxonomy/id/12345"));
        filterResourceURL.filter(requestContext);

        assertThat(headers.containsKey(HttpHeaders.ACCEPT), is(true));
        assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(equalTo(MediaType.APPLICATION_JSON)));
    }

    @Test
    public void validJsonAcceptHeaderHasPriorityOverXmlFormatParamReturnJsonAcceptHeader() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON);
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(mockedInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        assertThat(headers.containsKey(HttpHeaders.ACCEPT), is(true));
        assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(equalTo(MediaType.APPLICATION_JSON)));
    }

    @Test
    public void xmlFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.ACCEPT,MediaType.APPLICATION_OCTET_STREAM);
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","xml");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345?format=xml");
        when(mockedInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);


        filterResourceURL.filter(requestContext);

        assertThat(headers.containsKey(HttpHeaders.ACCEPT), is(true));
        assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(equalTo(MediaType.APPLICATION_XML)));
    }

    @Test(expected = ParamException.QueryParamException.class)
    public void invalidFormatParamThrowsQueryParamException() throws Exception{
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("format","invalid");

        when(requestContext.getUriInfo()).thenReturn(mockedInfo);
        when(mockedInfo.getPath()).thenReturn("/taxonomy/id/12345?format=invalid");
        when(mockedInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestContext.getHeaders()).thenReturn(headers);

        filterResourceURL.filter(requestContext);
    }


}