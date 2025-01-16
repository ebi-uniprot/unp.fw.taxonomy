package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import java.io.IOException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is used to test CORSFilter methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class CORSFilterTest {

    @Test
    public void filterAddCORSHeadersInResponseContext() throws IOException {
        CORSFilter filter = new CORSFilter();
        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext,responseContext);

        assertThat(headers.getFirst("Access-Control-Allow-Origin"),notNullValue());
        assertThat(headers.getFirst("Access-Control-Allow-Headers"),notNullValue());
        assertThat(headers.getFirst("Access-Control-Allow-Methods"),notNullValue());
    }

}