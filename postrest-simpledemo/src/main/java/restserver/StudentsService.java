package restserver;

import nl.fontys.sebivenlo.postrest.AbstractPostRestService;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Stateless
@Path( "students" )
public class StudentsService extends FantysCrudService {

    @Override
    protected String getRelName() {
        return "students";
    }

}
