package uk.ac.ebi.uniprot.taxonomyservice.imports.model;

/**
 * Class that represents taxonomy node that will be imported.
 *
 * Created by lgonzales on 25/04/16.
 */
public class TaxonomyImportNode {

    private Long taxonomyId;
    private Long parentId;
    private String mnemonic;
    private String scientificName;
    private String commonName;
    private String synonym;
    private String rank;
    private String superregnum;

    public Long getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(Long taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSuperregnum() {
        return superregnum;
    }

    public void setSuperregnum(String superregnum) {
        this.superregnum = superregnum;
    }

    @Override
    public String toString() {
        return "TaxonomyImportNode{" +
                "taxonomyId=" + taxonomyId +
                ", parentId=" + parentId +
                ", mnemonic='" + mnemonic + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", synonym='" + synonym + '\'' +
                ", rank='" + rank + '\'' +
                ", superregnum='" + superregnum + '\'' +
                '}';
    }
}
