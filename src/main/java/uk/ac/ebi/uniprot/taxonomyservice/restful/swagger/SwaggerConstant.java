package uk.ac.ebi.uniprot.taxonomyservice.restful.swagger;

public class SwaggerConstant {

    public static final String TAXONOMY_API_VALUE = "Rest services that return information about the taxonomy " +
            "tree. The endpoints can return information on a given taxonomic node, on nodes that are related " +
            "to a particular node, or provide a path of nodes from a specific start node to a specific end node." +
            "The endpoints can return responses in an JSON or XML format.";

    //TAXONOMY BY ID
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID = "The endpoint returns details on a given " +
            "taxonomy element, and also links to its parent, siblings and direct children.";

    public static final String NOTE_TAXONOMY_ID = "With taxonomy identification as parameter.";

    //TAXONOMY PARENT
    public static final String API_OPERATION_TAXONOMY_PARENT_BY_ID = "This endpoint returns details of the " +
            "parent taxonomic node";

    //TAXONOMY SIBLINGS
    public static final String API_OPERATION_TAXONOMY_SIBLINGS_BY_ID = "This service returns a list of siblings that " +
            "belong to a taxonomy element";

    //TAXONOMY CHILDREN
    public static final String API_OPERATION_TAXONOMY_CHILDREN_BY_ID = "This service returns a list of children that " +
            "belong to a taxonomy element";

    //TAXONOMY BY NAME
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_NAME = "This service returns a list of " +
            "taxonomies with a specific name. Each element, within the list, contains the node's details, links to " +
            "the parent, siblings and children";

    public static final String NOTE_TAXONOMY_DETAIL_BY_NAME = "with taxonomy name as parameter";

    //TAXONOMY RELATIONSHIP
    public static final String API_OPERATION_TAXONOMY_RELATIONSHIP = "This service returns the path between two " +
            "taxonomies showing their relationship";

    public static final String NOTE_TAXONOMY_RELATIONSHIP = "";

    // TAXONOMY PATH
    public static final String API_OPERATION_TAXONOMY_PATH = "This endpoint returns all taxonomy elements " +
            "that are related with the taxonomic node, with the given taxonomyId for a specific direction (TOP or " +
            "DOWN) and depth level";

    public static final String NOTE_TAXONOMY_PATH = "";


    //ERROR MESSAGES
    public static final String API_RESPONSE_303 = "Taxonomy identifier has changed. New identifier is: " +
            "{newId}.";

    public static final String API_RESPONSE_404 = "Taxonomy entry not found.";

    public static final String API_RESPONSE_400 = "Invalid request parameters:";

    public static final String ID_PARAMETER_IS_REQUIRED = "\"id\" is required";
    public static final String NAME_PARAMETER_IS_REQUIRED = "\"name\" is required";
    public static final String DEPTH_PARAMETER_IS_REQUIRED = "\"depth\" is required";
    public static final String DEPTH_PARAM_MIN_MAX = "\"depth\" must be between 1 and 5";
    public static final String DIRECTION_VALID_VALUES = "\"direction\" must be top or bottom";
    public static final String DIRECTION_PARAMETER_IS_REQUIRED = "\"direction\" is required";
    public static final String TO_PARAMETER_IS_REQUIRED = "\"to\" is required";
    public static final String FROM_PARAMETER_IS_REQUIRED = "\"from\" is required";
    public static final String TO_PARAMETER_VALID_NUMBER = "\"to\" must be a valid taxonomy identifier";
    public static final String FROM_PARAMETER_VALID_NUMBER = "\"from\" must be a valid taxonomy identifier";
    public static final String ID_PARAMETER_VALID_NUMBER = "\"id\" must be a valid taxonomy identifier";

    public static final String API_RESPONSE_500 =
            "Unexpected error has occurred. Could you please try again later.";

    public static final String REQUEST_PARAMETER_INVALID_VALUE =
            "Request parameter {parameterName} contains an unsupported value.";
}