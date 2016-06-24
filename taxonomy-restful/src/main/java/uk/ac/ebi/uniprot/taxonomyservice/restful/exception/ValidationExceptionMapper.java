package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This class is responsible to map and return any Contraint Validation (annotation validation, for example @NotNull)
 * error that happens in the any request parameter and build the proper response error object.
 *
 * It is also being used to log the error in the application log file.
 *
 * Created by lgonzales on 21/03/16.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context protected HttpServletRequest request;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));

        ConstraintViolationException constraintViolation = (ConstraintViolationException) exception;
        for (ConstraintViolation violation:constraintViolation.getConstraintViolations()) {
            error.addErrorMessage(violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

}
