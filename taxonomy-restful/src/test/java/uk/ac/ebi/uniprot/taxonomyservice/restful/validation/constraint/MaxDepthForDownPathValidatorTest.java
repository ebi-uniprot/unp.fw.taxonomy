package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lgonzales on 08/07/16.
 */
public class MaxDepthForDownPathValidatorTest {

    @Test
    public void isValidWithBottomValidSizeReturnTrue() throws Exception {
        MockedMaxDepthForDownPathValidator validator = new MockedMaxDepthForDownPathValidator(4);
        PathRequestParams param = getPathRequestParams(4,PathDirections.BOTTOM);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithBottomInvalidSizeReturnFalse() throws Exception {
        MockedMaxDepthForDownPathValidator validator = new MockedMaxDepthForDownPathValidator(4);
        PathRequestParams param = getPathRequestParams(5,PathDirections.BOTTOM);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(false));
    }


    @Test
    public void isValidWithTopValidSizeReturnTrue() throws Exception {
        MockedMaxDepthForDownPathValidator validator = new MockedMaxDepthForDownPathValidator(4);
        PathRequestParams param = getPathRequestParams(6,PathDirections.TOP);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(true));
    }


    @Test
    public void isValidWithNullDirectionValidSizeReturnTrue() throws Exception {
        MockedMaxDepthForDownPathValidator validator = new MockedMaxDepthForDownPathValidator(4);
        PathRequestParams param = getPathRequestParams(6,null);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(true));
    }

    private PathRequestParams getPathRequestParams(Integer depth, PathDirections direction){
        PathRequestParams pathRequestParams = new PathRequestParams();
        if(direction != null) {
            pathRequestParams.setDirection(direction.toString());
        }
        pathRequestParams.setDepth(depth);
        pathRequestParams.setId("");
        return pathRequestParams;
    }


    private class MockedMaxDepthForDownPathValidator extends MaxDepthForDownPath.MaxDepthForDownPathValidator{

        public MockedMaxDepthForDownPathValidator(int max){
            MaxDepthForDownPath maxDepthForDownPath = new MaxDepthForDownPath() {

                @Override public int max() {
                    return max;
                }

                @Override public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override public String message() {
                    return null;
                }

                @Override public Class<?>[] groups() {
                    return null;
                }

                @Override public Class<? extends Payload>[] payload() {
                    return null;
                }
            };
            initialize(maxDepthForDownPath);
        }
    }

}