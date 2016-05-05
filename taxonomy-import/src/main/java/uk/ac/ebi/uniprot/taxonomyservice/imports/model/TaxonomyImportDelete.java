package uk.ac.ebi.uniprot.taxonomyservice.imports.model;

/**
 * Class that represents deleted taxonomy node that will be imported.
 *
 * Created by lgonzales on 29/04/16.
 */
public class TaxonomyImportDelete {

    private Long taxonomyId;

    public Long getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(Long taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @Override public String toString() {
        return "TaxonomyImportDelete{" +
                "taxonomyId=" + taxonomyId +
                '}';
    }
}
