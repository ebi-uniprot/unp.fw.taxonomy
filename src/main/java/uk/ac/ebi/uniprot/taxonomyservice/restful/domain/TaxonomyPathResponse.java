package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by lgonzales on 07/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyPath")
public class TaxonomyPathResponse {

    private List<TaxonomyNode> taxonomyPath;

    public TaxonomyPathResponse() {
    }

    public TaxonomyPathResponse(
            List<TaxonomyNode> taxonomyPath) {
        this.taxonomyPath = taxonomyPath;
    }

    @XmlElement(name = "node", namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
    @XmlElementWrapper(name = "taxonomyPath")
    @JsonGetter(value = "taxonomyPath")
    public List<TaxonomyNode> getTaxonomyPath() {
        return taxonomyPath;
    }

    public void setTaxonomyPath(
            List<TaxonomyNode> taxonomyPath) {
        this.taxonomyPath = taxonomyPath;
    }

    @Override public String toString() {
        return "TaxonomyPathResponse{" +
                "taxonomyPath=" + taxonomyPath +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomyPathResponse that = (TaxonomyPathResponse) o;

        return getTaxonomyPath() != null ? getTaxonomyPath().equals(that.getTaxonomyPath()) :
                that.getTaxonomyPath() == null;

    }

    @Override public int hashCode() {
        return getTaxonomyPath() != null ? getTaxonomyPath().hashCode() : 0;
    }
}
