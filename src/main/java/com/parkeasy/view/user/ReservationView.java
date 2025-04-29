package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ReservationController;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * View for confirming and making a reservation
 * Shows reservation details and prompts for confirmation
 */
public class ReservationView extends JFrame {
    private User currentUser;
    private ParkingSpace parkingSpace;
    private ParkingSlot parkingSlot;
    private String vehicleId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private JLabel parkingAddressLabel;
    private JLabel slotNumberLabel;
    private JLabel vehicleLabel;
    private JLabel startTimeLabel;
    private JLabel endTimeLabel;
    private JLabel durationLabel;
    private JLabel costLabel;
    private JButton confirmButton;
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

        reservationController = new ReservationController(
                reservationService, parkingSpaceService, vehicleService);

        // Set up the frame
        setTitle("ParkEasy - Confirm Reservation");
        setSize(500, 400);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        parkingAddressLabel = new JLabel("Parking: " + parkingSpace.getParkingAddress());
        slotNumberLabel = new JLabel("Slot: " + parkingSlot.getSlotNumber());
        vehicleLabel = new JLabel("Vehicle: " + vehicleId);
        startTimeLabel = new JLabel("Start Time: " + startDateTime.format(formatter));
        endTimeLabel = new JLabel("End Time: " + endDateTime.format(formatter));

        // Calculate duration and cost
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        durationLabel = new JLabel(String.format("Duration: %d hours, %d minutes", hours, minutes));

        double hourlyRate = parkingSpace.getCostOfParking();
        double totalCost = hourlyRate * (hours + (minutes / 60.0));

        costLabel = new JLabel(String.format("Total Cost: $%.2f", totalCost));

        confirmButton = new JButton("Confirm Reservation");
        cancelButton = new JButton("Cancel");

        // Add action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmReservation();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Create a panel for the reservation details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add a title
        JLabel titleLabel = new JLabel("Reservation Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add the details
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        fieldsPanel.add(parkingAddressLabel);
        fieldsPanel.add(slotNumberLabel);
        fieldsPanel.add(vehicleLabel);
        fieldsPanel.add(startTimeLabel);
        fieldsPanel.add(endTimeLabel);
        fieldsPanel.add(durationLabel);
        fieldsPanel.add(costLabel);

        detailsPanel.add(fieldsPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        detailsPanel.add(buttonPanel);

        add(detailsPanel, BorderLayout.CENTER);
    }

    private void confirmReservation() {
        try {
            Map<String, Object> result = reservationController.createReservation(
                    currentUser.getUserID(),
                    vehicleId,
                    parkingSpace.getParkingID(),
                    parkingSlot.getSlotNumber(), // Changed from getSlotID() to getSlotNumber()
                    startDateTime.toLocalDate(),
                    startDateTime.toLocalTime(),
                    endDateTime.toLocalDate(),
                    endDateTime.toLocalTime()
            );

            boolean success = (boolean) result.get("success");
            if (success) {
                // Get reservation ID
                int reservationId = (int) result.get("reservationId");

                JOptionPane.showMessageDialog(this,
                        "Reservation created successfully!\nReservation ID: " + reservationId,
                        "Reservation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                // Proceed to payment
                openPaymentView(reservationId);

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

    private void openPaymentView(int reservationId) {
        // Calculate total cost
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        double hourlyRate = parkingSpace.getCostOfParking();
        double totalCost = hourlyRate * (hours + (minutes / 60.0));

        new PaymentView(currentUser, reservationId, totalCost);
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create mock data for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");

        ParkingSpace mockSpace = new ParkingSpace();
        mockSpace.setParkingID("P001");
        mockSpace.setParkingAddress("123 Main St");
        mockSpace.setCostOfParking(5.0f);

        ParkingSlot mockSlot = new ParkingSlot();
        // Remove the setSlotID call since it's deprecated
        mockSlot.setSlotNumber("A01");

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        SwingUtilities.invokeLater(() -> {
            new ReservationView(mockUser, mockSpace, mockSlot, "ABC123", start, end);
        });
    }
}