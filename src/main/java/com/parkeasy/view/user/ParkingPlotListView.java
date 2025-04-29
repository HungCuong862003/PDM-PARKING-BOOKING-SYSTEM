package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.ParkingSpaceViewController;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * View for searching and displaying parking plots
 * Allows sorting by price or rating
 */
public class ParkingPlotListView extends JFrame {
    private User currentUser;
    private ParkingSpaceViewController parkingSpaceViewController;
    
    private JTextField searchField;
    private JButton searchButton;
    private JButton sortByPriceButton;
    private JButton sortByRatingButton;
    private JTable parkingSpacesTable;
    private JButton backButton;
    
    private List<ParkingSpace> currentParkingSpaces;
    private boolean priceAscending = true;
    
    public ParkingPlotListView(User user) {
        this.currentUser = user;
        
        // Initialize controller
        parkingSpaceViewController = new ParkingSpaceViewController();
        
        // Set up the frame
        setTitle("ParkEasy - Search Parking Plots");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load initial data
        loadParkingSpaces();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        sortByPriceButton = new JButton("Sort by Price");
        sortByRatingButton = new JButton("Sort by Rating");
        backButton = new JButton("Back to Dashboard");
        
        // Create table for parking spaces
        String[] columnNames = {"Parking ID", "Address", "Price per Hour", "Available Slots", "Rating"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        parkingSpacesTable = new JTable(model);
        parkingSpacesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add double-click listener to open parking space details
        parkingSpacesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openParkingSpaceDetails();
                }
            }
        });
        
        // Add action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchParkingSpaces();
            }
        });
        
        sortByPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortByPrice();
            }
        });
        
        sortByRatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortByRating();
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
        
        // Top panel with search field and buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortPanel.add(sortByPriceButton);
        sortPanel.add(sortByRatingButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(sortPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(parkingSpacesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add instruction panel
        JPanel instructionPanel = new JPanel();
        JLabel instructionLabel = new JLabel("Double-click on a parking space to view details and make a reservation");
        instructionPanel.add(instructionLabel);
        add(instructionPanel, BorderLayout.SOUTH);
    }
    
    private void loadParkingSpaces() {
        try {
            currentParkingSpaces = parkingSpaceViewController.getAllParkingPlots();
            updateTable(currentParkingSpaces);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading parking spaces: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchParkingSpaces() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadParkingSpaces();
            return;
        }
        
        try {
            currentParkingSpaces = parkingSpaceViewController.searchParkingPlots(searchTerm);
            updateTable(currentParkingSpaces);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error searching parking spaces: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sortByPrice() {
        try {
            currentParkingSpaces = parkingSpaceViewController.sortParkingPlotsByPrice(priceAscending);
            updateTable(currentParkingSpaces);
            
            // Toggle sorting direction for next click
            priceAscending = !priceAscending;
            sortByPriceButton.setText("Sort by Price (" + (priceAscending ? "↑" : "↓") + ")");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error sorting parking spaces by price: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sortByRating() {
        try {
            currentParkingSpaces = parkingSpaceViewController.sortParkingPlotsByRating();
            updateTable(currentParkingSpaces);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error sorting parking spaces by rating: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<ParkingSpace> parkingSpaces) {
        DefaultTableModel model = (DefaultTableModel) parkingSpacesTable.getModel();
        model.setRowCount(0); // Clear table
        
        if (parkingSpaces == null || parkingSpaces.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No parking spaces found.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        for (ParkingSpace space : parkingSpaces) {
            // Get available slots count
            int availableSlots = calculateAvailableSlots(space);
            
            // Get average rating
            double averageRating = getAverageRating(space.getParkingID());
            
            Object[] row = {
                space.getParkingID(),
                space.getParkingAddress(),
                String.format("$%.2f", space.getCostOfParking()),
                availableSlots + "/" + space.getNumberOfSlots(),
                String.format("%.1f", averageRating)
            };
            
            model.addRow(row);
        }
    }
    
    private int calculateAvailableSlots(ParkingSpace space) {
        // This would typically use a service to get real data
        // For this example, we'll use a placeholder calculation
        return space.getNumberOfSlots() / 2; // Simulating 50% occupancy
    }
    
    private double getAverageRating(String parkingId) {
        // This would typically use a service to get real data
        // For this example, we'll generate a random rating between 1.0 and 5.0
        return parkingSpaceViewController.getAverageRating(parkingId);
    }
    
    private void openParkingSpaceDetails() {
        try {
            int selectedRow = parkingSpacesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String parkingId = (String) parkingSpacesTable.getValueAt(selectedRow, 0);
                ParkingSpace space = getParkingSpaceById(parkingId);
                
                if (space != null) {
                    new ParkingMapView(currentUser, space);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error opening parking space details: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private ParkingSpace getParkingSpaceById(String parkingId) {
        for (ParkingSpace space : currentParkingSpaces) {
            if (space.getParkingID().equals(parkingId)) {
                return space;
            }
        }
        return null;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        
        SwingUtilities.invokeLater(() -> {
            new ParkingPlotListView(mockUser);
        });
    }
}