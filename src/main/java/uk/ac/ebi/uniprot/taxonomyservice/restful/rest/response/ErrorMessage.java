package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class contains REST response body attributes when occur any error during Taxonomy request
 *
 * Created by lgonzales on 11/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyError")
public class ErrorMessage {

    private int internalCode;
    private String errorMessage;

    @XmlElement(required = true)
    public int getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(int internalCode) {
        this.internalCode = internalCode;
    }

    @XmlElement(required = true)
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override public String toString() {
        return "ErrorMessage{" +
                "internalCode=" + internalCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorMessage that = (ErrorMessage) o;

        if (getInternalCode() != that.getInternalCode()) {
            return false;
        }
        return getErrorMessage() != null ? getErrorMessage().equals(that.getErrorMessage()) :
                that.getErrorMessage() == null;

    }

    @Override public int hashCode() {
        int result = getInternalCode();
        result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
        return result;
    }
}
