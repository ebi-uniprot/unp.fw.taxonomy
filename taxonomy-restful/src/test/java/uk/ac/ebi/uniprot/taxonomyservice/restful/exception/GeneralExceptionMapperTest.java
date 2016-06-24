package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
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
 * This class is responsible to test GeneralExceptionMapper methods
 * Created by lgonzales on 30/03/16.
 */
public class GeneralExceptionMapperTest {

    private MockedGeneralExceptionMapper mockedGeneralExceptionMapper;

    private HttpServletRequest request;

    @Before
    public void initialiseMocks() {
        mockedGeneralExceptionMapper = new MockedGeneralExceptionMapper();
        request = mock(HttpServletRequest.class);
        mockedGeneralExceptionMapper.setRequest(request);
    }

    @Test
    public void assertNotFoundExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(TaxonomyConstants.API_RESPONSE_404_GENERAL);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = mockedGeneralExceptionMapper.toResponse(new NotFoundException());
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void assertBadRequestExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(TaxonomyConstants.API_RESPONSE_400+" BadRequest");

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = mockedGeneralExceptionMapper.toResponse(new BadRequestException("BadRequest"));
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void assertExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(TaxonomyConstants.API_RESPONSE_500);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = mockedGeneralExceptionMapper.toResponse(new Exception("ErrorMessage"));
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void assertWebApplicationExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(TaxonomyConstants.API_RESPONSE_500);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Response response = mockedGeneralExceptionMapper.toResponse(new WebApplicationException());
        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    private class MockedGeneralExceptionMapper extends GeneralExceptionMapper{

        public void setRequest(HttpServletRequest request){
            this.request = request;
        }

    }

}