/************************************************
 * Author: Savitha Samudrala
 * Assignment: Program 4
 * Class: CSC 4610
 ************************************************/

package xperience;

/**
 * Defines the event storage interface for different storage implementations.
 */
public interface EventStore {

    /**
     * Adds an event to storage.
     *
     * @param event The event to store.
     * @return True if successfully added, false if the event already exists.
     */
    boolean addEvent(Event event);

    /**
     * Retrieves an event from storage by name.
     *
     * @param name The name of the event to retrieve.
     * @return The event if found, otherwise null.
     */
    Event getEvent(String name);

    /**
     * Gets the total number of stored events.
     *
     * @return The number of stored events.
     */
    int getEventCount();
}
