# Email Client Project

A console-based email client application built with Java and MySQL database, demonstrating Object-Oriented Programming (OOP) concepts and Database Management System (DBMS) operations.

## Project Overview

This email client allows users to:
- Sign up and create accounts
- Login with credentials
- Compose and send emails to other users
- View inbox (received emails)
- View sent items (emails sent by user)
- Secure logout functionality

## Team Members and Contributions

### **Project Leader & Lead Developer: Vandan Sharma** 🏆
**Primary Responsibilities:**
- **System Architecture Design**: Designed complete project architecture and workflow specifications
- **Core Application Logic**: Developed `Main.java` with complete user interface and menu system
- **Database Integration**: Implemented `DatabaseHelper.java` with MySQL connectivity and transaction management
- **Project Management**: Coordinated team activities, code integration, and ensured project completion
- **Testing & Quality Assurance**: Conducted comprehensive testing and debugging of all components
- **Technical Leadership**: Made critical technical decisions and resolved complex integration issues

### **Database Architect: Shashwat Upadhyay**
**Responsibilities:**
- **Database Schema Design**: Created complete `schema.sql` with normalized tables and relationships
- **Sample Data Creation**: Developed comprehensive `sample_data.sql` with realistic test scenarios
- **MySQL Optimization**: Implemented indexes, constraints, and performance optimizations
- **Data Integrity**: Designed foreign key relationships and cascade operations
- **Database Documentation**: Created ER diagrams and database documentation

### **Backend Services Developer: Aksh Upase**
**Responsibilities:**
- **Email Service Implementation**: Developed `EmailService.java` with inbox/sent items functionality
- **Complex SQL Queries**: Implemented multi-table joins and advanced database operations
- **Business Logic**: Created email sending, receiving, and display mechanisms
- **Performance Optimization**: Optimized database queries for better performance

### **Authentication & Security Developer: Prathamesh Upase**
**Responsibilities:**
- **User Authentication System**: Implemented complete `UserService.java` with login/signup logic
- **Entity Development**: Created `User.java` and `Email.java` classes with proper OOP design
- **Input Validation**: Developed comprehensive validation for all user inputs
- **Security Features**: Implemented password validation and user session management
- **Error Handling**: Created user-friendly error messages and exception handling

### **Documentation & Deployment Specialist: Om Tundurwar**
**Responsibilities:**
- **Technical Documentation**: Created detailed README and setup guides
- **IntelliJ Setup Guide**: Developed comprehensive IDE configuration instructions
- **Installation Documentation**: Documented MySQL setup and JAR file configuration
- **Troubleshooting Guide**: Compiled common issues, solutions, and best practices
- **Code Documentation**: Added inline comments and JavaDoc documentation throughout the codebase

## Architecture

### Technologies Used
- **Java**: Core application logic with OOP principles
- **MySQL**: Relational database for data persistence
- **JDBC**: Database connectivity and operations
- **IntelliJ IDEA**: Development environment

### Project Structure

```
EmailClientProject/
│
├── lib/
│   └── mysql-connector-j-8.x.x.jar    # MySQL JDBC driver
│
├── src/
│   ├── Main.java              # Entry point and UI 
│   ├── DatabaseHelper.java    # JDBC & DB operations 
│   ├── User.java              # User entity 
│   ├── Email.java             # Email entity 
│   ├── UserService.java       # Authentication
│   └── EmailService.java      # Email operations
│
├── database/
│   ├── schema.sql             # MySQL schema 
│   └── sample_data.sql        # Sample data 
│
├── docs/
│   └── README.md              # Documentation 
│
└── IntelliJ_Setup_Guide.md    # Setup guide
```

## Database Schema

### User Table
```sql
CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) UNIQUE NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL
);
```

### Email Table
```sql
CREATE TABLE Email (
    EmailID INT AUTO_INCREMENT PRIMARY KEY,
    Subject VARCHAR(255) NOT NULL,
    Body TEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### EmailUser Junction Table
```sql
CREATE TABLE EmailUser (
    EmailID INT,
    UserID INT,
    Role ENUM('Sender', 'Receiver') NOT NULL,
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
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
- MySQL Server 8.0 or higher
- IntelliJ IDEA (recommended IDE)
- MySQL Connector/J JAR file

### IntelliJ IDEA Setup

1. **Create New Project:**
    - Open IntelliJ IDEA → New Project → Java
    - Name: `EmailClientProject`

2. **Add MySQL Connector:**
    - Create `lib/` folder in project root
    - Copy `mysql-connector-j-8.x.x.jar` to `lib/` folder
    - Right-click JAR → Add as Library

3. **Configure Database Connection:**
    - Update `DatabaseHelper.java` with your MySQL credentials:
   ```java
   private static final String DB_USER = "your_username";
   private static final String DB_PASSWORD = "your_password";
   ```

4. **Run the Application:**
    - Right-click `Main.java` → Run 'Main.main()'

### MySQL Database Setup

1. **Start MySQL Server**
2. **Create Database (Optional):**
   ```sql
   CREATE DATABASE email_client;
   ```
3. **The application will automatically create tables on first run**

### Loading Sample Data

To load sample data for testing:
```bash
mysql -u root -p email_client < database/sample_data.sql
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
- Username: `Vandan_Sharma`, Password: `vandan2006`
- Username: `Shashwat_Upadhyay`, Password: `shashwat2025`
- Username: `Aksh_Upase`, Password: `aksh123`

### Testing Workflow
1. Login with existing account or create new account
2. Send an email to another user
3. Check inbox for received emails
4. View sent items to confirm email delivery
5. Logout and login as different user to test full workflow

## Running the Application

### In IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Ensure MySQL connector JAR is added to libraries
3. Update database credentials in `DatabaseHelper.java`
4. Run `Main.java`

### Command Line (Alternative)
```bash
# Compile
javac -cp "lib/*" src/*.java -d out/

# Run
java -cp "lib/*:out" Main
```

## Error Handling

The application includes comprehensive error handling:
- Database connection failures with detailed messages
- Invalid user input validation
- Non-existent recipients detection
- Transaction rollbacks on failures
- MySQL-specific error codes handling
- Graceful error messages for users

## MySQL-Specific Features

- **AUTO_INCREMENT**: Primary key generation
- **ENUM Data Type**: Role constraint for EmailUser table
- **CASCADE DELETE**: Automatic cleanup of related records
- **UNIQUE Constraints**: Username uniqueness enforcement
- **TEXT Data Type**: Support for large email content
- **DATETIME**: Automatic timestamp generation

## Future Enhancements

Potential improvements for the application:
- Email deletion functionality
- Email search and filtering
- Attachment support
- Email forwarding and reply features
- Password encryption (bcrypt/hash)
- Email categories/folders
- Bulk email operations
- Rich text email support

## Performance Considerations

- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: Efficient database connection management
- **Transaction Management**: Atomic operations for data consistency
- **Memory Management**: Proper resource cleanup and garbage collection

## Security Features

- **SQL Injection Prevention**: Prepared statements for all queries
- **Input Validation**: Comprehensive validation for all user inputs
- **Session Management**: Secure user session handling
- **Error Message Security**: Non-revealing error messages

## Contributing

To contribute to this project:
1. Follow Java coding conventions
2. Maintain database normalization
3. Add appropriate error handling
4. Update documentation for new features
5. Test thoroughly before submitting changes

## Project Statistics

- **Total Lines of Code**: ~800 lines
- **Java Classes**: 6 classes
- **Database Tables**: 3 tables
- **Features Implemented**: 10+ core features
- **Development Time**: 4 weeks
- **Team Size**: 5 members

### Individual Contribution Breakdown:
- **[Your Name] (Project Leader)**:- Architecture, Core Logic, Integration
- **Team Member 2 (Database Architect)**:- Database Design & Optimization
- **Team Member 3 (Backend Developer)**:- Email Services & Complex Queries
- **Team Member 4 (Auth & Security Developer)**:- Authentication & Entity Classes
- **Team Member 5 (Documentation Specialist)**:- Documentation & Deployment

### Key Responsibilities by Team Member:

**🏆 Project Leader - Most Critical Components:**
- System architecture and design decisions
- Core application development and user interface
- Database connectivity and transaction management
- Team coordination and project integration
- Quality assurance and final testing

**Database Architect - Foundation Components:**
- Database schema design and normalization
- Performance optimization and indexing
- Data integrity and relationship design

**Backend Developer - Service Layer:**
- Email functionality and complex operations
- Multi-table database queries and joins
- Business logic implementation

**Authentication & Security Developer - Security Layer:**
- User management and authentication system
- Entity class development with OOP principles
- Input validation and security features

**Documentation Specialist - Support Components:**
- Comprehensive project documentation
- Setup guides and troubleshooting support
- Code documentation and comments

## License

This project is for educational purposes, demonstrating OOP and DBMS concepts in a practical application.

---

**Project Lead**: Vandan Sharma - Responsible for system architecture, core development, and project coordination.

*This project demonstrates advanced Java programming, MySQL database management, and collaborative software development practices.*