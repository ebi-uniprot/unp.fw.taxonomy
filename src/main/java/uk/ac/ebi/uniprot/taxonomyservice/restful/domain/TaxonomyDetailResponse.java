package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This model class represents a TaxonomyNode with list of children and siblings to help Taxonomy Tree navigation.
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response
 *
 * Created by lgonzales on 19/02/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyDetail")
public class TaxonomyDetailResponse {

    private TaxonomyNode node;
    private String parentLink;
    private List<TaxonomyNode> children;
    private List<TaxonomyNode> siblings;

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomyDetailResponse response = (TaxonomyDetailResponse) o;

        if ((getNode() != null) ? !getNode().equals(response.getNode()) : (response.getNode() != null)) {
            return false;
        }
        if ((getParentLink() != null) ? !getParentLink().equals(response.getParentLink()) :
                (response.getParentLink() != null)) {
            return false;
        }
        if ((getChildren() != null) ? !getChildren().equals(response.getChildren()) :
                (response.getChildren() != null)) {
            return false;
        }
        return getSiblings() != null ? getSiblings().equals(response.getSiblings()) : response.getSiblings() == null;

    }

    @Override public int hashCode() {
        int result = getNode() != null ? getNode().hashCode() : 0;
        result = 31 * result + (getParentLink() != null ? getParentLink().hashCode() : 0);
        result = 31 * result + (getChildren() != null ? getChildren().hashCode() : 0);
        result = 31 * result + (getSiblings() != null ? getSiblings().hashCode() : 0);
        return result;
    }

    public TaxonomyNode getNode() {
        return node;
    }

    public void setNode(TaxonomyNode node) {
        this.node = node;
    }

    public String getParentLink() {
        return parentLink;
    }

    public void setParentLink(String parentLink) {
        this.parentLink = parentLink;
    }

    @XmlElement(name = "child", namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
    @XmlElementWrapper(name = "children")
    @JsonGetter(value = "children")
    public List<TaxonomyNode> getChildren() {
        return children;
    }

    public void setChildren(List<TaxonomyNode> children) {
        this.children = children;
    }

    @XmlElement(name = "sibling", namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
    @XmlElementWrapper(name = "siblings")
    @JsonGetter(value = "siblings")
    public List<TaxonomyNode> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<TaxonomyNode> siblings) {
        this.siblings = siblings;
    }

    @Override public String toString() {
        return "TaxonomyDetailResponse{" +
                "node=" + node +
                ", parentLink='" + parentLink + '\'' +
                ", children=" + children +
                ", siblings=" + siblings +
                '}';
    }
}
