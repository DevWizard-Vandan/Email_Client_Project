package services;

import entities.User;
import java.sql.*;

/**
 * UserService - User Management Service
 * 
 * Handles user authentication, registration, and profile management.
 * Provides input validation and session management.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class UserService {
    
    private DatabaseHelper dbHelper;
    
    public UserService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    /**
     * User signup with validation
     */
    public boolean signup(User user) {
        // Validate input
        if (!isValidInput(user)) {
            System.err.println("Invalid user input for signup");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            
            // Check if username already exists
            if (getUserByUsername(user.getName()) != null) {
                System.err.println("Username already exists: " + user.getName());
                return false;
            }
            
            String sql = "INSERT INTO User (Name, Password, PersonalDetails) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getPersonalDetails());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("User registered successfully: " + user.getName());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * User login with credential verification
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.isEmpty()) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "SELECT * FROM User WHERE Name = ? AND Password = ? AND IsActive = TRUE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setName(rs.getString("Name"));
                user.setPassword(rs.getString("Password"));
                user.setPersonalDetails(rs.getString("PersonalDetails"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setLastLogin(rs.getTimestamp("LastLogin"));
                user.setActive(rs.getBoolean("IsActive"));
                
                System.out.println("Login successful for user: " + username);
                return user;
            }
            
            System.out.println("Login failed for user: " + username);
            return null;
            
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Logout user
     */
    public void logout(User user) {
        if (user != null) {
            System.out.println("User logged out: " + user.getName());
        }
    }
    
    /**
     * Update last login timestamp
     */
    public boolean updateLastLogin(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "UPDATE User SET LastLogin = CURRENT_TIMESTAMP WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "SELECT * FROM User WHERE Name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setName(rs.getString("Name"));
                user.setPassword(rs.getString("Password"));
                user.setPersonalDetails(rs.getString("PersonalDetails"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setLastLogin(rs.getTimestamp("LastLogin"));
                user.setActive(rs.getBoolean("IsActive"));
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            return null;
        } finally {
            dbHelper.closeResultSet(rs);
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
    
    /**
     * Validate user input
     */
    public boolean isValidInput(User user) {
        if (user == null) {
            return false;
        }
        
        // Username validation (3+ characters)
        if (user.getName() == null || user.getName().trim().length() < 3) {
            return false;
        }
        
        // Password validation (4+ characters)
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Update user profile
     */
    public boolean updateProfile(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbHelper.getConnection();
            String sql = "UPDATE User SET PersonalDetails = ? WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getPersonalDetails());
            pstmt.setInt(2, user.getUserId());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeStatement(pstmt);
            dbHelper.closeConnection(conn);
        }
    }
}
