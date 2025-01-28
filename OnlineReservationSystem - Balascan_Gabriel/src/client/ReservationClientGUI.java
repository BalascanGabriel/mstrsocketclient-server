package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationClientGUI extends JFrame {
    private int clientId = 0; // Nu mai este static
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextArea messageArea;
    private JTextField nameField;
    private JComboBox<String> dayComboBox;
    private JSpinner timeSpinner;
    private JSpinner peopleSpinner;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private JLabel activeClientsLabel;
    private JLabel clientLabel;

    public ReservationClientGUI() {
        super("Restaurant Reservation System");
        setupGUI();
        connectToServer();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650); // Fereastră mai mare
        setLocationRelativeTo(null);

        // Folosim CardLayout pentru a gestiona diferite panouri
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Panou principal cu două secțiuni
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Secțiunea din stânga pentru rezervări
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Make Reservation"));

        // Panel pentru input-uri cu un aspect mai plăcut
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Stilizare componente
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        dayComboBox = new JComboBox<>(days);
        dayComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        SpinnerNumberModel peopleModel = new SpinnerNumberModel(2, 1, 10, 1);
        peopleSpinner = new JSpinner(peopleModel);
        peopleSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        // Adăugare componente cu etichete stilizate
        addLabelAndComponent(inputPanel, "Name:", nameField, gbc, 0);
        addLabelAndComponent(inputPanel, "Day:", dayComboBox, gbc, 1);
        addLabelAndComponent(inputPanel, "Time:", timeSpinner, gbc, 2);
        addLabelAndComponent(inputPanel, "People:", peopleSpinner, gbc, 3);

        // Butoane stilizate
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton makeReservationButton = createStyledButton("Make Reservation", "reservation.png");
        JButton viewReservationsButton = createStyledButton("View Reservations", "view.png");
        JButton clearButton = createStyledButton("Clear", "clear.png");

        makeReservationButton.addActionListener(e -> makeReservation());
        viewReservationsButton.addActionListener(e -> viewReservations());
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(makeReservationButton);
        buttonPanel.add(viewReservationsButton);
        buttonPanel.add(clearButton);

        leftPanel.add(inputPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Secțiunea din dreapta pentru mesaje
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Reservations"));

        // Creăm modelul tabelului
        String[] columns = {"ID", "Client", "Day", "Time", "People"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(tableModel);
        reservationsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reservationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Setăm dimensiunile coloanelor
        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane tableScrollPane = new JScrollPane(reservationsTable);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Adăugăm un panel pentru mesaje de sistem sub tabel
        messageArea = new JTextArea(5, 40);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createTitledBorder("System Messages"));
        rightPanel.add(messageScrollPane, BorderLayout.SOUTH);

        // Adăugare panouri în layout-ul principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        cardPanel.add(mainPanel, "main");
        add(cardPanel);

        // Adaugă status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        // Panel pentru partea stângă a status bar-ului
        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        clientLabel = new JLabel("Client ID: " + clientId);
        clientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        activeClientsLabel = new JLabel("Clienți conectați: 0");
        activeClientsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        leftStatusPanel.add(clientLabel);
        leftStatusPanel.add(new JSeparator(JSeparator.VERTICAL));
        leftStatusPanel.add(activeClientsLabel);
        
        statusBar.add(leftStatusPanel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent component, 
                                    GridBagConstraints gbc, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);
    }

    private JButton createStyledButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            messageArea.append(String.format("[%s] [Client %d] %s%n", 
                timestamp, clientId, message));
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    private void clearFields() {
        nameField.setText("");
        dayComboBox.setSelectedIndex(0);
        peopleSpinner.setValue(2);
        // Resetează timeSpinner la ora curentă
        timeSpinner.setValue(new Date());
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 12345);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        handleServerMessage(message);
                    }
                } catch (IOException e) {
                    appendMessage("ERROR: Connection lost!");
                }
            }).start();

        } catch (IOException e) {
            appendMessage("ERROR: Could not connect to server!");
        }
    }

    private void handleServerMessage(String message) {
        System.out.println("Received message: " + message); // Pentru debug
        
        if (message.startsWith("CLIENT_ID:")) {
            clientId = Integer.parseInt(message.substring(10));
            SwingUtilities.invokeLater(() -> {
                setTitle("Restaurant Reservation System - Client " + clientId);
                clientLabel.setText("Client ID: " + clientId);
            });
        } 
        else if (message.startsWith("ACTIVE_CLIENTS:")) {
            try {
                int count = Integer.parseInt(message.substring(15));
                System.out.println("Updating active clients count to: " + count); // Pentru debug
                SwingUtilities.invokeLater(() -> {
                    activeClientsLabel.setText("Clienti conectati: " + count);
                });
            } catch (NumberFormatException e) {
                System.err.println("Error parsing active clients count: " + message);
            }
        } else if (message.equals("RESERVATION_SUCCESS")) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "Rezervarea a fost efectuata cu succes!\n" +
                    "Va asteptam cu drag!",
                    "Rezervare Confirmata",
                    JOptionPane.INFORMATION_MESSAGE);
                appendMessage("✓ Rezervare confirmata");
                clearFields();
                viewReservations();
            });
        } else if (message.equals("RESERVATION_FAILED")) {
            SwingUtilities.invokeLater(() -> {
                String time = ((JSpinner.DateEditor) timeSpinner.getEditor()).getFormat().format(timeSpinner.getValue());
                String day = dayComboBox.getSelectedItem().toString();
                
                String errorMsg = String.format(
                    "Ne pare rau, dar ora %s din ziua de %s este deja rezervata.\n" +
                    "Va rugam sa alegeti alt interval orar.", 
                    time, day);
                
                JOptionPane.showMessageDialog(this,
                    errorMsg,
                    "Slot Ocupat",
                    JOptionPane.WARNING_MESSAGE);
                
                appendMessage("✗ " + errorMsg);
            });
        } else if (message.contains("people,")) {
            updateReservationsTable(message);
        }
    }

    private void makeReservation() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Va rugam sa introduceti numele",
                "Eroare",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String time = ((JSpinner.DateEditor) timeSpinner.getEditor()).getFormat().format(timeSpinner.getValue());
            String name = nameField.getText().trim();
            String day = dayComboBox.getSelectedItem().toString();
            
            appendMessage("Se verifică disponibilitatea pentru " + day + " la ora " + time + "...");
            
            writer.println("1"); // Opțiunea pentru rezervare
            writer.println(name);
            writer.println(day);
            writer.println(time);
            writer.println(peopleSpinner.getValue().toString());
            writer.flush();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Format invalid pentru datele introduse",
                "Eroare",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewReservations() {
        // Curățăm tabelul înainte de a cere noile rezervări
        SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));
        writer.println("2"); // Opțiunea pentru vizualizare rezervări
    }

    private void updateReservationsTable(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Exemplu: "Client 0 - Dan, 4 people, Wednesday at 13:49"
                String[] mainParts = message.split(" at "); // Separă timpul
                String time = mainParts[1];
                
                String[] firstParts = mainParts[0].split(", "); // ["Client 0 - Dan", "4 people", "Wednesday"]
                String client = firstParts[0];
                String people = firstParts[1].split(" ")[0]; // "4" din "4 people"
                String day = firstParts[2];
                
                tableModel.addRow(new Object[]{
                    tableModel.getRowCount() + 1,
                    client,
                    day,
                    time,
                    people
                });
            } catch (Exception e) {
                System.err.println("Error parsing: " + message); // Pentru debug
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReservationClientGUI().setVisible(true);
        });
    }
} 