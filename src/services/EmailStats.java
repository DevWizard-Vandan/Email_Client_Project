package entities;

/**
 * EmailStats Data Class
 * 
 * Holds statistical information about a user's emails.
 * Used for dashboard displays and reporting.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class EmailStats {
    
    private int totalEmails;
    private int unreadEmails;
    private int starredEmails;
    private int sentEmails;
    private int receivedEmails;
    private long totalSize;
    
    /**
     * Default constructor
     */
    public EmailStats() {
        this.totalEmails = 0;
        this.unreadEmails = 0;
        this.starredEmails = 0;
        this.sentEmails = 0;
        this.receivedEmails = 0;
        this.totalSize = 0;
    }
    
    /**
     * Full constructor
     */
    public EmailStats(int totalEmails, int unreadEmails, int starredEmails, 
                     int sentEmails, int receivedEmails, long totalSize) {
        this.totalEmails = totalEmails;
        this.unreadEmails = unreadEmails;
        this.starredEmails = starredEmails;
        this.sentEmails = sentEmails;
        this.receivedEmails = receivedEmails;
        this.totalSize = totalSize;
    }
    
    // Getters and Setters
    
    public int getTotalEmails() {
        return totalEmails;
    }
    
    public void setTotalEmails(int totalEmails) {
        this.totalEmails = totalEmails;
    }
    
    public int getUnreadEmails() {
        return unreadEmails;
    }
    
    public void setUnreadEmails(int unreadEmails) {
        this.unreadEmails = unreadEmails;
    }
    
    public int getStarredEmails() {
        return starredEmails;
    }
    
    public void setStarredEmails(int starredEmails) {
        this.starredEmails = starredEmails;
    }
    
    public int getSentEmails() {
        return sentEmails;
    }
    
    public void setSentEmails(int sentEmails) {
        this.sentEmails = sentEmails;
    }
    
    public int getReceivedEmails() {
        return receivedEmails;
    }
    
    public void setReceivedEmails(int receivedEmails) {
        this.receivedEmails = receivedEmails;
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    
    /**
     * Get formatted total size string
     */
    public String getFormattedSize() {
        if (totalSize < 1024) {
            return totalSize + " B";
        } else if (totalSize < 1024 * 1024) {
            return String.format("%.2f KB", totalSize / 1024.0);
        } else if (totalSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", totalSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", totalSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Get read percentage
     */
    public double getReadPercentage() {
        if (totalEmails == 0) return 0.0;
        int readEmails = totalEmails - unreadEmails;
        return (readEmails * 100.0) / totalEmails;
    }
    
    /**
     * Get sent/received ratio
     */
    public double getSentReceivedRatio() {
        if (receivedEmails == 0) return 0.0;
        return (sentEmails * 1.0) / receivedEmails;
    }
    
    @Override
    public String toString() {
        return "EmailStats{" +
                "totalEmails=" + totalEmails +
                ", unreadEmails=" + unreadEmails +
                ", starredEmails=" + starredEmails +
                ", sentEmails=" + sentEmails +
                ", receivedEmails=" + receivedEmails +
                ", totalSize=" + getFormattedSize() +
                '}';
    }
}
