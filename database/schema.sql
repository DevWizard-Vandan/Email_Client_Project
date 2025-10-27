-- Enhanced Email Client Database Schema
-- MySQL Database - Professional Implementation

-- Create Database
CREATE DATABASE IF NOT EXISTS email_client;
USE email_client;

<<<<<<< HEAD
-- User Table - Core user information
CREATE TABLE IF NOT EXISTS User (
                                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                                    Name VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    PersonalDetails TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastLogin DATETIME,
    IsActive BOOLEAN DEFAULT TRUE,
    ProfilePicture VARCHAR(255),

    INDEX idx_user_name (Name),
    INDEX idx_user_active (IsActive)
    );

-- WebsiteSignUp Table - Track signup details
CREATE TABLE IF NOT EXISTS WebsiteSignUp (
                                             SignUpID INT AUTO_INCREMENT PRIMARY KEY,
                                             UserID INT NOT NULL,
                                             Name VARCHAR(100) NOT NULL,
    DomainName VARCHAR(100) NOT NULL,
    SignUpDate DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    INDEX idx_signup_user (UserID),
    INDEX idx_signup_date (SignUpDate)
    );

-- Folder Table - Email organization
CREATE TABLE IF NOT EXISTS Folder (
                                      FolderID INT AUTO_INCREMENT PRIMARY KEY,
                                      UserID INT NOT NULL,
                                      Name VARCHAR(100) NOT NULL,
    ParentFolderID INT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Color VARCHAR(7) DEFAULT '#3498db',
    IsSystem BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ParentFolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL,
    INDEX idx_folder_user (UserID),
    INDEX idx_folder_parent (ParentFolderID)
    );

-- Email Table - Enhanced with more fields
CREATE TABLE IF NOT EXISTS Email (
                                     EmailID INT AUTO_INCREMENT PRIMARY KEY,
                                     Subject VARCHAR(255) NOT NULL,
    Body LONGTEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    Priority ENUM('Low', 'Normal', 'High') DEFAULT 'Normal',
    IsHTML BOOLEAN DEFAULT FALSE,
    MessageID VARCHAR(255) UNIQUE,
    InReplyToID INT NULL,
    Size INT DEFAULT 0,

    FOREIGN KEY (InReplyToID) REFERENCES Email(EmailID) ON DELETE SET NULL,
    INDEX idx_email_timestamp (Timestamp),
    INDEX idx_email_priority (Priority),
    INDEX idx_email_reply (InReplyToID)
    );

-- EmailUser Junction Table - Enhanced with folder support
CREATE TABLE IF NOT EXISTS EmailUser (
                                         EmailID INT,
                                         UserID INT,
                                         Role ENUM('Sender', 'Receiver', 'CC', 'BCC') NOT NULL,
    FolderID INT NULL,
    IsRead BOOLEAN DEFAULT FALSE,
    IsStarred BOOLEAN DEFAULT FALSE,
    IsDeleted BOOLEAN DEFAULT FALSE,
    ReadAt DATETIME NULL,

    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (FolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL,
    INDEX idx_emailuser_folder (FolderID),
    INDEX idx_emailuser_read (IsRead),
    INDEX idx_emailuser_starred (IsStarred),
    INDEX idx_emailuser_deleted (IsDeleted)
    );

-- Attachment Table - File attachment support
CREATE TABLE IF NOT EXISTS Attachment (
                                          ID INT AUTO_INCREMENT PRIMARY KEY,
                                          EmailID INT NOT NULL,
                                          FileName VARCHAR(255) NOT NULL,
    FileSize BIGINT NOT NULL,
    MimeType VARCHAR(100) NOT NULL,
    FilePath VARCHAR(500) NOT NULL,
    UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    INDEX idx_attachment_email (EmailID),
    INDEX idx_attachment_size (FileSize)
    );

-- Email Labels/Tags Table - For advanced organization
CREATE TABLE IF NOT EXISTS EmailLabel (
                                          LabelID INT AUTO_INCREMENT PRIMARY KEY,
                                          UserID INT NOT NULL,
                                          Name VARCHAR(50) NOT NULL,
    Color VARCHAR(7) DEFAULT '#95a5a6',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    UNIQUE KEY unique_user_label (UserID, Name)
    );

-- Junction table for email-label relationships
CREATE TABLE IF NOT EXISTS EmailLabelMapping (
                                                 EmailID INT,
                                                 LabelID INT,

                                                 PRIMARY KEY (EmailID, LabelID),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (LabelID) REFERENCES EmailLabel(LabelID) ON DELETE CASCADE
    );

-- Insert Default System Folders for each user (trigger)
DELIMITER //
CREATE TRIGGER CreateDefaultFolders
    AFTER INSERT ON User
    FOR EACH ROW
BEGIN
    INSERT INTO Folder (UserID, Name, IsSystem) VALUES
                                                    (NEW.UserID, 'Inbox', TRUE),
                                                    (NEW.UserID, 'Sent', TRUE),
                                                    (NEW.UserID, 'Drafts', TRUE),
                                                    (NEW.UserID, 'Trash', TRUE),
                                                    (NEW.UserID, 'Spam', TRUE);
END//
DELIMITER ;

-- Views for easier querying
CREATE OR REPLACE VIEW EmailWithDetails AS
SELECT
    e.EmailID,
    e.Subject,
    e.Body,
    e.Timestamp,
    e.Priority,
    e.IsHTML,
    sender.Name AS SenderName,
    receiver.Name AS ReceiverName,
    eu_receiver.IsRead,
    eu_receiver.IsStarred,
    eu_receiver.FolderID,
    f.Name AS FolderName,
    GROUP_CONCAT(a.FileName) AS Attachments,
    COUNT(a.ID) AS AttachmentCount
FROM Email e
         LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender'
         LEFT JOIN User sender ON eu_sender.UserID = sender.UserID
         LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver'
         LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID
         LEFT JOIN Folder f ON eu_receiver.FolderID = f.FolderID
         LEFT JOIN Attachment a ON e.EmailID = a.EmailID
GROUP BY e.EmailID, eu_receiver.UserID;

-- Performance optimization indexes
CREATE INDEX idx_email_composite ON Email(Timestamp, Priority);
CREATE INDEX idx_emailuser_composite ON EmailUser(UserID, Role, IsDeleted, IsRead);
CREATE INDEX idx_folder_system ON Folder(UserID, IsSystem);

-- Full-text search index for email content
ALTER TABLE Email ADD FULLTEXT(Subject, Body);
=======
-- ==============================
-- User Table
-- ==============================
CREATE TABLE IF NOT EXISTS User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) UNIQUE NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(100) NOT NULL,
    Status ENUM('Active', 'Suspended', 'Deleted') DEFAULT 'Active',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ==============================
-- Email Table
-- ==============================
CREATE TABLE IF NOT EXISTS Email (
    EmailID INT AUTO_INCREMENT PRIMARY KEY,
    Subject VARCHAR(255) NOT NULL,
    Body TEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Sent', 'Draft', 'Deleted', 'Archived') DEFAULT 'Sent',
    ParentEmailID INT NULL,
    FOREIGN KEY (ParentEmailID) REFERENCES Email(EmailID) ON DELETE SET NULL
);

-- ==============================
-- EmailUser Junction Table
-- Many-to-many relationship between users and emails
-- ==============================
CREATE TABLE IF NOT EXISTS EmailUser (
    EmailID INT,
    UserID INT,
    Role ENUM('Sender', 'Receiver') NOT NULL,
    ReadStatus ENUM('Unread', 'Read') DEFAULT 'Unread',
    ReadAt DATETIME NULL,
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- ==============================
-- Attachments Table
-- ==============================
CREATE TABLE IF NOT EXISTS Attachments (
    AttachmentID INT AUTO_INCREMENT PRIMARY KEY,
    EmailID INT NOT NULL,
    FilePath VARCHAR(255) NOT NULL,
    FileSize INT NULL,
    MimeType VARCHAR(100) NULL,
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE
);

-- ==============================
-- Folder Table
-- ==============================
CREATE TABLE IF NOT EXISTS Folder (
    FolderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Type ENUM('Inbox', 'Sent', 'Drafts', 'Spam', 'Trash', 'Custom') DEFAULT 'Custom',
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- ==============================
-- WebsiteSignUp Table
-- Tracks websites/domains where users signed up
-- ==============================
CREATE TABLE IF NOT EXISTS WebsiteSignUp (
    SignUpID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    DomainName VARCHAR(100) NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- ==============================
-- Optional: Contact Table
-- (for managing address book)
-- ==============================
CREATE TABLE IF NOT EXISTS Contact (
    ContactID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    ContactName VARCHAR(100) NOT NULL,
    ContactEmail VARCHAR(100) NOT NULL,
    Notes TEXT NULL,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- ==============================
-- Indexes for Performance
-- ==============================
CREATE INDEX IF NOT EXISTS idx_emailuser_userid_role ON EmailUser(UserID, Role);
CREATE INDEX IF NOT EXISTS idx_emailuser_emailid_role ON EmailUser(EmailID, Role);
CREATE INDEX IF NOT EXISTS idx_email_timestamp ON Email(Timestamp);
CREATE INDEX IF NOT EXISTS idx_email_status ON Email(Status);
CREATE INDEX IF NOT EXISTS idx_user_name ON User(Name);
>>>>>>> de6888942811b13d0240c3867509755f279f6c18
