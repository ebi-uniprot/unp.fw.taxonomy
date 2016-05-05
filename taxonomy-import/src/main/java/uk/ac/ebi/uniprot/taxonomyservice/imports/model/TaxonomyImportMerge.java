package uk.ac.ebi.uniprot.taxonomyservice.imports.model;

/**
 * Class that represents merged taxonomy node that will be imported.
 *
 * Created by lgonzales on 29/04/16.
 */
public class TaxonomyImportMerge {

    private Long oldTaxonomyId;

    private Long newTaxonomyId;

    public Long getOldTaxonomyId() {
        return oldTaxonomyId;
    }

    public void setOldTaxonomyId(Long oldTaxonomyId) {
        this.oldTaxonomyId = oldTaxonomyId;
    }

    public Long getNewTaxonomyId() {
        return newTaxonomyId;
    }

    public void setNewTaxonomyId(Long newTaxonomyId) {
        this.newTaxonomyId = newTaxonomyId;
    }

    @Override public String toString() {
        return "TaxonomyImportMerge{" +
                "oldTaxonomyId=" + oldTaxonomyId +
                ", newTaxonomyId=" + newTaxonomyId +
                '}';
    }
}
