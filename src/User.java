package entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User Entity Class
 * 
 * Represents a user in the email client system.
 * Contains user credentials, profile information, and related data.
 * 
 * SECURITY WARNING: Passwords are stored in plain text.
 * FOR ACADEMIC/DEMO PURPOSES ONLY.
 * Production systems MUST use proper password hashing (BCrypt, Argon2, etc.)
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class User {
    
    private int userId;
    private String name;
    private String password; // PLAIN TEXT - DEMO ONLY
    private String personalDetails;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    private String profilePicture;
    private List<Folder> folders;
    
    /**
     * Default constructor
     */
    public User() {
        this.isActive = true;
        this.folders = new ArrayList<>();
    }
    
    /**
     * Constructor with name and password
     */
    public User(String name, String password) {
        this();
        this.name = name;
        this.password = password;
    }
    
    /**
     * Full constructor
     */
    public User(int userId, String name, String password, String personalDetails,
                Timestamp createdAt, Timestamp lastLogin, boolean isActive) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.personalDetails = personalDetails;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.folders = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPersonalDetails() {
        return personalDetails;
    }
    
    public void setPersonalDetails(String personalDetails) {
        this.personalDetails = personalDetails;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public List<Folder> getFolders() {
        return folders;
    }
    
    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
    
    /**
     * Add a folder to user's folder list
     */
    public void addFolder(Folder folder) {
        if (this.folders == null) {
            this.folders = new ArrayList<>();
        }
        this.folders.add(folder);
    }
    
    /**
     * Check if username is valid (3+ characters)
     */
    public boolean isValidUsername() {
        return name != null && name.trim().length() >= 3;
    }
    
    /**
     * Check if password is valid (4+ characters)
     */
    public boolean isValidPassword() {
        return password != null && password.length() >= 4;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(name, user.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", personalDetails='" + personalDetails + '\'' +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", isActive=" + isActive +
                '}';
    }
}
