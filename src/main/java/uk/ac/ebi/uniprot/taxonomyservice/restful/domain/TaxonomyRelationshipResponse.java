package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by lgonzales on 04/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRelationship")
public class TaxonomyRelationshipResponse {

    private boolean hasRelationship;

    private List<TaxonomyNode> relationshipPath;

    public boolean isHasRelationship() {
        return hasRelationship;
    }

    public void setHasRelationship(boolean hasRelationship) {
        this.hasRelationship = hasRelationship;
    }

    @XmlElement(name = "node", namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
    @XmlElementWrapper(name = "relationshipPath")
    @JsonGetter(value = "relationshipPath")
    public List<TaxonomyNode> getRelationshipPath() {
        return relationshipPath;
    }

    public void setRelationshipPath(
            List<TaxonomyNode> relationshipPath) {
        this.relationshipPath = relationshipPath;
    }

    @Override public String toString() {
        return "TaxonomyRelationshipResponse{" +
                "hasRelationship=" + hasRelationship +
                ", relationshipPath=" + relationshipPath +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomyRelationshipResponse that = (TaxonomyRelationshipResponse) o;

        if (isHasRelationship() != that.isHasRelationship()) {
            return false;
        }
        return getRelationshipPath() != null ? getRelationshipPath().equals(that.getRelationshipPath()) :
                that.getRelationshipPath() == null;

    }

    @Override public int hashCode() {
        int result = (isHasRelationship() ? 1 : 0);
        result = 31 * result + (getRelationshipPath() != null ? getRelationshipPath().hashCode() : 0);
        return result;
    }
}
