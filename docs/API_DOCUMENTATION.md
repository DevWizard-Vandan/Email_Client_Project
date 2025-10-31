---

### 📄 `API_DOCUMENTATION.md`
````markdown
# 📚 API Documentation - Professional Email Client

Complete reference for all service layer methods and their usage.

---

## 📑 Table of Contents
- [DatabaseHelper](#databasehelper)
- [UserService](#userservice)
- [EmailService](#emailservice)
- [FolderService](#folderservice)
- [AttachmentService](#attachmentservice)
- [Error Handling](#error-handling)
- [Usage Examples](#usage-examples)

---

## 🗄️ DatabaseHelper

**Purpose**: Manages database connections and schema initialization.

### Methods

#### `getConnection()`
```java
public Connection getConnection() throws SQLException
```
**Description**: Establishes and returns a database connection.

**Returns**: `Connection` object

**Throws**: `SQLException` if connection fails

**Example**:
```java
DatabaseHelper dbHelper = new DatabaseHelper();
Connection conn = dbHelper.getConnection();
// Use connection
dbHelper.closeConnection(conn);
```

---

#### `initializeDatabase()`
```java
public void initializeDatabase()
```
**Description**: Creates database if not exists and sets up all tables, triggers, and views.

**Example**:
```java
DatabaseHelper dbHelper = new DatabaseHelper();
dbHelper.initializeDatabase(); // One-time setup
```

---

#### `testConnection()`
```java
public boolean testConnection()
```
**Description**: Tests database connectivity.

**Returns**: `true` if connection successful, `false` otherwise

---

## 👤 UserService

**Purpose**: Handles user authentication and profile management.

### Methods

#### `signup(User user)`
```java
public boolean signup(User user)
```
**Description**: Registers a new user with validation.

**Parameters**:
- `user` - User object with name, password, and optional personal details

**Returns**: `true` if signup successful, `false` otherwise

**Validation**:
- Username: 3+ characters, unique
- Password: 4+ characters
- Automatic folder creation via trigger

**Example**:
```java
User newUser = new User();
newUser.setName("john_doe");
newUser.setPassword("secure123");
newUser.setPersonalDetails("John Doe, john@email.com");

boolean success = userService.signup(newUser);
if (success) {
    System.out.println("User registered successfully!");
}
```

---

#### `login(String username, String password)`
```java
public User login(String username, String password)
```
**Description**: Authenticates user credentials.

**Parameters**:
- `username` - User's username
- `password` - User's password (plain text for demo)

**Returns**: `User` object if successful, `null` if invalid credentials

**Example**:
```java
User user = userService.login("john_doe", "secure123");
if (user != null) {
    System.out.println("Login successful: " + user.getName());
} else {
    System.out.println("Invalid credentials");
}
```

---

#### `updateLastLogin(int userId)`
```java
public boolean updateLastLogin(int userId)
```
**Description**: Updates the last login timestamp for a user.

**Parameters**:
- `userId` - User's ID

**Returns**: `true` if update successful

---

#### `getUserByUsername(String username)`
```java
public User getUserByUsername(String username)
```
**Description**: Retrieves user by username.

**Returns**: `User` object or `null` if not found

---

#### `isValidInput(User user)`
```java
public boolean isValidInput(User user)
```
**Description**: Validates user input according to business rules.

**Validation Rules**:
- Username ≥ 3 characters
- Password ≥ 4 characters
- Non-null values

**Returns**: `true` if valid

---

## 📧 EmailService

**Purpose**: Manages all email operations including sending, retrieving, and searching.

### Methods

#### `sendEmail(Email email, int senderId, String recipientUsername)`
```java
public boolean sendEmail(Email email, int senderId, String recipientUsername)
```
**Description**: Sends an email with transactional consistency.

**Parameters**:
- `email` - Email object with subject, body, priority
- `senderId` - Sender's user ID
- `recipientUsername` - Recipient's username (not ID)

**Validation**:
- Recipient must exist
- Cannot send to self
- Subject and body are required

**Transaction**: Atomically creates email record and sender/receiver associations

**Example**:
```java
Email email = new Email();
email.setSubject("Meeting Reminder");
email.setBody("Don't forget our meeting tomorrow at 10 AM.");
email.setPriority("High");

boolean sent = emailService.sendEmail(email, currentUser.getUserId(), "alice_smith");
if (sent) {
    System.out.println("Email sent successfully!");
}
```

---

#### `getInboxEmails(int userId)`
```java
public List<Email> getInboxEmails(int userId)
```
**Description**: Retrieves all inbox emails for a user.

**Returns**: List of Email objects with sender details

**Sorting**: By timestamp (newest first)

---

#### `getSentEmails(int userId)`
```java
public List<Email> getSentEmails(int userId)
```
**Description**: Retrieves all sent emails for a user.

**Returns**: List of Email objects with receiver details

---

#### `getEmailsByFolder(int userId, int folderId)`
```java
public List<Email> getEmailsByFolder(int userId, int folderId)
```
**Description**: Retrieves emails from a specific folder.

**Parameters**:
- `userId` - User ID
- `folderId` - Folder ID

**Returns**: List of emails in the folder

---

#### `searchEmails(int userId, String searchTerm, String role)`
```java
public List<Email> searchEmails(int userId, String searchTerm, String role)
```
**Description**: Searches emails by keyword in subject or body.

**Parameters**:
- `userId` - User ID
- `searchTerm` - Keyword to search
- `role` - "Sender" or "Receiver"

**Returns**: List of matching emails

**Example**:
```java
List<Email> results = emailService.searchEmails(userId, "meeting", "Receiver");
System.out.println("Found " + results.size() + " emails");
```

---

#### `markAsRead(int emailId, int userId)`
```java
public boolean markAsRead(int emailId, int userId)
```
**Description**: Marks an email as read.

**Returns**: `true` if successful

---

#### `toggleStar(int emailId, int userId)`
```java
public boolean toggleStar(int emailId, int userId)
```
**Description**: Toggles star status of an email.

**Returns**: `true` if successful

---

#### `deleteEmail(int emailId, int userId)`
```java
public boolean deleteEmail(int emailId, int userId)
```
**Description**: Soft deletes an email (sets IsDeleted flag).

**Returns**: `true` if successful

**Note**: Email is not permanently removed from database

---

#### `getEmailStats(int userId)`
```java
public EmailStats getEmailStats(int userId)
```
**Description**: Retrieves comprehensive email statistics.

**Returns**: EmailStats object containing:
- Total emails
- Unread count
- Starred count
- Sent/received counts
- Total storage size

**Example**:
```java
EmailStats stats = emailService.getEmailStats(userId);
System.out.println("Total emails: " + stats.getTotalEmails());
System.out.println("Unread: " + stats.getUnreadEmails());
System.out.println("Storage: " + stats.getFormattedSize());
```

---

## 📁 FolderService

**Purpose**: Manages folder creation, organization, and email movement.

### Methods

#### `createFolder(int userId, String name, Integer parentId, String color)`
```java
public boolean createFolder(int userId, String name, Integer parentId, String color)
```
**Description**: Creates a new custom folder.

**Parameters**:
- `userId` - Owner's user ID
- `name` - Folder name
- `parentId` - Parent folder ID (null for root level)
- `color` - Hex color code (e.g., "#3498db")

**Returns**: `true` if created successfully

**Example**:
```java
boolean created = folderService.createFolder(userId, "Work", null, "#2980b9");
```

---

#### `getUserFolders(int userId)`
```java
public List<Folder> getUserFolders(int userId)
```
**Description**: Retrieves all folders for a user with email counts.

**Returns**: List of Folder objects with:
- Email count
- Unread count
- System/custom flag

**Sorting**: System folders first, then alphabetically

---

#### `moveEmailToFolder(int emailId, int userId, int folderId)`
```java
public boolean moveEmailToFolder(int emailId, int userId, int folderId)
```
**Description**: Moves an email to a different folder.

**Returns**: `true` if successful

---

#### `deleteFolder(int folderId, int userId)`
```java
public boolean deleteFolder(int folderId, int userId)
```
**Description**: Deletes a custom folder (system folders protected).

**Returns**: `true` if deleted, `false` if system folder

**Cascade**: Associated emails moved to Inbox

---

#### `renameFolder(int folderId, String newName)`
```java
public boolean renameFolder(int folderId, String newName)
```
**Description**: Renames a custom folder.

**Returns**: `true` if renamed, `false` if system folder

---

#### `getFolderEmailCount(int folderId)`
```java
public int getFolderEmailCount(int folderId)
```
**Description**: Gets email count for a folder.

**Returns**: Number of emails (excluding deleted)

---

## 📎 AttachmentService

**Purpose**: Manages file attachments including upload, download, and metadata.

### Methods

#### `saveAttachment(int emailId, File file)`
```java
public boolean saveAttachment(int emailId, File file)
```
**Description**: Saves file to filesystem and metadata to database.

**Parameters**:
- `emailId` - Associated email ID
- `file` - File object to attach

**Storage Location**: `attachments/user_{userId}/{timestamp}_{filename}`

**Validation**:
- File must exist
- Size limit: 25 MB (configurable)

**Returns**: `true` if saved successfully

**Example**:
```java
File file = new File("report.pdf");
boolean saved = attachmentService.saveAttachment(emailId, file);
```

---

#### `getEmailAttachments(int emailId)`
```java
public List<Attachment> getEmailAttachments(int emailId)
```
**Description**: Retrieves all attachments for an email.

**Returns**: List of Attachment objects with metadata

---

#### `downloadAttachment(int attachmentId, String destinationPath)`
```java
public boolean downloadAttachment(int attachmentId, String destinationPath)
```
**Description**: Downloads attachment to specified location.

**Parameters**:
- `attachmentId` - Attachment ID
- `destinationPath` - Full path where file should be saved

**Returns**: `true` if downloaded successfully

**Example**:
```java
boolean downloaded = attachmentService.downloadAttachment(
    attachmentId, 
    "C:/Downloads/report.pdf"
);
```

---

#### `deleteAttachment(int attachmentId)`
```java
public boolean deleteAttachment(int attachmentId)
```
**Description**: Deletes attachment from database and filesystem.

**Returns**: `true` if deleted successfully

---

#### `getTotalAttachmentSize(int emailId)`
```java
public long getTotalAttachmentSize(int emailId)
```
**Description**: Calculates total size of all attachments for an email.

**Returns**: Size in bytes

---

#### `getMimeType(String fileName)`
```java
public String getMimeType(String fileName)
```
**Description**: Determines MIME type from file extension.

**Supported Types**:
- Documents: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX
- Images: JPG, PNG, GIF, BMP
- Archives: ZIP, RAR, 7Z
- Media: MP3, MP4, AVI

**Returns**: MIME type string (e.g., "application/pdf")

---

## ⚠️ Error Handling

### Strategy
All service methods follow a consistent error handling pattern:
```java
try {
    // Database operations
    return successResult;
} catch (SQLException e) {
    System.err.println("Error description: " + e.getMessage());
    e.printStackTrace(); // For debugging
    return failureResult;
} finally {
    // Always close resources
    dbHelper.closeResultSet(rs);
    dbHelper.closeStatement(stmt);
    dbHelper.closeConnection(conn);
}
```

### Error Types

| Error | Cause | User Message | Technical Log |
|-------|-------|--------------|---------------|
| Login Failed | Invalid credentials | "Invalid username or password" | "Login failed for user: {username}" |
| Send Failed | Recipient not found | "Recipient does not exist" | "Recipient not found: {username}" |
| Connection Error | MySQL down | "Database connection failed" | Full stack trace logged |
| Validation Error | Invalid input | "Please check your input" | "Validation failed: {reason}" |

---

## 💡 Usage Examples

### Complete Email Workflow
```java
// Initialize services
DatabaseHelper dbHelper = new DatabaseHelper();
dbHelper.initializeDatabase();

UserService userService = new UserService(dbHelper);
EmailService emailService = new EmailService(dbHelper);
FolderService folderService = new FolderService(dbHelper);
AttachmentService attachmentService = new AttachmentService(dbHelper);

// 1. User Login
User user = userService.login("john_doe", "pass123");
if (user == null) {
    System.out.println("Login failed");
    return;
}

// 2. Compose Email
Email email = new Email();
email.setSubject("Quarterly Report");
email.setBody("Please review the attached quarterly report.");
email.setPriority("High");

// 3. Send Email
boolean sent = emailService.sendEmail(email, user.getUserId(), "alice_smith");
if (!sent) {
    System.out.println("Failed to send email");
    return;
}

// 4. Add Attachment
File report = new File("Q4_Report.pdf");
attachmentService.saveAttachment(email.getEmailId(), report);

// 5. View Inbox (as recipient)
User alice = userService.login("alice_smith", "pass456");
List<Email> inbox = emailService.getInboxEmails(alice.getUserId());

for (Email e : inbox) {
    System.out.println("From: " + e.getSenderName());
    System.out.println("Subject: " + e.getSubject());
    if (e.hasAttachments()) {
        System.out.println("Attachments: " + e.getAttachmentCount());
    }
}

// 6. Mark as Read
emailService.markAsRead(email.getEmailId(), alice.getUserId());

// 7. Get Statistics
EmailStats stats = emailService.getEmailStats(alice.getUserId());
System.out.println("Total emails: " + stats.getTotalEmails());
System.out.println("Unread: " + stats.getUnreadEmails());
```

---

### Folder Management Example
```java
// Get user folders
List<Folder> folders = folderService.getUserFolders(userId);

// Create custom folder
folderService.createFolder(userId, "Projects", null, "#8e44ad");

// Move email to folder
Folder projectsFolder = folders.stream()
    .filter(f -> f.getName().equals("Projects"))
    .findFirst()
    .orElse(null);

if (projectsFolder != null) {
    folderService.moveEmailToFolder(emailId, userId, projectsFolder.getFolderId());
}

// Get email count
int count = folderService.getFolderEmailCount(projectsFolder.getFolderId());
System.out.println("Emails in Projects: " + count);
```

---

### Search Example
```java
// Search inbox for keyword
String keyword = "report";
List<Email> results = emailService.searchEmails(
    userId, 
    keyword, 
    "Receiver"
);

System.out.println("Found " + results.size() + " emails matching '" + keyword + "'");

for (Email email : results) {
    System.out.println("- " + email.getSubject());
}
```

---

## 🔒 Security Considerations

### Password Storage
**⚠️ IMPORTANT**: This application uses **plain text** password storage for **educational purposes only**.

**Production Requirements**:
```java
// Instead of plain text:
user.setPassword("mypassword");

// Use hashing:
String hashedPassword = BCrypt.hashpw("mypassword", BCrypt.gensalt(12));
user.setPassword(hashedPassword);

// Verification:
if (BCrypt.checkpw(inputPassword, user.getPassword())) {
    // Login successful
}
```

### SQL Injection Prevention
All queries use **PreparedStatement** with parameter binding:
```java
// ✅ SAFE - Using PreparedStatement
String sql = "SELECT * FROM User WHERE Name = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, username);

// ❌ UNSAFE - String concatenation
String sql = "SELECT * FROM User WHERE Name = '" + username + "'";
```

---

## 📊 Performance Optimization

### Database Indexes
All foreign keys and frequently queried columns are indexed:
- `User.Name` (UNIQUE)
- `Email.Timestamp`
- `EmailUser.UserID`, `EmailUser.Role`
- Full-text index on `Email.Subject` and `Email.Body`

### Connection Management
- Use try-with-resources for automatic cleanup
- Always close ResultSet, Statement, and Connection
- Consider connection pooling for production
```java
try (Connection conn = dbHelper.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {
    // Use connection
} // Automatically closed
```

---

## 🧪 Testing Utilities

### Test Data Setup
```java
// Create test users
User testUser = new User("test_user", "test123");
userService.signup(testUser);

// Create test email
Email testEmail = new Email("Test Subject", "Test body");
testEmail.setPriority("Normal");
emailService.sendEmail(testEmail, senderId, "test_user");

// Verify
List<Email> inbox = emailService.getInboxEmails(testUserId);
assert inbox.size() > 0;
```

---

## 📞 Support

For additional help:
- See [README.md](README.md) for overview
- See [SETUP_GUIDE.md](SETUP_GUIDE.md) for installation
- Check console logs for detailed error messages
- Review source code comments for implementation details

---

**API Documentation Version 1.0 | Last Updated: January 2025**
````

---

## 📁 SECTION 7: ADDITIONAL CONFIGURATION FILES

### 📄 `database.properties` (Optional Configuration File)
````properties
# ============================================
# Database Configuration Properties
# ============================================

# JDBC Connection Settings
db.url=jdbc:mysql://localhost:3306/email_client
db.username=root
db.password=

# JDBC Driver
db.driver=com.mysql.cj.jdbc.Driver

# Connection Pool Settings (for future enhancement)
db.pool.size=10
db.pool.max.idle=5
db.pool.min.idle=2

# Connection Timeout (milliseconds)
db.connection.timeout=30000

# Query Timeout (seconds)
db.query.timeout=30

# ============================================
# Usage Instructions:
# 1. Place this file in the project root directory
# 2. Update credentials as needed
# 3. DatabaseHelper will automatically load these settings
# 4. If file not found, defaults in DatabaseHelper.java are used
# ============================================
````

---

### 📄 `attachments/README.txt`
````text
============================================
ATTACHMENTS DIRECTORY
============================================

This directory stores all email attachments uploaded through the application.

STRUCTURE:
attachments/
├── user_1/
│   ├── 1736428800000_document.pdf
│   └── 1736428900000_image.png
├── user_2/
│   └── 1736429000000_report.docx
└── README.txt (this file)

NAMING CONVENTION:
{timestamp}_{original_filename}

- Timestamp: Unix milliseconds when file was uploaded
- Original filename: Preserved for user reference

SECURITY NOTES:
- Each user has a separate subdirectory
- File paths are stored in the Attachment table
- Maximum file size: 25 MB (configurable)
- Deleted attachments are removed from filesystem

MAINTENANCE:
- Old attachments can be archived/deleted manually
- Ensure sufficient disk space
- Regular backups recommended

============================================
DO NOT DELETE THIS DIRECTORY
The application creates it automatically if missing.
============================================
````

---

## 📁 SECTION 8: BUILD AND DEPLOYMENT FILES

### 📄 `.gitignore` (If using Git)
````gitignore
# Compiled class files
*.class
out/
target/
build/

# IntelliJ IDEA
.idea/
*.iml
*.iws
*.ipr

# Eclipse
.classpath
.project
.settings/

# NetBeans
nbproject/private/
build/
nbbuild/
dist/
nbdist/

# macOS
.DS_Store
.AppleDouble
.LSOverride

# Windows
Thumbs.db
ehthumbs.db
Desktop.ini

# Database
*.sql.backup
*.db

# Attachments (uploaded files)
attachments/user_*/
!attachments/README.txt

# Logs
*.log

# Configuration (with sensitive data)
database.properties
config.properties

# Temporary files
*.tmp
*.temp
*.swp
*~
````

---

### 📄 `run_console.bat` (Windows Batch Script)
````batch
@echo off
REM ============================================
REM Console Email Client Launcher (Windows)
REM ============================================

echo Starting Professional Email Client (Console Mode)...
echo.

REM Set paths (adjust these for your system)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar

REM Compile
echo Compiling source files...
cd src
"%JAVA_HOME%\bin\javac" -cp ".;..\%MYSQL_JAR%" Main.java entities\*.java services\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! Please check for errors.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.

REM Run
echo Launching application...
"%JAVA_HOME%\bin\java" -cp ".;..\%MYSQL_JAR%" Main

pause
````

---

### 📄 `run_console.sh` (Unix/macOS Shell Script)
````bash
#!/bin/bash
# ============================================
# Console Email Client Launcher (Unix/macOS)
# ============================================

echo "Starting Professional Email Client (Console Mode)..."
echo

# Set paths (adjust these for your system)
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"

# Compile
echo "Compiling source files..."
cd src
"$JAVA_HOME/bin/javac" -cp ".:../$MYSQL_JAR" Main.java entities/*.java services/*.java

if [ $? -ne 0 ]; then
    echo
    echo "Compilation failed! Please check for errors."
    exit 1
fi

echo
echo "Compilation successful!"
echo

# Run
echo "Launching application..."
"$JAVA_HOME/bin/java" -cp ".:../$MYSQL_JAR" Main

# Make script executable:
# chmod +x run_console.sh
````

---

### 📄 `run_gui.bat` (Windows GUI Launcher)
````batch
@echo off
REM ============================================
REM GUI Email Client Launcher (Windows)
REM ============================================

echo Starting Professional Email Client (GUI Mode)...
echo.

REM Set paths (adjust these for your system)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAFX_PATH=C:\javafx-sdk-21.0.1\lib
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar

REM Compile
echo Compiling source files...
cd src
"%JAVA_HOME%\bin\javac" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp ".;..\%MYSQL_JAR%" MainGUI.java entities\*.java services\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! Please check for errors.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.

REM Run
echo Launching application...
"%JAVA_HOME%\bin\java" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp ".;..\%MYSQL_JAR%" MainGUI

pause
````

---

### 📄 `run_gui.sh` (Unix/macOS GUI Launcher)
````bash
#!/bin/bash
# ============================================
# GUI Email Client Launcher (Unix/macOS)
# ============================================

echo "Starting Professional Email Client (GUI Mode)..."
echo

# Set paths (adjust these for your system)
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
JAVAFX_PATH="/Library/Java/javafx-sdk-21.0.1/lib"
MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"

# Compile
echo "Compiling source files..."
cd src
"$JAVA_HOME/bin/javac" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp ".:../$MYSQL_JAR" MainGUI.java entities/*.java services/*.java

if [ $? -ne 0 ]; then
    echo
    echo "Compilation failed! Please check for errors."
    exit 1
fi

echo
echo "Compilation successful!"
echo

# Run
echo "Launching application..."
"$JAVA_HOME/bin/java" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp ".:../$MYSQL_JAR" MainGUI

# Make script executable:
# chmod +x run_gui.sh
````

---

## 🎉 PROJECT COMPLETION SUMMARY

### ✅ Complete File Checklist

**Java Source Files** (9 files):
- ✅ `Main.java` - Console application
- ✅ `MainGUI.java` - JavaFX GUI application
- ✅ `entities/User.java`
- ✅ `entities/Email.java`
- ✅ `entities/Folder.java`
- ✅ `entities/Attachment.java`
- ✅ `entities/EmailStats.java`
- ✅ `services/DatabaseHelper.java`
- ✅ `services/UserService.java`
- ✅ `services/EmailService.java`
- ✅ `services/FolderService.java`
- ✅ `services/AttachmentService.java`

**Database Files** (3 files):
- ✅ `database/schema.sql`
- ✅ `database/triggers_views.sql`
- ✅ `database/sample_data.sql`

**Resources** (1 file):
- ✅ `src/resources/styles.css`

**Documentation** (3 files):
- ✅ `docs/README.md`
- ✅ `docs/SETUP_GUIDE.md`
- ✅ `docs/API_DOCUMENTATION.md`

**Configuration Files** (6 files):
- ✅ `database.properties`
- ✅ `attachments/README.txt`
- ✅ `.gitignore`
- ✅ `run_console.bat`
- ✅ `run_console.sh`
- ✅ `run_gui.bat`
- ✅ `run_gui.sh`

**External Libraries**:
- ✅ `lib/mysql-connector-j-8.0.33.jar` (download separately)

---

### 📦 Final Project Structure
