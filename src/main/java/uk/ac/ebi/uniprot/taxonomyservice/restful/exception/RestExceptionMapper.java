package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

/**
 * This class is responsible to map and return any 500 error that happen in Rest services and build it response.
 *
 * It is mainly being used to log the error in the application log file.
 *
 * Created by lgonzales on 23/02/16.
 */
public class RestExceptionMapper implements
                                 ExceptionMapper<Exception> {
    public static final Logger logger = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override //TODO Improve the way we are handling exceptions...
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        logger.error("Http Request Error has occured with error message: ", exception.getMessage(), exception);
        ErrorMessage error = new ErrorMessage();
        if (exception instanceof org.glassfish.jersey.server.ParamException) {
    		error.setErrorMessage(SwaggerConstant.API_RESPONSE_400);
    		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else if (exception instanceof NotFoundException) {
    		error.setErrorMessage(SwaggerConstant.API_RESPONSE_404);
    		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(error).build();
        } else if (exception instanceof BadRequestException) {   		
    		error.setErrorMessage(SwaggerConstant.API_RESPONSE_400);
    		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else if (exception instanceof javax.ws.rs.WebApplicationException) {
        	error.setErrorMessage("Unexpected error happened, could you please try again later");
            javax.ws.rs.WebApplicationException e = (javax.ws.rs.WebApplicationException) exception;
            return Response
                    .status(e.getResponse().getStatus()).entity(error)
                    .build();
        } else {
        	error.setErrorMessage("Unexpected error happened, could you please try again later");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                    .build();
        }
    }
}