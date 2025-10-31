
CREATE DATABASE IF NOT EXISTS email_client;
USE email_client;

-- ========== USER TABLE ==========
CREATE TABLE IF NOT EXISTS User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    PersonalDetails TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastLogin DATETIME,
    IsActive BOOLEAN DEFAULT TRUE,
    INDEX idx_name (Name),
    INDEX idx_active (IsActive)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== EMAIL TABLE ==========
CREATE TABLE IF NOT EXISTS Email (
    EmailID INT AUTO_INCREMENT PRIMARY KEY,
    Subject VARCHAR(255) NOT NULL,
    Body LONGTEXT NOT NULL,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    Priority ENUM('Low', 'Normal', 'High') DEFAULT 'Normal',
    IsHTML BOOLEAN DEFAULT FALSE,
    INDEX idx_timestamp (Timestamp),
    INDEX idx_priority (Priority),
    FULLTEXT idx_search (Subject, Body)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== FOLDER TABLE ==========
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
    INDEX idx_user_folder (UserID, Name),
    INDEX idx_parent (ParentFolderID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== EMAILUSER JUNCTION TABLE ==========
CREATE TABLE IF NOT EXISTS EmailUser (
    EmailID INT,
    UserID INT,
    Role ENUM('Sender', 'Receiver') NOT NULL,
    FolderID INT NULL,
    IsRead BOOLEAN DEFAULT FALSE,
    IsStarred BOOLEAN DEFAULT FALSE,
    IsDeleted BOOLEAN DEFAULT FALSE,
    ReadAt DATETIME NULL,
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (FolderID) REFERENCES Folder(FolderID) ON DELETE SET NULL,
    INDEX idx_user_role (UserID, Role),
    INDEX idx_folder (FolderID),
    INDEX idx_read_status (IsRead),
    INDEX idx_starred (IsStarred),
    INDEX idx_deleted (IsDeleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== ATTACHMENT TABLE ==========
CREATE TABLE IF NOT EXISTS Attachment (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    EmailID INT NOT NULL,
    FileName VARCHAR(255) NOT NULL,
    FileSize BIGINT NOT NULL,
    MimeType VARCHAR(100) NOT NULL,
    FilePath VARCHAR(500) NOT NULL,
    UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    INDEX idx_email (EmailID),
    INDEX idx_uploaded (UploadedAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========== WEBSITE SIGNUP TABLE ==========
CREATE TABLE IF NOT EXISTS WebsiteSignUp (
    SignUpID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Name VARCHAR(100) NOT NULL,
    DomainName VARCHAR(100) NOT NULL,
    SignUpDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    INDEX idx_user (UserID),
    INDEX idx_domain (DomainName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
