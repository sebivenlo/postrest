package integrationtest;

import javax.sql.DataSource;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public class DataBaseAvailableTest {

    /**
     * Make sure the database is reachable.
     */
    @Test
    public void testDataBaseIsAvailable() {
        DataSource ds = TestDatasource.DS;
        assertNotNull( TestDatasource.DS.availableSince );
    }
}
