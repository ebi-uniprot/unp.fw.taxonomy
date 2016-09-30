package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.listener;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.FakeTaxonomyDataAccess;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test if neo4j indexes are being executed
 *
 * Created by lgonzales on 30/09/16.
 */
public class StartupListenerTest {

    private static FakeTaxonomyDataAccess neo4jDataAccess;

    @BeforeClass
    public static void setUpAndLoadMockDataFromCSVFile() {
        neo4jDataAccess = new FakeTaxonomyDataAccess("");
    }

    @AfterClass
    public static void tearDown() {
        neo4jDataAccess.getNeo4jDb().shutdown();
    }

    @Test
    public void onApplicationInitializedEventExecuteWithSuccess() throws Exception {
        StartupListener listener = new StartupListener();
        listener.setDataAccess(neo4jDataAccess);

        ApplicationEvent applicationEvent = mock(ApplicationEvent.class);
        when(applicationEvent.getType()).thenReturn(ApplicationEvent.Type.INITIALIZATION_FINISHED);

        listener.onEvent(applicationEvent);
    }

    @Test
    public void onApplicationDestryedEventExecuteWithSuccess() throws Exception {
        StartupListener listener = new StartupListener();

        ApplicationEvent applicationEvent = mock(ApplicationEvent.class);
        when(applicationEvent.getType()).thenReturn(ApplicationEvent.Type.DESTROY_FINISHED);

        listener.onEvent(applicationEvent);
    }

    @Test
    public void onRequestReturnNull() throws Exception {
        StartupListener listener = new StartupListener();
        assertThat(listener.onRequest(null),nullValue());
    }

}