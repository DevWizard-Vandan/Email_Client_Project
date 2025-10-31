# 🚀 Setup Guide - Professional Email Client

Complete step-by-step installation and configuration guide.

---

## 📋 Prerequisites Checklist

Before starting, ensure you have:
- [ ] JDK 17 or higher installed
- [ ] MySQL 8.0 or higher installed
- [ ] JavaFX SDK 21 or higher downloaded
- [ ] IntelliJ IDEA installed (Community or Ultimate)
- [ ] MySQL Connector/J 8.0.33 JAR file downloaded

---

## 1️⃣ Install Java Development Kit (JDK)

### Windows
1. Download JDK 17+ from [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Run installer and follow prompts
3. Set `JAVA_HOME` environment variable:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Environment Variables → New (System Variable)
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Java\jdk-17`
4. Verify installation:
````cmd
   java -version
   javac -version
````

### macOS
````bash
brew install openjdk@17
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
````

### Linux
````bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
````

---

## 2️⃣ Install MySQL Server

### Windows
1. Download MySQL Installer from [MySQL Website](https://dev.mysql.com/downloads/installer/)
2. Choose "Custom" installation
3. Select: MySQL Server 8.0, MySQL Workbench
4. Set root password (remember this!)
5. Complete installation

### macOS
````bash
brew install mysql@8.0
brew services start mysql@8.0
mysql_secure_installation
````

### Linux
````bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
````

### Verify MySQL Installation
````bash
mysql -u root -p
# Enter password when prompted
mysql> SELECT VERSION();
mysql> EXIT;
````

---

## 3️⃣ Download JavaFX SDK

1. Go to [OpenJFX Website](https://openjfx.io/)
2. Download JavaFX SDK 21 for your OS
3. Extract to a permanent location:
   - Windows: `C:\javafx-sdk-21.0.1`
   - macOS/Linux: `/Library/Java/javafx-sdk-21.0.1`
4. Note the path to the `lib` folder

---

## 4️⃣ Download MySQL Connector/J

1. Visit [MySQL Connector/J Download](https://dev.mysql.com/downloads/connector/j/)
2. Select Platform: "Platform Independent"
3. Download ZIP archive
4. Extract and locate `mysql-connector-j-8.0.33.jar`
5. Copy JAR to project's `lib/` folder

---

## 5️⃣ Configure IntelliJ IDEA

### Step 1: Open Project
1. Launch IntelliJ IDEA
2. **File** → **Open**
3. Select `EmailClientProject` folder
4. Click **OK**

### Step 2: Set Project SDK
1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. **Project** tab
3. **SDK**: Select JDK 17 or higher
4. **Language Level**: 17 - Sealed types, always-strict floating-point semantics
5. Click **Apply**

### Step 3: Add JavaFX Library
1. Still in Project Structure → **Libraries**
2. Click **+** (New Project Library) → **Java**
3. Navigate to JavaFX SDK `lib` folder
4. Select all JAR files
5. Click **OK** → Name it "javafx-21"
6. Click **Apply**

### Step 4: Add MySQL Connector Library
1. In **Libraries** tab, click **+** again
2. Navigate to `EmailClientProject/lib/`
3. Select `mysql-connector-j-8.0.33.jar`
4. Click **OK** → Name it "mysql-connector"
5. Click **Apply** → **OK**

### Step 5: Configure VM Options
1. **Run** → **Edit Configurations**
2. Click **+** → **Application**
3. Name: "EmailClient GUI"
4. Main class: `MainGUI`
5. **VM options**: Add the following (adjust path):

**Windows**:

--module-path "C:\javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml

**macOS/Linux**:
--module-path "/Library/Java/javafx-sdk-21.0.1/lib" --add-modules javafx.controls,javafx.fxml
6. Click **Apply** → **OK**

---

## 6️⃣ Configure Database

### Option 1: Auto-Configuration (Recommended)
The application automatically creates the database on first run. Just ensure MySQL is running.

### Option 2: Manual Setup
````sql
-- Open MySQL Workbench or command line
mysql -u root -p

-- Run the schema file
SOURCE /path/to/EmailClientProject/database/schema.sql;

-- (Optional) Load sample data
SOURCE /path/to/EmailClientProject/database/sample_data.sql;

-- (Optional) Create triggers and views
SOURCE /path/to/EmailClientProject/database/triggers_views.sql;
````

### Update Database Credentials
If using different credentials, edit `src/services/DatabaseHelper.java`:
````java
private static final String DB_URL = "jdbc:mysql://localhost:3306/email_client";
private static final String DB_USER = "your_username"; // Change this
private static final String DB_PASSWORD = "your_password"; // Change this
````

---

## 7️⃣ Build and Run

### Method 1: Run from IntelliJ
1. Open `MainGUI.java`
2. Right-click in editor → **Run 'MainGUI.main()'**
3. Application should launch

### Method 2: Run from Terminal
````bash
cd EmailClientProject/src

# Compile (adjust paths for your system)
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp "../lib/*:." MainGUI.java entities/*.java services/*.java

# Run
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp "../lib/*:." MainGUI
````

---

## 8️⃣ Verification Checklist

Run through these tests to ensure everything works:

- [ ] Application launches without errors
- [ ] Login screen displays correctly
- [ ] Can create a new account
- [ ] Can login with created account
- [ ] Main interface shows three panels
- [ ] Folders appear in left panel
- [ ] Can compose and send email
- [ ] Email appears in recipient's inbox
- [ ] Can view email content
- [ ] Can mark email as read
- [ ] Can star/unstar emails
- [ ] Statistics display correctly

---

## 🔧 Common Errors and Solutions

### Error 1: "JavaFX runtime components are missing"
**Solution**:

Verify JavaFX SDK path in VM options
Ensure --module-path points to correct location
Check that all JavaFX JARs are in the lib folder


### Error 2: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution**:

Verify mysql-connector-j-8.0.33.jar is in lib/ folder
Check that library is added in Project Structure
Rebuild project (Build → Rebuild Project)


### Error 3: "Access denied for user 'root'@'localhost'"
**Solution**:

Check MySQL credentials in DatabaseHelper.java
Verify MySQL server is running
Reset MySQL root password if forgotten


### Error 4: "Cannot connect to MySQL server"
**Solution**:

Start MySQL service:

Windows: services.msc → MySQL80 → Start
macOS: brew services start mysql
Linux: sudo systemctl start mysql


Check firewall settings
Verify port 3306 is not blocked


### Error 5: "OutOfMemoryError" when running
**Solution**:
Add to VM options:
-Xms512m -Xmx1024m

---

## 🎯 Testing the Application

### Test Scenario 1: User Registration
1. Launch application
2. Click "Sign Up" tab
3. Enter:
   - Username: `testuser`
   - Password: `test123`
   - Personal Details: `Test User, test@email.com`
4. Click "Sign Up"
5. ✅ Success message should appear

### Test Scenario 2: Sending Email
1. Login as `testuser`
2. Click "Compose"
3. Enter:
   - To: `john_doe` (from sample data)
   - Subject: `Test Email`
   - Body: `This is a test email`
   - Priority: `Normal`
4. Click "OK"
5. ✅ Email should appear in Sent folder

### Test Scenario 3: Receiving Email
1. Logout
2. Login as `john_doe` / `pass123`
3. Click Inbox
4. ✅ New email from `testuser` should appear

---

## 📚 Additional Resources

- [JavaFX Documentation](https://openjfx.io/openjfx-docs/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [IntelliJ IDEA Help](https://www.jetbrains.com/help/idea/)
- [Project API Documentation](API_DOCUMENTATION.md)

---

## 🆘 Still Having Issues?

If you encounter problems not covered here:

1. Check the [README.md](README.md) Troubleshooting section
2. Review console output for error messages
3. Verify all prerequisites are correctly installed
4. Check MySQL error logs: `/var/log/mysql/error.log`
5. Ensure no other applications are using port 3306

---

**Setup complete! You're ready to use the Professional Email Client** 🎉
