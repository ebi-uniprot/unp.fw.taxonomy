package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsEnumValue;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.MinMaxRequiredDepthForBottomPath;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.*;

/**
 * This class contains request parameters for /taxonomy/path requests
 *
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
@MinMaxRequiredDepthForBottomPath(max = 5, message = DEPTH_PARAM_MIN_MAX, requiredMessage = DEPTH_PARAMETER_IS_REQUIRED)
public class PathRequestParams {

    @NotNull(message = ID_PARAMETER_IS_REQUIRED)
    @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
    @QueryParam(value = "id")
    @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
    private String id;

    @QueryParam(value = "depth")
    @ApiParam(value = TAXONOMY_DEPTH_PARAM, required = true)
    private Integer depth;

    @IsEnumValue(enumClass = PathDirections.class,message = DIRECTION_VALID_VALUES,
            ignoreCase = true)
    @NotNull(message = DIRECTION_PARAMETER_IS_REQUIRED)
    @QueryParam(value = "direction")
    @ApiParam(value = TAXONOMY_DIRECTION_PARAM, required = true)
    private String direction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getDirection() {
        return direction;
    }

    @JsonIgnore
    public PathDirections getPathDirection() {
        return PathDirections.valueOf(direction.toUpperCase());
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override public String toString() {
        return "PathRequestParams{" +
                "id=" + id +
                ", depth=" + depth +
                ", direction=" + direction +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PathRequestParams that = (PathRequestParams) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getDepth() != null ? !getDepth().equals(that.getDepth()) : that.getDepth() != null) {
            return false;
        }
        return getDirection() != null ? getDirection().equals(that.getDirection()) : that.getDirection() == null;

    }

    @Override public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDepth() != null ? getDepth().hashCode() : 0);
        result = 31 * result + (getDirection() != null ? getDirection().hashCode() : 0);
        return result;
    }
}
