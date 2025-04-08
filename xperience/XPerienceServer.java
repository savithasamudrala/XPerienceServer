/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XPerienceServer launches a socket server that handles event submissions
 * with password-based validation using a ClientHandler.
 *
 * This version stores events in memory using EventStoreMemory.
 */
public class XPerienceServer {

    // Logger for server-side info and error messages
    private static final Logger logger = Logger.getLogger("xperience");

    // The event store used for storing incoming events
    private final EventStore eventStore;

    // Password list for one-time password validation
    private final PasswordList pwList;

    // Thread pool for handling multiple clients concurrently
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Constructs the server with a given event store and password list.
     *
     * @param eventStore The store where events are recorded
     * @param pwList     The list of one-time-use passwords
     */
    public XPerienceServer(EventStore eventStore, PasswordList pwList) {
        this.eventStore = eventStore;
        this.pwList = pwList;
    }

    /**
     * Starts the server on the specified port.
     * Each client is handled by a new thread via ClientHandler.
     *
     * @param port The port number to listen on
     */
    public void startServer(int port) {
        if (port < 1 || port > 65535) {
            logger.severe("Invalid port number: " + port);
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port " + port);

            while (true) {
                // Accept a client connection and delegate it to a handler
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket, eventStore, pwList));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server failed", e);
        }
    }

    /**
     * Main method: launches the server with command-line arguments.
     * Usage: java XPerienceServer <port> <passwordFile>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java XPerienceServer <port> <passwordFile>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String passwordFile = args[1];

        try {
            // Load password list from file
            PasswordList pwList = new PasswordList(passwordFile);
            // Use in-memory event store
            EventStore store = new EventStoreMemory();
            // Start the server
            new XPerienceServer(store, pwList).startServer(port);
        } catch (IOException e) {
            System.err.println("Could not load password file: " + e.getMessage());
            System.exit(1);
        }
    }
}
