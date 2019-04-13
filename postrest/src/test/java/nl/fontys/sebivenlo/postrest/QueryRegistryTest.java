package nl.fontys.sebivenlo.postrest;

import nl.fontys.sebivenlo.postrest.QueryRegistry;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Test;
import static org.junit.Assert.*;
import static nl.fontys.sebivenlo.postrest.QueryRegistry.QUERY_REGISTRY;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class QueryRegistryTest {

    /**
     * Test of values method, of class QueryRegistry.
     */
    @Test
    public void testValues() {
        System.out.println( "values" );
        QueryRegistry[] expResult = { QUERY_REGISTRY };
        QueryRegistry[] result = QueryRegistry.values();
        assertArrayEquals( expResult, result );
    }

    /**
     * Test of valueOf method, of class QueryRegistry.
     */
    @Test
    public void testValueOf() {
        System.out.println( "valueOf" );
        String name = "QUERY_REGISTRY";
        QueryRegistry result = QueryRegistry.valueOf( name );
        assertSame( instance, result );
    }

    /**
     * Test of getQuery method, of class QueryRegistry.
     */
    @Test
    public void testGetQuery() {
        System.out.println( "getQuery" );
        String qName = "newentity";
        String result = instance.getQuery( qName );
        assertThat( result, containsString( "jsonb_populate_record" ) );
    }

    QueryRegistry instance = QUERY_REGISTRY;

    /**
     * Test of getUpdatableColumns method, of class QueryRegistry.
     *
     * @throws java.lang.Exception for any reason defined in the api
     */
    @Test
    public void testGetUpdatableColumns() throws Exception {
        System.out.println( "getUpdatableColumns" );
        DataSource ds = TestDatasource.DS;
        String tableName = "students";
        List<String> result = instance.getUpdatableColumns( ds, tableName );
        assertEquals( 8, result.size() );
        System.out.println( "updateablecolumns=" + result );
    }

    /**
     * Test of getUpdatableColumns method, of class QueryRegistry.
     *
     * @throws java.sql.SQLException for any reason defined in the api
     */
    @Test
    public void testGetPrimaryColumns() throws SQLException {
        System.out.println( "getPrimaryColumns" );
        DataSource ds = TestDatasource.DS;
        String tableName = "courses";
        List<String> result = instance.getPrimaryColumns( ds, tableName );
        assertEquals( 1, result.size() );
        System.out.println( "primary colums=" + result );
        assertEquals( "course_id", result.get( 0 ) );
    }

    /**
     * Test of getUpdatableColumns method, of class QueryRegistry.
     *
     * @throws java.sql.SQLException for any reason defined in the api
     */
    @Test
    public void testGetAllColumns() throws SQLException {
        System.out.println( "getAllColumns" );
        DataSource ds = TestDatasource.DS;
        String tableName = "students";
        List<String> result = instance.getAllColumns( ds, tableName );
        assertEquals( 9, result.size() );
        System.out.println( "primary colums=" + result );
    }

    @Test
    public void testGetDType() throws SQLException {
        System.out.println( "get data types" );
        DataSource ds = TestDatasource.DS;
        Map<String, String> r = instance.getTypeMap( ds, "students" );
        assertEquals( "character varying(4)", r.get( "student_class" ) );
        //fail( "Test not implemented" );
    }

    @Test
    public void testErrorLookup() {
        String expected = "protocol violation";
        String code = "08P01";
        long s = System.nanoTime();
        String actual = instance.lookupError( code );
        long e1 = System.nanoTime() - s;
        System.out.println( "e = " + e1 );
        assertEquals( expected, actual );
        s = System.nanoTime();
        actual = instance.lookupError( "01P01" );
        assertEquals( "deprecated feature", actual );
        long e2 = System.nanoTime() - s;
        System.out.println( "e = " + e2 );
        long speedup = e1 / e2;
        System.out.println( "speedup = " + speedup );
    }

}
