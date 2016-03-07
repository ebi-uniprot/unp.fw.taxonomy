package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import java.util.List;

/**
 * Created by lgonzales on 04/03/16.
 */
public class TaxonomyRelationshipResponse {

    private boolean hasRelationship;

    private List<TaxonomyNode> relationshipPath;

    public boolean isHasRelationship() {
        return hasRelationship;
    }

    public void setHasRelationship(boolean hasRelationship) {
        this.hasRelationship = hasRelationship;
    }

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
