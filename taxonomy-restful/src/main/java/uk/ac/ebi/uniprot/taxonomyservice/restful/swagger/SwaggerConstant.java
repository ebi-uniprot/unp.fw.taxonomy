package uk.ac.ebi.uniprot.taxonomyservice.restful.swagger;

/**
 * Created by lgonzales on 04/03/16.
 */
public class SwaggerConstant {

    public static final String TAXONOMY_API_VALUE = "Rest services to return information about protein taxonomy " +
            "tree. Basically there are services that returns details about a taxonomy element and also details about " +
            "it parent, siblings and children. There are also services that return the relationship between 2 " +
            "taxonomy elements, and the path between them. Services can return JSON or XML format";

    //TAXONOMY BY ID
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID = "This service return details about a taxonomy " +
            "element, and also links for it parent, siblings and children ";

    public static final String NOTE_TAXONOMY_ID = "with taxonomy identification as parameter";

    //TAXONOMY PARENT
    public static final String API_OPERATION_TAXONOMY_PARENT_BY_ID = "This service return details about a taxonomy " +
            "parent element";

    //TAXONOMY SIBLINGS
    public static final String API_OPERATION_TAXONOMY_SIBLINGS_BY_ID = "This service return a list of siblings that " +
            "belongs to a taxonomy element";

    //TAXONOMY CHILDREN
    public static final String API_OPERATION_TAXONOMY_CHILDREN_BY_ID = "This service return a list of children that " +
            "belongs to a taxonomy element";
    //TAXONOMY BY NAME
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_NAME = "This service return a list of " +
            "taxonomies with a specific name. it element contains taxonomy detail and also links for it parent, " +
            "siblings and children";

    public static final String NOTE_TAXONOMY_DETAIL_BY_NAME = "with taxonomy name as parameter";

    //TAXONOMY RELATIONSHIP
    public static final String API_OPERATION_TAXONOMY_RELATIONSHIP = "This service return the path between to " +
            "taxonomies showing their relation";

    public static final String NOTE_TAXONOMY_RELATIONSHIP = "";

    // TAXONOMY PATH
    public static final String API_OPERATION_TAXONOMY_PATH = "This service return all taxonomies elements " +
            "that has relationship with taxonomyId in a specific direction (TOP or DOWN) and depth levels";

    public static final String NOTE_TAXONOMY_PATH = "";


    //TAXONOMY REQUEST PARAMETERS
    public static final String TAXONOMY_ID_PARAM = " Taxonomy element unique identification";
    public static final String TAXONOMY_NAME_PARAM = "Taxonomy name that will be searched in scientificName, " +
            "commonName and mnemonic";
    public static final String TAXONOMY_SEARCH_TYPE_PARAM = "Type of the search, valid values are EQUALSTO," +
            "STARTSWITH,ENDSWITH or CONTAINS";
    public static final String TAXONOMY_DEPTH_PARAM = "Number of depth levels for taxonomy path, valid values between" +
            " 1 and 5";
    public static final String TAXONOMY_DIRECTION_PARAM = "Direction for taxonomy path, valid values are TOP and " +
            "BOTTOM";
    public static final String TAXONOMY_FROM_PARAM = "Initial taxonomy element unique identification for relationship";
    public static final String TAXONOMY_TO_PARAM = "Final taxonomy element unique identification for relationship";

    //ERROR MESSAGES
    public static final String API_RESPONSE_303 = "Taxonomy identification changed and new identification is now " +
            "{newId}.";

    public static final String API_RESPONSE_404_GENERAL = "Taxonomy entry was not found for searched resource.";
    public static final String API_RESPONSE_404_ENTRY = "Taxonomy entry was not found for this taxonomy " +
            "identification.";
    public static final String API_RESPONSE_404_LINEAGE = "Taxonomy lineage was not found for this taxonomy " +
            "identification.";
    public static final String API_RESPONSE_404_NAME = "Taxonomy entries were not found for search name criteria.";
    public static final String API_RESPONSE_404_RELATIONSHIP = "Taxonomy entries were not found for searched " +
            "relationship.";
    public static final String API_RESPONSE_404_PATH = "Taxonomy entries were not found for searched " +
            "path.";

    public static final String API_RESPONSE_400 = "Invalid request parameters.";

    public static final String ID_PARAMETER_IS_REQUIRED = "id parameter is required";
    public static final String NAME_PARAMETER_IS_REQUIRED = "name parameter is required";
    public static final String NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES = "name parameter minimun size is 5 " +
            "characters for startWith, endsWith or contains searchType value";
    public static final String SEARCH_TYPE_VALID_VALUES = "searchType parameter value must be equalsto, startsWith, " +
            "endsWith or contains";
    public static final String DEPTH_PARAMETER_IS_REQUIRED = "depth parameter is required";
    public static final String DEPTH_PARAM_MIN_MAX = "depth param value must be between 1 and 5";
    public static final String DIRECTION_VALID_VALUES = "direction parameter value must be top or bottom";
    public static final String DIRECTION_PARAMETER_IS_REQUIRED = "direction parameter is required";
    public static final String TO_PARAMETER_IS_REQUIRED = "to parameter is required";
    public static final String FROM_PARAMETER_IS_REQUIRED = "from parameter is required";
    public static final String TO_PARAMETER_VALID_NUMBER = "The paramenter to must be a valid number";
    public static final String FROM_PARAMETER_VALID_NUMBER = "The paramenter from must be a valid number";
    public static final String ID_PARAMETER_VALID_NUMBER = "The paramenter id must be a valid number";

    public static final String API_RESPONSE_500 =
            "Unexpected error happened, could you please try again later";

    public static final String REQUEST_PARAMETER_INVALID_VALUE =
            "Request parameter {parameterName} contains not supported value.";
}
