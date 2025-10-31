package entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Folder Entity Class
 * 
 * Represents an email folder for organization.
 * Supports both system folders (Inbox, Sent, etc.) and custom user folders.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class Folder {
    
    private int folderId;
    private int userId;
    private String name;
    private Integer parentFolderId;
    private Timestamp createdAt;
    private String color;
    private boolean isSystem;
    
    // Additional fields for display
    private List<Folder> subFolders;
    private int emailCount;
    private int unreadCount;
    
    /**
     * Default constructor
     */
    public Folder() {
        this.color = "#3498db"; // Default blue color
        this.isSystem = false;
        this.subFolders = new ArrayList<>();
        this.emailCount = 0;
        this.unreadCount = 0;
    }
    
    /**
     * Constructor with basic fields
     */
    public Folder(int userId, String name) {
        this();
        this.userId = userId;
        this.name = name;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor for system folders
     */
    public Folder(int userId, String name, boolean isSystem) {
        this(userId, name);
        this.isSystem = isSystem;
    }
    
    /**
     * Full constructor
     */
    public Folder(int folderId, int userId, String name, Integer parentFolderId,
                  Timestamp createdAt, String color, boolean isSystem) {
        this.folderId = folderId;
        this.userId = userId;
        this.name = name;
        this.parentFolderId = parentFolderId;
        this.createdAt = createdAt;
        this.color = color;
        this.isSystem = isSystem;
        this.subFolders = new ArrayList<>();
    }
    
    /**
     * Constructor with counts
     */
    public Folder(int folderId, int userId, String name, boolean isSystem, 
                  int emailCount, int unreadCount) {
        this();
        this.folderId = folderId;
        this.userId = userId;
        this.name = name;
        this.isSystem = isSystem;
        this.emailCount = emailCount;
        this.unreadCount = unreadCount;
    }
    
    // Getters and Setters
    
    public int getFolderId() {
        return folderId;
    }
    
    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }
    
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
    
    public Integer getParentFolderId() {
        return parentFolderId;
    }
    
    public void setParentFolderId(Integer parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public boolean isSystem() {
        return isSystem;
    }
    
    public void setSystem(boolean system) {
        isSystem = system;
    }
    
    public List<Folder> getSubFolders() {
        return subFolders;
    }
    
    public void setSubFolders(List<Folder> subFolders) {
        this.subFolders = subFolders;
    }
    
    public int getEmailCount() {
        return emailCount;
    }
    
    public void setEmailCount(int emailCount) {
        this.emailCount = emailCount;
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    /**
     * Add a subfolder
     */
    public void addSubFolder(Folder folder) {
        if (this.subFolders == null) {
            this.subFolders = new ArrayList<>();
        }
        this.subFolders.add(folder);
    }
    
    /**
     * Get display name with unread count
     */
    public String getDisplayName() {
        if (unreadCount > 0) {
            return name + " (" + unreadCount + ")";
        }
        return name;
    }
    
    /**
     * Get folder icon based on type
     */
    public String getIcon() {
        if (name == null) return "📁";
        
        switch (name.toLowerCase()) {
            case "inbox": return "📥";
            case "sent": return "📤";
            case "drafts": return "📝";
            case "trash": return "🗑️";
            case "spam": return "⚠️";
            case "starred": return "⭐";
            case "important": return "❗";
            default: return "📁";
        }
    }
    
    /**
     * Check if folder can be deleted (non-system folders only)
     */
    public boolean canDelete() {
        return !isSystem;
    }
    
    /**
     * Check if folder can be renamed (non-system folders only)
     */
    public boolean canRename() {
        return !isSystem;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return folderId == folder.folderId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(folderId);
    }
    
    @Override
    public String toString() {
        return getIcon() + " " + getDisplayName();
    }
}
