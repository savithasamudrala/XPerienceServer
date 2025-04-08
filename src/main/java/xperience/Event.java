/************************************************
*
* Author: Savitha Samudrala
* Assignment: Program 4
* Class: CSC 4610
*
************************************************/

package xperience;

/**
 * Represents an event in the XPerience protocol.
 * Each event has a name, date, time, and description.
 */

public class Event {
     /** The unique name of the event. */
    private final String name;
    
    /** The date on which the event takes place. */
    private final String date;
    
    /** The time at which the event occurs. */
    private final String time;
    
    /** A brief description of the event. */
    private final String description;

    /**
     * Constructs a new Event
     *
     * @param name The unique name of the event
     * @param date The date of the event
     * @param time The time of the event
     * @param description A description of the event
     */

    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // Accessor methods
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }



     /**
     * Returns a string representation of the event.
     *
     * @return A string containing event details.
     */

    @Override
    public String toString() {
        return "Event{name='" + name + "', date='" + date + "', time='" + time + "', description='" + description + "'}";
    }
}
