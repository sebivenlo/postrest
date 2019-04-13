package restserver;

import nl.fontys.sebivenlo.postrest.AbstractPostRestService;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
@Stateless
@Path( "courses" )
public class CoursesService extends FantysCrudService {

    @Override
    public String getRelName() {
        return "courses";
    }

}
