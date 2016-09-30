package uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class is used to unit test MinMaxDepthForBottomPathValidator methods
 *
 * Created by lgonzales on 08/07/16.
 */
public class MinMaxDepthForBottomPathValidatorTest {

    @Test
    public void isValidWithBottomValidSizeReturnTrue() {
        MockedMinMaxDepthForBottomPathValidator validator = new MockedMinMaxDepthForBottomPathValidator(1,4);
        PathRequestParams param = getPathRequestParams(4,PathDirections.BOTTOM);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(true));
    }

    @Test
    public void isValidWithBottomInvalidSizeReturnFalse() {
        MockedMinMaxDepthForBottomPathValidator validator = new MockedMinMaxDepthForBottomPathValidator(1,4);
        PathRequestParams param = getPathRequestParams(5,PathDirections.BOTTOM);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithBottomInMinValidSizeReturnFalse() {
        MockedMinMaxDepthForBottomPathValidator validator = new MockedMinMaxDepthForBottomPathValidator(0,4);
        PathRequestParams param = getPathRequestParams(6,PathDirections.BOTTOM);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(false));
    }

    @Test
    public void isValidWithTopValidSizeReturnTrue() {
        MockedMinMaxDepthForBottomPathValidator validator = new MockedMinMaxDepthForBottomPathValidator(1,4);
        PathRequestParams param = getPathRequestParams(6,PathDirections.TOP);
        boolean result = validator.isValid(param,null);
        assertThat(result, is(true));
    }


    @Test
    public void isValidWithNullDirectionValidSizeReturnTrue() {
        MockedMinMaxDepthForBottomPathValidator validator = new MockedMinMaxDepthForBottomPathValidator(1,4);
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


    private class MockedMinMaxDepthForBottomPathValidator extends
                                                     MinMaxRequiredDepthForBottomPath.MinMaxDepthForBottomPathValidator {

        public MockedMinMaxDepthForBottomPathValidator(int min, int max){
            MinMaxRequiredDepthForBottomPath maxDepthForDownPath = new MinMaxRequiredDepthForBottomPath() {
                @Override public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override public String message() {return null;}

                @Override public int max() {return max;}

                @Override public int min() {return min;}

                @Override public String requiredMessage() {return "requiredMessage";}

                @Override public Class<?>[] groups() {return new Class<?>[0];}

                @Override public Class<? extends Payload>[] payload() {return null;}
            };
            initialize(maxDepthForDownPath);
        }
    }

}