package server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Manages saving and loading reservations to/from a file
public class ReservationStorage {
    private static final String FILE_NAME = "reservations.ser";

    // Load reservations from the file
    public static List<Reservation> loadReservations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Reservation>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>(); // No file yet, return empty list
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save reservations to the file
    public static void saveReservations(List<Reservation> reservations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
