package integrationtest;

import static integrationtest.TestDatasource.DS;
import io.restassured.RestAssured;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.joining;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Helper methods for tests.
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public abstract class RestTestBase {

    public static int PORT = 8080;
    public static String SERVER_URL = "http://localhost";
    public static String SERVICE_LOCATION = "/restserver-2018a/rest/v1.0/";

    @BeforeClass
    public static void getConfig() {
        Properties props = new Properties();
        try {
            props.load( new FileInputStream( "integrationtest.properties" ) );
            props.forEach( ( k, v ) -> System.out.println( k.toString() + " => " + v.toString() ) );
            PORT = Integer.valueOf( props.getProperty( "port", "8080" ) );
            SERVER_URL = props.getProperty( "base_url", SERVER_URL );
            SERVICE_LOCATION = props.getProperty( "service_location", SERVICE_LOCATION );
            System.out.println( "BASE_URL = " + SERVER_URL );
            System.out.println( "SERVICE_LOCATION = " + SERVICE_LOCATION );
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( RestTestBase.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( IOException ex ) {
            Logger.getLogger( RestTestBase.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    @Before
    public void setup() {
        RestAssured.port = PORT;
    }

    String serviceLocation( String tail ) {
        return SERVER_URL + ':' + PORT + SERVICE_LOCATION + tail + "/";
    }

    String readWholeFile( String fn ) {
        try {
            return Files.lines( Paths.get( fn ) )
                    .collect( joining( System.lineSeparator() ) );
        } catch ( IOException ex ) {
            Logger.getLogger( RestTestBase.class.getName() ).log( Level.SEVERE, null, ex );
            throw new RuntimeException( ex );
        }
    }

    void delete( String relName, String whereClause ) throws SQLException {
        String query = format( "delete from %s where %s", relName, whereClause );
        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( query ) ) {
            pst.execute();
            System.out.println( "delete " );
        }
    }

    void updateSomeTableColumn( String relName, String pkKey, String column, Object... pkVal ) throws SQLException {
        String query = format( "update %s set %s=? where %s=?", relName, column, pkKey );
        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( query ) ) {
            int col = 1;
            for ( Object val : pkVal ) {
                pst.setObject( col++, val );
            }
            pst.execute();
            System.out.println( "delete " );
        }
    }

    int tableSize( String tableName ) throws SQLException {
        String query = format( "select count(1) from %s", tableName );
        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( query ) ) {
            try ( ResultSet rs = pst.executeQuery(); ) {
                if ( rs.next() ) {
                    return rs.getInt( 1 );
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * Helper method to get a single value (which may be composite as in concatenated) from the
     * database using some query.
     * If the query returns more records, only the value found in the first record is returned.
     *
     * Because the double value from the database cannot be simply converted to double, use the type
     * converter method {@link RestTestBase#convertType(java.lang.Class, java.sql.ResultSet)}
     *
     * Any exceptions caught should be wrapped in a Runtime exception to not confuse the test
     * framework if
     * the test are not yet complete (such as in this exam).
     *
     * @param <T>    type to get
     * @param query  text
     * @param type   type of expected return type
     * @param params the parameters to the prepared statement
     *
     * @return the value retrieved or null if none is found
     * @throws SQLException when that database is angry
     */
    <T> T getSomeDBValue( String query, Class<? extends T> type, Object... params ) {

        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( query ) ) {
            int pnum = 1;
            for ( Object param : params ) {
                pst.setObject( pnum++, param );
            }
            try ( ResultSet rs = pst.executeQuery(); ) {
                if ( rs.next() ) {
                    return type.cast( rs.getObject( 1, type ) );
                } else {
                    return null;
                }
            }
        } catch ( SQLException ex ) {
            Logger.getLogger( RestTestBase.class.getName() ).log( Level.SEVERE, null, ex );
            throw new RuntimeException( ex );
        }
    }
}
