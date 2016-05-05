package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableInjector;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is a Lifecycle listener that is responsible for start/stop the custom service that is required
 * by the Rest Services.
 *
 * The lifecycle listener will be called when Jersey container start/stop or reload. Inject any service
 * that will need to be start/stop container wise should be injected here and start/stop accordingly.
 *
 * Created by lgonzales on 19/02/16.
 */
public class ServiceLifecycleManager extends AbstractContainerLifecycleListener {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLifecycleManager.class);

    private final Injector guiceInjector;

    public ServiceLifecycleManager(Injector injector) {
        super();
        this.guiceInjector = injector;
    }

    @Override
    public void onStartup(Container container) {

    }

    @Override
    public void onReload(Container container) {
        super.onReload(container);
        guiceInjector.getInstance(CloseableInjector.class).close();
        logger.info("ServiceInitor Reload");
    }

    @Override
    public void onShutdown(Container container) {
        super.onShutdown(container);
        guiceInjector.getInstance(CloseableInjector.class).close();
        logger.info("ServiceInitor Shutdown");
    }

}
