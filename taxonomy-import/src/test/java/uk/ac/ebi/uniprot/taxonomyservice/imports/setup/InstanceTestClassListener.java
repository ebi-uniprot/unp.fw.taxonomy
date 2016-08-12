package uk.ac.ebi.uniprot.taxonomyservice.imports.setup;

import java.io.IOException;

/**
 * This interface declares methods that will be executed when Integration test is created, this way we can setup
 * external resources and at the end of the test that enable close/remove external resources
 *
 * Source extracted From: https://dzone.com/articles/enhancing-spring-test
 *
 * Created by lgonzales on 10/08/16.
 */
public interface InstanceTestClassListener {

    /**
     * This method is executed just after test instance is created and can be used as an instance setup.
     *
     * @throws Exception
     */
    void beforeClassSetup() throws Exception;

    /**
     * This method is executed after test is executed and can be used to close/remove test resources.
     *
     * @throws IOException
     */
    void afterClassSetup() throws IOException;

}
