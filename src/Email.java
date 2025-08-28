import java.sql.Timestamp;

public class Email {
    private int emailId;
    private String subject;
    private String body;
    private Timestamp timestamp;
    private int senderId;
    private String senderName;
    private String receiverName;

    // Constructor for new email (without ID and timestamp)
    public Email(String subject, String body, int senderId) {
        this.subject = subject;
        this.body = body;
        this.senderId = senderId;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for existing email (from database)
    public Email(int emailId, String subject, String body, Timestamp timestamp, int senderId) {
        this.emailId = emailId;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.senderId = senderId;
    }

    // Full constructor for display purposes
    public Email(int emailId, String subject, String body, Timestamp timestamp,
                 int senderId, String senderName, String receiverName) {
        this.emailId = emailId;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverName = receiverName;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    @Override
    public String toString() {
        return "Email{" +
                "emailId=" + emailId +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", senderId=" + senderId +
                '}';
    }

    // Method for displaying email in inbox/sent format
    public void displayEmail() {
        System.out.println("====================");
        System.out.println("Email ID: " + emailId);
        System.out.println("From: " + (senderName != null ? senderName : "Unknown"));
        System.out.println("To: " + (receiverName != null ? receiverName : "Unknown"));
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("Sent: " + timestamp);
        System.out.println("====================");
    }
}