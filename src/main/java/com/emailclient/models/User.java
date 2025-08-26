package com.emailclient.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Model Class
 * Represents a user in the email client system
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String domain;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private List<Folder> folders;
    private List<Email> sentEmails;
    private List<Email> receivedEmails;
    
    // Constructors
    public User() {
        this.folders = new ArrayList<>();
        this.sentEmails = new ArrayList<>();
        this.receivedEmails = new ArrayList<>();
        this.isActive = true;
    }
    
    public User(String name, String email, String password, String domain) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.domain = domain;
        this.createdAt = LocalDateTime.now();
    }
    
    public User(int userId, String name, String email, String password, String domain) {
        this(name, email, password, domain);
        this.userId = userId;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public List<Folder> getFolders() {
        return folders;
    }
    
    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
    
    public List<Email> getSentEmails() {
        return sentEmails;
    }
    
    public void setSentEmails(List<Email> sentEmails) {
        this.sentEmails = sentEmails;
    }
    
    public List<Email> getReceivedEmails() {
        return receivedEmails;
    }
    
    public void setReceivedEmails(List<Email> receivedEmails) {
        this.receivedEmails = receivedEmails;
    }
    
    // Business Logic Methods
    
    /**
     * Add a folder to the user's folder list
     * @param folder The folder to add
     */
    public void addFolder(Folder folder) {
        if (folder != null && !folders.contains(folder)) {
            folders.add(folder);
            folder.setUserId(this.userId);
        }
    }
    
    /**
     * Remove a folder from the user's folder list
     * @param folder The folder to remove
     */
    public void removeFolder(Folder folder) {
        folders.remove(folder);
    }
    
    /**
     * Get folder by name
     * @param folderName The name of the folder
     * @return Folder object or null if not found
     */
    public Folder getFolderByName(String folderName) {
        return folders.stream()
                .filter(folder -> folder.getFolderName().equals(folderName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get inbox folder
     * @return Inbox folder or null if not found
     */
    public Folder getInboxFolder() {
        return getFolderByName("Inbox");
    }
    
    /**
     * Get sent folder
     * @return Sent folder or null if not found
     */
    public Folder getSentFolder() {
        return getFolderByName("Sent");
    }
    
    /**
     * Get drafts folder
     * @return Drafts folder or null if not found
     */
    public Folder getDraftsFolder() {
        return getFolderByName("Drafts");
    }
    
    /**
     * Get trash folder
     * @return Trash folder or null if not found
     */
    public Folder getTrashFolder() {
        return getFolderByName("Trash");
    }
    
    /**
     * Add an email to sent emails list
     * @param email The email to add
     */
    public void addSentEmail(Email email) {
        if (email != null && !sentEmails.contains(email)) {
            sentEmails.add(email);
        }
    }
    
    /**
     * Add an email to received emails list
     * @param email The email to add
     */
    public void addReceivedEmail(Email email) {
        if (email != null && !receivedEmails.contains(email)) {
            receivedEmails.add(email);
        }
    }
    
    /**
     * Update last login time
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", domain='" + domain + '\'' +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", isActive=" + isActive +
                ", foldersCount=" + folders.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId && email.equals(user.email);
    }
    
    @Override
    public int hashCode() {
        return email.hashCode();
    }
}