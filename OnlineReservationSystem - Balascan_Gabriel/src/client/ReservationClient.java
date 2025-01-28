package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client Class
public class ReservationClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Server address
        int port = 12345; // Server port

        try (
            Socket socket = new Socket(serverAddress, port);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the reservation server at " + serverAddress + ":" + port);

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                // Verifică dacă este un mesaj broadcast
                if (serverMessage.startsWith("BROADCAST: ")) {
                    System.out.println("\n" + serverMessage);
                    System.out.print("> "); // Reafișează promptul
                } else {
                    System.out.println("Server: " + serverMessage);

                    if (serverMessage.contains("Choose an action:") 
                            || serverMessage.contains("What is your name?")
                            || serverMessage.contains("How many people?")
                            || serverMessage.contains("At what time?")
                            || serverMessage.contains("Which day of the week?")) {
                        System.out.print("> ");
                        String userInput = scanner.nextLine();
                        writer.println(userInput);

                        if (userInput.equalsIgnoreCase("3") || userInput.equalsIgnoreCase("exit")) {
                            System.out.println("Closing connection...");
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
