package uk.ac.ebi.uniprot.taxonomyservice.restful.swagger;

/**
 * Created by lgonzales on 04/03/16.
 */
public class SwaggerConstant {

    public static final String TAXONOMY_API_VALUE = "Rest services to return information about protein taxonomy " +
            "tree. Basically there are services that returns details about a taxonomy element and also details about " +
            "it parent, siblings and children. There are also services that return the relationship between 2 " +
            "taxonomy elements, and the path between them. Services can return JSON or XML format";

    public static final String API_RESPONSE_404 = "Taxonomy entry is not found";

    public static final String API_RESPONSE_400 = "Invalid request parameters";

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

}
