package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ParamException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible to test ParamExceptionMapper methods
 * Created by lgonzales on 30/03/16.
 */
public class ParamExceptionMapperTest {

    private MockedParamExceptionMapper mockedParamExceptionMapper;

    private HttpServletRequest request;

    @Before
    public void initialiseMocks() {
        mockedParamExceptionMapper = new MockedParamExceptionMapper();
        request = mock(HttpServletRequest.class);
        mockedParamExceptionMapper.setRequest(request);
    }

    @Test
    public void assertParamExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(SwaggerConstant.REQUEST_PARAMETER_INVALID_VALUE.replace("{parameterName}","id"));

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = mockedParamExceptionMapper.toResponse(new ParamException.QueryParamException(new
                Exception("Error"),"id",""));

        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    private class MockedParamExceptionMapper extends ParamExceptionMapper{

        public void setRequest(HttpServletRequest request){
            this.request = request;
        }
    }

}