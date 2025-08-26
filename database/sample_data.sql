-- Sample Data for Email Client
-- Insert sample users
INSERT INTO User (Name, Email, Password, Domain) VALUES
('John Doe', 'john.doe@gmail.com', 'hashedpassword123', 'gmail.com'),
('Jane Smith', 'jane.smith@yahoo.com', 'hashedpassword456', 'yahoo.com'),
('Bob Johnson', 'bob.johnson@outlook.com', 'hashedpassword789', 'outlook.com'),
('Alice Brown', 'alice.brown@company.com', 'hashedpasswordabc', 'company.com');

-- Insert website signup records
INSERT INTO WebsiteSignUp (UserID, IPAddress, UserAgent) VALUES
(1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
(2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
(3, '192.168.1.102', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'),
(4, '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');

-- Insert default folders for each user
INSERT INTO Folder (UserID, FolderName, IsDefault) VALUES
-- John Doe's folders
(1, 'Inbox', TRUE),
(1, 'Sent', TRUE),
(1, 'Drafts', TRUE),
(1, 'Trash', TRUE),
(1, 'Work', FALSE),
(1, 'Personal', FALSE),

-- Jane Smith's folders
(2, 'Inbox', TRUE),
(2, 'Sent', TRUE),
(2, 'Drafts', TRUE),
(2, 'Trash', TRUE),
(2, 'Projects', FALSE),

-- Bob Johnson's folders
(3, 'Inbox', TRUE),
(3, 'Sent', TRUE),
(3, 'Drafts', TRUE),
(3, 'Trash', TRUE),
(3, 'Important', FALSE),

-- Alice Brown's folders
(4, 'Inbox', TRUE),
(4, 'Sent', TRUE),
(4, 'Drafts', TRUE),
(4, 'Trash', TRUE),
(4, 'Client Communications', FALSE);

-- Insert sample emails
INSERT INTO Email (Subject, Body, Priority, FolderID) VALUES
('Welcome to the Project', 'Hi team, welcome to our new email client project. Let me know if you have any questions.', 'NORMAL', 1),
('Meeting Reminder', 'Don''t forget about our team meeting tomorrow at 10 AM.', 'HIGH', 1),
('Project Update', 'Here''s the latest update on our progress. We''re making good headway.', 'NORMAL', 5),
('Quarterly Reports', 'Please find attached the quarterly reports for review.', 'HIGH', 1),
('Vacation Request', 'I would like to request vacation days from next Monday.', 'NORMAL', 6);

-- Insert email-user relationships
INSERT INTO EmailUser (EmailID, UserID, Role) VALUES
-- Email 1: From John to Jane
(1, 1, 'SENDER'),
(1, 2, 'RECEIVER'),

-- Email 2: From Jane to John and Bob
(2, 2, 'SENDER'),
(2, 1, 'RECEIVER'),
(2, 3, 'CC'),

-- Email 3: From Bob to Alice
(3, 3, 'SENDER'),
(3, 4, 'RECEIVER'),

-- Email 4: From Alice to John
(4, 4, 'SENDER'),
(4, 1, 'RECEIVER'),

-- Email 5: From John to Alice
(5, 1, 'SENDER'),
(5, 4, 'RECEIVER');

-- Insert sample attachments
INSERT INTO Attachment (EmailID, FileName, FilePath, FileSize, FileType) VALUES
(4, 'Q1_Report.pdf', '/attachments/q1_report.pdf', 2048000, 'application/pdf'),
(4, 'Financial_Summary.xlsx', '/attachments/financial_summary.xlsx', 1024000, 'application/vnd.ms-excel'),
(3, 'Project_Timeline.docx', '/attachments/project_timeline.docx', 512000, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');