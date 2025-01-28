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
                System.out.println("Server: " + serverMessage);

                // If the server prompts for an action or input, allow the user to respond
                if (serverMessage.contains("Choose an action:") 
                        || serverMessage.contains("What is your name?")
                        || serverMessage.contains("How many people?")
                        || serverMessage.contains("At what time?")) {
                    System.out.print("> "); // Prompt the user
                    String userInput = scanner.nextLine();
                    writer.println(userInput); // Send the input to the server

                    // If the user chooses to exit, break the loop
                    if (userInput.equalsIgnoreCase("3") || userInput.equalsIgnoreCase("exit")) {
                        System.out.println("Closing connection...");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
