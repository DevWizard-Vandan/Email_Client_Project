import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailService {

    /**
     * Send an email from current user to specified recipient
     * @param email Email object to send
     * @param toUsername Username of recipient
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(Email email, String toUsername) {
        // Validate input
        if (email == null) {
            System.out.println("Email cannot be null!");
            return false;
        }

        if (toUsername == null || toUsername.trim().isEmpty()) {
            System.out.println("Recipient username cannot be empty!");
            return false;
        }

        if (email.getSubject() == null || email.getSubject().trim().isEmpty()) {
            System.out.println("Subject cannot be empty!");
            return false;
        }

        if (email.getBody() == null || email.getBody().trim().isEmpty()) {
            System.out.println("Email body cannot be empty!");
            return false;
        }

        // Get recipient user
        User recipient = DatabaseHelper.getUserByName(toUsername.trim());
        if (recipient == null) {
            System.out.println("Recipient user not found!");
            return false;
        }

        // Check if sending to self
        if (recipient.getUserId() == email.getSenderId()) {
            System.out.println("Cannot send email to yourself!");
            return false;
        }

        // Insert email into database
        boolean success = DatabaseHelper.insertEmail(email, recipient.getUserId());

        if (success) {
            System.out.println("Email sent successfully to " + toUsername + "!");
        } else {
            System.out.println("Failed to send email!");
        }

        return success;
    }

    /**
     * View inbox for a specific user (Console version)
     * @param userId User ID to get inbox for
     */
    public void viewInbox(int userId) {
        List<Email> emails = getEmailsByRole(userId, "Receiver");

        if (emails.isEmpty()) {
            System.out.println("No emails in inbox.");
            return;
        }

        System.out.println("You have " + emails.size() + " email(s) in your inbox:");
        for (Email email : emails) {
            email.displayEmail();
        }
    }

    /**
     * View sent items for a specific user (Console version)
     * @param userId User ID to get sent items for
     */
    public void viewSentItems(int userId) {
        List<Email> emails = getEmailsByRole(userId, "Sender");

        if (emails.isEmpty()) {
            System.out.println("No emails in sent items.");
            return;
        }

        System.out.println("You have " + emails.size() + " email(s) in your sent items:");
        for (Email email : emails) {
            email.displayEmail();
        }
    }

    /**
     * Get emails by user role (Sender/Receiver) - GUI Version
     * @param userId User ID
     * @param role Role (Sender/Receiver)
     * @return List of emails with complete information
     */
    public List<Email> getEmailsByRole(int userId, String role) {
        List<Email> emails = new ArrayList<>();

        String sql = """
            SELECT e.EmailID, e.Subject, e.Body, e.Timestamp,
                   sender.Name as SenderName,
                   receiver.Name as ReceiverName
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
            LEFT JOIN User sender ON senderEU.UserID = sender.UserID
            LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
            LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
            WHERE eu.UserID = ? AND eu.Role = ?
            ORDER BY e.Timestamp DESC
        """;

        try (Connection conn = DatabaseHelper.getConnection();   //AU
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, role);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Email email = new Email(
                        rs.getInt("EmailID"),
                        rs.getString("Subject"),
                        rs.getString("Body"),
                        rs.getTimestamp("Timestamp"),
                        0, // senderId not needed for display
                        rs.getString("SenderName"),
                        rs.getString("ReceiverName")
                );
                emails.add(email);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving emails: " + e.getMessage());
        }

        return emails;
    }

    /**
     * Get inbox emails for GUI
     * @param userId User ID
     * @return List of received emails
     */
    public List<Email> getInboxEmails(int userId) {
        return getEmailsByRole(userId, "Receiver");
    }

    /**
     * Get sent emails for GUI
     * @param userId User ID
     * @return List of sent emails
     */
    public List<Email> getSentEmails(int userId) {
        return getEmailsByRole(userId, "Sender");
    }

    /**
     * Get total email count for a user (inbox + sent)
     * @param userId User ID
     * @return Total email count
     */
    public int getTotalEmailCount(int userId) {
        String sql = """
            SELECT COUNT(*) as total
            FROM EmailUser
            WHERE UserID = ?
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error getting email count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get inbox count for a user
     * @param userId User ID
     * @return Inbox email count
     */
    public int getInboxCount(int userId) {
        String sql = """
            SELECT COUNT(*) as count
            FROM EmailUser
            WHERE UserID = ? AND Role = 'Receiver'
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error getting inbox count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get sent items count for a user
     * @param userId User ID
     * @return Sent email count
     */
    public int getSentCount(int userId) {
        String sql = """
            SELECT COUNT(*) as count
            FROM EmailUser
            WHERE UserID = ? AND Role = 'Sender'
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error getting sent count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Search emails by subject or body content
     * @param userId User ID
     * @param searchTerm Search term
     * @param role Role to search in (Sender/Receiver/null for both)
     * @return List of matching emails
     */
    public List<Email> searchEmails(int userId, String searchTerm, String role) {
        List<Email> emails = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
            SELECT e.EmailID, e.Subject, e.Body, e.Timestamp,
                   sender.Name as SenderName,
                   receiver.Name as ReceiverName
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
            LEFT JOIN User sender ON senderEU.UserID = sender.UserID
            LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
            LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
            WHERE eu.UserID = ? AND (e.Subject LIKE ? OR e.Body LIKE ?)
        """);

        if (role != null) {
            sqlBuilder.append(" AND eu.Role = ?");
        }

        sqlBuilder.append(" ORDER BY e.Timestamp DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setInt(1, userId);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            if (role != null) {
                pstmt.setString(4, role);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Email email = new Email(
                        rs.getInt("EmailID"),
                        rs.getString("Subject"),
                        rs.getString("Body"),
                        rs.getTimestamp("Timestamp"),
                        0, // senderId not needed for display
                        rs.getString("SenderName"),
                        rs.getString("ReceiverName")
                );
                emails.add(email);
            }

        } catch (SQLException e) {
            System.err.println("Error searching emails: " + e.getMessage());
        }

        return emails;
    }
}
