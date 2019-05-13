package integrationtest;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.postgresql.ds.PGConnectionPoolDataSource;

/**
 * Test data source for as postgresql database running on localhost port 5432,
 * to be used in database integration tests. Most methods are left
 * unimplemented.
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
enum TestDatasource implements DataSource {
    DS;
    // field to make the availability testable.
    LocalDateTime availableSince;
    private final PGConnectionPoolDataSource source
            = new PGConnectionPoolDataSource();

    final static String dbHost = "localhost";
    final static String dbUser = "exam";
    final static String dbPassword = "exam";
    final static String dbDatabase = "fantysuniversity";

    TestDatasource() {
        System.out.println( "using pooling data source" );
        source.setServerName( dbHost );
        source.setDatabaseName( dbDatabase );
        source.setUser( dbUser );
        source.setPassword( dbPassword );
        try ( Connection conn = getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery( "select now()" ); ) {
            while ( rs.next() ) {
                Timestamp r = rs.getTimestamp( 1 );
                availableSince = r.toLocalDateTime();
                System.out.println( "database says it is now " + r.toString() );
            }
        } catch ( SQLException ex ) {
            Logger.getLogger( TestDatasource.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = source.getConnection();
        return conn;
    }

    @Override
    public Connection getConnection( String username, String password ) throws
            SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLogWriter( PrintWriter out ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoginTimeout( int seconds ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T unwrap( Class<T> iface ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isWrapperFor(
            Class<?> iface ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

}
