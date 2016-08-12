package uk.ac.ebi.uniprot.taxonomyservice.imports.setup;

import java.io.IOException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class provide a Spring JUnit runner implementation that will trigger the setup methods when test instance is
 * created and after complete all test execution
 *
 * Source extracted From: https://dzone.com/articles/enhancing-spring-test
 *
 * Created by lgonzales on 10/08/16.
 */
public class SpringInstanceTestClassRunner extends SpringJUnit4ClassRunner {

    private static final Logger logger = LoggerFactory.getLogger(SpringInstanceTestClassRunner.class);

    private InstanceTestClassListener instanceSetupListener;

    public SpringInstanceTestClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        if (test instanceof InstanceTestClassListener && instanceSetupListener == null) {
            instanceSetupListener = (InstanceTestClassListener) test;
            instanceSetupListener.beforeClassSetup();
        }
        return test;
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        if (instanceSetupListener != null) {
            try {
                instanceSetupListener.afterClassSetup();
            } catch (IOException e) {
                logger.error("Error executing afterClassSetup: ",e);
            }
        }
    }

}
