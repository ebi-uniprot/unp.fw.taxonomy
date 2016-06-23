package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents page metadata information in a search result content
 *
 * Created by lgonzales on 21/06/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "pageInfo", propOrder = {"resultsPerPage","currentPage","totalRecords"})
public class PageInformation {

    private int resultsPerPage;

    private int currentPage;

    private int totalRecords;

    @XmlElement(required = true)
    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    @XmlElement(required = true)
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @XmlElement(required = true)
    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Override public String toString() {
        return "PageInformation{" +
                "resultsPerPage=" + resultsPerPage +
                ", currentPage=" + currentPage +
                ", totalRecords=" + totalRecords +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PageInformation that = (PageInformation) o;

        if (getResultsPerPage() != that.getResultsPerPage()) {
            return false;
        }
        if (getCurrentPage() != that.getCurrentPage()) {
            return false;
        }
        return getTotalRecords() == that.getTotalRecords();

    }

    @Override public int hashCode() {
        int result = getResultsPerPage();
        result = 31 * result + getCurrentPage();
        result = 31 * result + getTotalRecords();
        return result;
    }
}
