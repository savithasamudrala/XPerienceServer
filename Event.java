package xperience;
public class Event {
    private final String name;
    private final String date;
    private final String time;
    private final String description;

    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{name='" + name + "', date='" + date + "', time='" + time + "', description='" + description + "'}";
    }
}