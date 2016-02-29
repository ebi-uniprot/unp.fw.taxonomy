package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to provide services about taxonomy tree and return Taxonomy details
 *
 * Created by lgonzales on 19/02/16.
 *
 */
@Path("taxonomy")
public class TaxonomyRest {
   public static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    @Inject
    private TaxonomyDataAccess dataAccess;

    @GET
    @Path("/{taxonomyId}.json")
    @Produces({MediaType.APPLICATION_JSON})
   public Response getTaxonomyDetailsByIdJson(@PathParam("taxonomyId") long taxonomyId,@Context Request request){
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdJson");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
   }

    @GET
    @Path("/{taxonomyId}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getTaxonomyDetailsByIdXml(@PathParam("taxonomyId") long taxonomyId,@Context Request request){
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }

}
