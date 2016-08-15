package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values;

/**
 * This enum represents all fields that can be searched in /taxonomy/name endpoint
 *
 * Created by lgonzales on 21/06/16.
 */
public enum FieldNames {
    SCIENTIFICNAME("scientificNameLowerCase"),
    COMMONNAME("commonNameLowerCase"),
    MNEMONIC("mnemonicLowerCase");

    private String seachFieldName;

    FieldNames(String fieldName) {
        this.seachFieldName = fieldName;
    }

    public String getSearchFieldName() {
        return seachFieldName;
    }
}
