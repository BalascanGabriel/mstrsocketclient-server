package client;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private boolean succeeded;
    private Frame owner;

    public LoginDialog(Frame owner) {
        super(owner, "Welcome to Restaurant Reservation System", true);
        this.owner = owner;
        setupGUI();
    }

    private void setupGUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(nameLabel, cs);

        nameField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(nameField, cs);

        JLabel phoneLabel = new JLabel("Phone: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(phoneLabel, cs);

        phoneField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(phoneField, cs);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            if (validateFields()) {
                succeeded = true;
                dispose();
            }
        });

        JPanel bp = new JPanel();
        bp.add(loginButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your name",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your phone number",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
} 