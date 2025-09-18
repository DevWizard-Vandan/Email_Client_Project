-- Sample Data for Email Client Database
-- Safe to re-run due to INSERT IGNORE
-- This file inserts sample users, emails, relationships, attachments, folders, and signups.

USE email_client;

-- Insert Sample Users (Consider hashing passwords in production)
INSERT IGNORE INTO User (Name, Email, Password) VALUES
('Vandan_Sharma', 'vandan@gmail.com', 'vandan2006'),
('Shashwat_Upadhyay', 'shashwat@gmail.com', 'shashwat2025'),
('Aksh_Upase', 'aksh@gmail.com', 'aksh25'),
('Prathamesh_Upase', 'prathamesh@gmail.com', 'prathamesh06'),
('Om_Tundurwar', 'om@gmail.com', 'om123');

-- Insert Sample Emails
INSERT IGNORE INTO Email (EmailID, Subject, Body, Timestamp) VALUES
(1, 'Welcome to the Team!', 'Hi there! Welcome to our amazing team. We are excited to have you on board.', '2024-01-15 09:30:00'),
(2, 'Project Update', 'The project is progressing well. We should be ready for the next phase by Friday.', '2024-01-16 14:20:00'),
(3, 'Meeting Reminder', 'Don\'t forget about our team meeting tomorrow at 2 PM in the conference room.', '2024-01-17 10:45:00'),
(4, 'Lunch Plans', 'Would you like to grab lunch together today? I know a great new restaurant nearby.', '2024-01-18 11:15:00'),
(5, 'Report Submission', 'Please find the attached monthly report. Let me know if you have any questions.', '2024-01-19 16:30:00'),
(6, 'Thank You!', 'Thank you for your help with the project. Your contribution was invaluable.', '2024-01-20 08:00:00'),
(7, 'Weekend Plans', 'Any plans for the weekend? Would love to catch up if you\'re free.', '2024-01-21 17:45:00');

-- Insert Sample EmailUser relationships
-- Updated comments & ensured no duplicate receivers
-- Email 1: Vandan -> Shashwat
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(1, 1, 'Sender'),
(1, 2, 'Receiver');

-- Email 2: Shashwat -> Aksh
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(2, 2, 'Sender'),
(2, 3, 'Receiver');

-- Email 3: Prathamesh -> Vandan
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(3, 4, 'Sender'),
(3, 1, 'Receiver');

-- Email 4: Aksh -> Vandan
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(4, 3, 'Sender'),
(4, 1, 'Receiver');

-- Email 5: Om -> Prathamesh
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(5, 5, 'Sender'),
(5, 4, 'Receiver');

-- Email 6: Vandan -> Shashwat
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(6, 1, 'Sender'),
(6, 2, 'Receiver');

-- Email 7: Shashwat -> Aksh
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(7, 2, 'Sender'),
(7, 3, 'Receiver');

-- Insert Sample Attachments
INSERT IGNORE INTO Attachments (EmailID, FilePath) VALUES
(5, '/attachments/report.pdf'),
(6, '/attachments/thankyou.png');

-- Insert Sample Folders
INSERT IGNORE INTO Folder (Name, UserID) VALUES
('Inbox', 1),
('Sent', 1),
('Drafts', 2),
('Inbox', 2),
('Inbox', 3),
('Inbox', 4),
('Inbox', 5);

-- Insert Sample Website SignUps
INSERT IGNORE INTO WebsiteSignUp (Name, DomainName, UserID) VALUES
('Gmail Signup', 'gmail.com', 1),
('Gmail Signup', 'gmail.com', 2),
('Gmail Signup', 'gmail.com', 3),
('Yahoo Signup', 'yahoo.com', 4),
('Outlook Signup', 'outlook.com', 5);
