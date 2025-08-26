package com.emailclient.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Email Model Class
 * Represents an email in the system
 */
public class Email {
    private int emailId;
    private String subject;
    private String body;
    private LocalDateTime sentAt;
    private Priority priority;
    private boolean isRead;
    private boolean isDeleted;
    private int folderId;
    
    // Related objects
    private User sender;
    private List<User> receivers;
    private List<User> ccUsers;
    private List<User> bccUsers;
    private List<Attachment> attachments;
    private Folder folder;
    
    // Enums
    public enum Priority {
        LOW, NORMAL, HIGH
    }
    
    // Constructors
    public Email() {
        this.receivers = new ArrayList<>();
        this.ccUsers = new ArrayList<>();
        this.bccUsers = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.priority = Priority.NORMAL;
        this.isRead = false;
        this.isDeleted = false;
        this.sentAt = LocalDateTime.now();
    }
    
    public Email(String subject, String body) {
        this();
        this.subject = subject;
        this.body = body;
    }
    
    public Email(String subject, String body, User sender) {
        this(subject, body);
        this.sender = sender;
    }
    
    public Email(int emailId, String subject, String body, LocalDateTime sentAt) {
        this(subject, body);
        this.emailId = emailId;
        this.sentAt = sentAt;
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
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
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
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public List<User> getReceivers() {
        return receivers;
    }
    
    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }
    
    public List<User> getCcUsers() {
        return ccUsers;
    }
    
    public void setCcUsers(List<User> ccUsers) {
        this.ccUsers = ccUsers;
    }
    
    public List<User> getBccUsers() {
        return bccUsers;
    }
    
    public void setBccUsers(List<User> bccUsers) {
        this.bccUsers = bccUsers;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    public Folder getFolder() {
        return folder;
    }
    
    public void setFolder(Folder folder) {
        this.folder = folder;
        if (folder != null) {
            this.folderId = folder.getFolderId();
        }
    }
    
    // Business Logic Methods
    
    /**
     * Add a receiver to the email
     * @param receiver The user to add as receiver
     */
    public void addReceiver(User receiver) {
        if (receiver != null && !receivers.contains(receiver)) {
            receivers.add(receiver);
        }
    }
    
    /**
     * Add a CC user to the email
     * @param ccUser The user to add as CC
     */
    public void addCcUser(User ccUser) {
        if (ccUser != null && !ccUsers.contains(ccUser)) {
            ccUsers.add(ccUser);
        }
    }
    
    /**
     * Add a BCC user to the email
     * @param bccUser The user to add as BCC
     */
    public void addBccUser(User bccUser) {
        if (bccUser != null && !bccUsers.contains(bccUser)) {
            bccUsers.add(bccUser);
        }
    }
    
    /**
     * Add an attachment to the email
     * @param attachment The attachment to add
     */
    public void addAttachment(Attachment attachment) {
        if (attachment != null && !attachments.contains(attachment)) {
            attachments.add(attachment);
            attachment.setEmailId(this.emailId);
        }
    }
    
    /**
     * Remove an attachment from the email
     * @param attachment The attachment to remove
     */
    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
    }
    
    /**
     * Mark email as read
     */
    public void markAsRead() {
        this.isRead = true;
    }
    
    /**
     * Mark email as unread
     */
    public void markAsUnread() {
        this.isRead = false;
    }
    
    /**
     * Move email to trash (soft delete)
     */
    public void moveToTrash() {
        this.isDeleted = true;
    }
    
    /**
     * Restore email from trash
     */
    public void restoreFromTrash() {
        this.isDeleted = false;
    }
    
    /**
     * Get all recipients (TO + CC + BCC)
     * @return List of all recipients
     */
    public List<User> getAllRecipients() {
        List<User> allRecipients = new ArrayList<>();
        allRecipients.addAll(receivers);
        allRecipients.addAll(ccUsers);
        allRecipients.addAll(bccUsers);
        return allRecipients;
    }
    
    /**
     * Check if email has attachments
     * @return true if email has attachments, false otherwise
     */
    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }
    
    /**
     * Get attachment count
     * @return Number of attachments
     */
    public int getAttachmentCount() {
        return attachments.size();
    }
    
    /**
     * Get total attachment size in bytes
     * @return Total size of all attachments
     */
    public long getTotalAttachmentSize() {
        return attachments.stream()
                .mapToLong(Attachment::getFileSize)
                .sum();
    }
    
    /**
     * Get email preview (first 100 characters of body)
     * @return Preview text
     */
    public String getPreview() {
        if (body == null || body.isEmpty()) {
            return "";
        }
        return body.length() > 100 ? body.substring(0, 100) + "..." : body;
    }
    
    @Override
    public String toString() {
        return "Email{" +
                "emailId=" + emailId +
                ", subject='" + subject + '\'' +
                ", sentAt=" + sentAt +
                ", priority=" + priority +
                ", isRead=" + isRead +
                ", isDeleted=" + isDeleted +
                ", sender=" + (sender != null ? sender.getEmail() : "null") +
                ", receiversCount=" + receivers.size() +
                ", attachmentsCount=" + attachments.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return emailId == email.emailId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(emailId);
    }
}