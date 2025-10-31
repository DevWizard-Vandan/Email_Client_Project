package services;

import entities.Attachment;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AttachmentService - File Attachment Management Service
 * 
 * Handles file attachment operations including upload, download,
 * and deletion. Stores files in the file system and metadata in database.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class AttachmentService {
    
    private DatabaseHelper dbHelper;
    private static final String ATTACHMENT_BASE_PATH = "attachments";
    
    public AttachmentService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        initializeAttachmentDirectory();
    }
    
    /**
     * Initialize attachment directory
     */
    private void initializeAttachmentDirectory() {
        try {
            File baseDir = new File(ATTACHMENT_BASE_PATH);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
                System.out.println("✓ Created attachments directory");
            }
        } catch (Exception e) {
            System.err.println("Error creating attachments directory: " + e.getMessage());
        }
    }
    
    /**
     * Save attachment to file system and database
     */
    public boolean saveAttachment(int emailId, File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            System.err.println("Invalid file");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtGetUser = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            // Get user ID from email
            String sqlGetUser = "SELECT UserID FROM EmailUser WHERE EmailID = ? AND Role = 'Sender' LIMIT 1";
            pstmtGetUser = conn.prepareStatement(sqlGetUser);
            pstmtGetUser.setInt(1, emailId);
            rs = pstmtGetUser.executeQuery();
            
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt("UserID");
            } else {
                System.err.println("Email not found");
                return false;
            }
            
            // Create user-specific directory
            File userDir = new File(ATTACHMENT_BASE_PATH + File.separator + "user_" + userId);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            
            // Generate unique filename
            long timestamp = System.currentTimeMillis();
            String uniqueFilename = timestamp + "_" + file.getName();
            String destPath = userDir.getPath() + File.separator + uniqueFilename;
            
            // Copy file
            Files.copy(file.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
            
            // Save metadata to database
            String sql = "INSERT INTO Attachment (EmailID, FileName, FileSize, MimeType, FilePath) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emailId);
            pstmt.setString(2, file.getName());
            pstmt.setLong(3, file.length());
            pstmt.setString(4, getMimeType(file.getName()));
            pstmt.setString(5, destPath);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("Attachment saved: " + file.getName());
                return true;
            }
            
            return false;
            
        } catch (SQLException | IOException e) {
            System.err.println("Error saving attachment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmtGetUser);
