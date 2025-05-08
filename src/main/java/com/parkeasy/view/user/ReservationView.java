// Modified ReservationView.java
package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ReservationController;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Enhanced view for confirming and making a reservation
 * Shows reservation details with improved UI and prompts for confirmation
 */
public class ReservationView extends JFrame {
    private User currentUser;
    private ParkingSpace parkingSpace;
    private ParkingSlot parkingSlot;
    private String vehicleId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private JLabel summaryTitleLabel;
    private JPanel detailsPanel;
    private JButton confirmButton;
    private JButton editButton;
    private JButton cancelButton;

    private ReservationController reservationController;

    public ReservationView(User user, ParkingSpace parkingSpace, ParkingSlot parkingSlot,
                           String vehicleId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.currentUser = user;
        this.parkingSpace = parkingSpace;
        this.parkingSlot = parkingSlot;
        this.vehicleId = vehicleId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        // Initialize services and controller
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService();
        ParkingSpaceService parkingSpaceService = new ParkingSpaceService();
        UserService userService = new UserService();

        reservationController = new ReservationController(
                reservationService, parkingSpaceService, vehicleService, userService);

        // Set up the frame
        setTitle("ParkEasy - Confirm Reservation");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Make the frame visible
        setVisible(true);
    }
    private void initComponents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");

        // Main title
        summaryTitleLabel = new JLabel("Reservation Summary");
        summaryTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        summaryTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Details panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Add parking space details
        JPanel parkingPanel = createDetailPanel("Parking Location", parkingSpace.getParkingAddress());
        detailsPanel.add(parkingPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add slot details
        JPanel slotPanel = createDetailPanel("Parking Slot", parkingSlot.getSlotNumber());
        detailsPanel.add(slotPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add vehicle details
        JPanel vehiclePanel = createDetailPanel("Vehicle", vehicleId);
        detailsPanel.add(vehiclePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add time details
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timePanel.setBorder(BorderFactory.createTitledBorder("Reservation Time"));

        JLabel startLabel = new JLabel("From: " + startDateTime.format(formatter));
        startLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel endLabel = new JLabel("To: " + endDateTime.format(formatter));
        endLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        timePanel.add(startLabel);
        timePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        timePanel.add(endLabel);

        detailsPanel.add(timePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Calculate duration and cost
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        JPanel durationPanel = createDetailPanel("Duration",
                String.format("%d hours, %d minutes", hours, minutes));
        detailsPanel.add(durationPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        float hourlyRate = parkingSpace.getCostOfParking();
        float totalCost = (float) (hourlyRate * (hours + (minutes / 60.0)));

        JPanel costPanel = new JPanel();
        costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.Y_AXIS));
        costPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        costPanel.setBorder(BorderFactory.createTitledBorder("Payment"));

        JLabel rateLabel = new JLabel("Rate: " + String.format("%,.0f VND", hourlyRate) + " per hour");
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel totalLabel = new JLabel("Total Cost: " + String.format("%,.0f VND", totalCost));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 100, 0)); // Dark green

        costPanel.add(rateLabel);
        costPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        costPanel.add(totalLabel);

        detailsPanel.add(costPanel);

        // Action buttons
        confirmButton = new JButton("Confirm Reservation");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 128, 0)); // Green
        confirmButton.setForeground(Color.WHITE);

        editButton = new JButton("Edit Reservation");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmReservation();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editReservation();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private JPanel createDetailPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueLabel);

        return panel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
        topPanel.add(summaryTitleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with scrollable details
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(confirmButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 15, 15, 15));

        // Add a note about payment
        JLabel noteLabel = new JLabel("Note: You will be redirected to the payment page after confirmation.");
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        bottomPanel.add(noteLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void confirmReservation() {
        try {
            // Get user's current balance
            float userBalance = currentUser.getBalance();

            // Calculate expected fee
            Duration duration = Duration.between(startDateTime, endDateTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            if (minutes > 0) {
                hours += 1; // Round up to the next hour
            }

            float hourlyRate = parkingSpace.getCostOfParking();
            float expectedFee = hourlyRate * hours;

            // Check if user has enough balance
            if (userBalance < expectedFee) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient balance. You need " + String.format("%,.0f VND", expectedFee) +
                                " but your balance is " + String.format("%,.0f VND", userBalance),
                        "Insufficient Balance",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm with user
            int option = JOptionPane.showConfirmDialog(this,
                    "This reservation will cost " + String.format("%,.0f VND", expectedFee) +
                            ".\nYour balance after reservation will be " +
                            String.format("%,.0f VND", (userBalance - expectedFee)) +
                            ".\nProceed with reservation?",
                    "Confirm Payment",
                    JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            // Proceed with reservation
            Map<String, Object> result = reservationController.createReservation(
                    currentUser.getUserID(),
                    vehicleId,
                    parkingSpace.getParkingID(),
                    parkingSlot.getSlotNumber(),
                    startDateTime.toLocalDate(),
                    startDateTime.toLocalTime(),
                    endDateTime.toLocalDate(),
                    endDateTime.toLocalTime()
            );

            boolean success = (boolean) result.get("success");
            if (success) {
                // Get reservation ID
                int reservationId = (int) result.get("reservationId");
                float    actualFee = (float) result.get("fee");

                JOptionPane.showMessageDialog(this,
                        "Reservation created successfully!\nReservation ID: " + reservationId +
                                "\nFee: " + String.format("%,.0f VND", actualFee),
                        "Reservation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                // Update user object with new balance
                currentUser.setBalance(userBalance - actualFee);

                // Close this window
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create reservation: " + result.get("message"),
                        "Reservation Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editReservation() {
        // Go back to the parking map view
        new ParkingMapView(currentUser, parkingSpace);
        dispose();
    }

    private void openPaymentView(int reservationId) {
        // Calculate total cost
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        float hourlyRate = parkingSpace.getCostOfParking();
        float totalCost = (float) (hourlyRate * (hours + (minutes / 60.0)));

    }
}