package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter.Neo4JQueryResulConverter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter.TaxonomyNodePathConverter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class responsible to execute cypher queries and map it to our model objects
 */
import org.neo4j.driver.*;

public class Neo4JQueryExecutor {

    private final Driver driver;

    public Neo4JQueryExecutor(Driver driver) {
        this.driver = driver;
    }

    public <T> Optional<T> executeQuery(String query, Map<String, Object> params, Neo4JQueryResulConverter<T> converter) {
        Optional<T> result = Optional.empty();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            Result queryResult = tx.run(query, params);

            if (queryResult.hasNext()) {
                result = converter.convert(queryResult.next().asMap());
            }

            tx.commit();
        }
        return result;
    }

    public <T> Optional<List<T>> executeQueryList(String query, Map<String, Object> params, Neo4JQueryResulConverter<T> converter) {
        List<T> items = new ArrayList<>();

        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            Result queryResult = tx.run(query, params);

            while (queryResult.hasNext()) {
                Optional<T> converted = converter.convert(queryResult.next().asMap());
                converted.ifPresent(items::add);
            }

            tx.commit();
        }

        return items.isEmpty() ? Optional.empty() : Optional.of(items);
    }

    public Optional<TaxonomyNode> executeQueryForPath(String query, Map<String, Object> params, long baseTaxId) {
        TaxonomyNode result = null;

        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            Result queryResult = tx.run(query, params);

            while (queryResult.hasNext()) {
                TaxonomyNodePathConverter converter = new TaxonomyNodePathConverter(baseTaxId, result);
                Optional<TaxonomyNode> converted = converter.convert(queryResult.next().asMap());

                if (converted.isPresent()) {
                    result = converted.get();
                }
            }

            tx.commit();
        }

        return Optional.ofNullable(result);
    }

}
