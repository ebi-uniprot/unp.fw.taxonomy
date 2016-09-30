package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.listener;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.FieldNames;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.SearchType;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to execute neo4j queries that will be able to initialize neo4j indexes when application
 * starts...
 *
 * Created by lgonzales on 29/09/16.
 */
@Provider
public class StartupListener implements ApplicationEventListener{

    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    private static final Long TAXONOMY_ID = 9606L;
    private static final String SCIENTIFIC_NAME = "Homo sapiens neanderthalensis";
    private static final String NAME = "HUMAN";

    @Inject
    private TaxonomyDataAccess dataAccess;

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        switch (applicationEvent.getType()) {
            case INITIALIZATION_FINISHED:
                logger.info("Taxonomy Service  creating ");
                dataAccess.getTaxonomyBaseNodeById(TAXONOMY_ID);
                searchNamesByFields(FieldNames.SCIENTIFICNAME,SCIENTIFIC_NAME);
                searchNamesByFields(FieldNames.COMMONNAME,NAME);
                searchNamesByFields(FieldNames.MNEMONIC,NAME);
                logger.info("Taxonomy Service  was initialized");
                break;
            case DESTROY_FINISHED:
                logger.info("Taxonomy Service  was destroyed.");
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }

    private void searchNamesByFields(FieldNames name, String value){
        NameRequestParams param = new NameRequestParams();
        param.setTaxonomyName(value);
        param.setFieldName(name.name());
        param.setSearchType(SearchType.EQUALSTO.name());
        dataAccess.getTaxonomyNodesByName(param,"");

        param.setSearchType(SearchType.CONTAINS.name());
        dataAccess.getTaxonomyNodesByName(param,"");

        param.setSearchType(SearchType.STARTSWITH.name());
        dataAccess.getTaxonomyNodesByName(param,"");

        param.setSearchType(SearchType.ENDSWITH.name());
        dataAccess.getTaxonomyNodesByName(param,"");
    }

    protected void setDataAccess(TaxonomyDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
