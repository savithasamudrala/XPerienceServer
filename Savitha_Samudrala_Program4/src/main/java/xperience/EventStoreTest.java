package xperience;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for EventStore using the in-memory implementation (EventStoreMemory).
 */
public class EventStoreTest {

    private EventStore store;

    /**
     * Set up a new in-memory store before each test.
     */
    @BeforeEach
    void setUp() {
        store = new EventStoreMemory();
    }

    /**
     * Test that an event can be added and retrieved correctly.
     */
    @Test
    void testAddAndRetrieveSingleEvent() {
        Event e = new Event("Dance Fest", "2025-12-10", "18:00", "Holiday dance party");
        assertTrue(store.addEvent(e), "Event should be added successfully");

        Event retrieved = store.getEvent("Dance Fest");
        assertNotNull(retrieved, "Retrieved event should not be null");
        assertEquals("Holiday dance party", retrieved.getDescription());
    }

    /**
     * Test that a duplicate event (same name) is not allowed.
     */
    @Test
    void testDuplicateEventNotAdded() {
        Event e1 = new Event("Music Night", "2025-11-01", "19:00", "Live performances");
        Event e2 = new Event("Music Night", "2025-11-01", "19:00", "Live performances again");

        assertTrue(store.addEvent(e1));
        assertFalse(store.addEvent(e2), "Duplicate event name should not be allowed");
        assertEquals(1, store.getEventCount());
    }

    /**
     * Test the event count functionality.
     */
    @Test
    void testEventCount() {
        assertEquals(0, store.getEventCount());

        store.addEvent(new Event("Game Jam", "2025-10-15", "15:00", "48-hour coding challenge"));
        store.addEvent(new Event("Art Expo", "2025-10-20", "17:00", "Student art exhibition"));

        assertEquals(2, store.getEventCount());
    }
}
