package uk.ac.ebi.uniprot.taxonomyservice.restful.swagger;

/**
 * This class contains Swagger constants for taxonomy service
 *
 * Created by lgonzales on 04/03/16.
 */
public class TaxonomyConstants {

    public static final String TAXONOMY_API_VALUE = "Rest services to return information about protein taxonomy " +
            "tree. Basically there are services that returns details about a taxonomy element and also details about " +
            "it parent, siblings and children. There are also services that return the relationship between 2 " +
            "taxonomy elements, and the path between them. Services can return JSON or XML format";

    //TAXONOMY DETAIL BY ID
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID = " This service returns details about a taxonomy " +
            "node, and also links to its parent, sibling and children nodes.";

    //TAXONOMY DETAIL BY ID LIST
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID_LIST = " This service returns a list of taxonomy " +
            "node details with links to its parent, sibling and children nodes.";

    public static final String NOTE_TAXONOMY_ID = "with taxonomy identification as parameter";

    //TAXONOMY BASE NODE BY ID
    public static final String API_OPERATION_TAXONOMY_NODE_BY_ID = "This service returns details about a taxonomy " +
            "node such as the rank, mnemonic, scientific name and common name.";

    //TAXONOMY BASE NODE BY ID LIST
    public static final String API_OPERATION_TAXONOMY_NODE_BY_ID_LIST = "This service returns a list of taxonomy node" +
            " details such as the rank, mnemonic, scientific name and common name.";

    //TAXONOMY PARENT
    public static final String API_OPERATION_TAXONOMY_PARENT_BY_ID = "This service returns details about the parent " +
            "node for a given taxonomy node with links to its parent, sibling and children nodes.";

    //TAXONOMY SIBLINGS
    public static final String API_OPERATION_TAXONOMY_SIBLINGS_BY_ID = "This service returns a list of sibling nodes " +
            "that belongs for a given taxonomy node with links to its parent, sibling and children nodes.";

    //TAXONOMY CHILDREN
    public static final String API_OPERATION_TAXONOMY_CHILDREN_BY_ID = "This service returns a list of children nodes" +
            " that belongs to a taxonomy node with links to its parent, sibling and children nodes.";

    //TAXONOMY PARENT NODE
    public static final String API_OPERATION_TAXONOMY_PARENT_NODE_BY_ID = "This service returns details about the " +
            "parent node for a given taxonomy node.";

    //TAXONOMY SIBLINGS NODE
    public static final String API_OPERATION_TAXONOMY_SIBLINGS_NODE_BY_ID = "This service returns a list of sibling " +
            "nodes that belongs for a given taxonomy node.";

    //TAXONOMY CHILDREN NODE
    public static final String API_OPERATION_TAXONOMY_CHILDREN_NODE_BY_ID = "This service returns a list of children " +
            "nodes that belongs to a taxonomy node.";

    //TAXONOMY BY NAME
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_NAME = "This service returns a list of taxonomic " +
            "nodes with the specific queried name. For each node, the service provides it’s taxonomic details and also " +
            "links to its parent, siblings and children nodes.";

    public static final String NOTE_TAXONOMY_DETAIL_BY_NAME = "with taxonomy name as parameter";

    //TAXONOMY BY NAME BASE NODE
    public static final String API_OPERATION_TAXONOMY_NODES_BY_NAME = "This service returns a list of taxonomic nodes" +
            " with a specific name. For each node, the service provides it’s taxonomic details such as the rank, " +
            "mnemonic, scientific name and common name.";

    //TAXONOMY RELATIONSHIP
    public static final String API_OPERATION_TAXONOMY_RELATIONSHIP = "This service returns the shortest path between " +
            "two taxonomy nodes showing their relationship.";

    public static final String NOTE_TAXONOMY_RELATIONSHIP = "";

    // TAXONOMY PATH
    public static final String API_OPERATION_TAXONOMY_PATH = "This service returns all taxonomic nodes that have a " +
            "relationship with the queried taxonomy ID in a specific direction (TOP or BOTTOM) and depth level.";

    public static final String NOTE_TAXONOMY_PATH = "";

    // TAXONOMY PATH NODES
    public static final String API_OPERATION_TAXONOMY_PATH_NODES = "This service returns paginated taxonomy node list that have a " +
            "relationship with the queried taxonomy ID in a specific direction (TOP or BOTTOM) and depth level.";

    public static final String NOTE_TAXONOMY_PATH_NODES = "";

    //TAXONOMY LINEAGE
    public static final String API_OPERATION_TAXONOMY_LINEAGE = "This service returns the taxonomic lineage for a " +
            "given taxonomy node. It lists the nodes as they appear in the taxonomic tree, with the more specific " +
            "listed first.";


    //TAXONOMY ANCESTOR
    public static final String API_OPERATION_TAXONOMY_ANCESTOR = "This service returns the lowest common ancestor " +
            "(LCA) of two taxonomy nodes.";

    public static final String NOTE_TAXONOMY_ANCESTOR_IDS = "";



    //TAXONOMY REQUEST PARAMETERS
    public static final String TAXONOMY_ID_PARAM = " Taxonomy element unique identification";
    public static final String TAXONOMY_IDS_PARAM = " A coma separated list of Taxonomy element unique identification";
    public static final String TAXONOMY_NAME_PARAM = "Taxonomy name that will be searched in scientificName, " +
            "commonName and mnemonic";
    public static final String TAXONOMY_SEARCH_TYPE_PARAM = "Type of the search, valid values are EQUALSTO," +
            "STARTSWITH,ENDSWITH or CONTAINS";
    public static final String TAXONOMY_FIELD_NAME_PARAM = "fieldName, valid values are SCIENTIFICNAME, COMMONNAME " +
            "or MNEMONIC";
    public static final String TAXONOMY_PAGE_NUMBER_PARAM = "pageNumber is the current page number";
    public static final String TAXONOMY_PAGE_SIZE_PARAM = "pageSize is the number of records returned in the request," +
            " max value is 200";
    public static final String TAXONOMY_DEPTH_PARAM = "Number of depth levels for taxonomy path, valid values between" +
            " 1 and 5";
    public static final String TAXONOMY_DIRECTION_PARAM = "Direction for taxonomy path, valid values are TOP and " +
            "BOTTOM";
    public static final String TAXONOMY_FROM_PARAM = "Initial taxonomy element unique identification for relationship";
    public static final String TAXONOMY_TO_PARAM = "Final taxonomy element unique identification for relationship";

    //ERROR MESSAGES
    public static final String API_RESPONSE_303 = "The taxonomy ID has changed to {newId}";

    public static final String API_RESPONSE_404_GENERAL = "No taxonomy results found for the searched resource";
    public static final String API_RESPONSE_404_ENTRY = "No results found for the taxonomy ID";
    public static final String API_RESPONSE_404_LINEAGE = "No lineage found for the taxonomy ID";
    public static final String API_RESPONSE_404_ANCESTOR = "No ancestors found for the taxonomy IDs";
    public static final String API_RESPONSE_404_NAME = "No results found for the taxonomy name";
    public static final String API_RESPONSE_404_RELATIONSHIP = "No taxonomy results found for the searched relationship";
    public static final String API_RESPONSE_404_PATH = "No taxonomy results found for the searched path";

    public static final String API_RESPONSE_400 = "Invalid request parameters";

    public static final String ID_PARAMETER_IS_REQUIRED = "The id parameter cannot be null";
    public static final String IDS_PARAMETER_IS_REQUIRED = "The ids parameter should be a comma separated list";
    public static final String NAME_PARAMETER_IS_REQUIRED = "The name parameter cannot be null";
    public static final String NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES = "The name parameter should be a minimum" +
            " of {min} characters for searchType values startWith, endsWith, contains";
    public static final String SEARCH_TYPE_VALID_VALUES = "The searchType parameter can only be equalsto, startsWith," +
            " endsWith or contains";
    public static final String FIELD_NAME_VALID_VALUES = "The fieldName parameter can only be scientificName, " +
            "commonName, mnemonic";
    public static final String PAGE_NUMBER_PARAMETER_VALID_NUMBER = "The pageNumber parameter should be a valid number";
    public static final String PAGE_NUMBER_PARAMETER_MIN_VALUE = "TThe pageNumber parameter should be a minimum of {min}";
    public static final String PAGE_SIZE_PARAMETER_VALID_NUMBER = "The pageSize parameter should be a valid number";
    public static final String PAGE_SIZE_PARAMETER_MAX_VALUE = "The pageSize parameter should be a maximum of {max}";
    public static final String DEPTH_PARAMETER_IS_REQUIRED = "The depth parameter cannot be null";
    public static final String DEPTH_PARAM_MAX = "The depth parameter should be less than or equals to {max} for the bottom direction";
    public static final String DEPTH_PARAM_MIN = "The depth parameter should be bigger or equals to {value}";
    public static final String DIRECTION_VALID_VALUES = "The direction parameter can only be top or bottom";
    public static final String DIRECTION_PARAMETER_IS_REQUIRED = "The direction parameter cannot be null";
    public static final String TO_PARAMETER_IS_REQUIRED = "The to parameter cannot be null";
    public static final String FROM_PARAMETER_IS_REQUIRED = "The from parameter cannot be null";
    public static final String TO_PARAMETER_VALID_NUMBER = "The to parameter should be a valid number";
    public static final String FROM_PARAMETER_VALID_NUMBER = "The from parameter should be a valid number";
    public static final String ID_PARAMETER_VALID_NUMBER = "The id parameter should be a valid number";
    public static final String IDS_PARAMETER_VALID_NUMBER = "The ids parameter should be a list of valid comma " +
            "separated taxonomy IDs";
    public static final String IDS_PARAMETER_MIN_MAX_SIZE = "The ids parameter should have between {minSize} and " +
            "{maxSize} valid comma separated taxonomy IDs";

    public static final String API_RESPONSE_500 =
            "Unexpected error. Please try again later.";

    public static final String REQUEST_PARAMETER_INVALID_VALUE =
            "Request parameter {parameterName} contains not supported value.";

    private TaxonomyConstants(){}
}
