package entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Email Entity Class
 * 
 * Represents an email message in the system.
 * Contains email metadata, content, and relationship information.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class Email {
    
    private int emailId;
    private String subject;
    private String body;
    private Timestamp timestamp;
    private String priority; // Low, Normal, High
    private boolean isHTML;
    private String messageId;
    private Integer inReplyToId;
    private long size;
    
    // Relationship fields
    private String senderName;
    private String receiverName;
    private boolean isRead;
    private boolean isStarred;
    private boolean isDeleted;
    private int folderId;
    private String folderName;
    
    // Attachment information
    private List<Attachment> attachments;
    private int attachmentCount;
    
    /**
     * Default constructor
     */
    public Email() {
        this.priority = "Normal";
        this.isHTML = false;
        this.isRead = false;
        this.isStarred = false;
        this.isDeleted = false;
        this.attachments = new ArrayList<>();
        this.attachmentCount = 0;
    }
    
    /**
     * Constructor with basic fields
     */
    public Email(String subject, String body) {
        this();
        this.subject = subject;
        this.body = body;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor with all required fields
     */
    public Email(int emailId, String subject, String body, Timestamp timestamp, String priority) {
        this();
        this.emailId = emailId;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.priority = priority;
    }
    
    /**
     * Full constructor for database retrieval
     */
    public Email(int emailId, String subject, String body, Timestamp timestamp, 
                 String priority, boolean isHTML, String senderName, String receiverName,
                 boolean isRead, boolean isStarred, int folderId) {
        this.emailId = emailId;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.priority = priority;
        this.isHTML = isHTML;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.isRead = isRead;
        this.isStarred = isStarred;
        this.folderId = folderId;
        this.isDeleted = false;
        this.attachments = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public int getEmailId() {
        return emailId;
    }
    
    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
        this.size = body != null ? body.length() : 0;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public boolean isHTML() {
        return isHTML;
    }
    
    public void setHTML(boolean HTML) {
        isHTML = HTML;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public Integer getInReplyToId() {
        return inReplyToId;
    }
    
    public void setInReplyToId(Integer inReplyToId) {
        this.inReplyToId = inReplyToId;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public boolean isStarred() {
        return isStarred;
    }
    
    public void setStarred(boolean starred) {
        isStarred = starred;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    public int getFolderId() {
        return folderId;
    }
    
    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        this.attachmentCount = attachments != null ? attachments.size() : 0;
    }
    
    public int getAttachmentCount() {
        return attachmentCount;
    }
    
    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
    
    /**
     * Add an attachment to this email
     */
    public void addAttachment(Attachment attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachment);
        this.attachmentCount = this.attachments.size();
    }
    
    /**
     * Check if email has attachments
     */
    public boolean hasAttachments() {
        return attachmentCount > 0 || (attachments != null && !attachments.isEmpty());
    }
    
    /**
     * Get priority icon for display
     */
    public String getPriorityIcon() {
        if (priority == null) return "🟡";
        switch (priority.toLowerCase()) {
            case "high": return "🔴";
            case "low": return "🟢";
            default: return "🟡";
        }
    }
    
    /**
     * Get formatted size string
     */
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Display email summary
     */
    public void displayEmail() {
        System.out.println("=".repeat(50));
        System.out.println("From: " + senderName);
        System.out.println("To: " + receiverName);
        System.out.println("Subject: " + subject);
        System.out.println("Date: " + timestamp);
        System.out.println("Priority: " + priority);
        if (hasAttachments()) {
            System.out.println("Attachments: " + attachmentCount);
        }
        System.out.println("-".repeat(50));
        System.out.println(body);
        System.out.println("=".repeat(50));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return emailId == email.emailId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(emailId);
    }
    
    @Override
    public String toString() {
        return "Email{" +
                "emailId=" + emailId +
                ", subject='" + subject + '\'' +
                ", from='" + senderName + '\'' +
                ", to='" + receiverName + '\'' +
                ", timestamp=" + timestamp +
                ", priority='" + priority + '\'' +
                ", isRead=" + isRead +
                ", isStarred=" + isStarred +
                '}';
    }
}
