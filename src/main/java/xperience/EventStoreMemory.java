/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements in-memory event storage using a thread-safe data structure.
 */
public class EventStoreMemory implements EventStore {

    /**
     * Stores events in a thread-safe map.
     */
    private final ConcurrentHashMap<String, Event> events = new ConcurrentHashMap<>();

    /**
     * Adds an event to in-memory storage.
     *
     * @param event The event to store.
     * @return True if the event was added successfully, false if it already exists.
     */
    @Override
    public boolean addEvent(Event event) {
        return events.putIfAbsent(event.getName(), event) == null; // Returns false if event already exists
    }

    /**
     * Retrieves an event from in-memory storage.
     *
     * @param name The name of the event to retrieve.
     * @return The event if found, otherwise null.
     */
    @Override
    public Event getEvent(String name) {
        return events.get(name);
    }

    /**
     * Gets the total number of stored events.
     *
     * @return The number of stored events.
     */
    @Override
    public int getEventCount() {
        return events.size();
    }
}
