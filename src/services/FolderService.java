package services;

import entities.Folder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FolderService - Folder Management Service
 * 
 * Handles folder CRUD operations, email organization,
 * and folder hierarchy management.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class FolderService {
    
    private DatabaseHelper dbHelper;
    
    public FolderService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    /**
     * Create a new folder
     */
    public boolean createFolder(int userId, String name, Integer parentId, String color) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "INSERT INTO Folder (UserID, Name, ParentFolderID, Color, IsSystem) VALUES (?, ?, ?, ?, FALSE)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            if (parentId != null) {
                pstmt.setInt(3, parentId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, color != null ? color : "#3498db");
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("Folder created: " + name);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating folder: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Get all folders for a user
     */
    public List<Folder> getUserFolders(int userId) {
        List<Folder> folders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT f.*, " +
                        "(SELECT COUNT(*) FROM EmailUser eu WHERE eu.FolderID = f.FolderID AND eu.IsDeleted = FALSE) as EmailCount, " +
                        "(SELECT COUNT(*) FROM EmailUser eu WHERE eu.FolderID = f.FolderID AND eu.IsRead = FALSE AND eu.IsDeleted = FALSE) as UnreadCount " +
                        "FROM Folder f " +
                        "WHERE f.UserID = ? " +
                        "ORDER BY f.IsSystem DESC, f.Name ASC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Folder folder = new Folder();
                folder.setFolderId(rs.getInt("FolderID"));
                folder.setUserId(rs.getInt("UserID"));
                folder.setName(rs.getString("Name"));
                
                int parentId = rs.getInt("ParentFolderID");
                folder.setParentFolderId(rs.wasNull() ? null : parentId);
                
                folder.setCreatedAt(rs.getTimestamp("CreatedAt"));
                folder.setColor(rs.getString("Color"));
                folder.setSystem(rs.getBoolean("IsSystem"));
                folder.setEmailCount(rs.getInt("EmailCount"));
                folder.setUnreadCount(rs.getInt("UnreadCount"));
                
                folders.add(folder);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user folders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return folders;
    }
    
    /**
     * Get inbox folder for user
     */
    public Folder getInboxFolder(int userId) {
        return getFolderByName(userId, "Inbox");
    }
    
    /**
     * Get folder by name
     */
    private Folder getFolderByName(int userId, String name) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT * FROM Folder WHERE UserID = ? AND Name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Folder folder = new Folder();
                folder.setFolderId(rs.getInt("FolderID"));
                folder.setUserId(rs.getInt("UserID"));
                folder.setName(rs.getString("Name"));
                
                int parentId = rs.getInt("ParentFolderID");
                folder.setParentFolderId(rs.wasNull() ? null : parentId);
                
                folder.setCreatedAt(rs.getTimestamp("CreatedAt"));
                folder.setColor(rs.getString("Color"));
                folder.setSystem(rs.getBoolean("IsSystem"));
                
                return folder;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting folder by name: " + e.getMessage());
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return null;
    }
    
    /**
     * Get folder by ID
     */
    public Folder getFolderById(int folderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT * FROM Folder WHERE FolderID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, folderId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Folder folder = new Folder();
                folder.setFolderId(rs.getInt("FolderID"));
                folder.setUserId(rs.getInt("UserID"));
                folder.setName(rs.getString("Name"));
                
                int parentId = rs.getInt("ParentFolderID");
                folder.setParentFolderId(rs.wasNull() ? null : parentId);
                
                folder.setCreatedAt(rs.getTimestamp("CreatedAt"));
                folder.setColor(rs.getString("Color"));
                folder.setSystem(rs.getBoolean("IsSystem"));
                
                return folder;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting folder by ID: " + e.getMessage());
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return null;
    }
    
    /**
     * Move email to folder
     */
    public boolean moveEmailToFolder(int emailId, int userId, int folderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "UPDATE EmailUser SET FolderID = ? WHERE EmailID = ? AND UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, folderId);
            pstmt.setInt(2, emailId);
            pstmt.setInt(3, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error moving email to folder: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Delete folder (non-system only)
     */
    public boolean deleteFolder(int folderId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            // Check if folder is system folder
            String sqlCheck = "SELECT IsSystem FROM Folder WHERE FolderID = ? AND UserID = ?";
            pstmt = conn.prepareStatement(sqlCheck);
            pstmt.setInt(1, folderId);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("IsSystem")) {
                System.err.println("Cannot delete system folder");
                return false;
            }
            
            rs.close();
            pstmt.close();
            
            // Delete folder
            String sql = "DELETE FROM Folder WHERE FolderID = ? AND UserID = ? AND IsSystem = FALSE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, folderId);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting folder: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Rename folder (non-system only)
     */
    public boolean renameFolder(int folderId, String newName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "UPDATE Folder SET Name = ? WHERE FolderID = ? AND IsSystem = FALSE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newName);
            pstmt.setInt(2, folderId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error renaming folder: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Get email count for folder
     */
    public int getFolderEmailCount(int folderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            
            String sql = "SELECT COUNT(*) FROM EmailUser WHERE FolderID = ? AND IsDeleted = FALSE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, folderId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting folder email count: " + e.getMessage());
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
        
        return 0;
    }
}
