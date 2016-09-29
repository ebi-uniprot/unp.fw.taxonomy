package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents error message REST response body information in a id list search
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response format
 *
 * Created by lgonzales on 22/09/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "taxonomiesError", propOrder = {"requestedId","errorMessage"})
public class TaxonomiesError {

    private long requestedId;

    private String errorMessage;

    @XmlElement(required = true)
    public long getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(long requestedId) {
        this.requestedId = requestedId;
    }

    @XmlElement(required = true)
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomiesError that = (TaxonomiesError) o;

        if (getRequestedId() != that.getRequestedId()) {
            return false;
        }
        return getErrorMessage() != null ? getErrorMessage().equals(that.getErrorMessage()) :
                that.getErrorMessage() == null;

    }

    @Override public int hashCode() {
        int result = (int) (getRequestedId() ^ (getRequestedId() >>> 32));
        result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "TaxonomiesError{" +
                "requestedId=" + requestedId +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
