package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ReservationController;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * View for displaying user's reservation history
 * Shows past and upcoming reservations with details
 */
public class ReservationHistoryView extends JFrame {
    private User currentUser;
    private ReservationController reservationController;
    
    private JTabbedPane tabbedPane;
    private JTable activeReservationsTable;
    private JTable pastReservationsTable;
    private JButton cancelReservationButton;
    private JButton viewDetailsButton;
    private JButton backButton;
    
    public ReservationHistoryView(User user) {
        this.currentUser = user;
        
        // Initialize services and controller
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();
        
        reservationController = new ReservationController(
            reservationService, parkingSpaceService, vehicleService);
        
        // Set up the frame
        setTitle("ParkEasy - Reservation History");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load data
        loadReservations();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Create tables for active and past reservations
        String[] columnNames = {"Reservation ID", "Vehicle", "Parking Address", "Slot", "Start Time", "End Time", "Status"};
        
        DefaultTableModel activeModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activeReservationsTable = new JTable(activeModel);
        activeReservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableModel pastModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pastReservationsTable = new JTable(pastModel);
        pastReservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Create buttons
        cancelReservationButton = new JButton("Cancel Reservation");
        viewDetailsButton = new JButton("View Details");
        backButton = new JButton("Back to Dashboard");
        
        // Add action listeners
        cancelReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelReservation();
            }
        });
        
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservationDetails();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Add tables to scroll panes
        JScrollPane activeScrollPane = new JScrollPane(activeReservationsTable);
        JScrollPane pastScrollPane = new JScrollPane(pastReservationsTable);
        
        // Add scroll panes to tabbed pane
        tabbedPane.addTab("Active Reservations", activeScrollPane);
        tabbedPane.addTab("Past Reservations", pastScrollPane);
        
        // Add tabbed pane to the center
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadReservations() {
        try {
            // Get active reservations
            List<Map<String, Object>> activeReservations = reservationController.getActiveReservations(currentUser.getUserID());
            updateReservationsTable(activeReservationsTable, activeReservations);
            
            // Get reservation history
            List<Map<String, Object>> pastReservations = reservationController.getReservationHistory(currentUser.getUserID());
            updateReservationsTable(pastReservationsTable, pastReservations);
            
            // Update tab titles with count
            tabbedPane.setTitleAt(0, "Active Reservations (" + activeReservations.size() + ")");
            tabbedPane.setTitleAt(1, "Past Reservations (" + pastReservations.size() + ")");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading reservations: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateReservationsTable(JTable table, List<Map<String, Object>> reservations) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear table
        
        if (reservations.isEmpty()) {
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Map<String, Object> reservationData : reservations) {
            Reservation reservation = (Reservation) reservationData.get("reservation");
            
            LocalDateTime startDateTime = (LocalDateTime) reservationData.get("startDateTime");
            LocalDateTime endDateTime = (LocalDateTime) reservationData.get("endDateTime");
            
            Object[] row = {
                reservation.getReservationID(),
                reservationData.get("vehicle"),
                reservationData.get("parkingAddress"),
                reservationData.get("slotNumber"),
                startDateTime.format(formatter),
                endDateTime.format(formatter),
                reservation.getStatus()
            };
            
            model.addRow(row);
        }
    }
    
    private void cancelReservation() {
        // Check if we're on the active reservations tab and a row is selected
        if (tabbedPane.getSelectedIndex() == 0) {
            int selectedRow = activeReservationsTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this,
                    "Please select a reservation to cancel.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int reservationId = (int) activeReservationsTable.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\nReservation ID: " + reservationId,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Map<String, Object> result = reservationController.cancelReservation(
                        reservationId, currentUser.getUserID());
                    
                    boolean success = (boolean) result.get("success");
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Reservation cancelled successfully.",
                            "Cancellation Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Reload data
                        loadReservations();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to cancel reservation: " + result.get("message"),
                            "Cancellation Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error cancelling reservation: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "You can only cancel active reservations.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void viewReservationDetails() {
        JTable currentTable = tabbedPane.getSelectedIndex() == 0 ? activeReservationsTable : pastReservationsTable;
        
        int selectedRow = currentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to view.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) currentTable.getValueAt(selectedRow, 0);
        
        try {
            Map<String, Object> details = reservationController.getReservationDetails(
                reservationId, currentUser.getUserID());
            
            if ((boolean) details.get("success")) {
                // Display reservation details in a dialog
                JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Create a grid for the details
                JPanel gridPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                
                Reservation reservation = (Reservation) details.get("reservation");
                
                // Add reservation details
                addDetailRow(gridPanel, "Reservation ID:", String.valueOf(reservation.getReservationID()));
                addDetailRow(gridPanel, "Vehicle:", details.get("vehicle").toString());
                addDetailRow(gridPanel, "Parking Location:", (String) details.get("parkingAddress"));
                addDetailRow(gridPanel, "Slot Number:", (String) details.get("slotNumber"));
                addDetailRow(gridPanel, "Status:", reservation.getStatus());
                
                // Format times
                LocalDateTime startDateTime = (LocalDateTime) details.get("startDateTime");
                LocalDateTime endDateTime = (LocalDateTime) details.get("endDateTime");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                
                addDetailRow(gridPanel, "Start Time:", startDateTime.format(formatter));
                addDetailRow(gridPanel, "End Time:", endDateTime.format(formatter));
                
                // Add duration and cost
                long durationHours = (long) details.get("durationHours");
                addDetailRow(gridPanel, "Duration:", durationHours + " hour(s)");
                
                double hourlyRate = (double) details.get("hourlyRate");
                addDetailRow(gridPanel, "Hourly Rate:", String.format("$%.2f", hourlyRate));
                
                double totalCost = (double) details.get("totalCost");
                addDetailRow(gridPanel, "Total Cost:", String.format("$%.2f", totalCost));
                
                detailsPanel.add(gridPanel, BorderLayout.CENTER);
                
                // If this is an active reservation, add extend option
                if (tabbedPane.getSelectedIndex() == 0) {
                    JButton extendButton = new JButton("Extend Reservation");
                    extendButton.addActionListener(e -> extendReservation(reservationId));
                    
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    buttonPanel.add(extendButton);
                    detailsPanel.add(buttonPanel, BorderLayout.SOUTH);
                }
                
                // Show in a dialog
                JOptionPane.showMessageDialog(this,
                    detailsPanel,
                    "Reservation Details",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to retrieve reservation details: " + details.get("message"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving reservation details: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    private void extendReservation(int reservationId) {
        // Create a panel for the extension options
        JPanel extensionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Add spinner for additional hours
        SpinnerNumberModel hourModel = new SpinnerNumberModel(1, 1, 24, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        
        extensionPanel.add(new JLabel("Additional Hours:"));
        extensionPanel.add(hourSpinner);
        
        int result = JOptionPane.showConfirmDialog(this,
            extensionPanel,
            "Extend Reservation",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int additionalHours = (int) hourSpinner.getValue();
            
            try {
                // First check if extension is possible
                Map<String, Object> checkResult = reservationController.checkReservationExtension(
                    reservationId, currentUser.getUserID(), LocalDateTime.now().plusHours(additionalHours));
                
                boolean canExtend = (boolean) checkResult.get("success");
                if (canExtend) {
                    // Now actually extend the reservation
                    Map<String, Object> extendResult = reservationController.extendReservation(
                        reservationId, currentUser.getUserID(), LocalDateTime.now().plusHours(additionalHours));
                    
                    boolean success = (boolean) extendResult.get("success");
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Reservation extended successfully.\n" +
                                "New end time: " + extendResult.get("newEndDateTime") + "\n" +
                                "Additional cost: $" + String.format("%.2f", extendResult.get("additionalCost")),
                            "Extension Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Check if additional payment is required
                        boolean requiresPayment = (boolean) extendResult.get("requiresPayment");
                        if (requiresPayment) {
                            // Open payment view for the additional amount
                            new PaymentView(currentUser, reservationId, (double) extendResult.get("additionalCost"));
                        }
                        
                        // Reload data
                        loadReservations();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to extend reservation: " + extendResult.get("message"),
                            "Extension Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Cannot extend reservation: " + checkResult.get("message"),
                        "Extension Not Possible",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error extending reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        
        SwingUtilities.invokeLater(() -> {
            new ReservationHistoryView(mockUser);
        });
    }
}