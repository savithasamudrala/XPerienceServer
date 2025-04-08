import xperience.PasswordList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordList class.
 */
public class PasswordListTest {

    private Path tempPasswordFile;

    /**
     * Create a temporary password file before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        tempPasswordFile = Files.createTempFile("passwords", ".txt");
        Files.write(tempPasswordFile, List.of("abc", "def", "ghi"));
    }

    /**
     * Test that a valid password can be used.
     */
    @Test
    void testUseValidPassword() throws IOException {
        PasswordList pwList = new PasswordList(tempPasswordFile.toString());
        assertTrue(pwList.use("abc"));
    }

    /**
     * Test that an invalid password is rejected.
     */
    @Test
    void testUseInvalidPassword() throws IOException {
        PasswordList pwList = new PasswordList(tempPasswordFile.toString());
        assertFalse(pwList.use("xyz"));
    }

    /**
     * Test that the same password cannot be reused.
     */
    @Test
    void testUseSamePasswordTwice() throws IOException {
        PasswordList pwList = new PasswordList(tempPasswordFile.toString());
        assertTrue(pwList.use("def"));
        assertFalse(pwList.use("def"));  // should fail on second use
    }
} 
