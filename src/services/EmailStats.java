import java.sql.*;
import java.util.ArrayList;

// EmailStats.java - Statistics Data Class
public class EmailStats {
	private int totalEmails;
	private int unreadEmails;
	private int starredEmails;
	private int sentEmails;
	private int receivedEmails;
	private int todayEmails;
	private int weekEmails;
	private long totalSize;

	// Constructors
	public EmailStats() {}

	// Getters and Setters
	public int getTotalEmails() { return totalEmails; }
	public void setTotalEmails(int totalEmails) { this.totalEmails = totalEmails; }

	public int getUnreadEmails() { return unreadEmails; }
	public void setUnreadEmails(int unreadEmails) { this.unreadEmails = unreadEmails; }

	public int getStarredEmails() { return starredEmails; }
	public void setStarredEmails(int starredEmails) { this.starredEmails = starredEmails; }

	public int getSentEmails() { return sentEmails; }
	public void setSentEmails(int sentEmails) { this.sentEmails = sentEmails; }

	public int getReceivedEmails() { return receivedEmails; }
	public void setReceivedEmails(int receivedEmails) { this.receivedEmails = receivedEmails; }

	public int getTodayEmails() { return todayEmails; }
	public void setTodayEmails(int todayEmails) { this.todayEmails = todayEmails; }

	public int getWeekEmails() { return weekEmails; }
	public void setWeekEmails(int weekEmails) { this.weekEmails = weekEmails; }

	public long getTotalSize() { return totalSize; }
	public void setTotalSize(long totalSize) { this.totalSize = totalSize; }

	// Utility methods
	public String getFormattedSize() {
		if (totalSize < 1024) return totalSize + " B";
		if (totalSize < 1024 * 1024) return (totalSize / 1024) + " KB";
		return (totalSize / (1024 * 1024)) + " MB";
	}

	@Override
	public String toString() {
		return "EmailStats{" +
				"total=" + totalEmails +
				", unread=" + unreadEmails +
				", starred=" + starredEmails +
				", sent=" + sentEmails +
				", received=" + receivedEmails +
				'}';
	}
}Helper.getConnection();
PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setInt(1, userId);
            pstmt.setString(2, folderName);
            if (parentFolderId != null) {
		pstmt.setInt(3, parentFolderId);
            } else {
					pstmt.setNull(3,Types.INTEGER);
            }
					pstmt.setString(4, color != null ? color : "#3498db");
            
            return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
		System.err.println("Error creating folder: " + e.getMessage());
		return false;
		}
		}

/**
 * Get all folders for a user
 */
public List<Folder> getUserFolders(int userId) {
	List<Folder> folders = new ArrayList<>();
	String sql = """
            SELECT f.FolderID, f.UserID, f.Name, f.ParentFolderID, f.CreatedAt, 
                   f.Color, f.IsSystem,
                   COUNT(eu.EmailID) as EmailCount,
                   SUM(CASE WHEN eu.IsRead = FALSE THEN 1 ELSE 0 END) as UnreadCount
            FROM Folder f
            LEFT JOIN EmailUser eu ON f.FolderID = eu.FolderID AND eu.IsDeleted = FALSE
            WHERE f.UserID = ?
            GROUP BY f.FolderID
            ORDER BY f.IsSystem DESC, f.Name ASC
        """;

	try (Connection conn = DatabaseHelper.getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			Folder folder = new Folder();
			folder.setFolderId(rs.getInt("FolderID"));
			folder.setUserId(rs.getInt("UserID"));
			folder.setName(rs.getString("Name"));
			folder.setParentFolderId((Integer) rs.getObject("ParentFolderID"));
			folder.setCreatedAt(rs.getTimestamp("CreatedAt"));
			folder.setColor(rs.getString("Color"));
			folder.setSystem(rs.getBoolean("IsSystem"));
			folder.setEmailCount(rs.getInt("EmailCount"));
			folder.setUnreadCount(rs.getInt("UnreadCount"));
			folders.add(folder);
		}

	} catch (SQLException e) {
		System.err.println("Error getting user folders: " + e.getMessage());
	}

	return folders;
}

/**
 * Get default inbox folder for user
 */
public Folder getInboxFolder(int userId) {
	String sql = "SELECT * FROM Folder WHERE UserID = ? AND Name = 'Inbox' AND IsSystem = TRUE";

	try (Connection conn = DatabaseHelper.getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			Folder folder = new Folder();
			folder.setFolderId(rs.getInt("FolderID"));
			folder.setUserId(rs.getInt("UserID"));
			folder.setName(rs.getString("Name"));
			folder.setSystem(rs.getBoolean("IsSystem"));
			return folder;
		}

	} catch (SQLException e) {
		System.err.println("Error getting inbox folder: " + e.getMessage());
	}

	return null;
}

/**
 * Move email to folder
 */
public boolean moveEmailToFolder(int emailId, int userId, int folderId) {
	String sql = "UPDATE EmailUser SET FolderID = ? WHERE EmailID = ? AND UserID = ?";

	try (Connection conn = DatabaseHelper.getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setInt(1, folderId);
		pstmt.setInt(2, emailId);
		pstmt.setInt(3, userId);

		return pstmt.executeUpdate() > 0;

	} catch (SQLException e) {
		System.err.println("Error moving email to folder: " + e.getMessage());
		return false;
	}
}

/**
 * Delete folder (if not system folder)
 */
public boolean deleteFolder(int folderId, int userId) {
	String sql = "DELETE FROM Folder WHERE FolderID = ? AND UserID = ? AND IsSystem = FALSE";

	try (Connection conn = DatabaseHelper.getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setInt(1, folderId);
		pstmt.setInt(2, userId);

		return pstmt.executeUpdate() > 0;

	} catch (SQLException e) {
		System.err.println("Error deleting folder: " + e.getMessage());
		return false;
	}
}
}