/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Handles a single client connection and processes its request to post an event.
 * Performs event validation and one-time password (OTP) verification.
 */
public class ClientHandler implements Callable<Void> {

    private static final Logger logger = Logger.getLogger("xperience");

    // Delimiter used to separate fields in the client message
    private static final String DELIM = "#";

    // Socket connection to the client
    private final Socket socket;

    // Reference to the event store (can be memory or DB-based)
    private final EventStore eventStore;

    // Reference to the one-time password list
    private final PasswordList pwList;

    // Regex patterns for validating input fields
    private static final Pattern NAME_PATTERN = Pattern.compile("^.{1,300}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern DESC_PATTERN = Pattern.compile("^.{1,65535}$");

    /**
     * Constructor for ClientHandler.
     *
     * @param socket     Client socket
     * @param eventStore The event store (memory or DB)
     * @param pwList     The list of one-time passwords
     */
    public ClientHandler(Socket socket, EventStore eventStore, PasswordList pwList) {
        this.socket = socket;
        this.eventStore = eventStore;
        this.pwList = pwList;
    }

    /**
     * Called by the executor service to handle the client connection.
     * Reads and processes the request input and sends appropriate responses.
     */
    @Override
    public Void call() {
        logger.info("Handling client at " + socket.getRemoteSocketAddress());

        try (
                Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.US_ASCII);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII)
        ) {
            in.useDelimiter(DELIM);
            processRequest(in, out);
        } catch (Exception ex) {
            logger.warning("Client communication failed: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Processes a request by validating fields and storing a valid event.
     */
    private void processRequest(Scanner in, PrintWriter out) {
        // Validate event name
        String name = readAndValidateField(in, out, "event name", NAME_PATTERN);
        if (name == null || eventStore.getEvent(name) != null) {
            reject(out, "Event name exists or is invalid");
            return;
        }

        // Validate date, time, and description
        String date = readAndValidateField(in, out, "date", DATE_PATTERN);
        if (date == null) return;

        String time = readAndValidateField(in, out, "time", TIME_PATTERN);
        if (time == null) return;

        String description = readAndValidateField(in, out, "description", DESC_PATTERN);
        if (description == null) return;

        // Validate password
        if (!in.hasNext()) {
            reject(out, "Missing password");
            return;
        }

        String password = in.next().trim();
        if (!pwList.use(password)) {
            reject(out, "Invalid or reused password");
            return;
        }

        // Create and store event if all fields are valid
        Event event = new Event(name, date, time, description);
        eventStore.addEvent(event);
        logger.info("Accepted: Event '" + name + "' added successfully");
        out.write("Aksept#" + eventStore.getEventCount() + "#");
    }

    /**
     * Reads a field from the client input and validates it using a regex pattern.
     *
     * @param in        Scanner for reading input
     * @param out       PrintWriter for writing output
     * @param fieldName Field name (used for logging/rejection message)
     * @param pattern   Regex pattern to validate input
     * @return Validated field value or null if invalid
     */
    private String readAndValidateField(Scanner in, PrintWriter out, String fieldName, Pattern pattern) {
        if (!in.hasNext()) {
            reject(out, "Missing " + fieldName);
            return null;
        }

        String value = in.next().trim();
        if (!pattern.matcher(value).matches()) {
            reject(out, "Invalid format for " + fieldName + " [" + value + "]");
            return null;
        }

        return value;
    }

    /**
     * Sends a rejection response to the client with a specific reason.
     *
     * @param out    PrintWriter for writing the rejection
     * @param reason Reason for rejection
     */
    private void reject(PrintWriter out, String reason) {
        logger.warning("Rejecting: " + reason);
        out.write("Reject#");
    }
}
