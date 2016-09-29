package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.API_RESPONSE_303;

/**
 * This class is responsible to build {@link Response} object for search requests
 *
 * Created by lgonzales on 26/09/16.
 */
public class PageResponseBuilder extends ResponseBuilder {

    private Map<String,Long> idsForHistoricalCheck;

    public PageResponseBuilder setIdsForHistoricalCheck(Map<String,Long> idsForHistoricalCheck) {
        this.idsForHistoricalCheck = idsForHistoricalCheck;
        return this;
    }

    @Override
    public Response buildResponse(){
        if(entity == null || notFoundErrorMessage ==null || request == null){
            throw new IllegalStateException("entity, notFoundErrorMessage and request are required attributes do " +
                    "build taxonomy response");
        }
        if(entity.isPresent()){
            return buildSucessResponse();
        } else {
            if(idsForHistoricalCheck != null){
                if(dataAccess == null){
                    throw new IllegalStateException("dataAccess is required attributes to check historical information"+
                            " to build taxonomy response");
                }
                String currentURL = URLUtil.getCurrentURL(request);
                List<Long> newIds = new ArrayList<>();
                for (String parameterName: idsForHistoricalCheck.keySet()) {
                    long taxonomyId = idsForHistoricalCheck.get(parameterName);
                    Optional<Long> newTaxonomyId = dataAccess.getTaxonomyHistoricalChange(taxonomyId);
                    if(newTaxonomyId.isPresent()) {
                        newIds.add(newTaxonomyId.get());
                        currentURL = URLUtil.getNewRedirectHeaderLocationURL(currentURL, parameterName, taxonomyId, newTaxonomyId.get());
                    }
                }
                if(!newIds.isEmpty()){
                    return buildRedirectResponse(currentURL,newIds);
                }
            }
            return buildNotFoundResponse();
        }
    }

    private Response buildSucessResponse() {
        return Response.ok(entity.get()).build();
    }

    private Response buildNotFoundResponse() {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));
        error.addErrorMessage(notFoundErrorMessage);
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    private Response buildRedirectResponse(String newURL,List<Long> newIds) {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));
        for (long id: newIds) {
            if(id > 0){
                error.addErrorMessage(API_RESPONSE_303.replace("{newId}",""+ id));
            }
        }
        return Response.status(Response.Status.SEE_OTHER).entity(error).header(HttpHeaders.LOCATION,newURL).build();
    }
}
