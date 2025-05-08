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
 * Includes optimized search with pagination
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

    // Pagination components
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JLabel pageInfoLabel;

    // Search result info
    private JLabel resultCountLabel;

    private List<ParkingSpace> currentParkingSpaces;
    private boolean priceAscending = true;

    // Loading indicator for search operations
    private JLabel statusLabel;

    public ParkingPlotListView(User user) {
        this.currentUser = user;

        // Initialize controller
        parkingSpaceViewController = new ParkingSpaceViewController();

        // Set up the frame
        setTitle("ParkEasy - Search Parking Plots");
        setSize(900, 600);
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

        // Pagination components
        prevPageButton = new JButton("Previous");
        nextPageButton = new JButton("Next");
        pageInfoLabel = new JLabel("Page 1 of 1");

        // Search result info
        resultCountLabel = new JLabel("");

        // Status indicator
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.BLUE);

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
        parkingSpacesTable.setRowHeight(24); // Increase row height for better readability

        // Set column widths
        parkingSpacesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Parking ID
        parkingSpacesTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Address
        parkingSpacesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
        parkingSpacesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Available Slots
        parkingSpacesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Rating

        // Make table sortable by clicking on column headers
        parkingSpacesTable.setAutoCreateRowSorter(true);

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

        // Allow pressing Enter in search field to search
        searchField.addActionListener(new ActionListener() {
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

        // Pagination button listeners
        prevPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPreviousPage();
            }
        });

        nextPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadNextPage();
            }
        });

        // Initially disable pagination buttons
        updatePaginationControls();
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
        searchPanel.add(statusLabel);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortPanel.add(sortByPriceButton);
        sortPanel.add(sortByRatingButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(sortPanel, BorderLayout.EAST);

        // Add result count below search
        JPanel resultInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultInfoPanel.add(resultCountLabel);
        topPanel.add(resultInfoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(parkingSpacesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with navigation and back button
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Pagination controls
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(prevPageButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        bottomPanel.add(paginationPanel, BorderLayout.NORTH);

        // Back button and instructions
        JPanel controlPanel = new JPanel(new BorderLayout());

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.add(backButton);
        controlPanel.add(backButtonPanel, BorderLayout.NORTH);

        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel instructionLabel = new JLabel("Double-click on a parking space to view details and make a reservation");
        instructionPanel.add(instructionLabel);
        controlPanel.add(instructionPanel, BorderLayout.SOUTH);

        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadParkingSpaces() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Loading...");

        // Use SwingWorker for background loading
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.getAllParkingPlots();
            }

            @Override
            protected void done() {
                try {
                    currentParkingSpaces = get();
                    updateTable(currentParkingSpaces);

                    // Update pagination info
                    updatePaginationControls();

                    // Update result count
                    resultCountLabel.setText("Showing all parking spaces");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error loading parking spaces: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
    }

    private void searchParkingSpaces() {
        final String searchTerm = searchField.getText().trim();

        if (searchTerm.isEmpty()) {
            loadParkingSpaces();
            return;
        }

        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Searching...");

        // Use SwingWorker for background searching
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.searchParkingPlots(searchTerm);
            }

            @Override
            protected void done() {
                try {
                    currentParkingSpaces = get();
                    updateTable(currentParkingSpaces);

                    // Update pagination info
                    updatePaginationControls();

                    // Update result count
                    int totalResults = parkingSpaceViewController.getTotalPages() * 10; // Approximate
                    resultCountLabel.setText("Found " + totalResults + " results for \"" + searchTerm + "\"");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error searching parking spaces: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
    }

    private void loadNextPage() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Loading next page...");

        // Use SwingWorker for background loading
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.loadNextPage();
            }

            @Override
            protected void done() {
                try {
                    List<ParkingSpace> nextPageSpaces = get();
                    if (!nextPageSpaces.isEmpty()) {
                        currentParkingSpaces = nextPageSpaces;
                        updateTable(currentParkingSpaces);
                    }

                    // Update pagination controls
                    updatePaginationControls();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error loading next page: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
    }

    private void loadPreviousPage() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Loading previous page...");

        // Use SwingWorker for background loading
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.loadPreviousPage();
            }

            @Override
            protected void done() {
                try {
                    List<ParkingSpace> prevPageSpaces = get();
                    if (!prevPageSpaces.isEmpty()) {
                        currentParkingSpaces = prevPageSpaces;
                        updateTable(currentParkingSpaces);
                    }

                    // Update pagination controls
                    updatePaginationControls();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error loading previous page: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
    }

    private void updatePaginationControls() {
        int currentPage = parkingSpaceViewController.getCurrentPage();
        int totalPages = parkingSpaceViewController.getTotalPages();

        // Page info (1-indexed for display)
        pageInfoLabel.setText("Page " + (currentPage + 1) + " of " + Math.max(1, totalPages));

        // Enable/disable buttons
        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1 && totalPages > 1);
    }

    private void sortByPrice() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Sorting by price...");

        final boolean ascending = priceAscending;

        // Use SwingWorker for background sorting
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.sortParkingPlotsByPrice(ascending);
            }

            @Override
            protected void done() {
                try {
                    currentParkingSpaces = get();
                    updateTable(currentParkingSpaces);

                    // Toggle sorting direction for next click
                    priceAscending = !priceAscending;
                    sortByPriceButton.setText("Sort by Price (" + (priceAscending ? "↑" : "↓") + ")");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error sorting parking spaces by price: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
    }

    private void sortByRating() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Sorting by rating...");

        // Use SwingWorker for background sorting
        SwingWorker<List<ParkingSpace>, Void> worker = new SwingWorker<List<ParkingSpace>, Void>() {
            @Override
            protected List<ParkingSpace> doInBackground() throws Exception {
                return parkingSpaceViewController.sortParkingPlotsByRating();
            }

            @Override
            protected void done() {
                try {
                    currentParkingSpaces = get();
                    updateTable(currentParkingSpaces);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ParkingPlotListView.this,
                            "Error sorting parking spaces by rating: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Reset cursor and status
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("");
                }
            }
        };

        worker.execute();
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
            float averageRating = parkingSpaceViewController.getAverageRating(space.getParkingID());

            Object[] row = {
                    space.getParkingID(),
                    space.getParkingAddress(),
                    String.format("%,.0f VND", space.getCostOfParking()),
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

    private void openParkingSpaceDetails() {
        try {
            int selectedRow = parkingSpacesTable.getSelectedRow();
            if (selectedRow >= 0) {
                // Convert view row index to model row index (in case table is sorted)
                int modelRow = parkingSpacesTable.convertRowIndexToModel(selectedRow);

                String parkingId = (String) parkingSpacesTable.getModel().getValueAt(modelRow, 0);
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
}