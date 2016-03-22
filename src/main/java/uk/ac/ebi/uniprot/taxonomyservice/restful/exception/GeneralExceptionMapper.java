package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to map and return any 500 error that happen in Rest services and build it response.
 *
 * It is also being used to log the error in the application log file.
 *
 * Created by lgonzales on 23/02/16.
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    public static final Logger logger = LoggerFactory.getLogger(GeneralExceptionMapper.class);

    @Context private HttpServletRequest request;

    @Override
    public Response toResponse(Exception exception) {
        logger.error("Http Request Error has occured with error message: ", exception.getMessage(), exception);
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(request.getRequestURL().toString(),request.getQueryString());

        if (exception instanceof NotFoundException) {
            error.addErrorMessage(SwaggerConstant.API_RESPONSE_404);
            return Response.status(Response.Status.NOT_FOUND).entity(error)
                    .build();
        } else if (exception instanceof BadRequestException) {
            error.addErrorMessage(SwaggerConstant.API_RESPONSE_400+" "+exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else if (exception instanceof javax.ws.rs.WebApplicationException) {
            error.addErrorMessage("Unexpected error happened, could you please try again later");
            javax.ws.rs.WebApplicationException e = (javax.ws.rs.WebApplicationException) exception;
            return Response
                    .status(e.getResponse().getStatus()).entity(error)
                    .build();
        } else {
            error.addErrorMessage("Unexpected error happened, could you please try again later");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                    .build();
        }
    }
}