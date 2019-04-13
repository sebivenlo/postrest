package nl.fontys.sebivenlo.postrest;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import static java.util.stream.Collectors.joining;
import javax.sql.DataSource;
import static nl.fontys.sebivenlo.postrest.QueryRegistry.QUERY_REGISTRY;

/**
 * Turns a normal query into a query returning a JSON result, by using a few
 * postgreSQL tricks.
 *
 * The static methods in this class will transform the query text, then
 * prepare it as a statement and execute it using the (optional parameters).
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public enum JSONQueryUtils {
    ; // no values

    /**
     * Queries with some presidentAllQuery string and produces a JSON result.
     * The result
     * will be one String containing a json string containing a JSON payload.
     * <p>
     * Implementation detail: This
     * method is postgreSQL specific, because it relies on postgreSQL specific
     * functions such as {@code array_to_json(anyarray)} and {@code array_agg(anyarray)}.
     *
     * The query parameter is wrapped as a string into a CTE (Common Table Expression or With clause),
     * whose result is wrapped in the said methods.
     * The result of the wrapped query will always be one JSON object (which in the end is just a string).
     *
     * In case an array type response is expected from the presidentAllQuery,
     * the database will always produce an array, even an empty one, which will look like "[]".
     *
     * When the jsontype parameter is JSONOBJECT, and the object is not found,
     * the response code should be 404 (not found). This is taken care of by the JSONType.handleNothing() method.
     *
     *
     * @param ds data source to use
     * @param query to send to the database
     * @param jtype to deal with the difference between array and object responses.
     * @param args optional parameters for the prepared statement.
     * @return a json string.
     * @throws java.sql.SQLException when the database is not available or is not able to process the query.
     */
    public static String queryToJsonString( DataSource ds, String query,
            JSONType jtype, Object... args ) throws SQLException {
        String result = "";
        final String qt = jtype
                .apply( query );
        System.out.println( "qt = " + qt );
        try ( final Connection connection = ds.getConnection();
                final PreparedStatement pst = connection.prepareStatement( qt ) ) {
            int cid = 1;
            for ( Object arg : args ) {
                pst.setObject( cid++, arg );
            }
            // try with resources to also release resultset properly
            try ( final ResultSet rs = pst.executeQuery() ) {
                if ( !rs.next() ) {
                    jtype.handleNothing();
                } else {
                    do {
                        result += rs.getString( 1 );
                    } while ( rs.next() );
                }
            }
            return result;
        }
    }

    /**
     * Create query text for update on any table.
     *
     * @param ds             data source to et the meta information.
     * @param tableName      to compute the query for
     * @param primaryColName the unique field to select one record, typically
     *                       the primary column
     *
     * @return the query text
     * @throws SQLException when database hick-up occurs.
     */
    public static String createUpdateQueryText( DataSource ds, String tableName, String primaryColName ) throws SQLException {
        List<String> upCols = QUERY_REGISTRY.getUpdatableColumns( ds, tableName );
        String cols = upCols
                .stream()
                .collect( joining( ", " ) );
        String queryText = format( QUERY_REGISTRY.getQuery( "update_query_template" ),
                primaryColName, tableName, tableName, cols, cols, primaryColName );
        return queryText;
    }
}
