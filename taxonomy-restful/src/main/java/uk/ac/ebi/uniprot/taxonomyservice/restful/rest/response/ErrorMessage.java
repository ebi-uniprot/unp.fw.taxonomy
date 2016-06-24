package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class contains REST response body attributes when occur any error during Taxonomy request
 *
 * Created by lgonzales on 11/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement
@XmlType(name = "taxonomyError", propOrder = {"requestedURL","errorMessages"})
public class ErrorMessage {

    private String requestedURL;
    private List<String> errorMessages;

    @XmlElement
    public String getRequestedURL() {
        return requestedURL;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    @XmlElement(name = "errorMessage")
    @XmlElementWrapper(name = "errorMessages")
    @JsonGetter(value = "errorMessage")
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String errorMessage){
        if(errorMessages == null){
            errorMessages = new ArrayList<>();
        }
        errorMessages.add(errorMessage);
    }

    @Override public String toString() {
        return "ErrorMessage{" +
                "requestedURL='" + requestedURL + '\'' +
                ", errorMessages=" + errorMessages +
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

        if (getRequestedURL() != null ? !getRequestedURL().equals(that.getRequestedURL()) :
                that.getRequestedURL() != null) {
            return false;
        }
        return getErrorMessages() != null ? getErrorMessages().equals(that.getErrorMessages()) :
                that.getErrorMessages() == null;

    }

    @Override public int hashCode() {
        int result = getRequestedURL() != null ? getRequestedURL().hashCode() : 0;
        result = 31 * result + (getErrorMessages() != null ? getErrorMessages().hashCode() : 0);
        return result;
    }
}
