package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by lgonzales on 08/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyNames")
public class TaxonomyNamesResponse {

    private List<TaxonomyDetailResponse> taxonomyNames;

    public TaxonomyNamesResponse() {
    }

    public TaxonomyNamesResponse(
            List<TaxonomyDetailResponse> taxonomyNames) {
        this.taxonomyNames = taxonomyNames;
    }

    @XmlElement(name = "nodeDetail", namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyDetail")
    @XmlElementWrapper(name = "taxonomyNames")
    @JsonGetter(value = "taxonomyNames")
    public List<TaxonomyDetailResponse> getTaxonomyNames() {
        return taxonomyNames;
    }

    public void setTaxonomyNames(
            List<TaxonomyDetailResponse> taxonomyNames) {
        this.taxonomyNames = taxonomyNames;
    }

}
