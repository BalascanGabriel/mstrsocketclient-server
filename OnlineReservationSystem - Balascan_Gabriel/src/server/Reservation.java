package server;

import java.io.Serializable;

// Represents a single reservation
public class Reservation implements Serializable {
    private String name;
    private int numberOfPeople;
    private String time;

    public Reservation(String name, int numberOfPeople, String time) {
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return name + ", " + numberOfPeople + " people, " + time;
    }
}
