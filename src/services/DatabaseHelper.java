package services;

import java.sql.*;
import java.io.*;
import java.util.Properties;

/**
 * DatabaseHelper - Database Connection and Utility Class
 * 
 * Manages database connections, schema initialization, and provides
 * connection pooling support. Uses MySQL 8.0+ JDBC driver.
 * 
 * Connection Details:
 * - URL: jdbc:mysql://localhost:3306/email_client
 * - User: root (configurable)
 * - Password: empty (configurable)
 * 
 * CHANGE THESE CREDENTIALS AS NEEDED FOR YOUR ENVIRONMENT
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class DatabaseHelper {
    
    // Database configuration - CHANGE THESE AS NEEDED
    private static final String DB_URL = "jdbc:mysql://localhost:3306/email_client";
    private static final String DB_USER = "root"; // CHANGE THIS
    private static final String DB_PASSWORD = ""; // CHANGE THIS
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Alternative: Load from properties file
    private Properties dbProperties;
    
    /**
     * Constructor - loads configuration
     */
    public DatabaseHelper() {
        try {
            // Load JDBC driver
            Class.forName(DB_DRIVER);
            System.out.println("✓ MySQL JDBC Driver loaded successfully");
            
            // Try to load from properties file if exists
            loadPropertiesIfExists();
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            System.err.println("Please ensure mysql-connector-j-8.x.x.jar is in the lib folder");
            e.printStackTrace();
        }
    }
    
    /**
     * Load database properties from file if exists
     */
    private void loadPropertiesIfExists() {
        try {
            File propFile = new File("database.properties");
            if (propFile.exists()) {
                dbProperties = new Properties();
                try (FileInputStream fis = new FileInputStream(propFile)) {
                    dbProperties.load(fis);
                    System.out.println("✓ Loaded database configuration from database.properties");
                }
            }
        } catch (IOException e) {
            System.out.println("ℹ No database.properties found, using default configuration");
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            String url = dbProperties != null ? dbProperties.getProperty("db.url", DB_URL) : DB_URL;
            String user = dbProperties != null ? dbProperties.getProperty("db.username", DB_USER) : DB_USER;
            String password = dbProperties != null ? dbProperties.getProperty("db.password", DB_PASSWORD) : DB_PASSWORD;
            
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Initialize database schema
     * Creates database if not exists and sets up all tables
     */
    public void initializeDatabase() {
        try {
            // First, connect to MySQL server (without specifying database)
            String serverUrl = DB_URL.substring(0, DB_URL.lastIndexOf("/"));
            String dbName = DB_URL.substring(DB_URL.lastIndexOf("/") + 1);
            
            try (Connection conn = DriverManager.getConnection(serverUrl, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Create database if not exists
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                System.out.println("✓ Database '" + dbName + "' ready");
            }
            
            // Now connect to the specific database and create tables
            try (Connection conn = getConnection()) {
                createTables(conn);
                createTriggersAndViews(conn);
                System.out.println("✓ Database schema initialized successfully");
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create all database tables
     */
    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Create User table
            String createUserTable = "CREATE TABLE IF NOT EXISTS User (" +
                "UserID INT AUTO_INCREMENT PRIMARY KEY, " +
                "Name VARCHAR(50) UNIQUE NOT NULL, " +
                "Password VARCHAR(255) NOT NULL, " +
                "PersonalDetails TEXT, " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "LastLogin DATETIME, " +
                "IsActive BOOLEAN DEFAULT TRUE, " +
                "INDEX idx_name (Name)" +
                ")";
            stmt.executeUpdate(createUserTable);
            System.out.println("  ✓ User table created");
            
            // Create Email table
            String createEmailTable = "CREATE TABLE IF NOT EXISTS Email (" +
                "EmailID INT AUTO_INCREMENT PRIMARY KEY, " +
                "Subject VARCHAR(255) NOT NULL, " +
                "Body LONGTEXT NOT NULL, " +
                "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "Priority ENUM('Low', 'Normal', 'High') DEFAULT 'Normal', " +
                "IsHTML BOOLEAN DEFAULT FALSE, " +
                "INDEX idx_timestamp (Timestamp), " +
                "FULLTEXT idx_search (Subject, Body)" +
                ")";
            stmt.executeUpdate(createEmailTable);
            System.out.println("  ✓ Email table created");
            
            // Create Folder table
            String createFolderTable = "CREATE TABLE IF NOT EXISTS Folder (" +
                "FolderID INT AUTO_INCREMENT PRIMARY KEY, " +
                "UserID INT NOT NULL, " +
                "Name VARCHAR(100) NOT NULL, " +
                "ParentFolderID INT NULL, " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "Color VARCHAR(7) DEFAULT '#3498db', " +
                "IsSystem BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE, " +
                "FOREIGN KEY (ParentFolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL, " +
                "INDEX idx_user_folder (UserID, Name)" +
                ")";
            stmt.executeUpdate(createFolderTable);
            System.out.println("  ✓ Folder table created");
            
            // Create EmailUser junction table
            String createEmailUserTable = "CREATE TABLE IF NOT EXISTS EmailUser (" +
                "EmailID INT, " +
                "UserID INT, " +
                "Role ENUM('Sender', 'Receiver') NOT NULL, " +
                "FolderID INT NULL, " +
                "IsRead BOOLEAN DEFAULT FALSE, " +
                "IsStarred BOOLEAN DEFAULT FALSE, " +
                "IsDeleted BOOLEAN DEFAULT FALSE, " +
                "ReadAt DATETIME NULL, " +
                "PRIMARY KEY (EmailID, UserID, Role), " +
                "FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE, " +
                "FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE, " +
                "FOREIGN KEY (FolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL, " +
                "INDEX idx_user_role (UserID, Role), " +
                "INDEX idx_folder (FolderID)" +
                ")";
            stmt.executeUpdate(createEmailUserTable);
            System.out.println("  ✓ EmailUser table created");
            
            // Create Attachment table
            String createAttachmentTable = "CREATE TABLE IF NOT EXISTS Attachment (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "EmailID INT NOT NULL, " +
                "FileName VARCHAR(255) NOT NULL, " +
                "FileSize BIGINT NOT NULL, " +
                "MimeType VARCHAR(100) NOT NULL, " +
                "FilePath VARCHAR(500) NOT NULL, " +
                "UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE, " +
                "INDEX idx_email (EmailID)" +
                ")";
            stmt.executeUpdate(createAttachmentTable);
            System.out.println("  ✓ Attachment table created");
            
            // Create WebsiteSignUp table
            String createSignUpTable = "CREATE TABLE IF NOT EXISTS WebsiteSignUp (" +
                "SignUpID INT AUTO_INCREMENT PRIMARY KEY, " +
                "UserID INT NOT NULL, " +
                "Name VARCHAR(100) NOT NULL, " +
                "DomainName VARCHAR(100) NOT NULL, " +
                "SignUpDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE, " +
                "INDEX idx_user (UserID)" +
                ")";
            stmt.executeUpdate(createSignUpTable);
            System.out.println("  ✓ WebsiteSignUp table created");
        }
    }
    
    /**
     * Create database triggers and views
     */
    private void createTriggersAndViews(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Drop trigger if exists
            try {
                stmt.executeUpdate("DROP TRIGGER IF EXISTS CreateDefaultFolders");
            } catch (SQLException e) {
                // Ignore if trigger doesn't exist
            }
            
            // Create trigger for default folders
            String createTrigger = 
                "CREATE TRIGGER CreateDefaultFolders " +
                "AFTER INSERT ON User " +
                "FOR EACH ROW " +
                "BEGIN " +
                "    INSERT INTO Folder (UserID, Name, IsSystem, Color) VALUES " +
                "    (NEW.UserID, 'Inbox', TRUE, '#3498db'), " +
                "    (NEW.UserID, 'Sent', TRUE, '#27ae60'), " +
                "    (NEW.UserID, 'Drafts', TRUE, '#f39c12'), " +
                "    (NEW.UserID, 'Trash', TRUE, '#e74c3c'), " +
                "    (NEW.UserID, 'Spam', TRUE, '#95a5a6'); " +
                "END";
            
            stmt.executeUpdate(createTrigger);
            System.out.println("  ✓ CreateDefaultFolders trigger created");
            
            // Create view for email details with sender/receiver names
            try {
                stmt.executeUpdate("DROP VIEW IF EXISTS EmailWithDetails");
            } catch (SQLException e) {
                // Ignore if view doesn't exist
            }
            
            String createView = 
                "CREATE VIEW EmailWithDetails AS " +
                "SELECT e.*, " +
                "       sender.Name as SenderName, " +
                "       receiver.Name as ReceiverName, " +
                "       eu_receiver.IsRead, " +
                "       eu_receiver.IsStarred, " +
                "       eu_receiver.FolderID " +
                "FROM Email e " +
                "LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender' " +
                "LEFT JOIN User sender ON eu_sender.UserID = sender.UserID " +
                "LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver' " +
                "LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID";
            
            stmt.executeUpdate(createView);
            System.out.println("  ✓ EmailWithDetails view created");
        }
    }
    
    /**
     * Close database connection safely
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close statement safely
     */
    public void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close result set safely
     */
    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
