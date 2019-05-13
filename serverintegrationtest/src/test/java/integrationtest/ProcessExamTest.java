package integrationtest;

import static org.junit.Assert.*;
import static io.restassured.RestAssured.given;
import java.sql.SQLException;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public class ProcessExamTest extends RestTestBase {

    String uri = serviceLocation( "processexam" );

    /**
     * Send the content of the file examresults.json to the server and expect it to be accepted and
     * that the result contains 39 records.
     * Steps:
     * <ul>
     * <li>Specify content type in header ("Content-Type", "application/json").</li>
     * <li>Set json String as body.</li>
     * <li>Post uri.</li>
     * <li>Check status code (200) and size (39), note that address of received json body is an
     * empty string.</li>
     * <li>Make sure that the data arrived in the data base by using the helper method
     * getSomeDBValue,
     * verifying that the grade for event_name "DVP" for student 3640472 is 7.8.</li>
     *
     */
    @Test
    public void testProcessExam() throws SQLException {
        delete( "exam_events", "event_name='DVP'" );
        String examResultsDocument = readWholeFile( "examresults.json" );
        String checkGradeQuery = "select cast(grade as float) from comprehensive_exam_results "
                + "where event_name=? and snummer=?";
        given()
                .header( "Content-Type", "application/json" )
                .body( examResultsDocument )
                .when()
                .post( uri )
                .then()
                .assertThat()
                .statusCode( 200 )
                .body( "", hasSize( 39 ) );

        Double actualgrade = this.getSomeDBValue( checkGradeQuery, Double.class, "DVP", 3640472 );
        assertEquals( "grade", 7.8, actualgrade, 0.0001 );
    }

    @After
    public void cleanup() throws SQLException {
        delete( "exam_events", "event_name='DVP'" );
    }
}
