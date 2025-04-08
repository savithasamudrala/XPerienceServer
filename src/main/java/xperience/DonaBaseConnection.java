package donabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Super cheesy database access class.
 */
public class DonaBaseConnection implements AutoCloseable {

  /**
   * JDBC Connection
   */
  private final Connection conn;

  /**
   * Name of logger
   */
  private static final String LOGGERNAME = "donabase";
  /**
   * Logger instance used for logging events, errors, or diagnostics
   */
  private static final Logger logger = Logger.getLogger(LOGGERNAME);

  /**
   * Create a DonaBase connection.
   * 
   * @param server   server to connect to
   * @param port     port to connect to
   * @param dbname   database to connect to
   * @param username database user name
   * @param password database user password
   * @throws DonaBaseException if connection fails
   */
  public DonaBaseConnection(String server, int port, String dbname, String username, String password)
      throws DonaBaseException {
    // Make connection using parameters
    try {
      conn = DriverManager.getConnection("jdbc:mysql://%s:%d/%s?allowMultiQueries=true".formatted(server, port, dbname),
          username, password);
      logger.info(() -> "Connected to %s:%d/%s as %s".formatted(server, port, dbname, username));
    } catch (SQLException ex) {
      logger.log(Level.SEVERE, () -> "Failed to connect to %s:%d/%s as %s".formatted(server, port, dbname, username));
      throw new DonaBaseException("Connect failed", ex);
    }
  }

  /**
   * Execute a query (select) statement and return results.
   * 
   * This will execute other statements; however, behavior is undefined
   * 
   * @param queryStmt query statement to execute
   * @return List of List of Strings where each row is a List of String where each
   *         String is a value result of the query.
   * @throws DonaBaseException if problem executing query.
   */
  public List<List<String>> query(String queryStmt) throws DonaBaseException {
    // Create and execute query statement
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(queryStmt)) {
      logger.info(() -> "Executed query: %s".formatted(queryStmt));
      // Get meta data about result
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      // Transfer results to List of Lists (rows)
      List<List<String>> rows = new ArrayList<>();
      while (rs.next()) {
        List<String> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          row.add(rs.getString(i));
        }
        rows.add(row);
      }
      return rows;
    } catch (SQLException ex) {
      logger.severe(() -> "Failed to execute query: %s".formatted(queryStmt));
      throw new DonaBaseException("Query failed", ex);
    }
  }

  /**
   * Execute an insert statement.
   * 
   * This will execute other statements; however, behavior is undefined
   * 
   * @param insertStmt statement
   * @return true if inserted at least one row; false otherwise.
   * @throws DonaBaseException if problem executing insert.
   */
  public boolean insert(String insertStmt) throws DonaBaseException {
    // Create and execute insert statement
    try (Statement stmt = conn.createStatement()) {
      // True if insert creates 1 or more rows
      logger.info(() -> "Executed insert: %s".formatted(insertStmt));
      return stmt.executeUpdate(insertStmt) > 0;
    } catch (SQLException ex) {
      logger.severe(() -> "Failed to execute insert: %s".formatted(insertStmt));
      throw new DonaBaseException("Update failed", ex);
    }
  }

  /**
   * Close connection.
   */
  public void close() {
    try {
      conn.close();
    } catch (SQLException ex) {
      logger.log(Level.INFO, () -> "Failed to close connection");
    }
  }
}
