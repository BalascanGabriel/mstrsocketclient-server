package server;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class ReservationServer {
    private static final int PORT = 12345;
    private static List<Reservation> reservations = ReservationStorage.loadReservations();
    private static List<ClientHandler> activeClients = Collections.synchronizedList(new ArrayList<>());
    private static int nextClientId = 1; // Simplu counter
    private static ServerMonitorGUI monitor; // Adăugăm monitorul

    public static void main(String[] args) {
        // Inițializăm și afișăm monitorul
        monitor = ServerMonitorGUI.getInstance();
        monitor.setVisible(true);
        monitor.log("Server starting on port " + PORT);
        
        // Reset la pornirea serverului
        activeClients.clear();
        nextClientId = 1;
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            monitor.log("Server started successfully");
            monitor.updateStatus("Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
                monitor.log("New client connected: Client " + handler.clientId);
            }
        } catch (IOException e) {
            monitor.log("ERROR: " + e.getMessage());
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private final Socket socket;
        private final int clientId;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            synchronized(ReservationServer.class) {
                this.clientId = nextClientId++;
                activeClients.add(this);
                monitor.log("Client " + clientId + " connected. Total clients: " + activeClients.size());
            }
        }

        private void updateAllClients() {
            String message = "ACTIVE_CLIENTS:" + activeClients.size();
            System.out.println("Sending update: " + message);
            
            synchronized(activeClients) {
                for (ClientHandler client : activeClients) {
                    if (client.writer != null) {
                        client.writer.println(message);
                        client.writer.flush();
                    }
                }
            }
        }

        @Override
        public void run() {
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Trimite ID-ul clientului
                writer.println("CLIENT_ID:" + clientId);
                updateAllClients(); // Actualizează toți clienții

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
                    synchronized(ReservationServer.class) {
                        activeClients.remove(this);
                        System.out.println("Client " + clientId + " deconectat. Clienți rămași: " + activeClients.size());
                        updateAllClients(); // Actualizează clienții rămași
                    }
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
                        monitor.log("Duplicate reservation detected: " + dayOfWeek + " at " + time);
                        writer.println("RESERVATION_FAILED");
                        return;
                    }
                }

                // Dacă am ajuns aici, înseamnă că putem face rezervarea
                Reservation reservation = new Reservation("Client " + clientId + " - " + name, 
                                                       numberOfPeople, time, dayOfWeek);
                reservations.add(reservation);
                ReservationStorage.saveReservations(reservations);
                monitor.log("New reservation added: " + reservation);
                
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