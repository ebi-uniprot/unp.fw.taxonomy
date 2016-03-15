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

    private String errorMessage;

    @XmlElement(required = true)
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override public String toString() {
        return "ErrorMessage{" +
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

        return getErrorMessage() != null ? getErrorMessage().equals(that.getErrorMessage()) :
                that.getErrorMessage() == null;

    }

    @Override public int hashCode() {
        return (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
    }
}
