package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents redirect REST response body information in a id list search
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response format
 *
 * Created by lgonzales on 22/09/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "taxonomiesRedirect", propOrder = {"requestedId","redirectLocation"})
public class TaxonomiesRedirect {

    private long requestedId;

    private String redirectLocation;

    @XmlElement(required = true)
    public long getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(long requestedId) {
        this.requestedId = requestedId;
    }

    @XmlElement(required = true)
    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomiesRedirect that = (TaxonomiesRedirect) o;

        if (getRequestedId() != that.getRequestedId()) {
            return false;
        }
        return getRedirectLocation() != null ? getRedirectLocation().equals(that.getRedirectLocation()) :
                that.getRedirectLocation() == null;

    }

    @Override public int hashCode() {
        int result = (int) (getRequestedId() ^ (getRequestedId() >>> 32));
        result = 31 * result + (getRedirectLocation() != null ? getRedirectLocation().hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "TaxonomiesRedirect{" +
                "requestedId=" + requestedId +
                ", redirectLocation='" + redirectLocation + '\'' +
                '}';
    }
}
