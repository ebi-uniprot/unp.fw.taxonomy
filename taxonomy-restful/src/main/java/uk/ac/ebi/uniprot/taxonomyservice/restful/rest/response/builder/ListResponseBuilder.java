package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.TaxonomiesError;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.TaxonomiesRedirect;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;

/**
 * This class is responsible to build {@link Response} object for search by id list requests
 *
 * Created by lgonzales on 26/09/16.
 */
public class ListResponseBuilder extends ResponseBuilder {

    private List<String> requestedIds;

    public ListResponseBuilder setRequestedIds(List<String> requestedIds) {
        this.requestedIds = requestedIds;
        return this;
    }

    @Override
    public Response buildResponse(){
        if(entity == null || requestedIds == null){
            throw new IllegalStateException("entity and requestedIds are required attributes to " +
                    "build taxonomies list response");
        }
        Taxonomies responseBody = new Taxonomies();
        List<TaxonomiesRedirect> redirects = new ArrayList<>();
        List<TaxonomiesError> errors = new ArrayList<>();
        if(entity.isPresent()){
            responseBody = (Taxonomies) entity.get();
        }
        List<Long> notfoundIds = getNotFoundIds(responseBody);
        if((notfoundIds != null && !notfoundIds.isEmpty()) &&
                (notFoundErrorMessage == null || request == null || dataAccess == null)){
            throw new IllegalStateException("dataAccess, notFoundErrorMessage and request are required attributes to " +
                    "build taxonomies list response");
        }
        for (Long notfoundId: notfoundIds) {
            Optional<Long> newTaxonomyId = dataAccess.getTaxonomyHistoricalChange(notfoundId);
            if(newTaxonomyId.isPresent()) {
                String redirectURL = URLUtil.getTaxonomyIdBasePath(request) + newTaxonomyId.get();
                TaxonomiesRedirect redirect = new TaxonomiesRedirect();
                redirect.setRequestedId(notfoundId);
                redirect.setRedirectLocation(redirectURL);
                redirects.add(redirect);
            }else{
                TaxonomiesError error = new TaxonomiesError();
                error.setRequestedId(notfoundId);
                error.setErrorMessage(notFoundErrorMessage);
                errors.add(error);
            }
        }
        if(!redirects.isEmpty()){
            responseBody.setRedirects(redirects);
        }
        if(!errors.isEmpty()){
            responseBody.setErrors(errors);
        }
        return Response.ok(responseBody).build();
    }

    private List<Long> getNotFoundIds(Taxonomies responseBody) {
        List<Long> notFoundIds = requestedIds.stream().map(Long::valueOf).collect(Collectors.toList());
        if(responseBody.getTaxonomies() != null){
            for (TaxonomyNode node: responseBody.getTaxonomies()) {
                notFoundIds.remove(node.getTaxonomyId());
            }
        }
        return notFoundIds;
    }


}
