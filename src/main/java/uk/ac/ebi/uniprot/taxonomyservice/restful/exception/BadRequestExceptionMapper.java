package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lgonzales on 11/03/16.
 */
public class BadRequestExceptionMapper  implements ExceptionMapper<BadRequestException> {

    public static final Logger logger = LoggerFactory.getLogger(BadRequestExceptionMapper.class);

    @Override public Response toResponse(BadRequestException e) {
        return null;
    }
}
