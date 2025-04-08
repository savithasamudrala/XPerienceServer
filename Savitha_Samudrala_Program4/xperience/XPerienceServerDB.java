/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.io.IOException;
import donabase.DonaBaseException;

/**
 * XPerienceServerDB sets up the database-backed version of the XPerience server.
 * It validates clients using a one-time password list and stores events in a MySQL database.
 */
public class XPerienceServerDB {

    // Logger for system messages
    private static final Logger logger = Logger.getLogger("xperience");

    // Database-backed event store
    private final EventStore eventStore;

    // Password list used for one-time password validation
    private final PasswordList pwList;

    // Executor service to manage threads for client connections
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Constructs the DB version of the server.
     *
     * @param eventStore Database-backed event store
     * @param pwList     One-time password list
     */
    public XPerienceServerDB(EventStore eventStore, PasswordList pwList) {
        this.eventStore = eventStore;
        this.pwList = pwList;
    }

    /**
     * Starts the server on the specified port and listens for client connections.
     *
     * @param port Port to bind the server to
     */
    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started successfully on port " + port);

            while (true) {
                // Accept a new client and assign it to a new thread
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket, eventStore, pwList));
            }
        } catch (IOException e) {
            logger.severe("Server error: " + e.getMessage());
        }
    }

    /**
     * Main method to launch the database-backed server.
     * Expected arguments: <port> <dbServer> <passwordFile>
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            logger.severe("Usage: java XPerienceServerDB <port> <dbServer> <passwordFile>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String dbServer = args[1];
        String passwordFile = args[2];

        try {
            // Initialize database-backed store and password list
            EventStore store = new EventStoreDB(dbServer);
            PasswordList pwList = new PasswordList(passwordFile);

            // Launch the server
            new XPerienceServerDB(store, pwList).startServer(port);
        } catch (DonaBaseException | IOException e) {
            logger.severe("Initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
