package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

/**
 * This class contains all cypher queries that is executed in Neo4J
 */
public class CypherQueryConstants {


    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = {id} " +
                    "with p MATCH (s:Node)-[r:CHILD_OF]->(p) where s.taxonomyId <> {id} RETURN s as node SKIP {skip} " +
                    "LIMIT {limit}";

    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_TOTAL =
            "MATCH (n1:Node)-[r:CHILD_OF]->(p:Node) WHERE n1.taxonomyId = {id} " +
                    "with p MATCH (n:Node)-[r:CHILD_OF]->(p) RETURN count(n)-1 as totalRecords";

    static final String GET_TAXONOMY_BASE_NODE_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId = {id} RETURN n as node";

    static final String GET_TAXONOMY_BASE_NODE_LIST_BY_IDS_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId in {ids} RETURN n as node";

    static final String GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = {id} RETURN p as node";

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = {id} RETURN n as node SKIP {skip} LIMIT {limit}";

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_TOTAL =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = {id} RETURN count(n) as totalRecords";

    static final String GET_TAXONOMY_DETAIL_MATCH_BASE = "with n,p return n as node,p.taxonomyId as parentId," +
            "extract(path in (:Node)-[:CHILD_OF]->(n) | extract( r in relationships(path) | startNode(r).taxonomyId ))"+
            " as children," +
            "extract(path in (p)<-[:CHILD_OF]-(:Node) | extract( r in relationships(path) | startNode(r).taxonomyId ))"+
            " as siblings";

    static final String GET_TAXONOMY_DETAILS_BY_ID_CYPHER_QUERY = "MATCH (n:Node) WHERE n.taxonomyId = {id} " +
            "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_DETAILS_BY_ID_LIST_CYPHER_QUERY = "MATCH (n:Node) WHERE n.taxonomyId in" +
            " {ids} OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY_WITH_DETAIL = "MATCH (c:Node)-[r:CHILD_OF]->" +
            "(n:Node) WHERE c.taxonomyId = {id} with n OPTIONAL MATCH (n:Node)-[r:CHILD_OF]->(p:Node) " +
            GET_TAXONOMY_DETAIL_MATCH_BASE;

    static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_WITH_DETAIL = "MATCH (n:Node)-[r:CHILD_OF]->" +
            "(p:Node) where p.taxonomyId = {id} "+GET_TAXONOMY_DETAIL_MATCH_BASE + " SKIP {skip} LIMIT {limit}";

    static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_WITH_DETAIL = " MATCH (n1:Node)" +
            "-[r:CHILD_OF]->(p:Node) WHERE n1.taxonomyId = {id} with p MATCH (n:Node)-[r:CHILD_OF]->(p) where n" +
            ".taxonomyId <> {id} with n,p return n as node,p.taxonomyId as parentId, " +
            "extract(path in (:Node)-[:CHILD_OF]->(n) | extract( r in relationships(path) | startNode(r).taxonomyId ))"+
            "as children SKIP {skip} LIMIT {limit}";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE =
            "MATCH (n:Node) WHERE {nameWhere} ";

    static final String GET_TAXONOMY_NODES_BY_NAME_CYPHER_QUERY=
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE+"return n as node SKIP {skip} LIMIT {limit}";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY =
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE +"OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) "+
                    GET_TAXONOMY_DETAIL_MATCH_BASE + " SKIP {skip} LIMIT {limit}";

    static final String GET_TAXONOMY_DETAILS_BY_NAME_TOTAL_RECORDS_CYPHER_QUERY =
            GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY_BASE + "RETURN count(n) as totalRecords";

    static final String GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY =
            "MATCH (n1:Node),(n2:Node) where n1.taxonomyId = {from} and n2.taxonomyId = {to} " +
                    "return relationships(shortestpath((n1)-[:CHILD_OF*]-(n2))) as r";

    static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE =
            "MATCH p = (n1:Node)<-[:CHILD_OF*1..{depth}]-(n2:Node) where n1.taxonomyId = {id}";

    static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE+" return relationships(p) as r";

    static final String GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_PAGINATED =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE+" unwind nodes(p) as n with distinct n as node return node skip {skip} limit {limit}";

    static final String GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL =
            GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY_BASE+" unwind nodes(p) as n with distinct n as node return count(node) as totalRecords";

    static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE =
            "MATCH p = (n1:Node)-[:CHILD_OF*1..]->(n2:Node) where n1.taxonomyId = {id}";

    static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE+" return relationships(p) as r";

    static final String GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_PAGINATED =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE+" unwind nodes(p) as n with distinct n as node where size(node.taxonomyId) > 0 return node skip {skip} limit {limit}";

    static final String GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL =
            GET_TAXONOMY_PATH_TOP_CYPHER_QUERY_BASE+" unwind nodes(p) as n with distinct n as node where size(node.taxonomyId) > 0 return count(node) as totalRecords";

    static final String CHECK_HISTORICAL_CHANGE_CYPHER_QUERY =
            "MATCH (m:Merged)-[r:MERGED_TO]->(n:Node) where m.taxonomyId = {id} RETURN n.taxonomyId as taxonomyId";

    static final String GET_TAXONOMY_LIENAGE_CYPHER_QUERY = "MATCH (n:Node) WHERE n.taxonomyId = {id} " +
            "with nodes(last((n:Node)-[:CHILD_OF*]->(:Node))) as lineage unwind lineage as node return node";

}
