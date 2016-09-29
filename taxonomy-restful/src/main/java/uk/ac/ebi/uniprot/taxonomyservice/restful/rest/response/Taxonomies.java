package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the REST response body object that will be returned when user request
 * /taxonomy/name/{taxonomyName}, it has a list of taxonomies with the searched {taxonomyName}
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response format
 *
 * Created by lgonzales on 08/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement
@XmlType(name = "taxonomies", propOrder = {"taxonomies","errors","redirects","pageInfo"})
public class Taxonomies {

    private List<TaxonomyNode> taxonomies;

    private List<TaxonomiesError> errors;

    private List<TaxonomiesRedirect> redirects;

    private PageInformation pageInfo;

    public Taxonomies() {
    }

    public Taxonomies(List<TaxonomyNode> taxonomyNames) {
        this.taxonomies = taxonomyNames;
    }

    @XmlElement(name = "taxonomy")
    @XmlElementWrapper(name = "taxonomies")
    @JsonGetter(value = "taxonomies")
    public List<TaxonomyNode> getTaxonomies() {
        return taxonomies;
    }

    @JsonSetter(value = "taxonomies")
    public void setTaxonomies(
            List<TaxonomyNode> taxonomies) {
        this.taxonomies = taxonomies;
    }

    @XmlElement(name = "error")
    @XmlElementWrapper(name = "errors")
    @JsonGetter(value = "errors")
    public List<TaxonomiesError> getErrors() {
        return errors;
    }

    @JsonSetter(value = "errors")
    public void setErrors(List<TaxonomiesError> errors) {
        this.errors = errors;
    }

    @XmlElement(name = "redirect")
    @XmlElementWrapper(name = "redirects")
    @JsonGetter(value = "redirects")
    public List<TaxonomiesRedirect> getRedirects() {
        return redirects;
    }

    @JsonSetter(value = "redirects")
    public void setRedirects(List<TaxonomiesRedirect> redirects) {
        this.redirects = redirects;
    }

    @XmlElement
    public PageInformation getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInformation pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override public String toString() {
        return "Taxonomies{" +
                "taxonomies=" + taxonomies +
                ", errors=" + errors +
                ", redirects=" + redirects +
                ", pageInfo=" + pageInfo +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Taxonomies that = (Taxonomies) o;

        if (getTaxonomies() != null ? !getTaxonomies().equals(that.getTaxonomies()) : that.getTaxonomies() != null) {
            return false;
        }
        return getPageInfo() != null ? getPageInfo().equals(that.getPageInfo()) : that.getPageInfo() == null;

    }

    @Override public int hashCode() {
        int result = getTaxonomies() != null ? getTaxonomies().hashCode() : 0;
        result = 31 * result + (getPageInfo() != null ? getPageInfo().hashCode() : 0);
        return result;
    }
}
