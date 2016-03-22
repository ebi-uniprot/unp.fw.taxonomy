package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsEnumValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * This class contains request parameters for /taxonomy/path requests
 *
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
public class PathRequestParams {


    @NotNull(message = "id parameter is required")
    @QueryParam(value = "id")
    @ApiParam(value = "id", required = true)
    private Long id;

    @NotNull(message = "depth parameter is required")
    @Min(value = 1, message = "depth param value must be between 1 and 5")
    @Max(value = 5, message = "depth param value must be between 1 and 5")
    @QueryParam(value = "depth")
    @ApiParam(value = "depth", required = true)
    private Integer depth;

    @IsEnumValue(enumClass = PathDirections.class,message = "direction parameter value must be top or bottom",
            ignoreCase = true)
    @NotNull(message = "direction parameter is required")
    @QueryParam(value = "direction")
    @ApiParam(value = "direction", required = true)
    private String direction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return getDirection() == that.getDirection();

    }

    @Override public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDepth() != null ? getDepth().hashCode() : 0);
        result = 31 * result + (getDirection() != null ? getDirection().hashCode() : 0);
        return result;
    }
}
