-- Sample Data for Email Client Database
-- This file contains sample users and emails for testing
-- MySQL Database

USE email_client;

-- Insert Sample Users
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
(5, 'Report Submission', 'Please find the attached monthly report. Let me know if you have any questions.', '2024-01-19 16:30:00');

-- Insert Sample EmailUser relationships
-- Email 1: alice_smith -> john_doe (Welcome message)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(1, 2, 'Sender'),    -- alice_smith is sender
(1, 1, 'Receiver');  -- john_doe is receiver

-- Email 2: john_doe -> bob_wilson (Project update)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(2, 1, 'Sender'),    -- john_doe is sender
(2, 3, 'Receiver');  -- bob_wilson is receiver

-- Email 3: sarah_jones -> alice_smith (Meeting reminder)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(3, 4, 'Sender'),    -- sarah_jones is sender
(3, 2, 'Receiver');  -- alice_smith is receiver

-- Email 4: bob_wilson -> john_doe (Lunch plans)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(4, 3, 'Sender'),    -- bob_wilson is sender
(4, 1, 'Receiver');  -- john_doe is receiver

-- Email 5: mike_brown -> sarah_jones (Report submission)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(5, 5, 'Sender'),    -- mike_brown is sender
(5, 4, 'Receiver');  -- sarah_jones is receiver

-- Additional emails for better testing
INSERT IGNORE INTO Email (EmailID, Subject, Body, Timestamp) VALUES
(6, 'Thank You!', 'Thank you for your help with the project. Your contribution was invaluable.', '2024-01-20 08:00:00'),
(7, 'Weekend Plans', 'Any plans for the weekend? Would love to catch up if you\'re free.', '2024-01-21 17:45:00');

-- Email 6: john_doe -> alice_smith (Thank you)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(6, 1, 'Sender'),    -- john_doe is sender
(6, 2, 'Receiver');  -- alice_smith is receiver

-- Email 7: alice_smith -> bob_wilson (Weekend plans)
INSERT IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(7, 2, 'Sender'),    -- alice_smith is sender
(7, 3, 'Receiver');  -- bob_wilson is receiver

-- Insert Sample Attachments
INSERT IGNORE INTO Attachments (EmailID, FilePath) VALUES
(5, '/attachments/report.pdf'),  -- for "Report Submission"
(6, '/attachments/thankyou.png'); -- for "Thank You!"

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
