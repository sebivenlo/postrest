package integrationtest;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import java.sql.SQLException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
public class StudentsTest extends RestTestBase {

    String uri = serviceLocation( "students" );

    /**
     * Test getEntityById. Steps:
     * <ul>
     * <li>Specify content type in header ("Content-Type",
     * "application/json").</li>
     * <li>Get a Student at the uri, using the id 3690804</li>
     * <li>Verify Content type in header</li>
     * <li>Check status code (200)</li>
     * <li>Check that body contains lastname "Zeno" and first name
     * "Berneice".</li>
     *
     */
    @Test
    public void test2GetEntityById() {
        given().header( "Content-Type", "application/json" ).
                when()
                .get( uri + 3690804 )
                .then()
                .header( "Content-Type", "application/json" )
                .statusCode( 200 )
                .header( "Content-Type", "application/json" )
                .body( "lastname", equalTo( "Zeno" ) )
                .body( "firstname", equalTo( "Berneice" ) );
    }

    // <editor-fold defaultstate="collapsed" desc="NO EXAM WORK HERE">
    //Start Solution::replacewith::
    @Test
    public void test1GetEntities() {
        given().header( "Content-Type", "application/json" ).
                when()
                .get( uri )
                .then()
                .statusCode( 200 )
                .header( "Content-Type", "application/json" )
                .body( "", hasSize( 71 ) );
    }

    private static String TEST_EMAIL = "dirkofdanmark@student.olifantys.nl";

    @Test
    public void test3AddStudent() throws IOException, SQLException {
        delete( "students", "email='dirkofdanmark@student.olifantys.nl'" );
        int tableSize = tableSize( "students" );
        String json = readWholeFile( "newstudent.json" );
        given()
                .header( "Content-Type", "application/json" )
                .body( json )
                .when()
                .post( uri ).
                then().
                assertThat().
                statusCode( 200 ).
                body( "email", equalTo( TEST_EMAIL ) );
        assertEquals( tableSize + 1, tableSize( "students" ) );
    }

    @Test
    public void test4UpdateLastName() throws SQLException, IOException {
        String json = readWholeFile( "updatestudentname.json" );
        given()
                .header( "Content-Type", "application/json" )
                .body( json )
                .when()
                .put( uri ).
                then().
                assertThat().
                statusCode( 200 ).
                body( "lastname", equalTo( "Elder-Flowers" ) );
    }

    @Test
    public void test5DeleteStudent() throws SQLException {
        int addedStudent = 0;
        final String queryText = "select snummer from students where email=?";
        addedStudent = getSomeDBValue( queryText, Integer.class, TEST_EMAIL );
        String u = uri + addedStudent;
        System.out.println( "u = " + u );
        given()
                .header( "Content-Type", "application/json" )
                .when()
                .delete( u )
                .then()
                .assertThat()
                .statusCode( 200 )
                .body( "firstname", equalTo( "Dirk" ) );
        //fail( "Test not implemented" );
    }
}
