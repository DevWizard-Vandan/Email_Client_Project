-- Email Client Database Schema
-- Drop existing tables if they exist (in proper order to avoid foreign key constraints)
DROP TABLE IF EXISTS Attachment;
DROP TABLE IF EXISTS EmailUser;
DROP TABLE IF EXISTS Email;
DROP TABLE IF EXISTS Folder;
DROP TABLE IF EXISTS WebsiteSignUp;
DROP TABLE IF EXISTS User;

-- Create User table
CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Domain VARCHAR(50) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LastLogin TIMESTAMP NULL,
    IsActive BOOLEAN DEFAULT TRUE
);

-- Create WebsiteSignUp table (tracks registration details)
CREATE TABLE WebsiteSignUp (
    SignUpID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    SignUpDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IPAddress VARCHAR(45),
    UserAgent TEXT,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- Create Folder table
CREATE TABLE Folder (
    FolderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    FolderName VARCHAR(100) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsDefault BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    UNIQUE KEY unique_user_folder (UserID, FolderName)
);

-- Create Email table
CREATE TABLE Email (
    EmailID INT AUTO_INCREMENT PRIMARY KEY,
    Subject VARCHAR(255) NOT NULL,
    Body TEXT NOT NULL,
    SentAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Priority ENUM('LOW', 'NORMAL', 'HIGH') DEFAULT 'NORMAL',
    IsRead BOOLEAN DEFAULT FALSE,
    IsDeleted BOOLEAN DEFAULT FALSE,
    FolderID INT NULL,
    FOREIGN KEY (FolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL
);

-- Create EmailUser table (many-to-many relationship between Email and User)
CREATE TABLE EmailUser (
    EmailUserID INT AUTO_INCREMENT PRIMARY KEY,
    EmailID INT NOT NULL,
    UserID INT NOT NULL,
    Role ENUM('SENDER', 'RECEIVER', 'CC', 'BCC') NOT NULL,
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    UNIQUE KEY unique_email_user_role (EmailID, UserID, Role)
);

-- Create Attachment table
CREATE TABLE Attachment (
    AttachmentID INT AUTO_INCREMENT PRIMARY KEY,
    EmailID INT NOT NULL,
    FileName VARCHAR(255) NOT NULL,
    FilePath VARCHAR(500) NOT NULL,
    FileSize BIGINT NOT NULL,
    FileType VARCHAR(100),
    UploadedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_user_email ON User(Email);
CREATE INDEX idx_email_sent_at ON Email(SentAt);
CREATE INDEX idx_emailuser_receiver ON EmailUser(UserID, Role);
CREATE INDEX idx_email_folder ON Email(FolderID);
CREATE INDEX idx_attachment_email ON Attachment(EmailID);

-- Insert default folders for system
-- Note: These will be created programmatically for each user during sign-up