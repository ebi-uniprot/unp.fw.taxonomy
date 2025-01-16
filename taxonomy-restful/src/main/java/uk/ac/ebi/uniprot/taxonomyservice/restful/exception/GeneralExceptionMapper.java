package uk.ac.ebi.uniprot.taxonomyservice.restful.exception;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.API_RESPONSE_400;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.API_RESPONSE_404_GENERAL;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.API_RESPONSE_500;

/**
 * This class is responsible to map and return any 500 error that happen in Rest services and build it response.
 *
 * It is also being used to log the error in the application log file.
 *
 * Created by lgonzales on 23/02/16.
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger logger = LoggerFactory.getLogger(GeneralExceptionMapper.class);

    @Context protected HttpServletRequest request;

    @Override
    public Response toResponse(Exception exception) {
        logger.error("Http Request Error has occured with error message: ", exception.getMessage(), exception);
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));

        if (exception instanceof NotFoundException) {
            error.addErrorMessage(API_RESPONSE_404_GENERAL);
            return Response.status(Response.Status.NOT_FOUND).entity(error).type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } else if (exception instanceof BadRequestException) {
            error.addErrorMessage(API_RESPONSE_400+" "+exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else if (exception instanceof jakarta.ws.rs.WebApplicationException) {
            error.addErrorMessage(API_RESPONSE_500);
            jakarta.ws.rs.WebApplicationException e = (jakarta.ws.rs.WebApplicationException) exception;
            return Response
                    .status(e.getResponse().getStatus()).entity(error)
                    .build();
        } else {
            error.addErrorMessage(API_RESPONSE_500);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                    .build();
        }
    }
}