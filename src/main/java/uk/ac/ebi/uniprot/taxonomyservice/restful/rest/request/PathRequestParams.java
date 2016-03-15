package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request;

import io.swagger.annotations.ApiParam;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * Created by lgonzales on 10/03/16.
 */
@RequestScoped
public class PathRequestParams {

    @NotNull
    @QueryParam(value = "id")
    @ApiParam(value = "id", required = true)
    private long id;

    @NotNull
    @QueryParam(value = "depth")
    @ApiParam(value = "depth", required = true)
    private int depth;

    @NotNull
    @QueryParam(value = "direction")
    @ApiParam(value = "direction", required = true)
    private PathDirections direction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public PathDirections getDirection() {
        return direction;
    }

    public void setDirection(PathDirections direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        try {
            this.direction = PathDirections.valueOf(direction);
        }catch (Exception e){
            throw new BadRequestException("direction parameter must be "+ PathDirections.TOP+" or " +
                    ""+ PathDirections.BOTTOM);
        }
    }

}
