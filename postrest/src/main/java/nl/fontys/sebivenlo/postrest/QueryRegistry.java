package nl.fontys.sebivenlo.postrest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import javax.sql.DataSource;

/**
 * This class is a singleton, providing lookup and caching for database meta-data
 * and sql query files.
 *
 * This way queries can be externalised for the java source code files
 * and avoid the ugly format that results from having to mix various quoting styles and line endings
 * between java and sql. The query files can be found in the source folder
 * {@code src/main/resources/sql}.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum QueryRegistry {
    QUERY_REGISTRY;

    /**
     * caches.
     */
    private final Map<String, String> lookup = new ConcurrentHashMap<>();
    private final Map<String, Meta> updateableColumns
            = new ConcurrentHashMap<>();
    private final Map<String, String> errorCodes = new HashMap<>();

    /**
     * Retrieve a named query from the resources. The convention is that the query name is the file
     * base name without the .sql ending
     *
     * @param qName query name
     *
     * @return the string comprising the query.
     */
    public String getQuery( String qName ) {
        String query = lookup.computeIfAbsent( qName, this::loadQuery );
        getLogger().log( Level.INFO, "retrieved {0}={1}", new Object[]{ qName,
            query } );
        return query;
    }

    /**
     * Get metadata about a relation, either column or view.
     * The data for a table is fetched from the database on first request and cached. Second and
     * further retrievals are returned from a cache.
     *
     * @param ds        datasource to reach the database
     * @param tableName t retrieve the meta data for
     *
     * @return meta data for the tableName.
     * @throws SQLException when the database does not like or cannot answer what we ask.
     */
    private Meta getColumnMeta( final DataSource ds,
            final String tableName )
            throws SQLException {
        return updateableColumns.computeIfAbsent( tableName, ( t )
                -> loadColumnMetaData( ds,
                        t ) );
    }

    /**
     * Get the columns that are updatable in a relation, either column or view.
     *
     * @param ds        datasource to reach the database
     * @param tableName t retrieve the meta data for
     *
     * @return list of column names
     * @throws SQLException when the database does not like or cannot answer what we ask.
     */
    public List<String> getUpdatableColumns( final DataSource ds,
            final String tableName ) throws SQLException {
        return getColumnMeta( ds, tableName ).updatable;
    }

    /**
     * Get the primary column names (multiple if the table uses composite primary keys).
     *
     * @param ds        datasource for db access
     * @param tableName to get the data for
     *
     * @return the list of primary column names. Typically the list has just one element
     * @throws SQLException when the database does not like or cannot answer what we ask.
     */
    public List<String> getPrimaryColumns( final DataSource ds,
            final String tableName ) throws SQLException {
        return getColumnMeta( ds, tableName ).primary;
    }

    /**
     * Get the primary column names (multiple if the table uses composite primary keys).
     * This returns the first element of the list.
     *
     * @param ds        datasource for db access
     * @param tableName to get the data for
     *
     * @return the primary column name.
     * @throws SQLException when the database does not like or cannot answer what we ask.
     */
    public String getPrimaryColumnName( final DataSource ds,
            final String tableName ) throws SQLException {
        return getColumnMeta( ds, tableName ).primary.get( 0 );
    }

    /**
     * Get all column names of a table.
     *
     * @param ds        data source
     * @param tableName to use
     *
     * @return the list of column names
     * @throws SQLException when the database does not like or cannot answer what we ask.
     */
    public List<String> getAllColumns( final DataSource ds,
            final String tableName ) throws SQLException {
        return getColumnMeta( ds, tableName ).all;
    }

    /**
     * Get the types of the columns for a table
     *
     * @param ds        data source
     * @param tableName to get the data for
     *
     * @return the map of column name to (java sql) type
     * @throws SQLException when the data requested is not available from the database for some
     *                      reason
     */
    public Map<String, String> getTypeMap( final DataSource ds,
            final String tableName ) throws SQLException {
        return getColumnMeta( ds, tableName ).dtypes;
    }

    /**
     * Meta data object for one table. The object is typically retrieved on first request and then
     * fetch from a map, which caches previous results. The meta data stores list and one map. The
     * data is returned suing a unmodifiableXXX wrapper, to prevent changes to the lists and map.
     */
    private static class Meta {

        final List<String> _updatable = new ArrayList<>();
        final List<String> updatable = Collections
                .unmodifiableList( _updatable );
        final List<String> _primary = new ArrayList<>();
        final List<String> primary = Collections.unmodifiableList( _primary );
        final List<String> _all = new ArrayList<>();
        final List<String> all = Collections.unmodifiableList( _all );
        final Map<String, String> _dtypes = new ConcurrentHashMap<>();
        final Map<String, String> dtypes = Collections.unmodifiableMap( _dtypes );
    }

    /**
     * Get the meta data from the database.
     *
     * @param ds        data source
     * @param tableName described by the meta data
     *
     * @return a meta object containing the data in lists and a map.
     */
    private Meta loadColumnMetaData( final DataSource ds,
            final String tableName ) {
        String qt = QUERY_REGISTRY.getQuery( "getcolumndata" );
        Meta result = new Meta();
        try ( final Connection connection = ds.getConnection();
                final PreparedStatement pst = connection.prepareStatement( qt ) ) {
            pst.setString( 1, tableName );
            try ( ResultSet rs = pst.executeQuery() ) {
                while ( rs.next() ) {
                    String colName = rs.getString( "name" );
                    final boolean isPrimary = rs.getBoolean( "primary_key" );
                    if ( !isPrimary ) {
                        result._updatable.add( colName );
                    } else {
                        result._primary.add( colName );
                    }
                    result._all.add( colName );
                    result._dtypes.put( colName, rs.getString( "dtype" ) );
                }
            }
        } catch ( SQLException ex ) {
            Logger.getLogger( QueryRegistry.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
        return result;
    }

    private Logger logger = null;

    private Logger getLogger() {
        if ( logger == null ) {
            logger = Logger.getLogger( getClass().getName() );
        }
        return logger;
    }

    /**
     * Load a query for the first time.
     *
     * @param qName base name of the query file
     *
     * @return the query as string
     */
    private String loadQuery( String qName ) {
        Path path;
        String queryText = null;
        try {
//            URL resource = getClass().getResource( "/sql/" + qName + ".sql" );
//            System.out.println( "resource = " + resource );
            path = Paths.get( getClass().getResource( "/sql/" + qName + ".sql" ).toURI() );
            queryText
                    = Files.lines( path )
                            .filter( ( String s ) -> !s.startsWith( "--" ) )
                            .collect( joining( System.lineSeparator() ) );
        } catch ( URISyntaxException | IOException ex ) {
            Logger.getLogger( QueryRegistry.class.getName() )
                    .log( Level.SEVERE, "loading " + qName, ex );
        }
        return queryText;
    }

    /**
     * Lookup a postgresql error code from a map.
     *
     * @param code to lookup
     *
     * @return the English error text
     */
    public String lookupError( String code ) {
        loadErrorCodes();
        return this.errorCodes.get( code );
    }

    /**
     * Read the error codes from a file.
     */
    private void loadErrorCodes() {
        // load on first reference.
        if ( errorCodes.isEmpty() ) {
            try {
                Path path = Paths.get( getClass()
                        .getResource( "/sql/errorcodes.csv" ).toURI() );
                final Map<String, String> codesFromFile
                        = Files.lines( path )
                                .filter( s -> !s.startsWith( "--" ) )
                                .map( s -> s.split( ";" ) )
                                .filter( s -> s.length > 1 )
                                .map( x -> new SimpleEntry<>( x[ 0 ], x[ 1 ] ) )
                                .collect( toMap( SimpleEntry::getKey,
                                        SimpleEntry::getValue ) );
                errorCodes.putAll( codesFromFile );

            } catch ( URISyntaxException | IOException ex ) {
                Logger.getLogger( QueryRegistry.class.getName() )
                        .log( Level.SEVERE, null, ex );
            }
        }
    }
}
