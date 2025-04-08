/*
 * Test for XPerience server
 *
 * Author: Donahoo
 *
 * You only have permission to use this file in the author's class.  You may not
 * share by any means with anybody.  This includes, but is not limited to, sharing on
 * public source repositories, uploading to class materials websites, etc.
 */
package xperience;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * XPerience Server test
 * Note:  This is a non-standard use of unit testing.  Should be using
 * a system/E2E test framework, but selected this to limit technologies
 * that must be learned in class.
 *
 * @version 1.0
 */
class XPerienceServerJUnitTest {
    /**
     * Rejection string
     */
    private static final String REJECT = "Reject#";
    /**
     * Default character encoding
     */
    private static final Charset ENC = StandardCharsets.US_ASCII;
    /**
     * Server identity
     */
    private static String server;
    /**
     * Port
     */
    private static int port;
    /**
     * Socket connecting to server
     */
    private Socket clntSock;
    /**
     * Writer talking to server
     */
    private PrintWriter out;

    /**
     * Send string to writer
     *
     * @param sndStr string to send
     * @param out    writer sink
     */
    static void send(String sndStr, PrintWriter out) {
        int half = sndStr.length() / 2;
        out.write(sndStr.substring(0, half));
        out.flush();
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException _) {
//        }
        out.write(sndStr.substring(half));
        out.flush();
    }

    /**
     * Returns true if character is ASCII printable
     *
     * @param c character to test
     */
    static boolean isPrintable(char c) {
        return (c >= 32 && c < 127);
    }

    /**
     * Set up before any tests
     * Expects system properties:
     * server - identity of server
     * port - port of server
     * You can set these with -D, e.g., -Dserver="10.1.2.3"
     */
    @BeforeAll
    static void setUp() {
        try {
            server = Objects.requireNonNull(System.getProperty("server"));
            port = Integer.parseInt(System.getProperty("port"));
        } catch (Exception ex) {
            System.err.println("Test setup failed: " + ex);
            throw ex;
        }
    }

    /**
     * Create set of messages to send and expected responses
     *
     * @return stream of send/expected
     */
    static Stream<Arguments> testInteraction() {
        return Stream.of(
                arguments("Danoke#2025-02-12#20:00#Fusion of Karaoke and Dance#testpass#", "Accept#1#"),
                arguments("Dona Dance#2025-02-14#20:00#Dance like you don’t care#xperiencepass#", "Accept#2#"),
                arguments("Dona Dance Dance#2025-02-14#21:00#Light the night#eventpass#", "Accept#3#"),
                // Repeat (already used password)
                arguments("Danoke#2025-02-12#20:00#Fusion of Karaoke and Dance#testpass#", REJECT),
                arguments("Dona Dance#2025-03-14#08:00#Dance dance like you don’t care#xperiencepass#", REJECT),
                arguments("Dona Dance Dance#2025-03-14#20:00#Light the night#eventpass#", REJECT),
                arguments("Safety#2025-02-16#20:00#Dance#serverpass#Leave your friends", "Accept#4#"),
                arguments("Safety#2025-02-16#20:00#Dance#serverpass#", REJECT),
                arguments("O".repeat(300) + "#2025-03-04#04:00#" + "D".repeat(1027) + "#dbpass#", "Accept#5#"),
                arguments("O".repeat(300) + "#2025-05-04#05:00#" + "D".repeat(1027) + "#dbpass#", REJECT),
                arguments("Bark#05-04-2025#05:00#Ping#anotherpass#", REJECT),   // malformed date
                arguments("Mouse#2025-05-04#05#Squeak#anotherpass#", REJECT),   // malformed time
                arguments("Cat#2025-05-04#05:00#Meow#finalpass#", "Accept#6#")
        );
    }

    /**
     * Test send and receive of expected messages
     *
     * @param send     string to send
     * @param expected expected string
     * @throws IOException if I/O problem
     */
    @ParameterizedTest
    @MethodSource
    void testInteraction(String send, String expected) throws IOException {
        // Send string
        send(send, out);
        // Test response
        String response = readAllChars();
        assertTrue(response.startsWith(expected), "Expected %s but got %s".formatted(expected, response));
        // Normally, you would never do this in JUnit tests.  Added to allow
        // responses with non-protocol compliant endings (e.g., \n) to pass with
        // warning.
        if (!response.equals(expected)) {
            System.err.println("Expected: " + expected + ", got: " + response);
        }
    }

    /**
     * Read all bytes, convert to ASCII characters, replace non-printable
     * characters with [decimal], and return string
     *
     * @throws IOException if I/O problem
     */
    String readAllChars() throws IOException {
        byte[] buf = clntSock.getInputStream().readAllBytes();
        StringBuilder response = new StringBuilder();
        for (byte b : buf) {
            // If printable
            if (isPrintable((char) b)) {
                response.append((char) b);
            } else {
                // not printable (so show [decimal])
                response.append("[").append(b).append("]");
            }
        }
        return response.toString();
    }

    /**
     * Setup for each test
     *
     * @throws IOException if I/O problem
     */
    @BeforeEach
    void testSetUp() throws IOException {
        // Connect to server:port
        clntSock = new Socket(server, port);
        out = new PrintWriter(clntSock.getOutputStream(), true, ENC);
    }

    /**
     * Teardown for each test
     *
     * @throws IOException if I/O problem
     */
    @AfterEach
    void testTearDown() throws IOException {
        out.close();
        clntSock.close();
    }
}
