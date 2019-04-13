package restserver;

import javax.annotation.Resource;
import javax.sql.DataSource;
import nl.fontys.sebivenlo.postrest.AbstractPostRestService;

/**
 * Base class for all endpoints using the same data source.
 *
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 */
abstract class FantysCrudService extends AbstractPostRestService {

    /**
     * Injection point for the common data source for all services of Fantys
     * University.
     *
     * The lookup parameter value to the Resource annotation should be defined
     * in the web server and by injected by it.
     *
     * In Payara this implies that your have to define a JDBC pool and a jdbc
     * resource with the name used below.
     *
     * @param ds the data source to inject.
     */
    @Resource( lookup = "jdbc/fantys" )
    void setDataSource( DataSource ds ) {
        this.dataSource = ds;
    }
}
