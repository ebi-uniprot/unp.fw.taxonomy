package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

/**
 * This class contains all cypher queries that is executed in Neo4J
 */
public class CypherQueryConstants {

    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = $id " +
                    "WITH p MATCH (s:Node)-[r:CHILD_OF]->(p) WHERE s.taxonomyId <> $id RETURN s AS node SKIP $skip " +
                    "LIMIT $limit";

    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_TOTAL =
            "MATCH (n1:Node)-[r:CHILD_OF]->(p:Node) WHERE n1.taxonomyId = $id " +
                    "WITH p MATCH (n:Node)-[r:CHILD_OF]->(p) RETURN count(n)-1 AS totalRecords";

    static final String GET_TAXONOMY_BASE_NODE_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId = $id RETURN n AS node";

    static final String GET_TAXONOMY_BASE_NODE_LIST_BY_IDS_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId IN $ids RETURN n AS node";

    static final String GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = $id RETURN p AS node";

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = $id RETURN n AS node SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_TOTAL =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = $id RETURN count(n) AS totalRecords";

    static final String GET_TAXONOMY_DETAIL_MATCH_BASE =
            "WITH n, p RETURN n AS node, p.taxonomyId AS parentId, " +
                    "[(n)<-[:CHILD_OF]-(c:Node) | c.taxonomyId] AS children, " +
                    "[(p)<-[:CHILD_OF]-(s:Node) WHERE s <> n | s.taxonomyId] AS siblings";

    static final String GET_TAXONOMY_DETAILS_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId = $id " +
                    "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) " + GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_DETAILS_BY_ID_LIST_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId IN $ids " +
                    "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) " + GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY_WITH_DETAIL =
            "MATCH (c:Node)-[r:CHILD_OF]->(n:Node) WHERE c.taxonomyId = $id " +
                    "WITH n OPTIONAL MATCH (n)-[r:CHILD_OF]->(p:Node) " + GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_WITH_DETAIL =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = $id " +
                    GET_TAXONOMY_DETAIL_MATCH_BASE + " SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_WITH_DETAIL =
            "MATCH (n1:Node)-[r:CHILD_OF]->(p:Node) WHERE n1.taxonomyId = $id " +
                    "WITH p MATCH (n:Node)-[r:CHILD_OF]->(p) WHERE n.taxonomyId <> $id " +
                    "WITH n, p RETURN n AS node, p.taxonomyId AS parentId, " +
                    "[(n)<-[:CHILD_OF]-(c:Node) | c.taxonomyId] AS children SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE =
            "MATCH (n:Node) WHERE $nameWhere ";

    static final String GET_TAXONOMY_NODES_BY_NAME_CYPHER_QUERY =
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE + "RETURN n AS node SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY =
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE +
                    "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) " +
                    GET_TAXONOMY_DETAIL_MATCH_BASE + " SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_TOTAL_RECORDS_CYPHER_QUERY =
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE + "RETURN count(n) AS totalRecords";

    static final String GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY =
            "MATCH (n1:Node), (n2:Node) WHERE n1.taxonomyId = $from AND n2.taxonomyId = $to " +
                    "RETURN relationships(shortestPath((n1)-[:CHILD_OF*]-(n2))) AS r";

    static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE =
            "MATCH p = (n1:Node)<-[:CHILD_OF*1..$depth]-(n2:Node) WHERE n1.taxonomyId = $id";

    static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE + " RETURN relationships(p) AS r";

    static final String GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_PAGINATED =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE +
                    " UNWIND nodes(p) AS n WITH DISTINCT n AS node RETURN node SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE +
                    " UNWIND nodes(p) AS n WITH DISTINCT n AS node RETURN count(node) AS totalRecords";

    static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE =
            "MATCH p = (n1:Node)-[:CHILD_OF*1..]->(n2:Node) WHERE n1.taxonomyId = $id";

    static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE + " RETURN relationships(p) AS r";

    static final String GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_PAGINATED =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE +
                    " UNWIND nodes(p) AS n WITH DISTINCT n AS node WHERE size(n.taxonomyId) > 0 RETURN node SKIP $skip LIMIT $limit";

    static final String GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE +
                    " UNWIND nodes(p) AS n WITH DISTINCT n AS node WHERE size(n.taxonomyId) > 0 RETURN count(node) AS totalRecords";

    static final String CHECK_HISTORICAL_CHANGE_CYPHER_QUERY =
            "MATCH (m:Merged)-[r:MERGED_TO]->(n:Node) WHERE m.taxonomyId = $id RETURN n.taxonomyId AS taxonomyId";

    static final String GET_TAXONOMY_LIENAGE_CYPHER_QUERY =
            "MATCH path = (n:Node)-[:CHILD_OF*]->(:Node) " +
                    "WHERE n.taxonomyId = $id " +
                    "WITH [node in nodes(path) | node] AS lineage " +
                    "UNWIND lineage AS node " +
                    "RETURN node";

}
