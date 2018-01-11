package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter;

import java.util.Map;
import java.util.Optional;

/**
 * This converter is responsible to convert a single property from cypher query into an object
 */
public class PropertyConverter implements Neo4JQueryResulConverter<Object> {

    private String propertyName;

    public PropertyConverter(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Optional<Object> convert(Map<String, Object> row) {
        if(propertyName != null) {
            return getProperty(row, propertyName);
        }else{
            return Optional.ofNullable(row);
        }
    }
}
