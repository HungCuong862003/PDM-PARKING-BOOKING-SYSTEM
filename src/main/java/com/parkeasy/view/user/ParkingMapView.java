package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ParkingSpaceViewController;
import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * View for displaying parking space details and slot availability
 * Also allows users to make reservations
 */
public class ParkingMapView extends JFrame {
    private User currentUser;
    private ParkingSpace parkingSpace;
    private ParkingSpaceViewController parkingSpaceViewController;
    
    private JLabel parkingNameLabel;
    private JTextArea descriptionArea;
    private JLabel priceLabel;
    private JLabel ratingLabel;
    private JLabel availabilityLabel;
    private JLabel scheduleLabel;
    
    private JTable slotsTable;
    private JTable reviewsTable;
    
    private JPanel dateTimePanel;
    private JSpinner dateStartSpinner;
    private JSpinner timeStartSpinner;
    private JSpinner dateEndSpinner;
    private JSpinner timeEndSpinner;
    
    private JButton reserveButton;
    private JButton backButton;
    
    private List<ParkingSlot> availableSlots;
    
    public ParkingMapView(User user, ParkingSpace parkingSpace) {
        this.currentUser = user;
        this.parkingSpace = parkingSpace;
        
        // Initialize controller
        parkingSpaceViewController = new ParkingSpaceViewController();
        
        // Set up the frame
        setTitle("ParkEasy - Parking Plot Details");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load data
        loadParkingSpaceDetails();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        parkingNameLabel = new JLabel(parkingSpace.getParkingAddress());
        parkingNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        descriptionArea = new JTextArea(parkingSpace.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        priceLabel = new JLabel("Price: $" + String.format("%.2f", parkingSpace.getCostOfParking()) + " per hour");
        
        double averageRating = parkingSpaceViewController.getAverageRating(parkingSpace.getParkingID());
        ratingLabel = new JLabel("Rating: " + String.format("%.1f", averageRating) + "/5.0");
        
        int totalSlots = parkingSpace.getNumberOfSlots();
        int availableSlotCount = calculateAvailableSlots();
        availabilityLabel = new JLabel("Availability: " + availableSlotCount + "/" + totalSlots + " slots available");
        
        scheduleLabel = new JLabel("Operating Hours: 24/7"); // This would be loaded from actual data
        
        // Create parking slots table
        String[] slotColumns = {"Slot Number", "Status"};
        DefaultTableModel slotModel = new DefaultTableModel(slotColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        slotsTable = new JTable(slotModel);
        
        // Create reviews table
        String[] reviewColumns = {"User", "Rating", "Comment", "Date"};
        DefaultTableModel reviewModel = new DefaultTableModel(reviewColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewsTable = new JTable(reviewModel);
        
        // Date and time selection
        dateTimePanel = new JPanel();
        
        // Start date/time
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        dateStartSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(dateStartSpinner, "MM/dd/yyyy");
        dateStartSpinner.setEditor(startDateEditor);
        
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        timeStartSpinner = new JSpinner(startTimeModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(timeStartSpinner, "HH:mm");
        timeStartSpinner.setEditor(startTimeEditor);
        
        // End date/time
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        dateEndSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(dateEndSpinner, "MM/dd/yyyy");
        dateEndSpinner.setEditor(endDateEditor);
        
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        timeEndSpinner = new JSpinner(endTimeModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(timeEndSpinner, "HH:mm");
        timeEndSpinner.setEditor(endTimeEditor);
        
        // Set current date/time as default
        java.util.Date now = new java.util.Date();
        dateStartSpinner.setValue(now);
        timeStartSpinner.setValue(now);
        
        // Set end time to 1 hour later by default
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(now);
        cal.add(java.util.Calendar.HOUR, 1);
        dateEndSpinner.setValue(cal.getTime());
        timeEndSpinner.setValue(cal.getTime());
        
        reserveButton = new JButton("Make Reservation");
        backButton = new JButton("Back to Search");
        
        // Add action listeners
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeReservation();
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
        
        // Top panel with parking name
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(parkingNameLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // Left panel with details
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Parking Information"));
        infoPanel.add(priceLabel);
        infoPanel.add(ratingLabel);
        infoPanel.add(availabilityLabel);
        infoPanel.add(scheduleLabel);
        
        // Description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        // Reviews panel
        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder("Reviews"));
        reviewsPanel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);
        
        leftPanel.add(infoPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(descPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(reviewsPanel);
        
        // Right panel with slots and reservation
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        // Slots panel
        JPanel slotsPanel = new JPanel(new BorderLayout());
        slotsPanel.setBorder(BorderFactory.createTitledBorder("Available Slots"));
        slotsPanel.add(new JScrollPane(slotsTable), BorderLayout.CENTER);
        
        // Reservation panel
        JPanel reservationPanel = new JPanel();
        reservationPanel.setBorder(BorderFactory.createTitledBorder("Make a Reservation"));
        reservationPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        reservationPanel.add(new JLabel("Start Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        reservationPanel.add(dateStartSpinner, gbc);
        
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        reservationPanel.add(new JLabel("Start Time:"), gbc);
        
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.WEST;
        reservationPanel.add(timeStartSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        reservationPanel.add(new JLabel("End Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        reservationPanel.add(dateEndSpinner, gbc);
        
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        reservationPanel.add(new JLabel("End Time:"), gbc);
        
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.WEST;
        reservationPanel.add(timeEndSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        reservationPanel.add(reserveButton, gbc);
        
        rightPanel.add(slotsPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(reservationPanel);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadParkingSpaceDetails() {
        try {
            // Load available slots
            LocalDateTime startDateTime = getStartDateTime();
            LocalDateTime endDateTime = getEndDateTime();
            
            availableSlots = parkingSpaceViewController.getAvailableSlots(
                parkingSpace.getParkingID(), startDateTime, endDateTime);
            
            updateSlotsTable();
            
            // Load reviews
            List<ParkingReview> reviews = parkingSpaceViewController.getParkingReviews(parkingSpace.getParkingID());
            updateReviewsTable(reviews);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading parking space details: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSlotsTable() {
        DefaultTableModel model = (DefaultTableModel) slotsTable.getModel();
        model.setRowCount(0); // Clear table
        
        if (availableSlots == null || availableSlots.isEmpty()) {
            model.addRow(new Object[]{"No slots available", "N/A"});
            return;
        }
        
        for (ParkingSlot slot : availableSlots) {
            String status = slot.getAvailability() ? "Available" : "Occupied";
            model.addRow(new Object[]{slot.getSlotNumber(), status});
        }
    }
    
    private void updateReviewsTable(List<ParkingReview> reviews) {
        DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
        model.setRowCount(0); // Clear table
        
        if (reviews == null || reviews.isEmpty()) {
            model.addRow(new Object[]{"No reviews yet", "", "", ""});
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (ParkingReview review : reviews) {
            model.addRow(new Object[]{
                "User " + review.getUserId(), // We would typically load the actual username
                review.getRating() + "/5",
                review.getComment(),
                review.getReviewDate().format(formatter)
            });
        }
    }
    
    private int calculateAvailableSlots() {
        // This would typically use a service to get real data
        // For this example, we'll use a placeholder calculation
        return parkingSpace.getNumberOfSlots() / 2; // Simulating 50% occupancy
    }
    
    private LocalDateTime getStartDateTime() {
        java.util.Date startDate = (java.util.Date) dateStartSpinner.getValue();
        java.util.Date startTime = (java.util.Date) timeStartSpinner.getValue();
        
        // Combine date and time
        java.util.Calendar dateCal = java.util.Calendar.getInstance();
        dateCal.setTime(startDate);
        
        java.util.Calendar timeCal = java.util.Calendar.getInstance();
        timeCal.setTime(startTime);
        
        dateCal.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY));
        dateCal.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE));
        
        // Convert to LocalDateTime
        return dateCal.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }
    
    private LocalDateTime getEndDateTime() {
        java.util.Date endDate = (java.util.Date) dateEndSpinner.getValue();
        java.util.Date endTime = (java.util.Date) timeEndSpinner.getValue();
        
        // Combine date and time
        java.util.Calendar dateCal = java.util.Calendar.getInstance();
        dateCal.setTime(endDate);
        
        java.util.Calendar timeCal = java.util.Calendar.getInstance();
        timeCal.setTime(endTime);
        
        dateCal.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY));
        dateCal.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE));
        
        // Convert to LocalDateTime
        return dateCal.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }
    
    private void makeReservation() {
        try {
            // Check if there are available slots
            if (availableSlots == null || availableSlots.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No slots available for reservation.", 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if a slot is selected
            int selectedRow = slotsTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a parking slot.", 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if selected slot is available
            String status = (String) slotsTable.getValueAt(selectedRow, 1);
            if (!"Available".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Selected slot is not available.", 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected slot
            ParkingSlot selectedSlot = availableSlots.get(selectedRow);
            
            // Check if user has vehicles
            List<String> userVehicles = getUserVehicles();
            if (userVehicles.isEmpty()) {
                int addVehicle = JOptionPane.showConfirmDialog(this, 
                    "You don't have any vehicles registered. Would you like to add a vehicle now?", 
                    "No Vehicles", 
                    JOptionPane.YES_NO_OPTION);
                
                if (addVehicle == JOptionPane.YES_OPTION) {
                    // Open vehicle management to add a vehicle
                    new VehicleManagementView(currentUser);
                }
                return;
            }
            
            // Select vehicle for reservation
            String selectedVehicle = (String) JOptionPane.showInputDialog(
                this, 
                "Select a vehicle for this reservation:", 
                "Select Vehicle", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                userVehicles.toArray(), 
                userVehicles.get(0));
            
            if (selectedVehicle == null) {
                // User cancelled vehicle selection
                return;
            }
            
            // Get date and time for reservation
            LocalDateTime startDateTime = getStartDateTime();
            LocalDateTime endDateTime = getEndDateTime();
            
            // Validate date and time
            if (startDateTime.isAfter(endDateTime)) {
                JOptionPane.showMessageDialog(this, 
                    "End time must be after start time.", 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (startDateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, 
                    "Start time cannot be in the past.", 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Open reservation details view
            new ReservationView(currentUser, parkingSpace, selectedSlot, selectedVehicle, 
                startDateTime, endDateTime);
            
            // Close this window
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error making reservation: " + ex.getMessage(), 
                "Reservation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<String> getUserVehicles() {
        // This would typically use a service to get real data
        // For this example, we'll return a mock list
        java.util.List<String> vehicles = new java.util.ArrayList<>();
        vehicles.add("ABC123");
        vehicles.add("XYZ789");
        return vehicles;
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
        mockSpace.setNumberOfSlots(10);
        mockSpace.setDescription("Downtown parking garage with 24/7 security.");
        
        SwingUtilities.invokeLater(() -> {
            new ParkingMapView(mockUser, mockSpace);
        });
    }
}