/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import donabase.DonaBaseException;
import donabase.DonaBaseConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements event storage using a MySQL database.
 */
public class EventStoreDB implements EventStore {

    /** Logger instance for logging database operations. */
    private static final Logger logger = Logger.getLogger("xperience");

    /** Database connection (shared across all queries). */
    private final DonaBaseConnection dbConn;

    /**
     * Constructs an EventStore that connects to a MySQL database.
     *
     * @param dbServer The database server hostname or IP.
     * @throws DonaBaseException If a connection error occurs.
     */
    public EventStoreDB(String dbServer) throws DonaBaseException {
        this.dbConn = new DonaBaseConnection(dbServer, 3306, "Samudrala", "event_user", "user");
        logger.info("Successfully connected to MySQL database on server: " + dbServer);
    }

    /**
     * Escapes single quotes for SQL safety.
     */
    private static String escape(String input) {
        return input.replace("'", "''");
    }

    /**
     * Inserts a new event into the database.
     *
     * @param event The event to store.
     * @return True if the event was added successfully, false if an error occurred.
     */
    @Override
    public boolean addEvent(Event event) {
        try {
            String sql = "INSERT INTO Event (name, event_date, event_time, description) VALUES ('" +
                         escape(event.getName()) + "', '" +
                         escape(event.getDate()) + "', '" +
                         escape(event.getTime()) + "', '" +
                         escape(event.getDescription()) + "')";

            dbConn.insert(sql);
            logger.info("Event added to DB: " + event.getName());
            return true;
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Database error while inserting event: " + event.getName(), e);
            return false;
        }
    }

    /**
     * Retrieves an event from the database by name.
     *
     * @param name Event name to retrieve.
     * @return Event object if found, otherwise null.
     */
    @Override
    public Event getEvent(String name) {
        try {
            String query = "SELECT name, event_date, event_time, description FROM Event WHERE name = '" + escape(name) + "'";
            List<List<String>> results = dbConn.query(query);

            if (!results.isEmpty()) {
                List<String> row = results.get(0);
                return new Event(row.get(0), row.get(1), row.get(2), row.get(3));
            }
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Database error while retrieving event", e);
        }
        return null;
    }

    /**
     * Retrieves the total event count from the database.
     *
     * @return The total number of events stored.
     */
    @Override
    public int getEventCount() {
        try {
            List<List<String>> results = dbConn.query("SELECT COUNT(*) FROM Event");
            return results.isEmpty() ? 0 : Integer.parseInt(results.get(0).get(0));
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Database error while retrieving event count", e);
            return 0;
        }
    }
}
