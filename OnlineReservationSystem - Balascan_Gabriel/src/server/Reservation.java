package server;

import java.io.Serializable;

// Represents a single reservation
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 0;
    private final int id;
    private String name;
    private int numberOfPeople;
    private String time;
    private String dayOfWeek;

    public Reservation(String name, int numberOfPeople, String time, String dayOfWeek) {
        this.id = ++counter;
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public String getTime() { return time; }
    public String getDayOfWeek() { return dayOfWeek; }

    @Override
    public String toString() {
        return String.format("%s, %d people, %s at %s", 
            name, numberOfPeople, dayOfWeek, time);
    }
}
