package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant.REQUEST_PARAMETER_INVALID_VALUE;

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

    private static final Logger logger = LoggerFactory.getLogger(ParamExceptionMapper.class);

    @Context protected HttpServletRequest request;

    @Override
    public Response toResponse(ParamException exception) {
        logger.error("Param exception error has occured with error message: ", exception.getMessage(), exception);
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));
        error.addErrorMessage(REQUEST_PARAMETER_INVALID_VALUE
                .replaceFirst("\\{parameterName\\}", exception.getParameterName()));
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
