import java.sql.*;

public class DatabaseHelper {
    // MySQL database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/email_client";
    private static final String DB_USER = "root"; // Change this to your MySQL username
    private static final String DB_PASSWORD = "Naman@2006"; // Change this to your MySQL password

    // Get database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver (not required in modern JDBC, but kept for legacy)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Make sure mysql-connector-j.jar is in classpath", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Initialize database with tables
    public static void initializeDatabase() {
        // First, try to create database itself using a system-level connection
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD)) {
            createDatabase(conn);
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
        // Now, connect to email_client and create tables
        try (Connection conn = getConnection()) {
            createTables(conn);
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            System.err.println("Please ensure MySQL server is running and credentials are correct.");
        }
    }

    // Create database if it doesn't exist
    private static void createDatabase(Connection conn) throws SQLException {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS email_client";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createDatabaseSQL);
        }
    }

    // Create necessary tables
    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Use the email_client database
        stmt.execute("USE email_client");

        // Create User table
        String createUserTable = """
            CREATE TABLE IF NOT EXISTS User (
                UserID INT AUTO_INCREMENT PRIMARY KEY,
                Name VARCHAR(50) UNIQUE NOT NULL,
                Email VARCHAR(100) NOT NULL,
                Password VARCHAR(100) NOT NULL
            )
        """;

        // Create Email table
        String createEmailTable = """
            CREATE TABLE IF NOT EXISTS Email (
                EmailID INT AUTO_INCREMENT PRIMARY KEY,
                Subject VARCHAR(255) NOT NULL,
                Body TEXT NOT NULL,
                Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;

        // Create EmailUser table (junction table)
        String createEmailUserTable = """
            CREATE TABLE IF NOT EXISTS EmailUser (
                EmailID INT,
                UserID INT,
                Role ENUM('Sender', 'Receiver') NOT NULL,
                PRIMARY KEY (EmailID, UserID, Role),
                FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
                FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
            )
        """;

        stmt.execute(createUserTable);
        stmt.execute(createEmailTable);
        stmt.execute(createEmailUserTable);

        stmt.close();
    }

    // Insert user into database
    public static boolean insertUser(User user) {
        String sql = "INSERT INTO User (Name, Email, Password) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                System.err.println("Username already exists!");
            }
            return false;
        }
    }

    // Validate user login
    public static User validateUser(String name, String password) {
        String sql = "SELECT * FROM User WHERE Name = ? AND Password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return null;
    }

    // Get user by username
    public static User getUserByName(String name) {
        String sql = "SELECT * FROM User WHERE Name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }

    // Insert email and create sender/receiver relationships
    public static boolean insertEmail(Email email, int receiverId) {
        String insertEmailSQL = "INSERT INTO Email (Subject, Body, Timestamp) VALUES (?, ?, ?)";
        String insertSenderSQL = "INSERT INTO EmailUser (EmailID, UserID, Role) VALUES (?, ?, 'Sender')";
        String insertReceiverSQL = "INSERT INTO EmailUser (EmailID, UserID, Role) VALUES (?, ?, 'Receiver')";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert email
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertEmailSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt1.setString(1, email.getSubject());
                pstmt1.setString(2, email.getBody());
                pstmt1.setTimestamp(3, email.getTimestamp());

                int rowsAffected = pstmt1.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                // Get generated email ID
                ResultSet generatedKeys = pstmt1.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int emailId = generatedKeys.getInt(1);
                    email.setEmailId(emailId);

                    // Insert sender relationship
                    try (PreparedStatement pstmt2 = conn.prepareStatement(insertSenderSQL)) {
                        pstmt2.setInt(1, emailId);
                        pstmt2.setInt(2, email.getSenderId());
                        pstmt2.executeUpdate();
                    }

                    // Insert receiver relationship
                    try (PreparedStatement pstmt3 = conn.prepareStatement(insertReceiverSQL)) {
                        pstmt3.setInt(1, emailId);
                        pstmt3.setInt(2, receiverId);
                        pstmt3.executeUpdate();
                    }

                    conn.commit(); // Commit transaction
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting email: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }
}
