package com.emailclient.services;

import com.emailclient.dao.UserDAO;
import com.emailclient.models.User;
import com.emailclient.models.WebsiteSignUp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Authentication Service
 * Handles user authentication, registration, and security-related operations
 */
public class AuthService {
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Register a new user
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param domain Email domain
     * @param ipAddress User's IP address (optional)
     * @param userAgent User's browser agent (optional)
     * @return true if registration successful, false otherwise
     */
    public boolean register(String name, String email, String password, String domain, 
                          String ipAddress, String userAgent) {
        
        // Input validation
        if (!isValidInput(name, email, password, domain)) {
            return false;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            System.err.println("Email already exists: " + email);
            return false;
        }
        
        try {
            // Hash the password
            String hashedPassword = hashPassword(password);
            
            // Create user object
            User user = new User(name, email, hashedPassword, domain);
            
            // Save user to database
            int userId = userDAO.createUser(user);
            
            if (userId != -1) {
                // Create signup record
                WebsiteSignUp signUp = new WebsiteSignUp(userId, ipAddress, userAgent);
                userDAO.createSignUpRecord(signUp);
                
                System.out.println("User registered successfully: " + email);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Authenticate user login
     * @param email User's email
     * @param password User's password
     * @return true if login successful, false otherwise
     */
    public boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.err.println("Email and password are required");
            return false;
        }
        
        try {
            // Hash the provided password
            String hashedPassword = hashPassword(password);
            
            // Find user by email and hashed password
            User user = userDAO.findUserByEmailAndPassword(email, hashedPassword);
            
            if (user != null) {
                // Update last login
                userDAO.updateLastLogin(user.getUserId());
                user.updateLastLogin();
                
                // Set current user
                this.currentUser = user;
                
                System.out.println("Login successful for: " + email);
                return true;
            } else {
                System.err.println("Invalid email or password");
            }
            
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getEmail());
            currentUser = null;
        }
    }
    
    /**
     * Check if user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Change user password
     * @param currentPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (!isLoggedIn()) {
            System.err.println("User must be logged in to change password");
            return false;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            System.err.println("New password must be at least 6 characters long");
            return false;
        }
        
        try {
            // Verify current password
            String hashedCurrentPassword = hashPassword(currentPassword);
            if (!hashedCurrentPassword.equals(currentUser.getPassword())) {
                System.err.println("Current password is incorrect");
                return false;
            }
            
            // Hash new password
            String hashedNewPassword = hashPassword(newPassword);
            
            // Update in database
            boolean updated = userDAO.updatePassword(currentUser.getUserId(), hashedNewPassword);
            
            if (updated) {
                currentUser.setPassword(hashedNewPassword);
                System.out.println("Password changed successfully");
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update user profile
     * @param name New name
     * @param email New email (must be unique)
     * @param domain New domain
     * @return true if update successful, false otherwise
     */
    public boolean updateProfile(String name, String email, String domain) {
        if (!isLoggedIn()) {
            System.err.println("User must be logged in to update profile");
            return false;
        }
        
        if (!isValidInput(name, email, null, domain)) {
            return false;
        }
        
        // Check if new email is different and already exists
        if (!email.equals(currentUser.getEmail()) && userDAO.emailExists(email)) {
            System.err.println("Email already exists: " + email);
            return false;
        }
        
        try {
            // Update user object
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setDomain(domain);
            
            // Update in database
            boolean updated = userDAO.updateUser(currentUser);
            
            if (updated) {
                System.out.println("Profile updated successfully");
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deactivate current user account
     * @param password Password confirmation
     * @return true if deactivated successfully, false otherwise
     */
    public boolean deactivateAccount(String password) {
        if (!isLoggedIn()) {
            System.err.println("User must be logged in to deactivate account");
            return false;
        }
        
        try {
            // Verify password
            String hashedPassword = hashPassword(password);
            if (!hashedPassword.equals(currentUser.getPassword())) {
                System.err.println("Password is incorrect");
                return false;
            }
            
            // Deactivate in database
            boolean deactivated = userDAO.deactivateUser(currentUser.getUserId());
            
            if (deactivated) {
                System.out.println("Account deactivated successfully");
                logout();
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error deactivating account: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Validate input parameters
     * @param name User name
     * @param email User email
     * @param password User password (can be null for updates)
     * @param domain Email domain
     * @return true if valid, false otherwise
     */
    private boolean isValidInput(String name, String email, String password, String domain) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Name is required");
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Email is required");
            return false;
        }
        
        if (!isValidEmail(email)) {
            System.err.println("Invalid email format");
            return false;
        }
        
        if (password != null && password.length() < 6) {
            System.err.println("Password must be at least 6 characters long");
            return false;
        }
        
        if (domain == null || domain.trim().isEmpty()) {
            System.err.println("Domain is required");
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid format, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Hash password using SHA-256 with salt
     * @param password Plain text password
     * @return Hashed password
     * @throws NoSuchAlgorithmException if hashing algorithm not available
     */
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        // In a production environment, you should use a proper salt for each password
        // For simplicity, we're using a static salt here
        String salt = "EmailClientSalt2024";
        String saltedPassword = password + salt;
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());
        
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
    
    /**
     * Generate secure random salt
     * @return Random salt string
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Refresh current user data from database
     * @return true if refresh successful, false otherwise
     */
    public boolean refreshCurrentUser() {
        if (!isLoggedIn()) {
            return false;
        }
        
        User refreshedUser = userDAO.findUserById(currentUser.getUserId());
        if (refreshedUser != null) {
            this.currentUser = refreshedUser;
            return true;
        }
        
        return false;
    }
}