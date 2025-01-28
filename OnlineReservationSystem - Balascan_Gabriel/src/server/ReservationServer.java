package server;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.SwingUtilities;

// Server Class
public class ReservationServer {
    private static final int PORT = 12345;
    private static List<Reservation> reservations = ReservationStorage.loadReservations();
    private static List<ClientHandler> activeClients = new ArrayList<>();
    private static int nextClientId = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // ClientHandler class to manage each client connection
    static class ClientHandler extends Thread {
        private final Socket socket;
        private final int clientId;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            synchronized(ReservationServer.class) {
                this.clientId = nextClientId++;
                System.out.println("New client connected with ID: " + clientId);
            }
        }

        @Override
        public void run() {
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Trimite doar ID-ul clientului
                writer.println("CLIENT_ID:" + clientId);

                String input;
                while ((input = reader.readLine()) != null) {
                    if ("1".equals(input)) {
                        handleReservation(reader, writer);
                    } else if ("2".equals(input)) {
                        showReservations(writer);
                    } else if ("3".equals(input)) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client " + clientId + ": " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket for client " + clientId);
                }
            }
        }

        private void handleReservation(BufferedReader reader, PrintWriter writer) throws IOException {
            String name = reader.readLine();
            String dayOfWeek = reader.readLine();
            String time = reader.readLine();
            int numberOfPeople = Integer.parseInt(reader.readLine());

            synchronized (reservations) {
                // Verificăm explicit dacă există o rezervare la aceeași oră și zi
                for (Reservation r : reservations) {
                    if (r.getDayOfWeek().equalsIgnoreCase(dayOfWeek) && r.getTime().equals(time)) {
                        System.out.println("Rezervare duplicată detectată: " + dayOfWeek + " la " + time);
                        writer.println("RESERVATION_FAILED");
                        writer.flush(); // Ne asigurăm că mesajul ajunge la client
                        return;
                    }
                }

                // Dacă am ajuns aici, înseamnă că putem face rezervarea
                Reservation reservation = new Reservation("Client " + clientId + " - " + name, 
                                                       numberOfPeople, time, dayOfWeek);
                reservations.add(reservation);
                ReservationStorage.saveReservations(reservations);
                
                writer.println("RESERVATION_SUCCESS");
                writer.flush();
            }
        }

        private void showReservations(PrintWriter writer) {
            synchronized (reservations) {
                writer.println("RESERVATIONS_START");
                for (Reservation r : reservations) {
                    writer.println(r.toString());
                }
                writer.println("RESERVATIONS_END");
            }
        }
    }
}
