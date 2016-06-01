package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible to test GeneralExceptionMapper methods.
 *
 * Created by lgonzales on 30/03/16.
 */
public class GeneralExceptionMapperTest {

    private FakeGeneralExceptionMapper fakeGeneralExceptionMapper;

    private HttpServletRequest request;

    @Before
    public void initialiseMocks() {
        fakeGeneralExceptionMapper = new FakeGeneralExceptionMapper();
        request = mock(HttpServletRequest.class);
        fakeGeneralExceptionMapper.setRequest(request);
    }

    @Test
    public void notFoundExceptionReturnErrorMessage() {
        ErrorMessage expectedError = createExpectedErrorMessage(SwaggerConstant.API_RESPONSE_404);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = fakeGeneralExceptionMapper.toResponse(new NotFoundException());
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void badRequestExceptionReturnErrorMessage() {
        ErrorMessage expectedError = createExpectedErrorMessage(SwaggerConstant.API_RESPONSE_400 + " BadRequest");

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = fakeGeneralExceptionMapper.toResponse(new BadRequestException("BadRequest"));
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void exceptionReturnErrorMessage() {
        ErrorMessage expectedError = createExpectedErrorMessage(SwaggerConstant.API_RESPONSE_500);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = fakeGeneralExceptionMapper.toResponse(new Exception("ErrorMessage"));
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void webApplicationExceptionReturnErrorMessage() {
        ErrorMessage expectedError = createExpectedErrorMessage(SwaggerConstant.API_RESPONSE_500);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = fakeGeneralExceptionMapper.toResponse(new WebApplicationException());
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    private ErrorMessage createExpectedErrorMessage(String apiResponse404) {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(apiResponse404);
        return expectedError;
    }

    private class FakeGeneralExceptionMapper extends GeneralExceptionMapper{

        public void setRequest(HttpServletRequest request){
            this.request = request;
        }
    }
}