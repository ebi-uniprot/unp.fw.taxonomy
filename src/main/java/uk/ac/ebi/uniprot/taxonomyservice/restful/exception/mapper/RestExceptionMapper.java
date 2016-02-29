package uk.ac.ebi.uniprot.taxonomyservice.restful.exception.mapper;

import javax.ws.rs.WebApplicationException;
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
                                 ExceptionMapper<WebApplicationException> {
    public static final Logger logger = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException ex) {
        logger.error("Http Request Error [500] has occured with error message: ",ex.getMessage(),ex);
        return Response.status(500).build();
    }
}