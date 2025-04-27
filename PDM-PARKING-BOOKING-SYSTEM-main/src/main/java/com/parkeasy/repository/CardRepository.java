package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for handling card data access operations
 */
public class CardRepository {

    /**
     * Gets all cards associated with a specific user
     *
     * @param userId The ID of the user
     * @return List of cards belonging to the user
     */
    public List<Card> getCardsByUserId(int userId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM CARD WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Card card = new Card(
                            resultSet.getString("CardNumber"),
                            resultSet.getString("ValidTo"),
                            resultSet.getString("CardHolder"),
                            resultSet.getInt("UserID")
                    );
                    cards.add(card);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving cards for user: " + e.getMessage());
            e.printStackTrace();
        }

        return cards;
    }

    /**
     * Gets a specific card by its number
     *
     * @param cardNumber The unique card number
     * @return Card object if found, null otherwise
     */
    public Card getCardByNumber(String cardNumber) {
        String sql = "SELECT * FROM CARD WHERE CardNumber = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cardNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Card(
                            resultSet.getString("CardNumber"),
                            resultSet.getString("ValidTo"),
                            resultSet.getString("CardHolder"),
                            resultSet.getInt("UserID")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving card: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Inserts a new card into the database
     *
     * @param card The card to insert
     * @return true if insertion successful, false otherwise
     */
    public boolean insertCard(Card card) {
        String sql = "INSERT INTO CARD (CardNumber, ValidTo, CardHolder, UserID) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if card already exists
            if (cardExists(card.getCardNumber())) {
                return false;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, card.getCardNumber());
                preparedStatement.setString(2, card.getValidTo());
                preparedStatement.setString(3, card.getCardHolder());
                preparedStatement.setInt(4, card.getUserID());

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting card: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing card in the database
     *
     * @param cardNumber The card number to update
     * @param card The updated card information
     * @return true if update successful, false otherwise
     */
    public boolean updateCard(String cardNumber, Card card) {
        String sql = "UPDATE CARD SET ValidTo = ?, CardHolder = ?, UserID = ? WHERE CardNumber = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, card.getValidTo());
            preparedStatement.setString(2, card.getCardHolder());
            preparedStatement.setInt(3, card.getUserID());
            preparedStatement.setString(4, cardNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating card: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a card from the database
     *
     * @param cardNumber The card number to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCard(String cardNumber) {
        String sql = "DELETE FROM CARD WHERE CardNumber = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cardNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting card: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a card with a specific number exists
     *
     * @param cardNumber The card number to check
     * @return true if the card exists, false otherwise
     */
    public boolean cardExists(String cardNumber) {
        String sql = "SELECT COUNT(*) FROM CARD WHERE CardNumber = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cardNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if card exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets the number of cards owned by a user
     *
     * @param userId The ID of the user
     * @return The count of cards owned by the user
     */
    public int getCardCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM CARD WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting card count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Checks if a user is the owner of a specific card
     *
     * @param cardNumber The card number
     * @param userId The ID of the user
     * @return true if the user owns the card, false otherwise
     */
    public boolean isCardOwnedByUser(String cardNumber, int userId) {
        String sql = "SELECT COUNT(*) FROM CARD WHERE CardNumber = ? AND UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cardNumber);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking card ownership: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets all cards in the system (admin function)
     *
     * @return List of all cards
     */
    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM CARD";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getString("CardNumber"),
                        resultSet.getString("ValidTo"),
                        resultSet.getString("CardHolder"),
                        resultSet.getInt("UserID")
                );
                cards.add(card);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all cards: " + e.getMessage());
            e.printStackTrace();
        }

        return cards;
    }
}