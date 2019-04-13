package nl.fontys.sebivenlo.postrest;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.postgresql.ds.PGConnectionPoolDataSource;

/**
 * Test data source for as postgresql database running on localhost port 5432,
 * to be used in database integration tests. Most methods are left
 * unimplemented.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum TestDatasource implements DataSource {
    DS;
    //private String url = "jdbc:postgresql://localhost:5432";
    PGConnectionPoolDataSource source
            = new PGConnectionPoolDataSource();
    final static String DB_HOST = "localhost";
    final static String DB_USER = "exam";
    final static String DB_PASSWORD = "exam";
    final static String DB_DATABASE = "fantysuniversity";

    TestDatasource() {
        System.out.println( "using pooling data source" );
        source.setServerName(DB_HOST );
        source.setDatabaseName(DB_DATABASE );
        source.setUser(DB_USER );
        source.setPassword(DB_PASSWORD );
        try ( Connection conn = getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery( "select now()" ); ) {
            while ( rs.next() ) {
                Object r = rs.getObject( 1 );
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

    /**
    * The code below is intentionally NOT implemented because it is not required for the test.
    In production you will use a proper datasource provided by your webcontainer.
     */
    // <editor-fold defaultstate="collapsed" desc="unimplemented code">
    @Override
    public Connection getConnection( String username, String password ) throws
            SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void setLogWriter( PrintWriter out ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void setLoginTimeout( int seconds ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public <T> T unwrap( Class<T> iface ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public boolean isWrapperFor(
            Class<?> iface ) throws SQLException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
    // </editor-fold>

}
