package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        logger.error("Http Request Error has occured with error message: ", exception.getMessage(), exception);
        if (exception instanceof org.glassfish.jersey.server.ParamException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        } else if (exception instanceof com.fasterxml.jackson.core.JsonParseException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        } else if (exception instanceof NotFoundException) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        } else if (exception instanceof BadRequestException) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        } else if (exception instanceof javax.ws.rs.WebApplicationException) {
            javax.ws.rs.WebApplicationException e = (javax.ws.rs.WebApplicationException) exception;
            return Response
                    .status(e.getResponse().getStatus())
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}