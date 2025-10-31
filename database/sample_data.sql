USE email_client;

-- ========== INSERT SAMPLE USERS ==========
INSERT INTO User (Name, Password, PersonalDetails) VALUES
('john_doe', 'pass123', 'Software Engineer, johndoe@email.com, +1-555-0101'),
('alice_smith', 'pass456', 'Project Manager, alice.smith@email.com, +1-555-0102'),
('bob_wilson', 'pass789', 'Designer, bob.wilson@email.com, +1-555-0103'),
('sarah_jones', 'pass321', 'Marketing Specialist, sarah.j@email.com, +1-555-0104'),
('mike_brown', 'pass654', 'Data Analyst, mike.brown@email.com, +1-555-0105');

-- Note: Default folders are automatically created by trigger

-- ========== INSERT SAMPLE CUSTOM FOLDERS ==========
INSERT INTO Folder (UserID, Name, IsSystem, Color) VALUES
(1, 'Work', FALSE, '#2980b9'),
(1, 'Personal', FALSE, '#16a085'),
(2, 'Projects', FALSE, '#8e44ad'),
(3, 'Clients', FALSE, '#c0392b');

-- ========== INSERT SAMPLE EMAILS ==========

-- Email 1: Alice to John (High Priority, Unread)
INSERT INTO Email (Subject, Body, Priority, Timestamp) VALUES
('Q4 Project Kickoff Meeting', 
'Hi John,\n\nI hope this email finds you well. I wanted to schedule a kickoff meeting for our Q4 project. The meeting will cover project scope, timeline, and resource allocation.\n\nProposed time: Next Monday, 10 AM\nLocation: Conference Room A\n\nPlease confirm your availability.\n\nBest regards,\nAlice',
'High',
DATE_SUB(NOW(), INTERVAL 2 DAY));

SET @email1_id = LAST_INSERT_ID();
INSERT INTO EmailUser (EmailID, UserID, Role, FolderID, IsRead) VALUES
(@email1_id, 2, 'Sender', (SELECT FolderID FROM Folder WHERE UserID=2 AND Name='Sent'), TRUE),
(@email1_id, 1,# 🚀 Professional Email Client - Complete Project Files

This document contains all remaining project files for immediate copy-paste implementation.
