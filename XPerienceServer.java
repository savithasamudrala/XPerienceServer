package xperience;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;


/**
 * Server implementing XPerience protocol
 * @version 1.0
 */
public class XPerienceServer {
    private static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);
    private static final String DELIM = "#";
    //thread-safe set and list
    private static final Set<String> names = ConcurrentHashMap.newKeySet();
    private static final List<Event> events = Collections.synchronizedList(new ArrayList<>());
    private static int count = 0;

    public static void main(String[] args) {
// Get command-line arguments
        if (args.length != 1) {
            logger.severe("Incorrect parameter(s). Expected: <Port>");
            System.exit(1);
        }
        int servPort = Integer.parseInt(args[0]);
// Create a server socket to accept client connection requests
        try (ServerSocket servSock = new ServerSocket(servPort)) {
            logger.info("Port " + servPort + ": Server started.");
            while (true) { // Run forever, accepting and servicing connections
                try (Socket clntSock = servSock.accept()) {
                    handleClient(clntSock);
                }
            }
            /* NOT REACHED */
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Server setup failed", ex);
        }
    }

    /**
     * Handle client
     *
     * @param clntSock socket of newly accepted client
     */
    private static void handleClient(Socket clntSock) {
        logger.info(() -> "Handling client at " + clntSock.getRemoteSocketAddress());
// Prepare I/O, receive, and respond
        try (Scanner in = new Scanner(clntSock.getInputStream(),
                StandardCharsets.US_ASCII);
             PrintWriter out = new PrintWriter(clntSock.getOutputStream(), true,
                     StandardCharsets.US_ASCII)) {
// Set Scanner delimiter
            in.useDelimiter(DELIM);
// Read items and send for processing
            while (in.hasNextLine()) {
                String inString = in.nextLine().trim();
                processRequest(inString, out);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Client communication failed", ex);
        }
    }

    /**
     * Process request by taking in string, processing it, and accepting
     * or rejecting input as an event.
     *
     * @param inString String to process
     * @param out      Writer for processed string
     */
    private static void processRequest(String inString, PrintWriter out) {
        //check for empty input
        if (inString == null || inString.trim().isEmpty()) {
            out.println("Reject#");
            logger.warning("Empty input");
            return;
        }

        //separate words in string using delimiter #
        String[] eventDescriptors = inString.split(DELIM);

        //check for correct format
        if (eventDescriptors.length != 4) {
            out.println("Reject#");
            logger.warning(inString + " has incorrect format. Expected Format: <Name>#<Date>#<Time>#<Description>#");
            return;
        }

        //declare variables with corresponding descriptor
        String name = eventDescriptors[0].trim();
        String date = eventDescriptors[1].trim();
        String time = eventDescriptors[2].trim();
        String description = eventDescriptors[3].trim();

        //check for duplicate events
        if (names.contains(name)) {
            out.println("Reject#");
            logger.warning(name + " already exists.");
            return;
        }

        //add event
        Event event = new Event(name, date, time, description);
        events.add(event);
        names.add(name);
        count++;
        out.println("Accept#" + count + "#");
        logger.info(event + " accepted.");
    }
}
