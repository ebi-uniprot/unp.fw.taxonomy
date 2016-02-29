package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a Taxonomy Element(node) in the taxonomy Tree.
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response
 *
 * Created by lgonzales on 19/02/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomy")
public class TaxonomyNode {

    private long taxonomyId;
    private String mnemonic;
    private String scientificName;
    private String commonName;
    private String synonym;
    private long parentId;
    private String rank;

    @Override public int hashCode() {
        int result = (int) (getTaxonomyId() ^ (getTaxonomyId() >>> 32));
        result = 31 * result + (getMnemonic() != null ? getMnemonic().hashCode() : 0);
        result = 31 * result + (getScientificName() != null ? getScientificName().hashCode() : 0);
        result = 31 * result + (getCommonName() != null ? getCommonName().hashCode() : 0);
        result = 31 * result + (getSynonym() != null ? getSynonym().hashCode() : 0);
        result = 31 * result + (int) (getParentId() ^ (getParentId() >>> 32));
        result = 31 * result + (getRank() != null ? getRank().hashCode() : 0);
        return result;
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
        if (getParentId() != that.getParentId()) {
            return false;
        }
        if ((getMnemonic() != null) ? !getMnemonic().equals(that.getMnemonic()) : (that.getMnemonic() != null)) {
            return false;
        }
        if ((getScientificName() != null) ? !getScientificName().equals(that.getScientificName()) :
                (that.getScientificName() != null)) {
            return false;
        }
        if ((getCommonName() != null) ? !getCommonName().equals(that.getCommonName()) : (that.getCommonName() != null)) {
            return false;
        }
        if ((getSynonym() != null) ? !getSynonym().equals(that.getSynonym()) : (that.getSynonym() != null)) {
            return false;
        }
        return getRank() != null ? getRank().equals(that.getRank()) : that.getRank() == null;

    }

    @XmlElement(required = true)
    public long getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(long taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @XmlElement
    public long getParentId() {
        return parentId;
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

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override public String toString() {
        return "TaxonomyNode{" +
                "taxonomyId=" + taxonomyId +
                ", mnemonic='" + mnemonic + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", synonym='" + synonym + '\'' +
                ", parentId='" + parentId + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}
