package uk.ac.ebi.uniprot.taxonomyservice.restful.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to read application properties for file {@link #CONFIG_PROPERTY_FILE}
 *
 * Created by lgonzales on 09/06/16.
 */
public class TaxonomyProperties {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyProperties.class);
    private static final String CONFIG_PROPERTY_FILE = "config.properties";
    private static Properties configProperties;

    static {
        configProperties = TaxonomyProperties.loadProperties();
    }

    public enum APP_PROPERTY_NAME{
        NEO4J_DATABASE_PATH,
        SWAGGER_VERSION,
        SWAGGER_SERVICE_TITLE("Taxonomy Services"),
        SWAGGER_SERVICE_DESCRIPTION("Taxonomy Rest Services"),
        SWAGGER_BASE_PATH("/proteins/api/taxonomy"),
        SWAGGER_RESOURCE_PACKAGE("uk.ac.ebi.uniprot.taxonomyservice.restful.rest"),
        TAXONOMY_BASE_URI("http://0.0.0.0"),
        TAXONOMY_SERVICE_CONTEXT_PATH("/proteins/api/taxonomy"),
        TAXONOMY_DOCS_CONTEXT_PATH("/proteins/api/taxonomy/docs"),
        TAXONOMY_ACCESS_LOG_PATH("logs/access_log.txt"),
        TAXONOMY_GRIZZLY_CORE_THREAD_POOL_SIZE("10"),
        TAXONOMY_GRIZZLY_MAX_THREAD_POOL_SIZE("16");

        private String defaultValue;

        APP_PROPERTY_NAME(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        APP_PROPERTY_NAME() {
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    public static String getProperty(APP_PROPERTY_NAME propertyName){
        String propertyValue = configProperties.getProperty(propertyName.toString());
        if(propertyValue == null){
            propertyValue = propertyName.getDefaultValue();
        }
        return propertyValue;
    }

    public static int getIntProperty(APP_PROPERTY_NAME propertyName){
        return Integer.valueOf(getProperty(propertyName));
    }

    public static Properties getConfigProperties(){
        return configProperties;
    }

    /**
     * @return loaded properties
     */
    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream propertyInputStream = new FileInputStream(CONFIG_PROPERTY_FILE)) {
            properties.load(propertyInputStream);
        } catch (IOException e) {
            logger.warn("unable to load " + CONFIG_PROPERTY_FILE + " with FileInputStream");
        }
        if (properties.isEmpty()) {
            try (InputStream propertyInputStream = TaxonomyProperties.class.getResourceAsStream("/"
                    + CONFIG_PROPERTY_FILE)) {
                properties.load(propertyInputStream);
            } catch (IOException e) {
                logger.warn("unable to load " + CONFIG_PROPERTY_FILE + " with getResourceAsStream");
            }
        }
        return properties;
    }
}
