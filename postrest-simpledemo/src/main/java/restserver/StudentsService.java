package restserver;

import nl.fontys.sebivenlo.postrest.AbstractPostRestService;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
@Stateless
@Path( "students" )
public class StudentsService extends FantysCrudService {

    @Override
    protected String getRelName() {
        return "students";
    }

}
