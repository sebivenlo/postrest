package restserver;

import nl.fontys.sebivenlo.postrest.AbstractPostRestService;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
@Stateless
@Path( "results" )
public class ComprehensiveResults extends FantysCrudService {

    @Override
    protected String getRelName() {
        return "comprehensive_exam_results";
    }

    /**
     * The relation comprehensive_exam_results is a view, so a primary column cannot be retrieved
     * from the meta data. Return the appropriate constant here.
     *
     * @return the name of the identifying column of a result.
     */
    @Override
    protected String getIdName() {
        return "exam_result_id";
    }
}
