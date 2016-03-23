package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains REST response body attributes when occur any error during Taxonomy request
 *
 * Created by lgonzales on 11/03/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyRoot")
@XmlType(namespace = "http://www.ebi.ac.uk/uniprot/services/docs/xsd/taxonomyError")
public class ErrorMessage {

    public static final Logger logger = LoggerFactory.getLogger(ErrorMessage.class);

    private String requestedURL;
    private List<String> errorMessages;

    @XmlElement
    public String getRequestedURL() {
        return requestedURL;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public void setRequestedURL(String requestURL,String queryString) {
        if (queryString != null && !queryString.isEmpty()) {
            try {
                requestURL+= "?"+(URLEncoder.encode(queryString,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error encoding ErrorMessage.requestedURL: ",e);
            }
        }
        this.requestedURL = requestURL.toString();
    }

    @XmlElement(name = "errorMessage")
    @XmlElementWrapper(name = "errorMessages")
    @JsonGetter(value = "errorMessages")
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
