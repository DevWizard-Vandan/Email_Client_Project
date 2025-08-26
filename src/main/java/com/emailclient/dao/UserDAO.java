package com.emailclient.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Folder Model Class
 * Represents a folder in the email client
 */
class Folder {
    private int folderId;
    private int userId;
    private String folderName;
    private LocalDateTime createdAt;
    private boolean isDefault;
    private List<Email> emails;
    
    // Constructors
    public Folder() {
        this.emails = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.isDefault = false;
    }
    
    public Folder(String folderName, int userId) {
        this();
        this.folderName = folderName;
        this.userId = userId;
    }
    
    public Folder(int folderId, String folderName, int userId, boolean isDefault) {
        this(folderName, userId);
        this.folderId = folderId;
        this.isDefault = isDefault;
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
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public List<Email> getEmails() {
        return emails;
    }
    
    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
    
    // Business Logic Methods
    
    /**
     * Add email to folder
     * @param email The email to add
     */
    public void addEmail(Email email) {
        if (email != null && !emails.contains(email)) {
            emails.add(email);
            email.setFolderId(this.folderId);
            email.setFolder(this);
        }
    }
    
    /**
     * Remove email from folder
     * @param email The email to remove
     */
    public void removeEmail(Email email) {
        emails.remove(email);
        if (email != null) {
            email.setFolderId(0);
            email.setFolder(null);
        }
    }
    
    /**
     * Get email count in folder
     * @return Number of emails
     */
    public int getEmailCount() {
        return emails.size();
    }
    
    /**
     * Get unread email count
     * @return Number of unread emails
     */
    public int getUnreadEmailCount() {
        return (int) emails.stream()
                .filter(email -> !email.isRead())
                .count();
    }
    
    /**
     * Get emails sorted by sent date (newest first)
     * @return Sorted list of emails
     */
    public List<Email> getEmailsSortedByDate() {
        return emails.stream()
                .sorted((e1, e2) -> e2.getSentAt().compareTo(e1.getSentAt()))
                .toList();
    }
    
    @Override
    public String toString() {
        return "Folder{" +
                "folderId=" + folderId +
                ", userId=" + userId +
                ", folderName='" + folderName + '\'' +
                ", createdAt=" + createdAt +
                ", isDefault=" + isDefault +
                ", emailCount=" + emails.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Folder folder = (Folder) obj;
        return folderId == folder.folderId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(folderId);
    }
}

/**
 * Attachment Model Class
 * Represents a file attachment in an email
 */
class Attachment {
    private int attachmentId;
    private int emailId;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String fileType;
    private LocalDateTime uploadedAt;
    
    // Constructors
    public Attachment() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    public Attachment(String fileName, String filePath, long fileSize, String fileType) {
        this();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
    
    public Attachment(int attachmentId, int emailId, String fileName, String filePath, 
                     long fileSize, String fileType, LocalDateTime uploadedAt) {
        this(fileName, filePath, fileSize, fileType);
        this.attachmentId = attachmentId;
        this.emailId = emailId;
        this.uploadedAt = uploadedAt;
    }
    
    // Getters and Setters
    public int getAttachmentId() {
        return attachmentId;
    }
    
    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }
    
    public int getEmailId() {
        return emailId;
    }
    
    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    // Business Logic Methods
    
    /**
     * Get file extension from filename
     * @return File extension or empty string if no extension
     */
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * Get human-readable file size
     * @return Formatted file size (e.g., "1.5 MB")
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
        }
    }
    
    /**
     * Check if attachment is an image
     * @return true if file type indicates an image
     */
    public boolean isImage() {
        if (fileType == null) return false;
        return fileType.startsWith("image/");
    }
    
    /**
     * Check if attachment is a document
     * @return true if file type indicates a document
     */
    public boolean isDocument() {
        if (fileType == null) return false;
        return fileType.contains("pdf") || 
               fileType.contains("doc") || 
               fileType.contains("text") ||
               fileType.contains("presentation") ||
               fileType.contains("spreadsheet");
    }
    
    /**
     * Check if attachment is a video
     * @return true if file type indicates a video
     */
    public boolean isVideo() {
        if (fileType == null) return false;
        return fileType.startsWith("video/");
    }
    
    /**
     * Check if attachment is an audio file
     * @return true if file type indicates audio
     */
    public boolean isAudio() {
        if (fileType == null) return false;
        return fileType.startsWith("audio/");
    }
    
    @Override
    public String toString() {
        return "Attachment{" +
                "attachmentId=" + attachmentId +
                ", emailId=" + emailId +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + getFormattedFileSize() +
                ", fileType='" + fileType + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Attachment attachment = (Attachment) obj;
        return attachmentId == attachment.attachmentId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(attachmentId);
    }
}

/**
 * WebsiteSignUp Model Class
 * Represents user registration information
 */
class WebsiteSignUp {
    private int signUpId;
    private int userId;
    private LocalDateTime signUpDate;
    private String ipAddress;
    private String userAgent;
    
    // Constructors
    public WebsiteSignUp() {
        this.signUpDate = LocalDateTime.now();
    }
    
    public WebsiteSignUp(int userId, String ipAddress, String userAgent) {
        this();
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    public WebsiteSignUp(int signUpId, int userId, LocalDateTime signUpDate, 
                        String ipAddress, String userAgent) {
        this(userId, ipAddress, userAgent);
        this.signUpId = signUpId;
        this.signUpDate = signUpDate;
    }
    
    // Getters and Setters
    public int getSignUpId() {
        return signUpId;
    }
    
    public void setSignUpId(int signUpId) {
        this.signUpId = signUpId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getSignUpDate() {
        return signUpDate;
    }
    
    public void setSignUpDate(LocalDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    @Override
    public String toString() {
        return "WebsiteSignUp{" +
                "signUpId=" + signUpId +
                ", userId=" + userId +
                ", signUpDate=" + signUpDate +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}