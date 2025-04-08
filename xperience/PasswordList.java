/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Maintains a one-time-use password list for secure event posting.
 */
public class PasswordList {
    private final Set<String> passwords = new HashSet<>();

    /**
     * Constructs a PasswordList from a file.
     * Each password is on a separate line.
     *
     * @param filename Path to the password file.
     * @throws IOException If file cannot be read.
     */
    public PasswordList(String filename) throws IOException {
        System.out.println("Loading passwords from file: " + Path.of(filename).toAbsolutePath());

        for (String line : Files.readAllLines(Path.of(filename))) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                System.out.println("Loaded password: '" + trimmed + "'");
                passwords.add(trimmed);
            }
        }

        System.out.println("Total passwords loaded: " + passwords.size());
    }

    /**
     * Uses a password if it exists in the list.
     * Passwords are removed upon successful usage to prevent reuse.
     *
     * @param password Password to validate and use.
     * @return true if password is valid and unused, false otherwise.
     */
    public boolean use(String password) {
        System.out.println("Checking password: '" + password + "'");

        if (passwords.remove(password)) {
            System.out.println("Password accepted: '" + password + "'");
            return true;
        } else {
            System.out.println("Password rejected (invalid or reused): '" + password + "'");
            return false;
        }
    }
}
