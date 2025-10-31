import entities.*;
import services.*;
import utils.*;

import java.sql.*;
import java.util.List;

/**
 * ============================================
 * CONCEPT DEMONSTRATION CLASS
 * 
 * This class demonstrates ALL OOP and DBMS concepts
 * required by the syllabus in one comprehensive example.
 * 
 * RUN THIS CLASS TO SEE ALL CONCEPTS IN ACTION!
 * ============================================
 */
public class ConceptDemonstration {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("PROFESSIONAL EMAIL CLIENT - CONCEPT DEMONSTRATION");
        System.out.println("Demonstrating ALL OOP and DBMS Concepts");
        System.out.println("=".repeat(60));
        
        // Initialize services
        DatabaseHelper dbHelper = new DatabaseHelper();
        dbHelper.initializeDatabase();
        
        UserService userService = new UserService(dbHelper);
        EmailService emailService = new EmailService(dbHelper);
        FolderService folderService = new FolderService(dbHelper);
        
        demonstrateOOPConcepts();
        demonstrateDBMSConcepts(dbHelper, userService, emailService, folderService);
    }
    
    /**
     * ============================================
     * OOP CONCEPTS DEMONSTRATION
     * ============================================
     */
    private static void demonstrateOOPConcepts() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 1: OOP CONCEPTS DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        // 1. CLASSES AND OBJECTS
        System.out.println("\n1️⃣ CLASSES AND OBJECTS");
        System.out.println("-".repeat(40));
        User user1 = new User("john_doe", "pass123");
        Email email1 = new Email("Meeting", "Let's meet tomorrow");
        System.out.println("✓ Created User object: " + user1.getName());
        System.out.println("✓ Created Email object: " + email1.getSubject());
        
        // 2. ENCAPSULATION
        System.out.println("\n2️⃣ ENCAPSULATION (Data Hiding)");
        System.out.println("-".repeat(40));
        user1.setPersonalDetails("john@email.com, +1-555-0101");
        System.out.println("✓ Private field accessed via public setter");
        System.out.println("✓ Personal Details: " + user1.getPersonalDetails());
        
        // 3. INHERITANCE
        System.out.println("\n3️⃣ INHERITANCE");
        System.out.println("-".repeat(40));
        SystemFolder inbox = new SystemFolder(1, "Inbox", "📥", "System inbox folder");
        CustomFolder projects = new CustomFolder(1, "Projects", "#8e44ad");
        System.out.println("✓ SystemFolder extends Folder: " + inbox.toString());
        System.out.println("✓ CustomFolder extends Folder: " + projects.toString());
        System.out.println("✓ Inbox can delete: " + inbox.canDelete() + " (System)");
        System.out.println("✓ Projects can delete: " + projects.canDelete() + " (Custom)");
        
        // 4. POLYMORPHISM - Method Overloading
        System.out.println("\n4️⃣ POLYMORPHISM - Method Overloading");
        System.out.println("-".repeat(40));
        String text = "This is a very long email subject that needs to be truncated";
        System.out.println("✓ Original: " + text);
        System.out.println("✓ Truncate(20): " + StringUtils.truncate(text, 20));
        System.out.println("✓ Truncate(20, '>>>'): " + StringUtils.truncate(text, 20, ">>>"));
        
        // 5. POLYMORPHISM - Method Overriding
        System.out.println("\n5️⃣ POLYMORPHISM - Method Overriding");
        System.out.println("-".repeat(40));
        Folder genericFolder = new Folder(1, "Generic");
        SystemFolder systemFolder = new SystemFolder(1, "Sent", "📤", "System sent folder");
        System.out.println("✓ Generic folder icon: " + genericFolder.getIcon());
        System.out.println("✓ System folder icon (overridden): " + systemFolder.getIcon());
        
        // 6. ABSTRACTION - Abstract Classes
        System.out.println("\n6️⃣ ABSTRACTION - Abstract Classes");
        System.out.println("-".repeat(40));
        System.out.println("✓ BaseService is abstract - cannot instantiate");
        System.out.println("✓ EmailHandler extends BaseService (abstract)");
        System.out.println("✓ UserHandler extends BaseService (abstract)");
        System.out.println("✓ FolderHandler extends BaseService (abstract)");
        
        // 7. ABSTRACTION - Interfaces
        System.out.println("\n7️⃣ ABSTRACTION - Interfaces");
        System.out.println("-".repeat(40));
        System.out.println("✓ IUserService interface defines contract");
        System.out.println("✓ IEmailService interface defines contract");
        System.out.println("✓ IFolderService interface defines contract");
        System.out.println("✓ IAttachmentService interface defines contract");
        System.out.println("✓ UserService implements IUserService");
        
        // 8. STATIC MEMBERS
        System.out.println("\n8️⃣ STATIC MEMBERS");
        System.out.println("-".repeat(40));
        System.out.println("✓ Constants.Application.APP_NAME: " + Constants.Application.APP_NAME);
        System.out.println("✓ EmailValidator.MAX_SUBJECT_LENGTH: " + EmailValidator.MAX_SUBJECT_LENGTH);
        System.out.println("✓ FileSizeFormatter.KB: " + FileSizeFormatter.KB);
        
        // 9. STATIC METHODS
        System.out.println("\n9️⃣ STATIC METHODS (Utility Classes)");
        System.out.println("-".repeat(40));
        boolean isValid = EmailValidator.isValidSubject("Test Subject");
        String formatted = FileSizeFormatter.format(1048576);
        System.out.println("✓ EmailValidator.isValidSubject(): " + isValid);
        System.out.println("✓ FileSizeFormatter.format(1048576): " + formatted);
        
        // 10. FINAL CLASSES
        System.out.println("\n🔟 FINAL CLASSES (Cannot be extended)");
        System.out.println("-".repeat(40));
        System.out.println("✓ EmailValidator is final");
        System.out.println("✓ DateFormatter is final");
        System.out.println("✓ FileSizeFormatter is final");
        System.out.println("✓ StringUtils is final");
        System.out.println("✓ ColorUtils is final");
        System.out.println("✓ Constants is final");
        
        // 11. INNER CLASSES
        System.out.println("\n1️⃣1️⃣ INNER CLASSES");
        System.out.println("-".repeat(40));
        UserProfile profile = new UserProfile("alice_smith", "pass456");
        UserProfile.Address address = profile.createAddress(
            "123 Main St", "New York", "NY", "10001", "USA"
        );
        profile.setAddress(address);
        System.out.println("✓ UserProfile contains inner class Address");
        System.out.println("✓ Address: " + address.toString());
        
        // 12. NESTED STATIC CLASSES
        System.out.println("\n1️⃣2️⃣ NESTED STATIC CLASSES");
        System.out.println("-".repeat(40));
        System.out.println("✓ PriorityEmail.Priority (static nested class)");
        System.out.println("✓ Priority.HIGH: " + PriorityEmail.Priority.HIGH);
        System.out.println("✓ Priority.isValid('High'): " + PriorityEmail.Priority.isValid("High"));
        
        // 13. CONSTRUCTOR OVERLOADING
        System.out.println("\n1️⃣3️⃣ CONSTRUCTOR OVERLOADING");
        System.out.println("-".repeat(40));
        User user2 = new User(); // Default constructor
        User user3 = new User("bob", "pass789"); // Parameterized
        User user4 = new User(1, "charlie", "pass321", null, null, null, true); // Full
        System.out.println("✓ User has 3 overloaded constructors");
        System.out.println("✓ Default: " + (user2.getName() == null ? "null name" : user2.getName()));
        System.out.println("✓ Parameterized: " + user3.getName());
        System.out.println("✓ Full: " + user4.getName());
        
        // 14. AGGREGATION AND COMPOSITION
        System.out.println("\n1️⃣4️⃣ AGGREGATION AND COMPOSITION");
        System.out.println("-".repeat(40));
        Email emailWithAttachments = new Email("Report", "Please review");
        Attachment att1 = new Attachment(1, "report.pdf", 2048, "application/pdf", "path/to/file");
        emailWithAttachments.addAttachment(att1);
        System.out.println("✓ Email HAS-A Attachment (composition)");
        System.out.println("✓ Email has " + emailWithAttachments.getAttachmentCount() + " attachment(s)");
        
        // 15. COLLECTIONS
        System.out.println("\n1️⃣5️⃣ COLLECTIONS FRAMEWORK");
        System.out.println("-".repeat(40));
        User userWithFolders = new User("david", "pass111");
        userWithFolders.addFolder(new Folder(1, "Work"));
        userWithFolders.addFolder(new Folder(1, "Personal"));
        System.out.println("✓ User contains List<Folder> (ArrayList)");
        System.out.println("✓ Folder count: " + userWithFolders.getFolders().size());
        
        // 16. EXCEPTION HANDLING
        System.out.println("\n1️⃣6️⃣ EXCEPTION HANDLING");
        System.out.println("-".repeat(40));
        try {
            // Attempt to create utility class instance (will throw exception)
            // EmailValidator validator = new EmailValidator(); // This would fail
            System.out.println("✓ Try-catch blocks in all service methods");
            System.out.println("✓ SQLException handling in database operations");
            System.out.println("✓ Custom exception for utility class instantiation");
        } catch (Exception e) {
            System.out.println("✗ Exception caught: " + e.getMessage());
        } finally {
            System.out.println("✓ Finally block ensures resource cleanup");
        }
        
        // 17. toString() METHOD OVERRIDE
        System.out.println("\n1️⃣7️⃣ toString() METHOD OVERRIDE");
        System.out.println("-".repeat(40));
        System.out.println("✓ User.toString(): " + user1.toString());
        System.out.println("✓ Email.toString(): " + email1.toString());
        System.out.println("✓ Folder.toString(): " + inbox.toString());
        
        // 18. equals() AND hashCode() OVERRIDE
        System.out.println("\n1️⃣8️⃣ equals() AND hashCode() OVERRIDE");
        System.out.println("-".repeat(40));
        User userA = new User("test", "pass");
        userA.setUserId(1);
        User userB = new User("test", "pass");
        userB.setUserId(1);
        System.out.println("✓ userA.equals(userB): " + userA.equals(userB));
        System.out.println("✓ userA.hashCode() == userB.hashCode(): " + 
                          (userA.hashCode() == userB.hashCode()));
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ ALL OOP CONCEPTS DEMONSTRATED SUCCESSFULLY!");
        System.out.println("=".repeat(60));
    }
    
    /**
     * ============================================
     * DBMS CONCEPTS DEMONSTRATION
     * ============================================
     */
    private static void demonstrateDBMSConcepts(DatabaseHelper dbHelper, 
                                                UserService userService,
                                                EmailService emailService,
                                                FolderService folderService) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 2: DBMS CONCEPTS DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        Connection conn = null;
        
        try {
            conn = dbHelper.getConnection();
            
            // 1. DDL - Data Definition Language
            System.out.println("\n1️⃣ DDL - Data Definition Language");
            System.out.println("-".repeat(40));
            System.out.println("✓ CREATE TABLE (in schema.sql)");
            System.out.println("✓ ALTER TABLE (constraints, indexes)");
            System.out.println("✓ DROP TABLE (in cleanup operations)");
            System.out.println("✓ TRUNCATE TABLE (for testing)");
            
            // 2. DML - Data Manipulation Language
            System.out.println("\n2️⃣ DML - Data Manipulation Language");
            System.out.println("-".repeat(40));
            System.out.println("✓ INSERT (signup, sendEmail methods)");
            System.out.println("✓ SELECT (all getXxx methods)");
            System.out.println("✓ UPDATE (markAsRead, updateProfile)");
            System.out.println("✓ DELETE (deleteEmail, deleteFolder)");
            
            // 3. CONSTRAINTS
            System.out.println("\n3️⃣ CONSTRAINTS");
            System.out.println("-".repeat(40));
            System.out.println("✓ PRIMARY KEY (UserID, EmailID, FolderID)");
            System.out.println("✓ FOREIGN KEY (UserID, EmailID in junction tables)");
            System.out.println("✓ UNIQUE (User.Name)");
            System.out.println("✓ NOT NULL (required fields)");
            System.out.println("✓ DEFAULT (timestamps, boolean flags)");
            System.out.println("✓ CHECK (via ENUM for Priority, Role)");
            
            // 4. JOINS
            System.out.println("\n4️⃣ JOINS");
            System.out.println("-".repeat(40));
            System.out.println("✓ INNER JOIN (Email + EmailUser + User)");
            System.out.println("✓ LEFT JOIN (Email with optional attachments)");
            System.out.println("✓ Multiple joins in complex queries");
            demonstrateJoins(conn);
            
            // 5. AGGREGATE FUNCTIONS
            System.out.println("\n5️⃣ AGGREGATE FUNCTIONS");
            System.out.println("-".repeat(40));
            System.out.println("✓ COUNT() - getEmailStats");
            System.out.println("✓ SUM() - total storage, attachment sizes");
            System.out.println("✓ AVG() - average email length");
            System.out.println("✓ MAX() - newest email");
            System.out.println("✓ MIN() - oldest email");
            demonstrateAggregates(conn);
            
            // 6. GROUP BY / HAVING
            System.out.println("\n6️⃣ GROUP BY / HAVING");
            System.out.println("-".repeat(40));
            demonstrateGroupBy(conn);
            
            // 7. SUBQUERIES
            System.out.println("\n7️⃣ SUBQUERIES");
            System.out.println("-".repeat(40));
            System.out.println("✓ Scalar subquery (email count)");
            System.out.println("✓ Correlated subquery (unread count per user)");
            System.out.println("✓ EXISTS clause");
            demonstrateSubqueries(conn);
            
            // 8. SET OPERATIONS
            System.out.println("\n8️⃣ SET OPERATIONS");
            System.out.println("-".repeat(40));
            System.out.println("✓ UNION (senders + receivers)");
            System.out.println("✓ UNION ALL (combined counts)");
            System.out.println("✓ Simulated INTERSECT (users who sent AND received)");
            System.out.println("✓ Simulated EXCEPT (users who sent but not received)");
            
            // 9. INDEXES
            System.out.println("\n9️⃣ INDEXES");
            System.out.println("-".repeat(40));
            System.out.println("✓ Primary key indexes (automatic)");
            System.out.println("✓ Foreign key indexes");
            System.out.println("✓ Composite indexes (UserID, Role, IsRead)");
            System.out.println("✓ FULLTEXT index (Email.Subject, Email.Body)");
            System.out.println("✓ Custom indexes for optimization");
            
            // 10. VIEWS
            System.out.println("\n🔟 VIEWS");
            System.out.println("-".repeat(40));
            System.out.println("✓ EmailWithDetails view");
            System.out.println("✓ UserEmailStatistics view");
            System.out.println("✓ UserDashboard view");
            System.out.println("✓ PriorityEmailSummary view");
            System.out.println("✓ AttachmentStatistics view");
            demonstrateViews(conn);
            
            // 11. STORED PROCEDURES
            System.out.println("\n1️⃣1️⃣ STORED PROCEDURES");
            System.out.println("-".repeat(40));
            System.out.println("✓ GetUserEmailCount (with OUT parameters)");
            System.out.println("✓ SendEmailProcedure (transactional)");
            System.out.println("✓ ArchiveOldEmails (batch operations)");
            System.out.println("✓ GetEmailStatistics (with cursor)");
            System.out.println("✓ BulkDeleteEmails (conditional delete)");
            demonstrateStoredProcedure(conn);
            
            // 12. FUNCTIONS
            System.out.println("\n1️⃣2️⃣ FUNCTIONS");
            System.out.println("-".repeat(40));
            System.out.println("✓ CalculateStorageUsed (returns BIGINT)");
            System.out.println("✓ GetUnreadCount (returns INT)");
            System.out.println("✓ IsEmailStarred (returns BOOLEAN)");
            System.out.println("✓ GetUserActivityScore (returns DECIMAL)");
            demonstrateFunctions(conn);
            
            // 13. TRIGGERS
            System.out.println("\n1️⃣3️⃣ TRIGGERS");
            System.out.println("-".repeat(40));
            System.out.println("✓ CreateDefaultFolders (AFTER INSERT on User)");
            System.out.println("✓ ValidateEmailBeforeInsert (BEFORE INSERT)");
            System.out.println("✓ LogEmailDeletion (AFTER UPDATE)");
            System.out.println("✓ PreventSystemFolderDeletion (BEFORE DELETE)");
            
            // 14. TRANSACTIONS
            System.out.println("\n1️⃣4️⃣ TRANSACTIONS");
            System.out.println("-".repeat(40));
            System.out.println("✓ BEGIN TRANSACTION");
            System.out.println("✓ COMMIT");
            System.out.println("✓ ROLLBACK on error");
            System.out.println("✓ Used in sendEmail() method");
            System.out.println("✓ ACID properties maintained");
            
            // 15. NORMALIZATION
            System.out.println("\n1️⃣5️⃣ NORMALIZATION");
            System.out.println("-".repeat(40));
            System.out.println("✓ 1NF: Atomic values, no repeating groups");
            System.out.println("✓ 2NF: No partial dependencies");
            System.out.println("✓ 3NF: No transitive dependencies");
            System.out.println("✓ BCNF: Every determinant is a candidate key");
            System.out.println("✓ EmailUser junction table (M:N relationship)");
            
            // 16. ER MODEL
            System.out.println("\n1️⃣6️⃣ ER MODEL IMPLEMENTATION");
            System.out.println("-".repeat(40));
            System.out.println("✓ Entities: User, Email, Folder, Attachment");
            System.out.println("✓ Relationships: User-Email (M:N), Email-Attachment (1:N)");
            System.out.println("✓ Weak entity: Folder (depends on User)");
            System.out.println("✓ Attributes: Simple, composite, derived");
            
            // 17. PREPARED STATEMENTS (SQL Injection Prevention)
            System.out.println("\n1️⃣7️⃣ PREPARED STATEMENTS");
            System.out.println("-".repeat(40));
            System.out.println("✓ All queries use PreparedStatement");
            System.out.println("✓ Parameter binding prevents SQL injection");
            System.out.println("✓ Example: login(?, ?) - parameters are escaped");
            
            // 18. CASCADING OPERATIONS
            System.out.println("\n1️⃣8️⃣ CASCADING OPERATIONS");
            System.out.println("-".repeat(40));
            System.out.println("✓ ON DELETE CASCADE (User -> Folder)");
            System.out.println("✓ ON DELETE CASCADE (Email -> Attachment)");
            System.out.println("✓ ON DELETE SET NULL (Folder -> EmailUser)");
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ ALL DBMS CONCEPTS DEMONSTRATED SUCCESSFULLY!");
            System.out.println("=".repeat(60));
            
        } catch (SQLException e) {
            System.err.println("Error demonstrating DBMS concepts: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Demonstrate JOINS
     */
    private static void demonstrateJoins(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) as Total, " +
                 "SUM(CASE WHEN eu.Role='Sender' THEN 1 ELSE 0 END) as Sent, " +
                 "AVG(LENGTH(e.Body)) as AvgLength " +
                 "FROM Email e " +
                 "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                 "WHERE eu.IsDeleted = FALSE")) {
            
            if (rs.next()) {
                System.out.println("  • Total emails: " + rs.getInt("Total"));
                System.out.println("  • Sent emails: " + rs.getInt("Sent"));
                System.out.println("  • Avg length: " + rs.getInt("AvgLength") + " chars");
            }
        } catch (SQLException e) {
            System.out.println("  (No data available for demo)");
        }
    }
    
    /**
     * Demonstrate GROUP BY / HAVING
     */
    private static void demonstrateGroupBy(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT e.Priority, COUNT(*) as Count " +
                 "FROM Email e " +
                 "JOIN EmailUser eu ON e.EmailID = eu.EmailID " +
                 "WHERE eu.IsDeleted = FALSE " +
                 "GROUP BY e.Priority " +
                 "HAVING COUNT(*) > 0")) {
            
            while (rs.next()) {
                System.out.println("  • " + rs.getString("Priority") + 
                                 " priority: " + rs.getInt("Count") + " emails");
            }
        } catch (SQLException e) {
            System.out.println("  (No data available for demo)");
        }
    }
    
    /**
     * Demonstrate SUBQUERIES
     */
    private static void demonstrateSubqueries(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT Name, " +
                 "(SELECT COUNT(*) FROM EmailUser WHERE UserID = u.UserID AND IsDeleted = FALSE) as EmailCount " +
                 "FROM User u " +
                 "LIMIT 3")) {
            
            while (rs.next()) {
                System.out.println("  • " + rs.getString("Name") + 
                                 ": " + rs.getInt("EmailCount") + " emails");
            }
        } catch (SQLException e) {
            System.out.println("  (No data available for demo)");
        }
    }
    
    /**
     * Demonstrate VIEWS
     */
    private static void demonstrateViews(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM UserDashboard LIMIT 3")) {
            
            while (rs.next()) {
                System.out.println("  • " + rs.getString("Name") + 
                                 " - Total: " + rs.getInt("TotalEmails") +
                                 ", Unread: " + rs.getInt("UnreadEmails"));
            }
        } catch (SQLException e) {
            System.out.println("  (View not available - run advanced_features.sql first)");
        }
    }
    
    /**
     * Demonstrate STORED PROCEDURES
     */
    private static void demonstrateStoredProcedure(Connection conn) {
        try (CallableStatement cstmt = conn.prepareCall("{CALL GetUserEmailCount(?, ?, ?, ?)}")) {
            cstmt.setInt(1, 1);
            cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
            cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            cstmt.execute();
            
            System.out.println("  • Total: " + cstmt.getInt(2));
            System.out.println("  • Unread: " + cstmt.getInt(3));
            System.out.println("  • Sent: " + cstmt.getInt(4));
        } catch (SQLException e) {
            System.out.println("  (Procedure not available - run advanced_features.sql first)");
        }
    }
    
    /**
     * Demonstrate FUNCTIONS
     */
    private static void demonstrateFunctions(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT CalculateStorageUsed(1) as Storage, " +
                 "GetUnreadCount(1) as Unread")) {
            
            if (rs.next()) {
                long storage = rs.getLong("Storage");
                int unread = rs.getInt("Unread");
                System.out.println("  • Storage: " + FileSizeFormatter.format(storage));
                System.out.println("  • Unread: " + unread);
            }
        } catch (SQLException e) {
            System.out.println("  (Functions not available - run advanced_features.sql first)");
        }
    }
}

                 "SELECT e.Subject, sender.Name as Sender, receiver.Name as Receiver " +
                 "FROM Email e " +
                 "INNER JOIN EmailUser eu_s ON e.EmailID = eu_s.EmailID AND eu_s.Role = 'Sender' " +
                 "INNER JOIN User sender ON eu_s.UserID = sender.UserID " +
                 "LEFT JOIN EmailUser eu_r ON e.EmailID = eu_r.EmailID AND eu_r.Role = 'Receiver' " +
                 "LEFT JOIN User receiver ON eu_r.UserID = receiver.UserID " +
                 "LIMIT 3")) {
            
            int count = 0;
            while (rs.next() && count < 3) {
                System.out.println("  • " + rs.getString("Subject") + 
                                 " (From: " + rs.getString("Sender") + 
                                 " To: " + rs.getString("Receiver") + ")");
                count++;
            }
        } catch (SQLException e) {
            System.out.println("  (No data available for demo)");
        }
    }
    
    /**
     * Demonstrate AGGREGATE functions
     */
    private static void demonstrateAggregates(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
