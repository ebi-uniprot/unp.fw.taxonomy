package uk.ac.ebi.uniprot.taxonomyservice.restful.domain;

import uk.ac.ebi.uniprot.taxonomyservice.restful.util.BeanCreatorUtil;

import java.util.Collections;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * TaxonomyNodeTest is responsible to make sure that mergeFunctions are working fine in TaxonomyNode Class
 *
 * Created by lgonzales on 22/04/16.
 */
public class TaxonomyNodeTest {

    @Test
    public void assertMergeChildren() throws Exception {
        TaxonomyNode parentNode = BeanCreatorUtil.getTaxonomyNode(1);
        TaxonomyNode childNode2 = BeanCreatorUtil.getTaxonomyNode(2);
        TaxonomyNode childNode3 = BeanCreatorUtil.getTaxonomyNode(3);
        TaxonomyNode childNode4 = BeanCreatorUtil.getTaxonomyNode(4);

        parentNode.mergeChildren(childNode2);
        parentNode.mergeChildren(childNode2);
        assertThat(parentNode.getChildren().size(),is(1));

        parentNode.mergeChildren(childNode3);
        parentNode.mergeChildren(childNode3);
        assertThat(parentNode.getChildren().size(),is(2));

        parentNode.mergeChildren(childNode4);
        parentNode.mergeChildren(childNode4);
        assertThat(parentNode.getChildren().size(),is(3));

        Collections.sort(parentNode.getChildren());
        assertThat(parentNode.getChildren().get(0),equalTo(childNode2));
        assertThat(parentNode.getChildren().get(1),equalTo(childNode3));
        assertThat(parentNode.getChildren().get(2),equalTo(childNode4));
    }

    @Test
    public void assertMergeSiblings() throws Exception {
        TaxonomyNode parentNode = BeanCreatorUtil.getTaxonomyNode(1);
        TaxonomyNode siblingNode2 = BeanCreatorUtil.getTaxonomyNode(2);
        TaxonomyNode siblingNode3 = BeanCreatorUtil.getTaxonomyNode(3);
        TaxonomyNode siblingNode4 = BeanCreatorUtil.getTaxonomyNode(4);

        parentNode.mergeSiblings(siblingNode2);
        parentNode.mergeSiblings(siblingNode2);
        assertThat(parentNode.getSiblings().size(),is(1));

        parentNode.mergeSiblings(siblingNode3);
        parentNode.mergeSiblings(siblingNode3);
        assertThat(parentNode.getSiblings().size(),is(2));

        parentNode.mergeSiblings(siblingNode4);
        parentNode.mergeSiblings(siblingNode4);
        assertThat(parentNode.getSiblings().size(),is(3));

        Collections.sort(parentNode.getSiblings());
        assertThat(parentNode.getSiblings().get(0),equalTo(siblingNode2));
        assertThat(parentNode.getSiblings().get(1),equalTo(siblingNode3));
        assertThat(parentNode.getSiblings().get(2),equalTo(siblingNode4));
    }

    @Test
    public void assertMergeParent() throws Exception {
        TaxonomyNode node = BeanCreatorUtil.getTaxonomyNode(1);

        TaxonomyNode parent1 = BeanCreatorUtil.getTaxonomyNode(2);
        TaxonomyNode parent2 = BeanCreatorUtil.getTaxonomyNode(3);
        node.mergeParent(parent1);
        assertThat(node.getParent(),notNullValue());
        assertThat(node.getParent(),equalTo(parent1));

        node.mergeParent(parent2);
        assertThat(node.getParent(),equalTo(parent1));
    }

}