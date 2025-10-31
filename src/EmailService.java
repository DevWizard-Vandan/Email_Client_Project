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
