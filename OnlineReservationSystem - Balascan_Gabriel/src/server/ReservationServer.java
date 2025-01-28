package server;

import java.io.*;
import java.net.*;
import java.util.*;

// Server Class
public class ReservationServer {
    // List to store all reservations (shared resource)
    private static List<Reservation> reservations = ReservationStorage.loadReservations(); // Load saved reservations

    public static void main(String[] args) {
        int port = 12345; // Port number for the server
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                
                // Start a new thread for each client
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // ClientHandler class to manage each client connection
    static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)
            ) {
                writer.println("Welcome to the Reservation System!");
                writer.println("Choose an action: [1] Add reservation, [2] View reservations, [3] Exit");

                String action = reader.readLine();

                switch (action) {
                    case "1": // Add reservation
                        writer.println("What is your name?");
                        String name = reader.readLine();
                        writer.println("How many people?");
                        int numberOfPeople = Integer.parseInt(reader.readLine());
                        writer.println("At what time?");
                        String time = reader.readLine();

                        Reservation reservation = new Reservation(name, numberOfPeople, time);
                        synchronized (reservations) {
                            reservations.add(reservation);
                        }
                        ReservationStorage.saveReservations(reservations); // Save to file
                        writer.println("Reservation added: " + reservation);
                        break;

                    case "2": // View reservations
                        synchronized (reservations) {
                            if (reservations.isEmpty()) {
                                writer.println("No reservations available.");
                            } else {
                                writer.println("Current reservations:");
                                for (Reservation r : reservations) {
                                    writer.println(r.toString());
                                }
                            }
                        }
                        break;

                    case "3": // Exit
                        writer.println("Goodbye!");
                        break;

                    default:
                        writer.println("Invalid option.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
