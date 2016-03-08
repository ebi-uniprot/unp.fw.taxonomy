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
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID_JSON =
            "This service return details about a taxonomy " +
                    "element in json format";

    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_ID_XML =
            "This service return details about a taxonomy " +
                    "element in xml format";

    public static final String NOTE_TAXONOMY_DETAIL_BY_ID = "with taxonomy identification";

    //TAXONOMY BY NAME
    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_NAME_JSON = "This service return details about " +
            "taxonomies with a specific name in json format";

    public static final String API_OPERATION_TAXONOMY_DETAIL_BY_NAME_XML = "This service return details about " +
            "taxonomies with a specific name in xml format";

    public static final String NOTE_TAXONOMY_DETAIL_BY_NAME = "with taxonomy name";

    //TAXONOMY RELATIONSHIP
    public static final String API_OPERATION_TAXONOMY_RELATIONSHIP_JSON = "This service return details about " +
            "taxonomies with a specific name in json format";

    public static final String API_OPERATION_TAXONOMY_RELATIONSHIP_XML = "This service return details about " +
            "taxonomies with a specific name in xml format";

    public static final String NOTE_TAXONOMY_RELATIONSHIP = "with taxonomy identification";

    // TAXONOMY PATH
    public static final String NOTE_TAXONOMY_PATH = "with taxonomy identification";

    public static final String API_OPERATION_TAXONOMY_PATH_JSON = "This service return details about " +
            "taxonomies with a specific name in json format";

    public static final String API_OPERATION_TAXONOMY_PATH_XML = "This service return details about " +
            "taxonomies with a specific name in xml format";

}
