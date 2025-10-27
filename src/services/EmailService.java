// Enhanced EmailService.java - Updated with Professional Features
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnhancedEmailService extends EmailService {
	private AttachmentService attachmentService = new AttachmentService();
	private FolderService folderService = new FolderService();

	/**
	 * Send email with attachments and advanced features
	 */
	public boolean sendEnhancedEmail(Email email, String toUsername, List<java.io.File> attachments) {
		// First send the email using existing method
		boolean emailSent = super.sendEmail(email, toUsername);

		if (emailSent && attachments != null && !attachments.isEmpty()) {
			// Save attachments
			for (java.io.File file : attachments) {
				attachmentService.saveAttachment(email.getEmailId(), file);
			}
		}

		return emailSent;
	}

	/**
	 * Get emails for specific folder with enhanced details
	 */
	public List<Email> getEmailsForFolder(int userId, int folderId, String role) {
		List<Email> emails = new ArrayList<>();

		String sql = """
            SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority, e.IsHTML,
                   sender.Name as SenderName,
                   receiver.Name as ReceiverName,
                   eu.IsRead, eu.IsStarred, eu.FolderID,
                   f.Name as FolderName,
                   COUNT(a.ID) as AttachmentCount,
                   SUM(a.FileSize) as TotalSize
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
            LEFT JOIN User sender ON senderEU.UserID = sender.UserID
            LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
            LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
            LEFT JOIN Folder f ON eu.FolderID = f.FolderID
            LEFT JOIN Attachment a ON e.EmailID = a.EmailID
            WHERE eu.UserID = ? AND eu.FolderID = ? AND eu.Role = ? AND eu.IsDeleted = FALSE
            GROUP BY e.EmailID
            ORDER BY e.Timestamp DESC
        """;

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);
			pstmt.setInt(2, folderId);
			pstmt.setString(3, role);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Email email = new Email();
				email.setEmailId(rs.getInt("EmailID"));
				email.setSubject(rs.getString("Subject"));
				email.setBody(rs.getString("Body"));
				email.setTimestamp(rs.getTimestamp("Timestamp"));
				email.setPriority(rs.getString("Priority"));
				email.setHTML(rs.getBoolean("IsHTML"));
				email.setSenderName(rs.getString("SenderName"));
				email.setReceiverName(rs.getString("ReceiverName"));
				email.setRead(rs.getBoolean("IsRead"));
				email.setStarred(rs.getBoolean("IsStarred"));
				email.setFolderId(rs.getInt("FolderID"));
				email.setFolderName(rs.getString("FolderName"));
				email.setAttachmentCount(rs.getInt("AttachmentCount"));
				email.setSize(rs.getLong("TotalSize"));

				emails.add(email);
			}

		} catch (SQLException e) {
			System.err.println("Error retrieving emails for folder: " + e.getMessage());
		}

		return emails;
	}

	/**
	 * Mark email as read
	 */
	public boolean markAsRead(int emailId, int userId) {
		String sql = "UPDATE EmailUser SET IsRead = TRUE, ReadAt = CURRENT_TIMESTAMP WHERE EmailID = ? AND UserID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, emailId);
			pstmt.setInt(2, userId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error marking email as read: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Toggle star status
	 */
	public boolean toggleStar(int emailId, int userId) {
		String sql = "UPDATE EmailUser SET IsStarred = NOT IsStarred WHERE EmailID = ? AND UserID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, emailId);
			pstmt.setInt(2, userId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error toggling star: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Soft delete email (move to trash)
	 */
	public boolean deleteEmail(int emailId, int userId) {
		String sql = "UPDATE EmailUser SET IsDeleted = TRUE WHERE EmailID = ? AND UserID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, emailId);
			pstmt.setInt(2, userId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error deleting email: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Get email statistics for user
	 */
	public EmailStats getEmailStats(int userId) {
		EmailStats stats = new EmailStats();

		String sql = """
            SELECT 
                COUNT(*) as TotalEmails,
                SUM(CASE WHEN eu.IsRead = FALSE THEN 1 ELSE 0 END) as UnreadEmails,
                SUM(CASE WHEN eu.IsStarred = TRUE THEN 1 ELSE 0 END) as StarredEmails,
                SUM(CASE WHEN eu.Role = 'Sender' THEN 1 ELSE 0 END) as SentEmails,
                SUM(CASE WHEN eu.Role = 'Receiver' THEN 1 ELSE 0 END) as ReceivedEmails
            FROM EmailUser eu
            WHERE eu.UserID = ? AND eu.IsDeleted = FALSE
        """;

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				stats.setTotalEmails(rs.getInt("TotalEmails"));
				stats.setUnreadEmails(rs.getInt("UnreadEmails"));
				stats.setStarredEmails(rs.getInt("StarredEmails"));
				stats.setSentEmails(rs.getInt("SentEmails"));
				stats.setReceivedEmails(rs.getInt("ReceivedEmails"));
			}

		} catch (SQLException e) {
			System.err.println("Error getting email stats: " + e.getMessage());
		}

		return stats;
	}

	/**
	 * Search emails with advanced criteria
	 */
	public List<Email> searchEmailsAdvanced(int userId, String searchTerm, String priority,
											boolean unreadOnly, boolean starredOnly, int folderId) {
		List<Email> emails = new ArrayList<>();
		StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder.append("""
            SELECT e.EmailID, e.Subject, e.Body, e.Timestamp, e.Priority,
                   sender.Name as SenderName, receiver.Name as ReceiverName,
                   eu.IsRead, eu.IsStarred, eu.FolderID,
                   COUNT(a.ID) as AttachmentCount
            FROM Email e
            JOIN EmailUser eu ON e.EmailID = eu.EmailID
            LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
            LEFT JOIN User sender ON senderEU.UserID = sender.UserID
            LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
            LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
            LEFT JOIN Attachment a ON e.EmailID = a.EmailID
            WHERE eu.UserID = ? AND eu.IsDeleted = FALSE
        """);

		List<Object> params = new ArrayList<>();
		params.add(userId);

		if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			sqlBuilder.append(" AND (e.Subject LIKE ? OR e.Body LIKE ?)");
			String searchPattern = "%" + searchTerm + "%";
			params.add(searchPattern);
			params.add(searchPattern);
		}

		if (priority != null && !priority.equals("All")) {
			sqlBuilder.append(" AND e.Priority = ?");
			params.add(priority);
		}

		if (unreadOnly) {
			sqlBuilder.append(" AND eu.IsRead = FALSE");
		}

		if (starredOnly) {
			sqlBuilder.append(" AND eu.IsStarred = TRUE");
		}

		if (folderId > 0) {
			sqlBuilder.append(" AND eu.FolderID = ?");
			params.add(folderId);
		}

		sqlBuilder.append(" GROUP BY e.EmailID ORDER BY e.Timestamp DESC");

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Email email = new Email();
				email.setEmailId(rs.getInt("EmailID"));
				email.setSubject(rs.getString("Subject"));
				email.setBody(rs.getString("Body"));
				email.setTimestamp(rs.getTimestamp("Timestamp"));
				email.setPriority(rs.getString("Priority"));
				email.setSenderName(rs.getString("SenderName"));
				email.setReceiverName(rs.getString("ReceiverName"));
				email.setRead(rs.getBoolean("IsRead"));
				email.setStarred(rs.getBoolean("IsStarred"));
				email.setFolderId(rs.getInt("FolderID"));
				email.setAttachmentCount(rs.getInt("AttachmentCount"));

				emails.add(email);
			}

		} catch (SQLException e) {
			System.err.println("Error searching emails: " + e.getMessage());
		}

		return emails;
	}
}