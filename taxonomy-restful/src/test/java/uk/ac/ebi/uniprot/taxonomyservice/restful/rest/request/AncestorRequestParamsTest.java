package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * This class is used to test AncestorRequestParams methods
 *
 * Created by lgonzales on 07/07/16.
 */
public class AncestorRequestParamsTest {

    @Test
    public void getIdListWithValidListReturnValidList() {
        AncestorRequestParams param = getAncestorRequestParam("1,2,3,4,5");

        List<Long> idList = param.getIdList();

        assertThat(idList, notNullValue());
        assertThat(idList.size(),is(5));
    }


    @Test
    public void getIdListWithEmptyIdReturn() {
        AncestorRequestParams param = getAncestorRequestParam("");

        List<Long> idList = param.getIdList();

        assertThat(idList, notNullValue());
        assertThat(idList.size(),is(0));
    }

    @Test
    public void getIdListWithNullIdReturn() {
        AncestorRequestParams param = getAncestorRequestParam(null);

        List<Long> idList = param.getIdList();

        assertThat(idList, notNullValue());
        assertThat(idList.size(),is(0));
    }


    private AncestorRequestParams getAncestorRequestParam(String ids){
        AncestorRequestParams param = new AncestorRequestParams();
        param.setIds(ids);
        return param;
    }

}