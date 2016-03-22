package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to map and return any request parameter value error that happen in Rest services and build
 * the response error object.
 *
 * It is also being used to log the error in the application log file.
 *
 * Created by lgonzales on 21/03/16.
 */
@Provider
public class ParamExceptionMapper implements ExceptionMapper<ParamException> {

    public static final Logger logger = LoggerFactory.getLogger(ParamExceptionMapper.class);

    @Context private HttpServletRequest request;

    @Override
    public Response toResponse(ParamException exception) {
        logger.error("Param exception error has occured with error message: ", exception.getMessage(), exception);
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(request.getRequestURL().toString(),request.getQueryString());
        error.addErrorMessage("Request parameter "+exception.getParameterName() + " contains not supported value.");
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
