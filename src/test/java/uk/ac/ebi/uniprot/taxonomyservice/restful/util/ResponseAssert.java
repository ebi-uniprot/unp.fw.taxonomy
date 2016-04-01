package uk.ac.ebi.uniprot.taxonomyservice.restful.util;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;

import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This class is responsible to assert Taxonomy response objects
 *
 * Created by lgonzales on 31/03/16.
 */
public class ResponseAssert {

    public static final String REQUEST_URL = "http://localhost:12345/rest/test";

    public static void assertResponseErrorMessage(ErrorMessage expectedError, Response response) {
        assertThat(response.getEntity(), notNullValue());
        assertTrue(response.getEntity() instanceof ErrorMessage);

        ErrorMessage errorResponse = (ErrorMessage) response.getEntity();
        assertThat(errorResponse, notNullValue());
        Assert.assertThat(errorResponse.getErrorMessages(),notNullValue());
        assertThat(errorResponse.getErrorMessages().size(), is(expectedError.getErrorMessages().size()));
        Collections.sort(errorResponse.getErrorMessages());
        Collections.sort(expectedError.getErrorMessages());
        assertThat(errorResponse, is(expectedError));
    }

}
