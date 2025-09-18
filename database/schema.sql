-- Email Client Database Schema
-- MySQL Database

-- Create Database
CREATE DATABASE IF NOT EXISTS email_client;
USE email_client;

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
