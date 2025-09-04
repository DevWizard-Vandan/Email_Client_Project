-- Email Client Database Schema
-- MySQL Database

-- Create Database
CREATE DATABASE IF NOT EXISTS email_client;
USE email_client;

-- User Table
-- Stores user account information
CREATE TABLE IF NOT EXISTS User (
                                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                                    Name VARCHAR(50) UNIQUE NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL
    );

-- Email Table
-- Stores email content and metadata
CREATE TABLE IF NOT EXISTS Email (
                                     EmailID INT AUTO_INCREMENT PRIMARY KEY,
                                     Subject VARCHAR(255) NOT NULL,
    Body TEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- EmailUser Junction Table
-- Links emails to users with roles (Sender/Receiver)
-- This table implements the many-to-many relationship between emails and users
CREATE TABLE IF NOT EXISTS EmailUser (
                                         EmailID INT,
                                         UserID INT,
                                         Role ENUM('Sender', 'Receiver') NOT NULL,
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
    );

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_emailuser_userid_role ON EmailUser(UserID, Role);
CREATE INDEX IF NOT EXISTS idx_email_timestamp ON Email(Timestamp);
CREATE INDEX IF NOT EXISTS idx_user_name ON User(Name);

-- Attachments Table
-- Stores file attachments linked to emails
CREATE TABLE IF NOT EXISTS Attachments (
    AttachmentID INT AUTO_INCREMENT PRIMARY KEY,
    EmailID INT NOT NULL,
    FilePath VARCHAR(255) NOT NULL,
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE
);

-- Folder Table
-- Stores folders (Inbox, Sent, Drafts, etc.) for each user
CREATE TABLE IF NOT EXISTS Folder (
    FolderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- WebsiteSignUp Table
-- Stores the websites/domains where users signed up
CREATE TABLE IF NOT EXISTS WebsiteSignUp (
    SignUpID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    DomainName VARCHAR(100) NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);
