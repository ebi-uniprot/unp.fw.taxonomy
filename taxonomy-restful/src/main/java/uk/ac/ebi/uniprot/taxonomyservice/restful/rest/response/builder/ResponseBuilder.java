package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * This abstract class contains commons methods and attributes to build {@link Response} objects
 *
 * Created by lgonzales on 23/09/16.
 */
public abstract class ResponseBuilder{

    protected Optional entity;
    protected HttpServletRequest request;
    protected String notFoundErrorMessage;
    protected TaxonomyDataAccess dataAccess;

    public ResponseBuilder setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public ResponseBuilder setNotFoundErrorMessage(String notFoundErrorMessage) {
        this.notFoundErrorMessage = notFoundErrorMessage;
        return this;
    }

    public ResponseBuilder setDataAccess(TaxonomyDataAccess dataAccess) {
        this.dataAccess = dataAccess;
        return this;
    }

    public ResponseBuilder setEntity(Optional entity) {
        this.entity = entity;
        return this;
    }

    public abstract Response buildResponse();

}
