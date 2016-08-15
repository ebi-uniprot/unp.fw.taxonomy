package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values;

/**
 * This enum contains possible searchType query param values for /taxonomy/name search in taxonomy
 *
 * Created by lgonzales on 09/06/16.
 */
public enum SearchType {
    EQUALSTO("="),
    STARTSWITH("STARTS WITH"),
    ENDSWITH("ENDS WITH"),
    CONTAINS("CONTAINS");

    private String searchQueryKeyWord;

    SearchType(String keyWord){
        this.searchQueryKeyWord = keyWord;
    }

    public String getSearchQueryKeyWord() {
        return searchQueryKeyWord;
    }
}
