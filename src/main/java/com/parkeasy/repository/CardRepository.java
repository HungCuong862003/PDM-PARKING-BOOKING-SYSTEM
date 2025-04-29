package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for handling database operations related to payment cards
 */
public class CardRepository {
    private static final Logger LOGGER = Logger.getLogger(CardRepository.class.getName());

    /**
     * Insert a new card into the database
     *
     * @param card The card to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertCard(Card card) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO CARD (CardNumber, ValidTo, CardHolder, UserID) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.setDate(2, new java.sql.Date(card.getValidTo().getTime()));
            preparedStatement.setString(3, card.getCardHolder());
            preparedStatement.setInt(4, card.getUserID());

            int rowsAffected = preparedStatement.executeUpdate();
            success = rowsAffected > 0;

            LOGGER.log(Level.INFO, "Inserted card: {0}, success: {1}", new Object[]{card.getCardNumber(), success});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting card: " + card.getCardNumber(), e);
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return success;
    }

    /**
     * Get all cards for a specific user
     *
     * @param userId The ID of the user
     * @return List of cards belonging to the user
     */
    public List<Card> getCardsByUserId(int userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Card> cards = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM CARD WHERE UserID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Card card = mapResultSetToCard(resultSet);
                cards.add(card);
            }

            LOGGER.log(Level.INFO, "Retrieved {0} cards for user ID: {1}", new Object[]{cards.size(), userId});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cards for user ID: " + userId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return cards;
    }

    /**
     * Get a card by its number
     *
     * @param cardNumber The card number to search for
     * @return The card with the given number, or null if not found
     */
    public Card getCardByNumber(String cardNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Card card = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM CARD WHERE CardNumber = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                card = mapResultSetToCard(resultSet);
                LOGGER.log(Level.INFO, "Retrieved card: {0}", cardNumber);
            } else {
                LOGGER.log(Level.INFO, "No card found with number: {0}", cardNumber);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving card: " + cardNumber, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return card;
    }

    /**
     * Update a card in the database
     *
     * @param card The card with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateCard(Card card) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE CARD SET ValidTo = ?, CardHolder = ? WHERE CardNumber = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, new java.sql.Date(card.getValidTo().getTime()));
            preparedStatement.setString(2, card.getCardHolder());
            preparedStatement.setString(3, card.getCardNumber());

            int rowsAffected = preparedStatement.executeUpdate();
            success = rowsAffected > 0;

            LOGGER.log(Level.INFO, "Updated card: {0}, success: {1}", new Object[]{card.getCardNumber(), success});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating card: " + card.getCardNumber(), e);
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return success;
    }

    /**
     * Delete a card from the database
     *
     * @param cardNumber The card number to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteCard(String cardNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM CARD WHERE CardNumber = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            success = rowsAffected > 0;

            LOGGER.log(Level.INFO, "Deleted card: {0}, success: {1}", new Object[]{cardNumber, success});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting card: " + cardNumber, e);
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return success;
    }

    /**
     * Check if a card exists in the database
     *
     * @param cardNumber The card number to check
     * @return true if the card exists, false otherwise
     */
    public boolean cardExists(String cardNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM CARD WHERE CardNumber = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }

            LOGGER.log(Level.FINE, "Card existence check: {0}, exists: {1}", new Object[]{cardNumber, exists});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if card exists: " + cardNumber, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return exists;
    }

    /**
     * Map a result set row to a Card object
     *
     * @param resultSet The result set to map
     * @return A Card object populated with data from the result set
     * @throws SQLException If an error occurs while accessing the result set
     */
    private Card mapResultSetToCard(ResultSet resultSet) throws SQLException {
        Card card = new Card();
        card.setCardNumber(resultSet.getString("CardNumber"));
        card.setValidTo(resultSet.getDate("ValidTo"));
        card.setCardHolder(resultSet.getString("CardHolder"));
        card.setUserID(resultSet.getInt("UserID"));
        return card;
    }

    /**
     * Remove a card from the database
     *
     * @param cardNumber The card number to remove
     * @return true if the card was removed successfully, false otherwise
     */
    public boolean removeCard(String cardNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM CARD WHERE CardNumber = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Card removed successfully: {0}", cardNumber);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No card found with number: {0}", cardNumber);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing card: " + cardNumber, e);
            return false;
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }
    /**
     * Add a new card to the database
     *
     * @param card The card to add
     * @return true if the card was added successfully, false otherwise
     */
    public boolean addCard(Card card) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO CARD (CardNumber, ValidTo, CardHolder, UserID) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.setDate(2, new java.sql.Date(card.getValidTo().getTime()));
            preparedStatement.setString(3, card.getCardHolder());
            preparedStatement.setInt(4, card.getUserID());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Card added successfully: {0}", card.getCardNumber());
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No rows affected when adding card: {0}", card.getCardNumber());
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding card: " + card.getCardNumber(), e);
            return false;
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }
}