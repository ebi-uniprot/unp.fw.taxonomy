package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a Taxonomy Element(node) in the taxonomy Tree.
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response
 *
 * Created by lgonzales on 19/02/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
public class TaxonomyNode {

    private long taxonomyId;
    private String mnemonic;
    private String scientificName;
    private String commonName;
    private String synonym;
    private String rank;

    private TaxonomyNode parent;
    private String parentLink;

    private List<TaxonomyNode> children;
    private List<String> childrenLinks;

    private List<TaxonomyNode> siblings;
    private List<String> siblingsLinks;

    @XmlElement(required = true)
    public long getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(long taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @XmlElement(required = true)
    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @XmlElement
    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    @XmlElement
    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @XmlElement
    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    @XmlElement
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @XmlElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
    public TaxonomyNode getParent() {
        return parent;
    }

    public void setParent(TaxonomyNode parent) {
        this.parent = parent;
    }

    @XmlElement
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

    @XmlElement(name = "childLink")
    @XmlElementWrapper(name = "childrenLinks")
    @JsonGetter(value = "childrenLinks")
    public List<String> getChildrenLinks() {
        return childrenLinks;
    }

    public void setChildrenLinks(List<String> childrenLinks) {
        this.childrenLinks = childrenLinks;
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

    @XmlElement(name = "siblingLinks")
    @XmlElementWrapper(name = "siblingsLinks")
    @JsonGetter(value = "siblingsLinks")
    public List<String> getSiblingsLinks() {
        return siblingsLinks;
    }

    public void setSiblingsLinks(List<String> siblingsLinks) {
        this.siblingsLinks = siblingsLinks;
    }

    @Override public String toString() {
        return "TaxonomyNode{" +
                "taxonomyId=" + taxonomyId +
                ", mnemonic='" + mnemonic + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", synonym='" + synonym + '\'' +
                ", rank='" + rank + '\'' +
                ", parent=" + parent +
                ", parentLink='" + parentLink + '\'' +
                ", children=" + children +
                ", childrenLinks=" + childrenLinks +
                ", siblings=" + siblings +
                ", siblingsLinks=" + siblingsLinks +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomyNode that = (TaxonomyNode) o;

        if (getTaxonomyId() != that.getTaxonomyId()) {
            return false;
        }
        if (getMnemonic() != null ? !getMnemonic().equals(that.getMnemonic()) : that.getMnemonic() != null) {
            return false;
        }
        if (getScientificName() != null ? !getScientificName().equals(that.getScientificName()) :
                that.getScientificName() != null) {
            return false;
        }
        if (getCommonName() != null ? !getCommonName().equals(that.getCommonName()) : that.getCommonName() != null) {
            return false;
        }
        if (getSynonym() != null ? !getSynonym().equals(that.getSynonym()) : that.getSynonym() != null) {
            return false;
        }
        if (getRank() != null ? !getRank().equals(that.getRank()) : that.getRank() != null) {
            return false;
        }
        if (getParent() != null ? !getParent().equals(that.getParent()) : that.getParent() != null) {
            return false;
        }
        if (getParentLink() != null ? !getParentLink().equals(that.getParentLink()) : that.getParentLink() != null) {
            return false;
        }
        if (getChildren() != null ? !getChildren().equals(that.getChildren()) : that.getChildren() != null) {
            return false;
        }
        if (getChildrenLinks() != null ? !getChildrenLinks().equals(that.getChildrenLinks()) :
                that.getChildrenLinks() != null) {
            return false;
        }
        if (getSiblings() != null ? !getSiblings().equals(that.getSiblings()) : that.getSiblings() != null) {
            return false;
        }
        return getSiblingsLinks() != null ? getSiblingsLinks().equals(that.getSiblingsLinks()) :
                that.getSiblingsLinks() == null;

    }

    @Override public int hashCode() {
        int result = (int) (getTaxonomyId() ^ (getTaxonomyId() >>> 32));
        result = 31 * result + (getMnemonic() != null ? getMnemonic().hashCode() : 0);
        result = 31 * result + (getScientificName() != null ? getScientificName().hashCode() : 0);
        result = 31 * result + (getCommonName() != null ? getCommonName().hashCode() : 0);
        result = 31 * result + (getSynonym() != null ? getSynonym().hashCode() : 0);
        result = 31 * result + (getRank() != null ? getRank().hashCode() : 0);
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        result = 31 * result + (getParentLink() != null ? getParentLink().hashCode() : 0);
        result = 31 * result + (getChildren() != null ? getChildren().hashCode() : 0);
        result = 31 * result + (getChildrenLinks() != null ? getChildrenLinks().hashCode() : 0);
        result = 31 * result + (getSiblings() != null ? getSiblings().hashCode() : 0);
        result = 31 * result + (getSiblingsLinks() != null ? getSiblingsLinks().hashCode() : 0);
        return result;
    }
}
