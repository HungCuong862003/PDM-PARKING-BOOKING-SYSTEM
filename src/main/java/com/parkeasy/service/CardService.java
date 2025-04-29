package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.repository.CardRepository;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for payment card operations
 */
public class CardService {
    private static final Logger LOGGER = Logger.getLogger(CardService.class.getName());

    private final CardRepository cardRepository;

    /**
     * Constructor with dependency injection
     */
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Default constructor
     */
    public CardService() {
        this.cardRepository = new CardRepository();
    }

    /**
     * Add a new card
     *
     * @param card The card to add
     * @return true if successful, false otherwise
     */
    public boolean addCard(Card card) {
        try {
            LOGGER.log(Level.INFO, "Adding card for user: {0}", card.getUserID());

            // Validate card data
            if (!isValidCard(card)) {
                LOGGER.log(Level.WARNING, "Invalid card data");
                return false;
            }

            // Check if card already exists
            if (cardRepository.cardExists(card.getCardNumber())) {
                LOGGER.log(Level.WARNING, "Card already exists: {0}", card.getCardNumber());
                return false;
            }

            return cardRepository.addCard(card);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding card", e);
            return false;
        }
    }

    /**
     * Get all cards for a user
     *
     * @param userId The ID of the user
     * @return List of cards
     */
    public List<Card> getCardsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting cards for user: {0}", userId);
            return cardRepository.getCardsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting cards for user", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get a card by number
     *
     * @param cardNumber The card number
     * @return The card or null if not found
     */
    public Card getCardByNumber(String cardNumber) {
        try {
            LOGGER.log(Level.INFO, "Getting card by number: {0}", cardNumber);
            return cardRepository.getCardByNumber(cardNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting card by number", e);
            return null;
        }
    }

    /**
     * Update a card
     *
     * @param card The card with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateCard(Card card) {
        try {
            LOGGER.log(Level.INFO, "Updating card: {0}", card.getCardNumber());

            // Validate card data
            if (!isValidCard(card)) {
                LOGGER.log(Level.WARNING, "Invalid card data");
                return false;
            }

            // Check if card exists
            if (!cardRepository.cardExists(card.getCardNumber())) {
                LOGGER.log(Level.WARNING, "Card not found: {0}", card.getCardNumber());
                return false;
            }

            return cardRepository.updateCard(card);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating card", e);
            return false;
        }
    }

    /**
     * Remove a card
     *
     * @param cardNumber The card number to remove
     * @return true if successful, false otherwise
     */
    public boolean removeCard(String cardNumber) {
        try {
            LOGGER.log(Level.INFO, "Removing card: {0}", cardNumber);

            // Check if card exists
            if (!cardRepository.cardExists(cardNumber)) {
                LOGGER.log(Level.WARNING, "Card not found: {0}", cardNumber);
                return false;
            }

            return cardRepository.removeCard(cardNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing card", e);
            return false;
        }
    }

    /**
     * Check if a card exists
     *
     * @param cardNumber The card number to check
     * @return true if exists, false otherwise
     */
    public boolean doesCardExist(String cardNumber) {
        try {
            LOGGER.log(Level.FINE, "Checking if card exists: {0}", cardNumber);
            return cardRepository.cardExists(cardNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if card exists", e);
            return false;
        }
    }

    /**
     * Validate card data
     *
     * @param card The card to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidCard(Card card) {
        // Check for null values
        if (card == null || card.getCardNumber() == null || card.getValidTo() == null ||
                card.getCardHolder() == null || card.getUserID() <= 0) {
            return false;
        }

        // Validate card number (basic validation)
        String cardNumber = card.getCardNumber().replaceAll("\\s|-", "");
        if (!cardNumber.matches("\\d{13,19}")) {
            return false;
        }

        // Check if card is not expired
        Date currentDate = new Date();
        if (card.getValidTo().before(currentDate)) {
            return false;
        }

        // Check card holder name is not empty
        return !card.getCardHolder().trim().isEmpty();
    }

    /**
     * Format card number for display (mask all but last 4 digits)
     *
     * @param cardNumber The card number to format
     * @return Formatted card number
     */
    public String formatCardNumberForDisplay(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "Invalid Card";
        }

        String cleaned = cardNumber.replaceAll("\\s|-", "");
        int length = cleaned.length();

        return "****-****-****-" + cleaned.substring(length - 4);
    }
}