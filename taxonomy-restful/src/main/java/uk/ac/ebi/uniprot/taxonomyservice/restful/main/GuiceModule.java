package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import com.google.inject.Scopes;
import com.mycila.guice.ext.closeable.CloseableModule;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.Neo4jTaxonomyDataAccess;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * GuiceModule is responsible to configure Guice and inject all necessary objects in application services
 *
 * When a new Injection is necessary (@Inject annotation is used) it must be configured (bind) in this GuiceModule
 * class
 *
 * Created by lgonzales on 19/02/16.
 */
public class GuiceModule extends AbstractModule {

    public static final String PACKAGE_SCAN = "uk.ac.ebi.uniprot.taxonomyservice.restful";
    private static final Logger logger = LoggerFactory.getLogger(GuiceModule.class);

    private final RestApp app;
    private final Properties configProperties;


    public GuiceModule(RestApp restApp, Properties configProperties) {
        this.app = restApp;
        this.configProperties = configProperties;
    }

    /**
     * This method is responsible to inject all necessary objects in application services
     */
    @Override
    protected void configure() {
        // Bind the custom LifecycleManager
        bind(LifecycleManager.class).in(Scopes.SINGLETON);

        Names.bindProperties(binder(), configProperties);

        logger.info("Registering Neo4j data access service");

        bind(TaxonomyDataAccess.class).to(Neo4jTaxonomyDataAccess.class).in(Scopes.SINGLETON);

        app.packages(PACKAGE_SCAN);
        app.packages(PACKAGE_SCAN + ".request");
    }

}
