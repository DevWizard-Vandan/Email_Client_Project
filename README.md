# Email Client Project

A console-based email client application built with Java and SQLite database, demonstrating Object-Oriented Programming (OOP) concepts and Database Management System (DBMS) operations.

## Project Overview

This email client allows users to:
- Sign up and create accounts
- Login with credentials
- Compose and send emails to other users
- View inbox (received emails)
- View sent items (emails sent by user)
- Secure logout functionality

## Architecture

### Technologies Used
- **Java**: Core application logic with OOP principles
- **SQLite**: Lightweight database for data persistence
- **JDBC**: Database connectivity and operations

### Project Structure

```
EmailClientProject/
│
├── src/
│   ├── Main.java              # Entry point and user interface
│   ├── DatabaseHelper.java    # JDBC connections & database operations
│   ├── User.java              # User entity class
│   ├── Email.java             # Email entity class
│   ├── UserService.java       # User authentication and management
│   └── EmailService.java      # Email operations (send/receive)
│
├── database/
│   ├── schema.sql             # Database schema definition
│   └── sample_data.sql        # Sample data for testing
│
├── docs/
│   └── README.md              # This documentation
│
└── email_client.db            # SQLite database file (created at runtime)
```

## Database Schema

### User Table
```sql
CREATE TABLE User (
    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
    Name TEXT UNIQUE NOT NULL,
    Email TEXT NOT NULL,
    Password TEXT NOT NULL
);
```

### Email Table
```sql
CREATE TABLE Email (
    EmailID INTEGER PRIMARY KEY AUTOINCREMENT,
    Subject TEXT NOT NULL,
    Body TEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### EmailUser Junction Table
```sql
CREATE TABLE EmailUser (
    EmailID INTEGER,
    UserID INTEGER,
    Role TEXT NOT NULL CHECK (Role IN ('Sender', 'Receiver')),
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);
```

## Application Workflow

### 1. User Sign Up / Login
- **Java (OOP)**: User enters details → `User` object created
- **Database (DBMS)**:
   - Signup → Insert into `User` table
   - Login → Validate with `SELECT * FROM User WHERE Name=? AND Password=?`

### 2. Composing & Sending Email
- **Java (OOP)**: User types subject, body, receiver → `Email` object created
- **Database (DBMS)**:
   - Insert into `Email` table (subject, body, timestamp)
   - Insert into `EmailUser` table with sender and receiver relationships

### 3. Viewing Inbox
- **Java (OOP)**: User opens Inbox
- **Database (DBMS)**: Query `EmailUser` where `Role='Receiver'` and join with `Email` table

### 4. Viewing Sent Items
- **Java (OOP)**: User opens Sent folder
- **Database (DBMS)**: Query `EmailUser` where `Role='Sender'` and join with `Email` table

### 5. Logging Out
- **Java (OOP)**: End user session and clear current objects
- **Database (DBMS)**: No changes needed (data persisted)

## Setup and Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- SQLite JDBC driver

### Running the Application

1. **Compile the Java files:**
   ```bash
   javac -cp ".:sqlite-jdbc-3.x.x.jar" *.java
   ```

2. **Run the application:**
   ```bash
   java -cp ".:sqlite-jdbc-3.x.x.jar" Main
   ```

3. **Database Setup:**
   - The application automatically creates the SQLite database (`email_client.db`) on first run
   - Tables are created automatically using the schema defined in `DatabaseHelper.java`

### Loading Sample Data

To load sample data for testing:
```bash
sqlite3 email_client.db < database/sample_data.sql
```

## Features

### User Management
- **Secure Registration**: Username uniqueness validation
- **Input Validation**: Email format and password strength checks
- **Session Management**: Secure login/logout functionality

### Email Operations
- **Compose Email**: Rich email composition with subject and body
- **Send Email**: Deliver emails to registered users
- **Inbox View**: Display received emails with sender information
- **Sent Items**: View all emails sent by the current user
- **Timestamp Tracking**: Automatic timestamp recording

### Database Features
- **Transaction Safety**: ACID compliance for email operations
- **Foreign Key Constraints**: Data integrity enforcement
- **Indexing**: Optimized query performance
- **Junction Table Design**: Flexible sender-receiver relationships

## OOP Concepts Demonstrated

1. **Encapsulation**: Private fields with public getter/setter methods
2. **Abstraction**: Service layer abstracts complex database operations
3. **Single Responsibility**: Each class has a specific purpose
4. **Data Modeling**: Real-world entities represented as Java objects

## DBMS Concepts Demonstrated

1. **Relational Design**: Normalized database schema
2. **CRUD Operations**: Complete Create, Read, Update, Delete functionality
3. **Joins**: Complex queries joining multiple tables
4. **Transactions**: Atomic operations for data consistency
5. **Constraints**: Primary keys, foreign keys, and check constraints

## Usage Examples

### Sample User Accounts (from sample_data.sql)
- Username: `john_doe`, Password: `password123`
- Username: `alice_smith`, Password: `alice2024`
- Username: `bob_wilson`, Password: `bobsecure`

### Testing Workflow
1. Login with existing account or create new account
2. Send an email to another user
3. Check inbox for received emails
4. View sent items to confirm email delivery
5. Logout and login as different user to test full workflow

## Error Handling

The application includes comprehensive error handling:
- Database connection failures
- Invalid user input
- Non-existent recipients
- Transaction rollbacks on failures
- Graceful error messages for users

## Future Enhancements

Potential improvements for the application:
- Email deletion functionality
- Email search and filtering
- Attachment support
- Email forwarding and reply features
- Password encryption
- Email categories/folders
- Bulk email operations

## Contributing

To contribute to this project:
1. Follow Java coding conventions
2. Maintain database normalization
3. Add appropriate error handling
4. Update documentation for new features
5. Test thoroughly before submitting changes

## License

This project is for educational purposes, demonstrating OOP and DBMS concepts in a practical application.