-- Email Client Database Schema
-- SQLite Database

-- User Table
-- Stores user account information
CREATE TABLE IF NOT EXISTS User (
                                    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                                    Name TEXT UNIQUE NOT NULL,
                                    Email TEXT NOT NULL,
                                    Password TEXT NOT NULL
);

-- Email Table
-- Stores email content and metadata
CREATE TABLE IF NOT EXISTS Email (
                                     EmailID INTEGER PRIMARY KEY AUTOINCREMENT,
                                     Subject TEXT NOT NULL,
                                     Body TEXT NOT NULL,
                                     Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- EmailUser Junction Table
-- Links emails to users with roles (Sender/Receiver)
-- This table implements the many-to-many relationship between emails and users
CREATE TABLE IF NOT EXISTS EmailUser (
                                         EmailID INTEGER,
                                         UserID INTEGER,
                                         Role TEXT NOT NULL CHECK (Role IN ('Sender', 'Receiver')),
    PRIMARY KEY (EmailID, UserID, Role),
    FOREIGN KEY (EmailID) REFERENCES Email(EmailID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
    );

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_emailuser_userid_role ON EmailUser(UserID, Role);
CREATE INDEX IF NOT EXISTS idx_email_timestamp ON Email(Timestamp);
CREATE INDEX IF NOT EXISTS idx_user_name ON User(Name);