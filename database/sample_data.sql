-- Sample Data for Email Client Database
-- This file contains sample users and emails for testing

-- Insert Sample Users
INSERT OR IGNORE INTO User (Name, Email, Password) VALUES
('john_doe', 'john@example.com', 'password123'),
('alice_smith', 'alice@example.com', 'alice2024'),
('bob_wilson', 'bob@example.com', 'bobsecure'),
('sarah_jones', 'sarah@example.com', 'sarah456'),
('mike_brown', 'mike@example.com', 'mike789');

-- Insert Sample Emails
INSERT OR IGNORE INTO Email (EmailID, Subject, Body, Timestamp) VALUES
(1, 'Welcome to the Team!', 'Hi there! Welcome to our amazing team. We are excited to have you on board.', '2024-01-15 09:30:00'),
(2, 'Project Update', 'The project is progressing well. We should be ready for the next phase by Friday.', '2024-01-16 14:20:00'),
(3, 'Meeting Reminder', 'Don''t forget about our team meeting tomorrow at 2 PM in the conference room.', '2024-01-17 10:45:00'),
(4, 'Lunch Plans', 'Would you like to grab lunch together today? I know a great new restaurant nearby.', '2024-01-18 11:15:00'),
(5, 'Report Submission', 'Please find the attached monthly report. Let me know if you have any questions.', '2024-01-19 16:30:00');

-- Insert Sample EmailUser relationships
-- Email 1: alice_smith -> john_doe (Welcome message)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(1, 2, 'Sender'),    -- alice_smith is sender
(1, 1, 'Receiver');  -- john_doe is receiver

-- Email 2: john_doe -> bob_wilson (Project update)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(2, 1, 'Sender'),    -- john_doe is sender
(2, 3, 'Receiver');  -- bob_wilson is receiver

-- Email 3: sarah_jones -> alice_smith (Meeting reminder)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(3, 4, 'Sender'),    -- sarah_jones is sender
(3, 2, 'Receiver');  -- alice_smith is receiver

-- Email 4: bob_wilson -> john_doe (Lunch plans)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(4, 3, 'Sender'),    -- bob_wilson is sender
(4, 1, 'Receiver');  -- john_doe is receiver

-- Email 5: mike_brown -> sarah_jones (Report submission)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(5, 5, 'Sender'),    -- mike_brown is sender
(5, 4, 'Receiver');  -- sarah_jones is receiver

-- Additional emails for better testing
INSERT OR IGNORE INTO Email (EmailID, Subject, Body, Timestamp) VALUES
(6, 'Thank You!', 'Thank you for your help with the project. Your contribution was invaluable.', '2024-01-20 08:00:00'),
(7, 'Weekend Plans', 'Any plans for the weekend? Would love to catch up if you''re free.', '2024-01-21 17:45:00');

-- Email 6: john_doe -> alice_smith (Thank you)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(6, 1, 'Sender'),    -- john_doe is sender
(6, 2, 'Receiver');  -- alice_smith is receiver

-- Email 7: alice_smith -> bob_wilson (Weekend plans)
INSERT OR IGNORE INTO EmailUser (EmailID, UserID, Role) VALUES
(7, 2, 'Sender'),    -- alice_smith is sender
(7, 3, 'Receiver');  -- bob_wilson is receiver