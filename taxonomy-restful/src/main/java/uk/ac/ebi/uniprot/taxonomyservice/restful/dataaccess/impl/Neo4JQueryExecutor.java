package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
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
public class Neo4JQueryExecutor {

    protected GraphDatabaseService neo4jDb;

    public Neo4JQueryExecutor(GraphDatabaseService neo4jDb) {
        this.neo4jDb = neo4jDb;
    }

    <T> Optional<T> executeQuery(String query, Map<String, Object> params, Neo4JQueryResulConverter<T> converter ){
        Optional<T> result = Optional.empty();
        try (Transaction tx = neo4jDb.beginTx();
             Result queryResult = neo4jDb.execute(query,params ) )
        {
            if (queryResult.hasNext()) {
                result = converter.convert(queryResult.next());
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        return result;
    }

    <T> Optional<List<T>> executeQueryList(String query, Map<String, Object> params, Neo4JQueryResulConverter<T> converter ){
        List<T> items = new ArrayList<>();
        try ( Transaction tx = neo4jDb.beginTx();
              Result queryResult = neo4jDb.execute(query,params ) )
        {
            while (queryResult.hasNext()) {
                Optional<T> converted = converter.convert(queryResult.next());
                converted.ifPresent(items::add);
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        Optional<List<T>> result = Optional.empty();
        if(!items.isEmpty()){
            result = Optional.of(items);
        }
        return result;
    }

     Optional<TaxonomyNode> executeQueryForPath(String query, Map<String, Object> params, long baseTaxId){
        TaxonomyNode result = null;
        try ( Transaction tx = neo4jDb.beginTx();
              Result queryResult = neo4jDb.execute(query,params ) )
        {
            while (queryResult.hasNext()) {
                TaxonomyNodePathConverter converter = new TaxonomyNodePathConverter(baseTaxId,result);
                Optional<TaxonomyNode> converted = converter.convert(queryResult.next());
                if(converted.isPresent()){
                    result = converted.get();
                }
            }
            queryResult.close();
            tx.success();
            tx.close();
        }

        return Optional.ofNullable(result);
    }

    public void shutdown() {
        neo4jDb.shutdown();
    }
}
