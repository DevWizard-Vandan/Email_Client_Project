package services;

import entities.Email;
import entities.EmailStats;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailService - Email Management Service
 * 
 * Handles all email CRUD operations, search functionality,
 * and email statistics. Implements transactional email sending.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class EmailService {
    
    private DatabaseHelper dbHelper;
    
    public EmailService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    /**
     * Send email with transaction management
     */
    public boolean sendEmail(Email email, int senderId, String recipientUsername) {
        Connection conn = null;
        PreparedStatement pstmtEmail = null;
        PreparedStatement pstmtSender = null;
        PreparedStatement pstmtReceiver = null;
        PreparedStatement pstmtGetUser = null;
        PreparedStatement pstmtGetFolder = null;
        ResultSet rsEmail = null;
        ResultSet rsUser = null;
        ResultSet rsFolder = null;
        
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Validate recipient exists
            String sqlGetUser = "SELECT UserID FROM User WHERE Name = ?";
            pstmtGetUser = conn.prepareStatement(sqlGetUser);
            pstmtGetUser.setString(1, recipientUsername);
            rsUser = pstmtGetUser.executeQuery();
            
            if (!rsUser.next()) {
                System.err.println("Recipient not found: " + recipientUsername);
                return false;
            }
            
            int receiverId = rsUser.getInt("UserID");
            
            // Cannot send to self
            if (senderId == receiverId) {
                System.err.println("Cannot send email to yourself");
                return false;
            }
            
            // Insert email
            String sqlEmail = "INSERT INTO Email (Subject, Body, Priority) VALUES (?, ?, ?)";
            pstmtEmail = conn.prepareStatement(sqlEmail, Statement.RETURN_GENERATED_KEYS);
            pstmtEmail.setString(1, email.getSubject());
            pstmtEmail.setString(2, email.getBody());
            pstmtEmail.setString(3, email.getPriority());
            pstmtEmail.executeUpdate();
            
            rsEmail = pstmtEmail.getGeneratedKeys();
            if (!rsEmail.next()) {
                throw new SQLException("Failed to get generated email ID");
            }
            
            int emailId = rsEmail.getInt(1);
            email.setEmailId(emailId);
            
            // Get Sent folder for sender
            String sqlGetSentFolder = "SELECT FolderID FROM Folder WHERE UserID = ? AND Name = 'Sent'";
            pstmtGetFolder = conn.prepareStatement(sqlGetSentFolder);
            pstmtGetFolder.setInt(1, senderId);
            rsFolder = pstmtGetFolder.executeQuery();
            
            Integer sentFolderId = null;
            if (rsFolder.next()) {
                sentFolderId = rsFolder.getInt("FolderID");
            }
            rsFolder.close();
            
            // Insert sender record
            String sqlSender = "INSERT INTO EmailUser (EmailID, UserID, Role, FolderID) VALUES (?, ?, 'Sender', ?)";
            pstmtSender = conn.prepareStatement(sqlSender);
            pstmtSender.setInt(1, emailId);
            pstmtSender.setInt(2, senderId);
            if (sentFolderId != null) {
                pstmtSender.setInt(3, sentFolderId);
            } else {
                pstmtSender.setNull(3, Types.INTEGER);
            }
            pstmtSender.executeUpdate();
            
            // Get Inbox folder for receiver
            pstmtGetFolder = conn.prepareStatement("SELECT FolderID FROM Folder WHERE UserID = ? AND Name = 'Inbox'");
            pstmtGetFolder.setInt(1, receiverId);
            rsFolder = pstmtGetFolder.executeQuery();
            
            Integer inboxFolderId = null;
            if (rsFolder.next()) {
                inboxFolderId = rsFolder.getInt("FolderID");
            }
            
            // Insert receiver record
            String sqlReceiver = "INSERT INTO EmailUser (EmailID, UserID, Role, FolderID) VALUES (?, ?, 'Receiver', ?)";
            pstmtReceiver = conn.prepareStatement(sqlReceiver);
            pstmtReceiver.setInt(1, emailId);
            pstmtReceiver.setInt(2, receiverId);
            if (inboxFolderId != null) {
                pstmtReceiver.setInt(3, inboxFolderId);
            } else {
                pstmtReceiver.setNull(3, Types.INTEGER);
            }
            pstmtReceiver.executeUpdate();
            
            conn.commit(); // Commit transaction
            System.out.println("Email sent successfully: ID=" + emailId);
            return true;
            
        } catch (SQLException e) {
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            System.err.println("Send email error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.closeResultSet(rsEmail);
            dbHelper.closeResultSet(rsUser);
            dbHelper.closeResultSet(rsFolder);
            dbHelper.closeStatement(pstmtEmail);
            dbHelper.closeStatement(pstmtSender);
            dbHelper.closeStatement(pstmtReceiver);
            dbHelper.closeStatement(pstmtGetUser);
            dbHelper.closeStatement(pstmtGetFolder);
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting autocommit: " + e.getMessage());
                }
            }
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Get inbox emails for user
     */
    public List<Email> getInboxEmails(int userId) {
        return getEmailsByRole(userId, "Receiver");
    }
    
    /**
     * Get sent emails for user
     */
    public List<Email> getSentEmails(int userId) {
        return getEmailsByRole(userId, "Sender");
    }
    
    /**
     * Get emails by role (Sender/Receiver)
     */
    public List<Email> getEmailsByRole(int userId, String role) {
        List<Email> emails = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql;
            if (role.equals("Sender")) {
                sql = "SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority, e.IsHTML, " +
                      "receiver.Name as ReceiverName, sender.Name as SenderName, " +
                      "eu.IsRead, eu.IsStarred, eu.FolderID, " +
                      "(SELECT COUNT(*) FROM Attachment WHERE EmailID = e.EmailID) as AttachmentCount " +
                      "FROM Email e " +
                      "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                      "JOIN User sender ON eu.UserID = sender.UserID " +
                      "LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver' " +
                      "LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID " +
                      "WHERE eu.UserID = ? AND eu.Role = ? AND eu.IsDeleted = FALSE " +
                      "ORDER BY e.Timestamp DESC";
            } else {
                sql = "SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority, e.IsHTML, " +
                      "sender.Name as SenderName, receiver.Name as ReceiverName, " +
                      "eu.IsRead, eu.IsStarred, eu.FolderID, " +
                      "(SELECT COUNT(*) FROM Attachment WHERE EmailID = e.EmailID) as AttachmentCount " +
                      "FROM Email e " +
                      "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                      "JOIN User receiver ON eu.UserID = receiver.UserID " +
                      "LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender' " +
                      "LEFT JOIN User sender ON eu_sender.UserID = sender.UserID " +
                      "WHERE eu.UserID = ? AND eu.Role = ? AND eu.IsDeleted = FALSE " +
                      "ORDER BY e.Timestamp DESC";
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, role);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Email email = new Email();
                email.setEmailId(rs.getInt("EmailID"));
                email.setSubject(rs.getString("Subject"));
                email.setBody(rs.getString("Body"));
                email.setTimestamp(rs.getTimestamp("Timestamp"));
                email.setPriority(rs.getString("Priority"));
                email.setHTML(rs.getBoolean("IsHTML"));
                email.setSenderName(rs.getString("SenderName"));
                email.setReceiverName(rs.getString("ReceiverName"));
                email.setRead(rs.getBoolean("IsRead"));
                email.setStarred(rs.getBoolean("IsStarred"));
                email.setFolderId(rs.getInt("FolderID"));
                email.setAttachmentCount(rs.getInt("AttachmentCount"));
                
                emails.add(email);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting emails by role: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return emails;
    }
    
    /**
     * Get emails by folder
     */
    public List<Email> getEmailsByFolder(int userId, int folderId) {
        List<Email> emails = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority, e.IsHTML, " +
                        "sender.Name as SenderName, receiver.Name as ReceiverName, " +
                        "eu.IsRead, eu.IsStarred, eu.FolderID, eu.Role, " +
                        "(SELECT COUNT(*) FROM Attachment WHERE EmailID = e.EmailID) as AttachmentCount " +
                        "FROM Email e " +
                        "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                        "LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender' " +
                        "LEFT JOIN User sender ON eu_sender.UserID = sender.UserID " +
                        "LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver' " +
                        "LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID " +
                        "WHERE eu.UserID = ? AND eu.FolderID = ? AND eu.IsDeleted = FALSE " +
                        "ORDER BY e.Timestamp DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, folderId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Email email = new Email();
                email.setEmailId(rs.getInt("EmailID"));
                email.setSubject(rs.getString("Subject"));
                email.setBody(rs.getString("Body"));
                email.setTimestamp(rs.getTimestamp("Timestamp"));
                email.setPriority(rs.getString("Priority"));
                email.setHTML(rs.getBoolean("IsHTML"));
                email.setSenderName(rs.getString("SenderName"));
                email.setReceiverName(rs.getString("ReceiverName"));
                email.setRead(rs.getBoolean("IsRead"));
                email.setStarred(rs.getBoolean("IsStarred"));
                email.setFolderId(rs.getInt("FolderID"));
                email.setAttachmentCount(rs.getInt("AttachmentCount"));
                
                emails.add(email);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting emails by folder: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return emails;
    }
    
    /**
     * Search emails by keyword
     */
    public List<Email> searchEmails(int userId, String searchTerm, String role) {
        List<Email> emails = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority, e.IsHTML, " +
                        "sender.Name as SenderName, receiver.Name as ReceiverName, " +
                        "eu.IsRead, eu.IsStarred, eu.FolderID, " +
                        "(SELECT COUNT(*) FROM Attachment WHERE EmailID = e.EmailID) as AttachmentCount " +
                        "FROM Email e " +
                        "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                        "LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender' " +
                        "LEFT JOIN User sender ON eu_sender.UserID = sender.UserID " +
                        "LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver' " +
                        "LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID " +
                        "WHERE eu.UserID = ? AND eu.Role = ? AND eu.IsDeleted = FALSE " +
                        "AND (e.Subject LIKE ? OR e.Body LIKE ?) " +
                        "ORDER BY e.Timestamp DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, role);
            pstmt.setString(3, "%" + searchTerm + "%");
            pstmt.setString(4, "%" + searchTerm + "%");
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Email email = new Email();
                email.setEmailId(rs.getInt("EmailID"));
                email.setSubject(rs.getString("Subject"));
                email.setBody(rs.getString("Body"));
                email.setTimestamp(rs.getTimestamp("Timestamp"));
                email.setPriority(rs.getString("Priority"));
                email.setHTML(rs.getBoolean("IsHTML"));
                email.setSenderName(rs.getString("SenderName"));
                email.setReceiverName(rs.getString("ReceiverName"));
                email.setRead(rs.getBoolean("IsRead"));
                email.setStarred(rs.getBoolean("IsStarred"));
                email.setFolderId(rs.getInt("FolderID"));
                email.setAttachmentCount(rs.getInt("AttachmentCount"));
                
                emails.add(email);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching emails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return emails;
    }
    
    /**
     * Mark email as read
     */
    public boolean markAsRead(int emailId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "UPDATE EmailUser SET IsRead = TRUE, ReadAt = CURRENT_TIMESTAMP " +
                        "WHERE EmailID = ? AND UserID = ? AND Role = 'Receiver'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emailId);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking as read: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Toggle star status
     */
    public boolean toggleStar(int emailId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "UPDATE EmailUser SET IsStarred = NOT IsStarred WHERE EmailID = ? AND UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emailId);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error toggling star: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Delete email (soft delete)
     */
    public boolean deleteEmail(int emailId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "UPDATE EmailUser SET IsDeleted = TRUE WHERE EmailID = ? AND UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emailId);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting email: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Get email statistics
     */
    public EmailStats getEmailStats(int userId) {
        EmailStats stats = new EmailStats();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            // Total emails
            String sqlTotal = "SELECT COUNT(*) FROM EmailUser WHERE UserID = ? AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlTotal);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setTotalEmails(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            
            // Unread emails
            String sqlUnread = "SELECT COUNT(*) FROM EmailUser WHERE UserID = ? AND Role = 'Receiver' AND IsRead = FALSE AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlUnread);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setUnreadEmails(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            
            // Starred emails
            String sqlStarred = "SELECT COUNT(*) FROM EmailUser WHERE UserID = ? AND IsStarred = TRUE AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlStarred);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setStarredEmails(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            
            // Sent emails
            String sqlSent = "SELECT COUNT(*) FROM EmailUser WHERE UserID = ? AND Role = 'Sender' AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlSent);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setSentEmails(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            
            // Received emails
            String sqlReceived = "SELECT COUNT(*) FROM EmailUser WHERE UserID = ? AND Role = 'Receiver' AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlReceived);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setReceivedEmails(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            
            // Total size
            String sqlSize = "SELECT SUM(LENGTH(e.Body)) FROM Email e " +
                           "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                           "WHERE eu.UserID = ? AND eu.IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sqlSize);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setTotalSize(rs.getLong(1));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting email stats: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return stats;
    }
}
