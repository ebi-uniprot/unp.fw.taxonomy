package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter;

import java.util.Map;
import java.util.Optional;

/**
 * Interface responsible to converty cypher query results into an object.
 *
 * @param <T> Object that is returned in the converter
 */
public interface Neo4JQueryResulConverter<T> {

    Optional<T> convert(Map<String, Object> row);

    default Optional<Object> getProperty(Map<String, Object> row, String propertyName){
        Object propertyValue = row.getOrDefault(propertyName,null);
        if(propertyValue != null && !propertyValue.toString().isEmpty()){
            return Optional.of(propertyValue);
        }else{
            return Optional.empty();
        }
    }

}
