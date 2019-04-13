package restserver;

import nl.fontys.sebivenlo.postrest.JSONQueryUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import static java.util.stream.Collectors.joining;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static restserver.DemoDatasource.DS;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class UpdateTest {

    @After
    public void cleanup() throws SQLException {
        runQuery( DS, "update students set lastname='Flowers' where snummer=3640457 returning *" );
    }

    @Test
    public void testUpdateQuery() throws SQLException, IOException {
        String queryText = JSONQueryUtils.createUpdateQueryText( DS, "uppi", "id" );
        runQuery( DS, "update uppi set naam='poppi' returning *" );
        System.out.println( "queryText = [" + queryText + "]" );
        String uppiUp = "{\"id\":1,\"naam\":\"duppi\"}";
        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( queryText ) ) {
            pst.setObject( 1, uppiUp );
            try (
                    ResultSet rs = pst.executeQuery() ) {
                while ( rs.next() ) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    for ( int i = 1; i <= columnCount; i++ ) {
                        Object val = rs.getObject( i );
                        System.out.println( "rs=" + val );
                        if ( i == 2 ) {
                            assertEquals( "duppi", ( String ) val );
                        }
                    }
                }
            }
        }
        //fail( "Test not implemented" );
    }

    @Test
    public void testUpdateStudent() throws SQLException, IOException {
        String queryText = JSONQueryUtils.createUpdateQueryText( DS, "students", "snummer" );
        runQuery( DS, "update students set lastname='Flowers' where snummer=3640457 returning *" );
        System.out.println( "queryText = [" + queryText + "]" );
        String json = readWholeFile( "updatestudentname.json" );
        try ( Connection con = DS.getConnection();
                PreparedStatement pst = con.prepareStatement( queryText ) ) {
            pst.setObject( 1, json );
            try (
                    ResultSet rs = pst.executeQuery() ) {
                while ( rs.next() ) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String lastname = rs.getString( "lastname" );
                    assertEquals( "Elder-Flowers", lastname );
                    for ( int i = 1; i <= columnCount; i++ ) {
                        Object val = rs.getObject( i );
                        String columnName = metaData.getColumnName( i );
                        System.out.println( columnName + "=" + val );
                    }
                }
            }
        }
        // fail( "Test not implemented" );
    }

    /**
     * Read file from file system.
     *
     * @param fn filename to read
     *
     * @return the content of the file
     * @throws IOException
     */
    String readWholeFile( String fn ) throws IOException {
        return Files.lines( Paths.get( fn ) )
                .collect( joining( System.lineSeparator() ) );
    }

    private void runQuery( DataSource ds, String query ) throws SQLException {
        try ( Connection con = ds.getConnection();
                PreparedStatement p = con.prepareStatement( query ) ) {
            p.executeQuery();
        }
    }
}
