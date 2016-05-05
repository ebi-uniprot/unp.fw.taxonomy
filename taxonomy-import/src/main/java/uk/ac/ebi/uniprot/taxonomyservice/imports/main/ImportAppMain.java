package uk.ac.ebi.uniprot.taxonomyservice.imports.main;

import uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Main class for full import(load) the Taxonomy Neo4J database.
 *
 * Created by lgonzales on 22/04/16.
 */
@Import(TaxonomyImportConfig.class)
@SpringBootApplication
public class ImportAppMain {

    public static void main(String[] args){
        SpringApplication.run(ImportAppMain.class, args);
    }
}
