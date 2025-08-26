package com.emailclient.dao;

import com.emailclient.models.Email;
import com.emailclient.models.User;
import com.emailclient.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Email Data Access Object
 * Handles all database operations related to Email entity
 */
public class EmailDAO {

    /**
     * Create a new email in the database
     * @param email The email to create
     * @param senderId The ID of the sender
     * @param receiverIds List of receiver IDs
     * @param ccIds List of CC user IDs (can be null)
     * @param bccIds List of BCC user IDs (can be null)
     * @return Generated email ID, or -1 if creation failed
     */
    public int createEmail(Email email, int senderId, List<Integer> receiverIds,
                           List<Integer> ccIds, List<Integer> bccIds) {
        String emailSql = "INSERT INTO Email (Subject, Body, Priority, FolderID) VALUES (?, ?, ?, ?)";
        String emailUserSql = "INSERT INTO EmailUser (EmailID, UserID, Role) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert email
            int emailId = -1;
            try (PreparedStatement emailStmt = conn.prepareStatement(emailSql, Statement.RETURN_GENERATED_KEYS)) {
                emailStmt.setString(1, email.getSubject());
                emailStmt.setString(2, email.getBody());
                emailStmt.setString(3, email.getPriority().name());
                emailStmt.setObject(4, email.getFolderId() != 0 ? email.getFolderId() : null);

                int affectedRows = emailStmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = emailStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            emailId = generatedKeys.getInt(1);
                            email.setEmailId(emailId);
                        }
                    }
                }
            }

            if (emailId == -1) {
                conn.rollback();
                return null;
            }

            /**
             * Mark email as read
             * @param emailId The email ID
             * @param userId The user ID
             * @return true if updated successfully, false otherwise
             */
            public boolean markAsRead(int emailId, int userId) {
                String sql = """
            UPDATE Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            SET e.IsRead = true
            WHERE e.EmailID = ? AND eu.UserID = ? AND eu.Role IN ('RECEIVER', 'CC', 'BCC')
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, emailId);
                    pstmt.setInt(2, userId);

                    return pstmt.executeUpdate() > 0;

                } catch (SQLException e) {
                    System.err.println("Error marking email as read: " + e.getMessage());
                    return false;
                }
            }

            /**
             * Mark email as unread
             * @param emailId The email ID
             * @param userId The user ID
             * @return true if updated successfully, false otherwise
             */
            public boolean markAsUnread(int emailId, int userId) {
                String sql = """
            UPDATE Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            SET e.IsRead = false
            WHERE e.EmailID = ? AND eu.UserID = ? AND eu.Role IN ('RECEIVER', 'CC', 'BCC')
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, emailId);
                    pstmt.setInt(2, userId);

                    return pstmt.executeUpdate() > 0;

                } catch (SQLException e) {
                    System.err.println("Error marking email as unread: " + e.getMessage());
                    return false;
                }
            }

            /**
             * Move email to folder
             * @param emailId The email ID
             * @param userId The user ID
             * @param folderId The target folder ID
             * @return true if moved successfully, false otherwise
             */
            public boolean moveToFolder(int emailId, int userId, int folderId) {
                String sql = """
            UPDATE Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            SET e.FolderID = ?
            WHERE e.EmailID = ? AND eu.UserID = ?
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, folderId);
                    pstmt.setInt(2, emailId);
                    pstmt.setInt(3, userId);

                    return pstmt.executeUpdate() > 0;

                } catch (SQLException e) {
                    System.err.println("Error moving email to folder: " + e.getMessage());
                    return false;
                }
            }

            /**
             * Delete email (soft delete)
             * @param emailId The email ID
             * @param userId The user ID
             * @return true if deleted successfully, false otherwise
             */
            public boolean deleteEmail(int emailId, int userId) {
                String sql = """
            UPDATE Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            SET e.IsDeleted = true
            WHERE e.EmailID = ? AND eu.UserID = ?
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, emailId);
                    pstmt.setInt(2, userId);

                    return pstmt.executeUpdate() > 0;

                } catch (SQLException e) {
                    System.err.println("Error deleting email: " + e.getMessage());
                    return false;
                }
            }

            /**
             * Permanently delete email
             * @param emailId The email ID
             * @return true if deleted successfully, false otherwise
             */
            public boolean permanentlyDeleteEmail(int emailId) {
                String sql = "DELETE FROM Email WHERE EmailID = ?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, emailId);
                    return pstmt.executeUpdate() > 0;

                } catch (SQLException e) {
                    System.err.println("Error permanently deleting email: " + e.getMessage());
                    return false;
                }
            }

            /**
             * Search emails by subject or body
             * @param userId The user ID
             * @param searchTerm The search term
             * @return List of matching emails
             */
            public List<Email> searchEmails(int userId, String searchTerm) {
                String sql = """
            SELECT e.*, u.Name as SenderName, u.Email as SenderEmail
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            JOIN EmailUser sender_eu ON e.EmailID = sender_eu.EmailID AND sender_eu.Role = 'SENDER'
            JOIN User u ON sender_eu.UserID = u.UserID
            WHERE eu.UserID = ? AND e.IsDeleted = false
            AND (e.Subject LIKE ? OR e.Body LIKE ?)
            ORDER BY e.SentAt DESC
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, userId);
                    pstmt.setString(2, "%" + searchTerm + "%");
                    pstmt.setString(3, "%" + searchTerm + "%");

                    return executeEmailQuery(pstmt);

                } catch (SQLException e) {
                    System.err.println("Error searching emails: " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            /**
             * Get unread email count for a user
             * @param userId The user ID
             * @return Number of unread emails
             */
            public int getUnreadEmailCount(int userId) {
                String sql = """
            SELECT COUNT(*)
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            WHERE eu.UserID = ? AND eu.Role IN ('RECEIVER', 'CC', 'BCC')
            AND e.IsRead = false AND e.IsDeleted = false
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, userId);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error getting unread email count: " + e.getMessage());
                }
                return 0;
            }

            /**
             * Helper method to execute email queries
             * @param sql The SQL query
             * @param params Query parameters
             * @return List of emails
             */
            private List<Email> getEmailsByQuery(String sql, Object... params) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }

                    return executeEmailQuery(pstmt);

                } catch (SQLException e) {
                    System.err.println("Error executing email query: " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            /**
             * Execute email query and return results
             * @param pstmt Prepared statement
             * @return List of emails
             * @throws SQLException if SQL error occurs
             */
            private List<Email> executeEmailQuery(PreparedStatement pstmt) throws SQLException {
                List<Email> emails = new ArrayList<>();

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Email email = mapResultSetToEmail(rs);
                        // Load recipients for each email
                        loadEmailRecipients(email);
                        emails.add(email);
                    }
                }

                return emails;
            }

            /**
             * Load recipients for an email
             * @param email The email to load recipients for
             */
            private void loadEmailRecipients(Email email) {
                String sql = """
            SELECT u.UserID, u.Name, u.Email, eu.Role
            FROM EmailUser eu
            JOIN User u ON eu.UserID = u.UserID
            WHERE eu.EmailID = ?
            """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, email.getEmailId());

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            User user = new User();
                            user.setUserId(rs.getInt("UserID"));
                            user.setName(rs.getString("Name"));
                            user.setEmail(rs.getString("Email"));

                            String role = rs.getString("Role");
                            switch (role) {
                                case "SENDER":
                                    email.setSender(user);
                                    break;
                                case "RECEIVER":
                                    email.addReceiver(user);
                                    break;
                                case "CC":
                                    email.addCcUser(user);
                                    break;
                                case "BCC":
                                    email.addBccUser(user);
                                    break;
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error loading email recipients: " + e.getMessage());
                }
            }

            /**
             * Map ResultSet to Email object
             * @param rs The ResultSet
             * @return Email object
             * @throws SQLException if SQL error occurs
             */
            private Email mapResultSetToEmail(ResultSet rs) throws SQLException {
                Email email = new Email();
                email.setEmailId(rs.getInt("EmailID"));
                email.setSubject(rs.getString("Subject"));
                email.setBody(rs.getString("Body"));

                Timestamp sentAt = rs.getTimestamp("SentAt");
                if (sentAt != null) {
                    email.setSentAt(sentAt.toLocalDateTime());
                }

                String priorityStr = rs.getString("Priority");
                if (priorityStr != null) {
                    email.setPriority(Email.Priority.valueOf(priorityStr));
                }

                email.setRead(rs.getBoolean("IsRead"));
                email.setDeleted(rs.getBoolean("IsDeleted"));
                email.setFolderId(rs.getInt("FolderID"));

                // Set sender information if available
                try {
                    String senderName = rs.getString("SenderName");
                    String senderEmail = rs.getString("SenderEmail");
                    if (senderName != null && senderEmail != null) {
                        User sender = new User();
                        sender.setName(senderName);
                        sender.setEmail(senderEmail);
                        email.setSender(sender);
                    }
                } catch (SQLException e) {
                    // Sender info might not be in all queries, ignore
                }

                return email;
            }
        } -1;
    }

    // Insert sender relationship
            try (PreparedStatement userStmt = conn.prepareStatement(emailUserSql)) {
        userStmt.setInt(1, emailId);
        userStmt.setInt(2, senderId);
        userStmt.setString(3, "SENDER");
        userStmt.executeUpdate();

        // Insert receiver relationships
        if (receiverIds != null) {
            for (int receiverId : receiverIds) {
                userStmt.setInt(1, emailId);
                userStmt.setInt(2, receiverId);
                userStmt.setString(3, "RECEIVER");
                userStmt.addBatch();
            }
        }

        // Insert CC relationships
        if (ccIds != null) {
            for (int ccId : ccIds) {
                userStmt.setInt(1, emailId);
                userStmt.setInt(2, ccId);
                userStmt.setString(3, "CC");
                userStmt.addBatch();
            }
        }

        // Insert BCC relationships
        if (bccIds != null) {
            for (int bccId : bccIds) {
                userStmt.setInt(1, emailId);
                userStmt.setInt(2, bccId);
                userStmt.setString(3, "BCC");
                userStmt.addBatch();
            }
        }

        userStmt.executeBatch();
    }
            
            conn.commit();
            System.out.println("Email created successfully with ID: " + emailId);
            return emailId;

} catch (SQLException e) {
        if (conn != null) {
        try {
        conn.rollback();
                } catch (SQLException rollbackEx) {
        System.err.println("Error during rollback: " + rollbackEx.getMessage());
        }
        }
        System.err.println("Error creating email: " + e.getMessage());
        return -1;
        } finally {
        if (conn != null) {
        try {
        conn.setAutoCommit(true);
                } catch (SQLException e) {
        System.err.println("Error resetting auto-commit: " + e.getMessage());
        }
        }
        }
        }

/**
 * Get emails for a user's inbox
 * @param userId The user ID
 * @return List of emails in inbox
 */
public List<Email> getInboxEmails(int userId) {
    String sql = """
            SELECT e.*, u.Name as SenderName, u.Email as SenderEmail
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            JOIN EmailUser sender_eu ON e.EmailID = sender_eu.EmailID AND sender_eu.Role = 'SENDER'
            JOIN User u ON sender_eu.UserID = u.UserID
            WHERE eu.UserID = ? AND eu.Role = 'RECEIVER' AND e.IsDeleted = false
            ORDER BY e.SentAt DESC
            """;

    return getEmailsByQuery(sql, userId);
}

/**
 * Get emails sent by a user
 * @param userId The user ID
 * @return List of sent emails
 */
public List<Email> getSentEmails(int userId) {
    String sql = """
            SELECT e.*, u.Name as SenderName, u.Email as SenderEmail
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            JOIN User u ON eu.UserID = u.UserID
            WHERE eu.UserID = ? AND eu.Role = 'SENDER' AND e.IsDeleted = false
            ORDER BY e.SentAt DESC
            """;

    return getEmailsByQuery(sql, userId);
}

/**
 * Get emails in a specific folder
 * @param userId The user ID
 * @param folderId The folder ID
 * @return List of emails in the folder
 */
public List<Email> getEmailsByFolder(int userId, int folderId) {
    String sql = """
            SELECT e.*, u.Name as SenderName, u.Email as SenderEmail
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            JOIN EmailUser sender_eu ON e.EmailID = sender_eu.EmailID AND sender_eu.Role = 'SENDER'
            JOIN User u ON sender_eu.UserID = u.UserID
            WHERE eu.UserID = ? AND e.FolderID = ? AND e.IsDeleted = false
            ORDER BY e.SentAt DESC
            """;

    return getEmailsByQuery(sql, userId, folderId);
}

/**
 * Get a specific email by ID
 * @param emailId The email ID
 * @param userId The user ID (to ensure user has access)
 * @return Email object or null if not found
 */
public Email getEmailById(int emailId, int userId) {
    String sql = """
            SELECT e.*, u.Name as SenderName, u.Email as SenderEmail
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            JOIN EmailUser sender_eu ON e.EmailID = sender_eu.EmailID AND sender_eu.Role = 'SENDER'
            JOIN User u ON sender_eu.UserID = u.UserID
            WHERE e.EmailID = ? AND eu.UserID = ?
            """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, emailId);
        pstmt.setInt(2, userId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Email email = mapResultSetToEmail(rs);
                // Load recipients
                loadEmailRecipients(email);
                return email;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting email by ID: " + e.getMessage());
    }
    return