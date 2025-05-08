package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ParkingSpaceViewController;
import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Enhanced view for displaying parking space details and slot availability
 * Allows users to make reservations with real-time availability updates
 */
public class ParkingMapView extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ParkingMapView.class.getName());
    private User currentUser;
    private ParkingSpace parkingSpace;
    private ParkingSpaceViewController parkingSpaceViewController;
    private VehicleService vehicleService;

    private JLabel parkingNameLabel;
    private JTextArea descriptionArea;
    private JLabel priceLabel;
    private JLabel ratingLabel;
    private JLabel availabilityLabel;

    private JTable slotsTable;
    private JTable reviewsTable;

    // Enhanced date time selection panel
    private JPanel dateTimePanel;
    private JDatePicker startDatePicker;
    private JSpinner startTimeSpinner;
    private JDatePicker endDatePicker;
    private JSpinner endTimeSpinner;

    // Vehicle selection
    private JComboBox<String> vehicleComboBox;
    private JButton addVehicleButton;
    private JButton checkAvailabilityButton;
    private JButton reserveButton;
    private JButton backButton;

    private List<ParkingSlot> availableSlots;
    private List<Vehicle> userVehicles;

    public ParkingMapView(User user, ParkingSpace parkingSpace) {
        this.currentUser = user;
        this.parkingSpace = parkingSpace;

        // Initialize controllers and services
        parkingSpaceViewController = new ParkingSpaceViewController();
        vehicleService = new VehicleService();

        // Set up the frame
        setTitle("ParkEasy - Parking Space Details");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Load data
        loadParkingSpaceDetails();
        loadUserVehicles();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        parkingNameLabel = new JLabel(parkingSpace.getParkingAddress());
        parkingNameLabel.setFont(new Font("Arial", Font.BOLD, 22));

        descriptionArea = new JTextArea(parkingSpace.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));

        priceLabel = new JLabel("Price: $" + String.format("%.2f", parkingSpace.getCostOfParking()) + " per hour");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        float averageRating = (float) parkingSpaceViewController.getAverageRating(parkingSpace.getParkingID());
        ratingLabel = new JLabel("Rating: " + String.format("%.1f", averageRating) + "/5.0");
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 16));

        int totalSlots = parkingSpace.getNumberOfSlots();
        int availableSlotCount = calculateAvailableSlots();
        availabilityLabel = new JLabel("Availability: " + availableSlotCount + "/" + totalSlots + " slots available");
        availabilityLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Create parking slots table with better visual appearance
        String[] slotColumns = {"Slot Number", "Status"};
        DefaultTableModel slotModel = new DefaultTableModel(slotColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        slotsTable = new JTable(slotModel);
        slotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotsTable.setRowHeight(30);
        slotsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        slotsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Custom cell renderer for better status visualization
        slotsTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                label.setOpaque(true);
                label.setText(value != null ? value.toString() : "");
                label.setHorizontalAlignment(SwingConstants.CENTER);

                // Style based on status column
                if (column == 1 && value != null) {
                    if ("Available".equals(value)) {
                        label.setBackground(new Color(220, 255, 220)); // Light green
                        label.setForeground(new Color(0, 100, 0)); // Dark green text
                    } else {
                        label.setBackground(new Color(255, 220, 220)); // Light red
                        label.setForeground(new Color(153, 0, 0)); // Dark red text
                    }
                } else {
                    if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                        label.setForeground(table.getSelectionForeground());
                    } else {
                        label.setBackground(table.getBackground());
                        label.setForeground(table.getForeground());
                    }
                }

                return label;
            }
        });

        // Create reviews table
        String[] reviewColumns = {"User", "Rating", "Comment", "Date"};
        DefaultTableModel reviewModel = new DefaultTableModel(reviewColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewsTable = new JTable(reviewModel);
        reviewsTable.setRowHeight(25);

        // Enhanced date time selection with more user-friendly components
        dateTimePanel = new JPanel(new GridBagLayout());
        dateTimePanel.setBorder(BorderFactory.createTitledBorder("Reservation Time"));

        // Start date - using custom date picker for better UX
        startDatePicker = new JDatePicker(LocalDate.now());

        // Start time spinner with 30-min increments
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startTimeModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);

        // End date
        endDatePicker = new JDatePicker(LocalDate.now());

        // End time spinner
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endTimeModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);

        // Set current date/time as default
        java.util.Date now = new java.util.Date();
        startTimeSpinner.setValue(now);

        // Set end time to 1 hour later by default
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(now);
        cal.add(java.util.Calendar.HOUR, 1);
        endTimeSpinner.setValue(cal.getTime());

        // Vehicle selection
        vehicleComboBox = new JComboBox<>();
        addVehicleButton = new JButton("Add New Vehicle");

        // Action buttons
        checkAvailabilityButton = new JButton("Check Availability");
        checkAvailabilityButton.setBackground(new Color(70, 130, 180)); // Steel blue
        checkAvailabilityButton.setForeground(Color.WHITE);

        reserveButton = new JButton("Make Reservation");
        reserveButton.setBackground(new Color(0, 128, 0)); // Green
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setEnabled(false); // Disabled until availability check

        backButton = new JButton("Back to Search");

        // Add action listeners
        checkAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAvailability();
            }
        });

        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeReservation();
            }
        });

        addVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewVehicle();
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

        // Top panel with parking name and basic info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(parkingNameLabel);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.add(priceLabel);
        infoPanel.add(ratingLabel);
        infoPanel.add(availabilityLabel);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main content panel - split into left and right
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(400);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Left panel with description, slots, and reviews
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Slots panel
        JPanel slotsPanel = new JPanel(new BorderLayout());
        slotsPanel.setBorder(BorderFactory.createTitledBorder("Available Slots"));
        slotsPanel.add(new JScrollPane(slotsTable), BorderLayout.CENTER);

        // Reviews panel
        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder("Reviews"));
        reviewsPanel.add(new JScrollPane(reviewsTable), BorderLayout.CENTER);

        leftPanel.add(descPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(slotsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(reviewsPanel);

        // Right panel with reservation form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 0),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Make a Reservation"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        ));

        // Date/time selection
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dateTimePanel.add(new JLabel("Start Date:"), gbc);

        gbc.gridx = 1;
        dateTimePanel.add(startDatePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dateTimePanel.add(new JLabel("Start Time:"), gbc);

        gbc.gridx = 1;
        dateTimePanel.add(startTimeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dateTimePanel.add(new JLabel("End Date:"), gbc);

        gbc.gridx = 1;
        dateTimePanel.add(endDatePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dateTimePanel.add(new JLabel("End Time:"), gbc);

        gbc.gridx = 1;
        dateTimePanel.add(endTimeSpinner, gbc);

        // Vehicle selection panel
        JPanel vehiclePanel = new JPanel(new GridBagLayout());
        vehiclePanel.setBorder(BorderFactory.createTitledBorder("Vehicle"));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        vehiclePanel.add(new JLabel("Select Vehicle:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        vehiclePanel.add(vehicleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        vehiclePanel.add(addVehicleButton, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkPanel.add(checkAvailabilityButton);

        JPanel reservePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        reservePanel.add(reserveButton);

        buttonsPanel.add(checkPanel);
        buttonsPanel.add(reservePanel);

        // Add all components to right panel
        rightPanel.add(dateTimePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(vehiclePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(buttonsPanel);
        rightPanel.add(Box.createVerticalGlue());

        // Add panels to the split pane
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void loadParkingSpaceDetails() {
        try {
            // Load available slots with default time range (now + 1 hour)
            LocalDateTime startDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = startDateTime.plusHours(1);

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

    private void loadUserVehicles() {
        try {
            // Get vehicles from database using the VehicleService
            userVehicles = vehicleService.getVehiclesByUserId(currentUser.getUserID());

            // Update vehicle combobox
            vehicleComboBox.removeAllItems();

            if (userVehicles == null || userVehicles.isEmpty()) {
                vehicleComboBox.addItem("No vehicles available");
                reserveButton.setEnabled(false);
            } else {
                for (Vehicle v : userVehicles) {
                    vehicleComboBox.addItem(v.getVehicleID());
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading vehicles for user: " + currentUser.getUserID(), ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading vehicles: " + ex.getMessage(),
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
        LocalDate startDate = startDatePicker.getDate();

        // Extract time from spinner
        java.util.Date startTimeDate = (java.util.Date) startTimeSpinner.getValue();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(startTimeDate);

        LocalTime startTime = LocalTime.of(
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE)
        );

        return LocalDateTime.of(startDate, startTime);
    }

    private LocalDateTime getEndDateTime() {
        LocalDate endDate = endDatePicker.getDate();

        // Extract time from spinner
        java.util.Date endTimeDate = (java.util.Date) endTimeSpinner.getValue();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(endTimeDate);

        LocalTime endTime = LocalTime.of(
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE)
        );

        return LocalDateTime.of(endDate, endTime);
    }
    private void checkAvailability() {
        try {
            // Validate date times
            LocalDateTime startDateTime = getStartDateTime();
            LocalDateTime endDateTime = getEndDateTime();

            if (startDateTime.isAfter(endDateTime)) {
                JOptionPane.showMessageDialog(this,
                        "End time must be after start time.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                        "Start time cannot be in the past.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get available slots for the selected time period
            availableSlots = parkingSpaceViewController.getAvailableSlots(
                    parkingSpace.getParkingID(), startDateTime, endDateTime);

            updateSlotsTable();

            // Enable reserve button if there are available slots
            reserveButton.setEnabled(availableSlots != null && !availableSlots.isEmpty());

            // Update availability label
            int totalSlots = parkingSpace.getNumberOfSlots();
            int availableSlotCount = availableSlots != null ? availableSlots.size() : 0;
            availabilityLabel.setText("Availability: " + availableSlotCount + "/" + totalSlots + " slots available");

            // Show confirmation message
            JOptionPane.showMessageDialog(this,
                    "Found " + availableSlotCount + " available slots for the selected time period.\n" +
                            "Please select a slot and click 'Make Reservation' to continue.",
                    "Availability Check",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error checking availability: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void makeReservation() {
        try {
            System.out.println("Starting makeReservation process...");

            // Check if there are available slots
            if (availableSlots == null || availableSlots.isEmpty()) {
                System.out.println("Error: No slots available for reservation");
                JOptionPane.showMessageDialog(this,
                        "No slots available for reservation.",
                        "Reservation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if a slot is selected
            int selectedRow = slotsTable.getSelectedRow();
            System.out.println("Selected row index: " + selectedRow);

            if (selectedRow < 0) {
                System.out.println("Error: No parking slot selected");
                JOptionPane.showMessageDialog(this,
                        "Please select a parking slot.",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if selected slot is available
            String status = (String) slotsTable.getValueAt(selectedRow, 1);
            System.out.println("Selected slot status: " + status);

            if (!"Available".equals(status)) {
                System.out.println("Error: Selected slot is not available");
                JOptionPane.showMessageDialog(this,
                        "Selected slot is not available.",
                        "Reservation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected slot
            ParkingSlot selectedSlot = availableSlots.get(selectedRow);
            System.out.println("Selected slot: " + selectedSlot.getSlotNumber());

            // Check if user has a vehicle selected
            System.out.println("Vehicle combo box selected index: " + vehicleComboBox.getSelectedIndex());
            System.out.println("User vehicles list size: " + (userVehicles != null ? userVehicles.size() : "null"));

            if (vehicleComboBox.getSelectedIndex() < 0 || userVehicles.isEmpty()) {
                System.out.println("Error: No vehicle selected");
                int addVehicle = JOptionPane.showConfirmDialog(this,
                        "You don't have any vehicles selected. Would you like to add a vehicle now?",
                        "No Vehicle Selected",
                        JOptionPane.YES_NO_OPTION);

                if (addVehicle == JOptionPane.YES_OPTION) {
                    addNewVehicle();
                }
                return;
            }

            // Get selected vehicle
            String selectedVehicleId = (String) vehicleComboBox.getSelectedItem();
            System.out.println("Selected vehicle ID: " + selectedVehicleId);

            // Get date and time for reservation
            LocalDateTime startDateTime = getStartDateTime();
            LocalDateTime endDateTime = getEndDateTime();
            System.out.println("Start date/time: " + startDateTime);
            System.out.println("End date/time: " + endDateTime);

            // Validate date and time one more time
            if (startDateTime.isAfter(endDateTime)) {
                System.out.println("Error: Start time after end time");
                JOptionPane.showMessageDialog(this,
                        "End time must be after start time.",
                        "Reservation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Error: Start time in the past");
                JOptionPane.showMessageDialog(this,
                        "Start time cannot be in the past.",
                        "Reservation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("All validation passed, creating ReservationView...");

            // Open reservation details view for confirmation
            new ReservationView(currentUser, parkingSpace, selectedSlot, selectedVehicleId,
                    startDateTime, endDateTime);

            System.out.println("ReservationView created, closing ParkingMapView");

            // Close this window
            dispose();

        } catch (Exception ex) {
            System.out.println("EXCEPTION in makeReservation: " + ex.getMessage());
            ex.printStackTrace();  // Print the stack trace for more detailed error info
            JOptionPane.showMessageDialog(this,
                    "Error making reservation: " + ex.getMessage(),
                    "Reservation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewVehicle() {
        // Open dialog to add a new vehicle
        String vehicleId = JOptionPane.showInputDialog(this,
                "Enter your vehicle license plate number:",
                "Add New Vehicle",
                JOptionPane.PLAIN_MESSAGE);

        if (vehicleId != null && !vehicleId.trim().isEmpty()) {
            // Create and add new vehicle
            Vehicle newVehicle = new Vehicle(vehicleId.trim(), currentUser.getUserID());
            userVehicles.add(newVehicle);

            // Update vehicle combobox
            vehicleComboBox.addItem(vehicleId.trim());
            vehicleComboBox.setSelectedItem(vehicleId.trim());

            JOptionPane.showMessageDialog(this,
                    "Vehicle added successfully!",
                    "Vehicle Added",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // Custom JDatePicker class for better date selection
    private class JDatePicker extends JPanel {
        private JComboBox<Integer> dayComboBox;
        private JComboBox<String> monthComboBox;
        private JComboBox<Integer> yearComboBox;

        public JDatePicker(LocalDate initialDate) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

            // Days 1-31
            Integer[] days = new Integer[31];
            for (int i = 0; i < 31; i++) {
                days[i] = i + 1;
            }
            dayComboBox = new JComboBox<>(days);

            // Months by name
            String[] months = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            monthComboBox = new JComboBox<>(months);

            // Years (current + 2 years ahead)
            int currentYear = LocalDate.now().getYear();
            Integer[] years = new Integer[3];
            for (int i = 0; i < 3; i++) {
                years[i] = currentYear + i;
            }
            yearComboBox = new JComboBox<>(years);

            // Set initial date
            dayComboBox.setSelectedItem(initialDate.getDayOfMonth());
            monthComboBox.setSelectedIndex(initialDate.getMonthValue() - 1);
            yearComboBox.setSelectedItem(initialDate.getYear());

            // Add components
            add(dayComboBox);
            add(monthComboBox);
            add(yearComboBox);
        }

        public LocalDate getDate() {
            int day = (Integer) dayComboBox.getSelectedItem();
            int month = monthComboBox.getSelectedIndex() + 1;
            int year = (Integer) yearComboBox.getSelectedItem();

            // Handle invalid dates (e.g., February 30)
            try {
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                // Return last day of month if date is invalid
                return LocalDate.of(year, month, 1).withDayOfMonth(
                        LocalDate.of(year, month, 1).lengthOfMonth());
            }
        }
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