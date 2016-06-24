package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode.*;

/**
 * This class represents a Taxonomy Element(node) in the taxonomy Tree.
 *
 * It contains annotations that will be used by Jackson Parser to build XML or JSON response
 *
 * Created by lgonzales on 19/02/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlType(name = "taxonomyNode", propOrder = {TAXONOMY_ID,MNEMONIC,SCIENTIFIC_NAME,COMMON_NAME,SYNONYM,RANK,
        PARENT,PARENT_LINK,CHILDREN,CHILDREN_LINKS,SIBLINGS,SIBLINGS_LINKS})
public class TaxonomyNode implements Comparable<TaxonomyNode>{

    public static final String TAXONOMY_ID = "taxonomyId";
    public static final String MNEMONIC = "mnemonic";
    public static final String SCIENTIFIC_NAME = "scientificName";
    public static final String COMMON_NAME = "commonName";
    public static final String SYNONYM = "synonym";
    public static final String RANK = "rank";
    public static final String PARENT = "parent";
    public static final String PARENT_LINK = "parentLink";
    public static final String CHILDREN = "children";
    public static final String CHILDREN_LINKS = "childrenLinks";
    public static final String SIBLINGS = "siblings";
    public static final String SIBLINGS_LINKS = "siblingsLinks";

    private long taxonomyId;
    private String mnemonic;
    private String scientificName;
    private String commonName;
    private String synonym;
    private String rank;

    private TaxonomyNode parent;
    private String parentLink;

    private List<TaxonomyNode> children;
    private List<String> childrenLinks;

    private List<TaxonomyNode> siblings;
    private List<String> siblingsLinks;

    @XmlElement(required = true)
    public long getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(long taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @XmlElement(required = true)
    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @XmlElement
    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    @XmlElement
    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @XmlElement
    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    @XmlElement
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @XmlElement
    public TaxonomyNode getParent() {
        return parent;
    }

    public void setParent(TaxonomyNode parent) {
        this.parent = parent;
    }

    public TaxonomyNode mergeParent(TaxonomyNode parent){
        if(this.parent == null){
            this.parent = parent;
        }
        return this.parent;
    }

    @XmlElement
    public String getParentLink() {
        return parentLink;
    }

    public void setParentLink(String parentLink) {
        this.parentLink = parentLink;
    }

    @XmlElement(name = "child")
    @XmlElementWrapper(name = CHILDREN)
    @JsonGetter(value = CHILDREN)
    public List<TaxonomyNode> getChildren() {
        return children;
    }

    @JsonSetter(value = "children")
    public void setChildren(List<TaxonomyNode> children) {
        this.children = children;
    }

    public TaxonomyNode mergeChildren(TaxonomyNode children){
        TaxonomyNode result = children;
        if(this.children == null){
            this.children = new ArrayList<>();
            this.children.add(children);
        }else{
            if(this.children.contains(children)){
                result = this.children.get(this.children.indexOf(children));
            }else{
                this.children.add(children);
            }
        }
        return result;
    }

    @XmlElement(name = "childLink")
    @XmlElementWrapper(name = CHILDREN_LINKS)
    @JsonGetter(value = CHILDREN_LINKS)
    public List<String> getChildrenLinks() {
        return childrenLinks;
    }

    @JsonSetter(value = CHILDREN_LINKS)
    public void setChildrenLinks(List<String> childrenLinks) {
        this.childrenLinks = childrenLinks;
    }

    @XmlElement(name = "sibling")
    @XmlElementWrapper(name = SIBLINGS)
    @JsonGetter(value = SIBLINGS)
    public List<TaxonomyNode> getSiblings() {
        return siblings;
    }

    @JsonSetter(value = SIBLINGS)
    public void setSiblings(List<TaxonomyNode> siblings) {
        this.siblings = siblings;
    }


    public TaxonomyNode mergeSiblings(TaxonomyNode sibling){
        TaxonomyNode result = sibling;
        if(this.siblings == null){
            this.siblings = new ArrayList<>();
            this.siblings.add(sibling);
        }else{
            if(this.siblings.contains(sibling)){
                result = this.siblings.get(this.siblings.indexOf(sibling));
            }else{
                this.siblings.add(sibling);
            }
        }
        return result;
    }

    @XmlElement(name = "siblingLinks")
    @XmlElementWrapper(name = SIBLINGS_LINKS)
    @JsonGetter(value = SIBLINGS_LINKS)
    public List<String> getSiblingsLinks() {
        return siblingsLinks;
    }

    @JsonSetter(value = SIBLINGS_LINKS)
    public void setSiblingsLinks(List<String> siblingsLinks) {
        this.siblingsLinks = siblingsLinks;
    }

    @Override public String toString() {
        return "TaxonomyNode{" +
                "taxonomyId=" + taxonomyId +
                ", mnemonic='" + mnemonic + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", synonym='" + synonym + '\'' +
                ", rank='" + rank + '\'' +
                ", parent=" + parent +
                ", parentLink='" + parentLink + '\'' +
                ", children=" + children +
                ", childrenLinks=" + childrenLinks +
                ", siblings=" + siblings +
                ", siblingsLinks=" + siblingsLinks +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaxonomyNode that = (TaxonomyNode) o;

        if (getTaxonomyId() != that.getTaxonomyId()) {
            return false;
        }else{
            return true;
        }
    }

    @Override public int hashCode() {
        int result = (int) (getTaxonomyId() ^ (getTaxonomyId() >>> 32));
        return result;
    }

    @Override
    public int compareTo(TaxonomyNode that) {
        if(this.equals(that)){
            return 0;
        }else{
            if(this.getTaxonomyId() < that.getTaxonomyId()){
                return -1;
            }else{
                return 1;
            }
        }
    }
}
