📧 Email Client Software (Java + DBMS Integration)
📌 Project Overview

This project is developed as part of two integrated semester courses:

Object-Oriented Programming (Java)

Database Management Systems (DBMS)

It simulates a simplified Email Client Application where users can sign up, log in, compose emails, send/receive emails, organize emails into folders, and manage attachments.

The project demonstrates OOP principles (encapsulation, inheritance, polymorphism) while using a relational database (SQL) for persistent storage.

🎯 Features

User Management

Sign up and log in securely.

Manage personal details.

Email Management

Compose, send, receive, and read emails.

Attach files with emails.

Inbox, Sent, and Custom Folders.

Folder System

Create and manage folders.

Move emails into different folders.

Database Integration (via JDBC)

All operations are persisted in a relational database.

Relational mapping between Users, Emails, Attachments, and Folders.

🏗️ Project Structure
EmailClientProject/
│
├── src/
│   ├── main/java/com/emailclient/
│   │   ├── Main.java
│   │   ├── AppConfig.java
│   │   ├── models/        # Java classes (User, Email, Attachment, Folder, WebsiteSignUp)
│   │   ├── dao/           # Data Access Objects (UserDAO, EmailDAO, etc.)
│   │   ├── services/      # Business logic (AuthService, EmailService, etc.)
│   │   ├── ui/            # Console/GUI (LoginUI, InboxUI, ComposeUI)
│   │   └── utils/         # DBConnection and helpers
│   │
│   └── resources/
│       └── db-config.properties
│
├── database/
│   ├── schema.sql         # Database schema
│   ├── sample_data.sql    # Test data
│   └── queries.sql        # Common queries
│
├── docs/
│   ├── ER_Diagram.jpg
│   ├── Workflow.pdf
│   ├── OOP_Syllabus.pdf
│   ├── DBMS_Syllabus.pdf
│   └── ProjectReport.docx
│
├── lib/                   # External libraries (if not using Maven/Gradle)
│   └── mysql-connector-java.jar
│
├── README.md              # This file

📂 Database Schema

The relational schema is based on the ER diagram.

Tables:

User(UserID, Name, Password, PersonalDetails, CreatedAt)

Folder(FolderID, Name, UserID)

Email(EmailID, Subject, Body, Timestamp, FolderID)

Attachment(ID, FilePath, EmailID)

WebsiteSignUp(SignUpID, Name, DomainName, UserID)

EmailUser(EmailID, UserID, Role)

👉 Full schema available in database/schema.sql
.

⚙️ Technologies Used

Programming Language: Java (OOP Concepts)

Database: MySQL / PostgreSQL (Relational DBMS)

Connectivity: JDBC (Java Database Connectivity)


Version Control: Git

🚀 Setup & Installation
1. Clone Repository
   git clone https://github.com/DevWizard-Vandan/Email_Client_Project.git
   cd Email_Client_Project

2. Database Setup

Create a new database in MySQL/Postgres:

CREATE DATABASE EmailClientDB;


Import schema:

mysql -u root -p EmailClientDB < database/schema.sql


(Optional) Insert sample data:

mysql -u root -p EmailClientDB < database/sample_data.sql

3. Configure Database Connection

Edit src/main/resources/db-config.properties:

db.url=jdbc:mysql://localhost:3306/EmailClientDB
db.username=root
db.password=your_password

4. Build & Run

If using Gradle:

./gradlew build
./gradlew run


If using Maven:

mvn clean install
mvn exec:java -Dexec.mainClass="com.emailclient.Main"

📊 Workflow

User signs up / logs in → Stored in User + WebsiteSignUp.

Compose email → Insert into Email, EmailUser, Attachment.

Inbox view → Query EmailUser where Role = "Receiver".

Sent items → Query EmailUser where Role = "Sender".

Move to folder → Update Email.FolderID.

👉 Detailed workflow diagram: docs/Workflow.pdf
.

🧪 Testing

Unit tests are included under src/test/java/com/emailclient/.

Example:

@Test
void testSendEmail() {
Email email = new Email("Hello", "Test body", LocalDateTime.now());
assertTrue(EmailService.sendEmail(user, email));
}

📖 Documentation

ER Diagram → docs/ER_Diagram.jpg

DBMS & OOP Syllabus → docs/OOP_Syllabus.pdf
, docs/DBMS_Syllabus.pdf

Workflow → docs/Workflow.pdf

Final Report → docs/ProjectReport.docx

👨‍💻 Authors

Vandan Sharma
B.Tech CSE (AI & ML), Semester Project – 2025

Shashwat Upadhyay
B.Tech CSE (AI & ML), Semester Project – 2025

Aksh Upase
B.Tech CSE (AI & ML), Semester Project – 2025

Prathamesh Upase
B.Tech CSE (AI & ML), Semester Project – 2025

Om Tundurwar
B.Tech CSE (AI & ML), Semester Project – 2025

📜 License

This project is created for academic purposes.
You may use, modify, and extend it for learning.