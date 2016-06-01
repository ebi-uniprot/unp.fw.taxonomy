package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible to test ValidationExceptionMapper methods
 * Created by lgonzales on 30/03/16.
 */
public class ValidationExceptionMapperTest {

    private FakeValidationExceptionMapper fakeValidationExceptionMapper;

    private HttpServletRequest request;

    @Before
    public void initialiseMocks() {
        fakeValidationExceptionMapper = new FakeValidationExceptionMapper();
        request = mock(HttpServletRequest.class);
        fakeValidationExceptionMapper.setRequest(request);
    }

    @Test
    public void asserOneExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(SwaggerConstant.DIRECTION_VALID_VALUES);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Set<ConstraintViolation<String>> errors = new HashSet<>();
        errors.add(ConstraintViolationImpl.forBeanValidation("",SwaggerConstant.DIRECTION_VALID_VALUES,String.class,"",null, null,null,
                null,null));

        Response response = fakeValidationExceptionMapper.toResponse(new ConstraintViolationException("Validation " +
                "error",errors));

        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    @Test
    public void twoExceptionReturnErrorMessage() {
        ErrorMessage expectedError = new ErrorMessage();
        expectedError.setRequestedURL(ResponseAssert.REQUEST_URL);
        expectedError.addErrorMessage(SwaggerConstant.DIRECTION_VALID_VALUES);
        expectedError.addErrorMessage(SwaggerConstant.ID_PARAMETER_IS_REQUIRED);

        when(request.getRequestURL()).thenReturn(new StringBuffer(ResponseAssert.REQUEST_URL));

        Set<ConstraintViolation<String>> errors = new HashSet<>();
        errors.add(ConstraintViolationImpl.forBeanValidation("",SwaggerConstant.DIRECTION_VALID_VALUES,String.class,"",null, null,null,
                null,null));
        errors.add(ConstraintViolationImpl.forBeanValidation("",SwaggerConstant.ID_PARAMETER_IS_REQUIRED,String.class,"",null, null,null,
                null,null));

        Response response = fakeValidationExceptionMapper.toResponse(new ConstraintViolationException("Validation " +
                "error",errors));

        ResponseAssert.assertResponseErrorMessage(expectedError, response);
    }

    private class FakeValidationExceptionMapper extends ValidationExceptionMapper {
        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }
    }
}