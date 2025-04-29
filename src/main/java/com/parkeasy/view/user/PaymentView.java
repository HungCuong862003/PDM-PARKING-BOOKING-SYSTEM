package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.PaymentController;
import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.CardService;
import main.java.com.parkeasy.service.PaymentService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * View for processing payments for reservations
 * Allows users to choose payment method and confirm payment
 */
public class PaymentView extends JFrame {
    private User currentUser;
    private int reservationId;
    private double amount;
    
    private JLabel amountLabel;
    private JPanel paymentMethodPanel;
    private JRadioButton balanceRadioButton;
    private JRadioButton cardRadioButton;
    private ButtonGroup paymentMethodGroup;
    
    private JPanel cardSelectionPanel;
    private JComboBox<String> cardComboBox;
    private JButton addCardButton;
    
    private JButton payButton;
    private JButton cancelButton;
    
    private PaymentController paymentController;
    
    public PaymentView(User user, int reservationId, double amount) {
        this.currentUser = user;
        this.reservationId = reservationId;
        this.amount = amount;
        
        // Initialize services and controller
        UserService userService = new UserService();
        CardService cardService = new CardService();
        PaymentService paymentService = new PaymentService();
        ReservationService reservationService = new ReservationService();
        
        paymentController = new PaymentController(
            paymentService, cardService, reservationService, userService);
        
        // Set up the frame
        setTitle("ParkEasy - Payment");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load user's cards
        loadUserCards();
        
        // Make the frame visible
        setVisible(true);
    }
    
    private void initComponents() {
        amountLabel = new JLabel(String.format("Amount to Pay: $%.2f", amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Payment method selection
        paymentMethodPanel = new JPanel();
        paymentMethodPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        
        balanceRadioButton = new JRadioButton("Use Balance");
        cardRadioButton = new JRadioButton("Use Card");
        
        paymentMethodGroup = new ButtonGroup();
        paymentMethodGroup.add(balanceRadioButton);
        paymentMethodGroup.add(cardRadioButton);
        
        // Default selection
        if (currentUser.getBalance() >= amount) {
            balanceRadioButton.setSelected(true);
        } else {
            cardRadioButton.setSelected(true);
            balanceRadioButton.setEnabled(false);
        }
        
        // Card selection
        cardSelectionPanel = new JPanel();
        cardSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Card"));
        
        cardComboBox = new JComboBox<>();
        addCardButton = new JButton("Add New Card");
        
        // Action buttons
        payButton = new JButton("Pay Now");
        cancelButton = new JButton("Cancel");
        
        // Add action listeners
        cardRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardSelectionPanel.setVisible(true);
                pack();
            }
        });
        
        balanceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardSelectionPanel.setVisible(false);
                pack();
            }
        });
        
        addCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewCard();
            }
        });
        
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
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
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add amount label
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        amountPanel.add(amountLabel);
        
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add current balance info
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.add(new JLabel(String.format("Current Balance: $%.2f", currentUser.getBalance())));
        
        mainPanel.add(balancePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add payment method selection
        paymentMethodPanel.setLayout(new BoxLayout(paymentMethodPanel, BoxLayout.Y_AXIS));
        paymentMethodPanel.add(balanceRadioButton);
        paymentMethodPanel.add(cardRadioButton);
        
        mainPanel.add(paymentMethodPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add card selection
        cardSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        cardSelectionPanel.add(cardComboBox);
        cardSelectionPanel.add(addCardButton);
        
        // Only show card selection if card payment is selected
        cardSelectionPanel.setVisible(cardRadioButton.isSelected());
        
        mainPanel.add(cardSelectionPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadUserCards() {
        try {
            List<Card> userCards = paymentController.getUserCards(currentUser.getUserID());
            
            cardComboBox.removeAllItems();
            
            if (userCards.isEmpty()) {
                cardComboBox.addItem("No cards available");
                cardRadioButton.setEnabled(false);
                
                // If no cards and insufficient balance, show message
                if (currentUser.getBalance() < amount) {
                    JOptionPane.showMessageDialog(this,
                        "You have insufficient balance and no saved cards.\nPlease add a card to proceed.",
                        "Payment Method Required",
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                cardRadioButton.setEnabled(true);
                for (Card card : userCards) {
                    // Format card number to show only last 4 digits
                    String cardNumber = card.getCardNumber();
                    String lastFour = cardNumber.substring(Math.max(0, cardNumber.length() - 4));
                    String displayText = "Card ending in " + lastFour;
                    
                    cardComboBox.addItem(displayText);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading payment cards: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewCard() {
        // Create a panel for the card information
        JPanel cardPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField cardNumberField = new JTextField(16);
        JTextField cardHolderField = new JTextField(20);
        
        // Month dropdown
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        
        // Year dropdown (current year + 10 years)
        int currentYear = java.time.LocalDate.now().getYear();
        String[] years = new String[10];
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear + i).substring(2); // Get last 2 digits
        }
        JComboBox<String> yearComboBox = new JComboBox<>(years);
        
        // Create expiry date panel
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expiryPanel.add(monthComboBox);
        expiryPanel.add(new JLabel("/"));
        expiryPanel.add(yearComboBox);
        
        cardPanel.add(new JLabel("Card Number:"));
        cardPanel.add(cardNumberField);
        cardPanel.add(new JLabel("Card Holder:"));
        cardPanel.add(cardHolderField);
        cardPanel.add(new JLabel("Expiry Date (MM/YY):"));
        cardPanel.add(expiryPanel);
        
        int result = JOptionPane.showConfirmDialog(this, cardPanel,
            "Add New Card", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String cardNumber = cardNumberField.getText().trim();
            String cardHolder = cardHolderField.getText().trim();
            String month = (String) monthComboBox.getSelectedItem();
            String year = (String) yearComboBox.getSelectedItem();
            
            if (cardNumber.isEmpty() || cardHolder.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all card information.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Create a Card object
                Card newCard = new Card();
                newCard.setCardNumber(cardNumber);
                newCard.setCardHolder(cardHolder);
                
                // Set expiry date (last day of selected month)
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.YEAR, 2000 + Integer.parseInt(year));
                cal.set(java.util.Calendar.MONTH, Integer.parseInt(month) - 1);
                cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
                
                newCard.setValidTo(cal.getTime());
                newCard.setUserID(currentUser.getUserID());
                
                // Save the card
                boolean added = paymentController.addCard(newCard);
                
                if (added) {
                    JOptionPane.showMessageDialog(this,
                        "Card added successfully.",
                        "Card Added",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reload cards
                    loadUserCards();
                    
                    // Select card payment method
                    cardRadioButton.setSelected(true);
                    cardSelectionPanel.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add card.",
                        "Card Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error adding card: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void processPayment() {
        try {
            String paymentMethod;
            String cardNumber = null;
            
            if (balanceRadioButton.isSelected()) {
                paymentMethod = "BALANCE";
            } else {
                paymentMethod = "CARD";
                
                // Get selected card
                int selectedIndex = cardComboBox.getSelectedIndex();
                if (selectedIndex < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a card.",
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get actual card number (this would come from the database in a real application)
                List<Card> userCards = paymentController.getUserCards(currentUser.getUserID());
                if (selectedIndex < userCards.size()) {
                    cardNumber = userCards.get(selectedIndex).getCardNumber();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Invalid card selection.",
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Process the payment
            Map<String, Object> result = paymentController.processPayment(reservationId, cardNumber, paymentMethod);
            
            boolean success = (boolean) result.get("success");
            if (success) {
                double amount = (double) result.get("amount");
                int paymentId = (int) result.get("paymentId");
                
                JOptionPane.showMessageDialog(this,
                    String.format("Payment of $%.2f processed successfully.\nPayment ID: %d", amount, paymentId),
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Get payment receipt
                showReceipt(paymentId);
                
                // Close this window
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to process payment: " + result.get("message"),
                    "Payment Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error processing payment: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReceipt(int paymentId) {
        try {
            Map<String, Object> receiptData = paymentController.getPaymentReceipt(paymentId);
            
            if ((boolean) receiptData.get("success")) {
                StringBuilder receipt = new StringBuilder();
                receipt.append("PAYMENT RECEIPT\n");
                receipt.append("==============\n\n");
                receipt.append("Receipt ID: ").append(receiptData.get("receiptId")).append("\n");
                receipt.append("Date: ").append(receiptData.get("paymentDate")).append("\n\n");
                receipt.append("Parking Location: ").append(receiptData.get("parkingAddress")).append("\n");
                receipt.append("Slot Number: ").append(receiptData.get("slotNumber")).append("\n");
                receipt.append("Reservation ID: ").append(receiptData.get("reservationId")).append("\n\n");
                receipt.append("Start Time: ").append(receiptData.get("startDateTime")).append("\n");
                receipt.append("End Time: ").append(receiptData.get("endDateTime")).append("\n\n");
                receipt.append("Payment Method: ").append(receiptData.get("paymentMethod")).append("\n");
                receipt.append("Amount Paid: $").append(String.format("%.2f", receiptData.get("amount"))).append("\n\n");
                receipt.append("Thank you for using ParkEasy!");
                
                // Show receipt in a dialog
                JTextArea textArea = new JTextArea(receipt.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Payment Receipt",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error generating receipt: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Create mock data for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        mockUser.setBalance(50.0);
        
        SwingUtilities.invokeLater(() -> {
            new PaymentView(mockUser, 1001, 25.0);
        });
    }
}