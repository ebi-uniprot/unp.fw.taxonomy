package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import java.util.ArrayList;
import java.util.List;

public class LifecycleManager {
    private final List<AutoCloseable> resources = new ArrayList<>();

    public void register(AutoCloseable resource) {
        resources.add(resource);
    }

    public void shutdown() {
        for (AutoCloseable resource : resources) {
            try {
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

