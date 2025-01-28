package server;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class AdminGUI extends JFrame {
    private JTable reservationsTable;
    private JTable clientsTable;
    private JLabel statusLabel;
    private DefaultTableModel reservationModel;
    private DefaultTableModel clientsModel;
    private JTextArea logArea;

    public AdminGUI() {
        super("Restaurant Management System - Admin Panel");
        setupGUI();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left panel - Tables
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Reservations table
        reservationModel = new DefaultTableModel(
            new String[]{"ID", "Client", "Date", "Time", "People", "Status"}, 0);
        reservationsTable = new JTable(reservationModel);
        JPanel reservationsPanel = new JPanel(new BorderLayout());
        reservationsPanel.setBorder(BorderFactory.createTitledBorder("Current Reservations"));
        reservationsPanel.add(new JScrollPane(reservationsTable));
        
        // Clients table
        clientsModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Status", "Connected Since"}, 0);
        clientsTable = new JTable(clientsModel);
        JPanel clientsPanel = new JPanel(new BorderLayout());
        clientsPanel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        clientsPanel.add(new JScrollPane(clientsTable));
        
        tablesPanel.add(reservationsPanel);
        tablesPanel.add(clientsPanel);
        
        // Right panel - Stats and Log
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.add(new JLabel("Total Reservations: 0"));
        statsPanel.add(new JLabel("Active Clients: 0"));
        statsPanel.add(new JLabel("Today's Reservations: 0"));
        statsPanel.add(new JLabel("Pending Requests: 0"));
        
        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("System Log"));
        logPanel.add(new JScrollPane(logArea));
        
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(logPanel, BorderLayout.CENTER);
        
        mainSplitPane.setLeftComponent(tablesPanel);
        mainSplitPane.setRightComponent(rightPanel);
        mainSplitPane.setDividerLocation(700);
        
        add(mainSplitPane);
        
        // Status bar
        statusLabel = new JLabel("Server running on port 12345");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void updateReservations(Reservation reservation) {
        SwingUtilities.invokeLater(() -> {
            reservationModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getName(),
                reservation.getDayOfWeek(),
                reservation.getTime(),
                reservation.getNumberOfPeople(),
                "Confirmed"
            });
        });
    }

    public void updateClients(String clientId, String name, String status) {
        SwingUtilities.invokeLater(() -> {
            clientsModel.addRow(new Object[]{
                clientId,
                name,
                status,
                new java.util.Date().toString()
            });
        });
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(String.format("[%tT] %s%n", new java.util.Date(), message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
} 