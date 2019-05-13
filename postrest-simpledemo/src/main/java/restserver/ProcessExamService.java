package restserver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static nl.fontys.sebivenlo.postrest.JSONQueryUtils.queryToJsonString;
import static nl.fontys.sebivenlo.postrest.JSONType.JSONPASSTHOUGH;
import static nl.fontys.sebivenlo.postrest.QueryRegistry.QUERY_REGISTRY;

/**
 * Take a JSon document and send it to the database where it will be taken apart
 * by a sql query using advanced postgreSQL features. The data is committed to
 * the relevant tables, such as the exam_events table and the exam_results
 * table. This 'middle-ware' just retrieves the proper query from the query
 * registry, combines it with the json document in a prepared query and send it
 * to the database. The database responds with a comprehensive view of the just
 * inserted data. The columns in that view are the same as that in the view used
 * by the comprehensive results service.
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
@Stateless
@Path("processexam")
public class ProcessExamService {

    @Resource(lookup = "jdbc/fantys")
    DataSource ds;

    //<editor-fold defaultstate="expanded" desc="TASK_4C_PRC2; __STUDENT_ID__; WEIGHT 40;">
    /**
     * Take a JSon document and send it to the database where it will be taken
     * apart by a sql query using advanced postgreSQL features. The data is
     * committed to the relevant tables, such as the exam_events table and the
     * exam_results table.
     *
     * Note that the query used already produces the required json array, so no
     * further wrapping by the JSONTYpe is needed.
     *
     * @param json document to insert
     * @param uriInfo from the server
     *
     * @return a comprehensive view of the data
     */
    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response processExamDocument(String json, @Context UriInfo uriInfo) {
        String queryText = QUERY_REGISTRY.getQuery("processresults");
        String result = "";
        //Start Solution::replacewith:://TODO 1C_PRC2 implement processExamDocument
        try {
            result = queryToJsonString(ds, queryText, JSONPASSTHOUGH, json);
        } catch (SQLException ex) {
            Logger.getLogger(ProcessExamService.class.getName()).log(
                    Level.SEVERE, null, ex);
            return Response.notModified().build();
        }
        return Response.ok(result).build();
        //End Solution::replacewith::return null;
    }
    //</editor-fold>
}
