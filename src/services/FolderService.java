import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderService {

	/**
	 * Create a new folder for a user
	 */
	public boolean createFolder(int userId, String folderName, Integer parentFolderId, String color) {
		String sql = "INSERT INTO Folder (UserID, Name, ParentFolderID, Color) VALUES (?, ?, ?, ?)";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, emailId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Attachment attachment = new Attachment();
				attachment.setId(rs.getInt("ID"));
				attachment.setEmailId(rs.getInt("EmailID"));
				attachment.setFileName(rs.getString("FileName"));
				attachment.setFileSize(rs.getLong("FileSize"));
				attachment.setMimeType(rs.getString("MimeType"));
				attachment.setFilePath(rs.getString("FilePath"));
				attachment.setUploadedAt(rs.getTimestamp("UploadedAt"));
				attachments.add(attachment);
			}

		} catch (SQLException e) {
			System.err.println("Error getting email attachments: " + e.getMessage());
		}

		return attachments;
	}

	/**
	 * Download attachment file
	 */
	public boolean downloadAttachment(int attachmentId, String destinationPath) {
		String sql = "SELECT FilePath, FileName FROM Attachment WHERE ID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, attachmentId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String filePath = rs.getString("FilePath");
				String fileName = rs.getString("FileName");

				Path source = Paths.get(filePath);
				Path destination = Paths.get(destinationPath, fileName);

				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
				return true;
			}

		} catch (SQLException | IOException e) {
			System.err.println("Error downloading attachment: " + e.getMessage());
		}

		return false;
	}

	/**
	 * Delete attachment from file system and database
	 */
	public boolean deleteAttachment(int attachmentId) {
		String sql = "SELECT FilePath FROM Attachment WHERE ID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, attachmentId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String filePath = rs.getString("FilePath");

				// Delete from database first
				String deleteSql = "DELETE FROM Attachment WHERE ID = ?";
				try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
					deleteStmt.setInt(1, attachmentId);
					deleteStmt.executeUpdate();
				}

				// Then delete file
				try {
					Files.deleteIfExists(Paths.get(filePath));
				} catch (IOException e) {
					System.err.println("Warning: Could not delete file: " + filePath);
				}

				return true;
			}

		} catch (SQLException e) {
			System.err.println("Error deleting attachment: " + e.getMessage());
		}

		return false;
	}

	/**
	 * Get MIME type from file extension
	 */
	private String getMimeType(String fileName) {
		String extension = fileName.toLowerCase();
		if (extension.endsWith(".pdf")) return "application/pdf";
		if (extension.endsWith(".doc")) return "application/msword";
		if (extension.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		if (extension.endsWith(".xls")) return "application/vnd.ms-excel";
		if (extension.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) return "image/jpeg";
		if (extension.endsWith(".png")) return "image/png";
		if (extension.endsWith(".gif")) return "image/gif";
		if (extension.endsWith(".zip")) return "application/zip";
		if (extension.endsWith(".txt")) return "text/plain";
		return "application/octet-stream";
	}

	/**
	 * Get total attachment size for an email
	 */
	public long getTotalAttachmentSize(int emailId) {
		String sql = "SELECT SUM(FileSize) as TotalSize FROM Attachment WHERE EmailID = ?";

		try (Connection conn = DatabaseHelper.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, emailId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getLong("TotalSize");
			}

		} catch (SQLException e) {
			System.err.println("Error getting total attachment size: " + e.getMessage());
		}

		return 0;
	}
}