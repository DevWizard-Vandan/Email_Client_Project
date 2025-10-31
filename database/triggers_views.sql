USE email_client;

-- ========== DROP EXISTING OBJECTS ==========
DROP TRIGGER IF EXISTS CreateDefaultFolders;
DROP VIEW IF EXISTS EmailWithDetails;
DROP VIEW IF EXISTS UserEmailStatistics;

-- ========== TRIGGER: Create Default Folders ==========
DELIMITER //

CREATE TRIGGER CreateDefaultFolders
AFTER INSERT ON User
FOR EACH ROW
BEGIN
    -- Create 5 default system folders for new user
    INSERT INTO Folder (UserID, Name, IsSystem, Color) VALUES
    (NEW.UserID, 'Inbox', TRUE, '#3498db'),
    (NEW.UserID, 'Sent', TRUE, '#27ae60'),
    (NEW.UserID, 'Drafts', TRUE, '#f39c12'),
    (NEW.UserID, 'Trash', TRUE, '#e74c3c'),
    (NEW.UserID, 'Spam', TRUE, '#95a5a6');
END//

DELIMITER ;

-- ========== VIEW: Email With Sender/Receiver Details ==========
CREATE VIEW EmailWithDetails AS
SELECT 
    e.EmailID,
    e.Subject,
    e.Body,
    e.Timestamp,
    e.Priority,
    e.IsHTML,
    sender.UserID as SenderID,
    sender.Name as SenderName,
    receiver.UserID as ReceiverID,
    receiver.Name as ReceiverName,
    eu_receiver.IsRead,
    eu_receiver.IsStarred,
    eu_receiver.IsDeleted,
    eu_receiver.FolderID,
    f.Name as FolderName
FROM Email e
LEFT JOIN EmailUser eu_sender ON e.EmailID = eu_sender.EmailID AND eu_sender.Role = 'Sender'
LEFT JOIN User sender ON eu_sender.UserID = sender.UserID
LEFT JOIN EmailUser eu_receiver ON e.EmailID = eu_receiver.EmailID AND eu_receiver.Role = 'Receiver'
LEFT JOIN User receiver ON eu_receiver.UserID = receiver.UserID
LEFT JOIN Folder f ON eu_receiver.FolderID = f.FolderID;

-- ========== VIEW: User Email Statistics ==========
CREATE VIEW UserEmailStatistics AS
SELECT 
    u.UserID,
    u.Name as Username,
    COUNT(DISTINCT eu.EmailID) as TotalEmails,
    SUM(CASE WHEN eu.Role = 'Sender' THEN 1 ELSE 0 END) as SentEmails,
    SUM(CASE WHEN eu.Role = 'Receiver' THEN 1 ELSE 0 END) as ReceivedEmails,
    SUM(CASE WHEN eu.IsRead = FALSE AND eu.Role = 'Receiver' THEN 1 ELSE 0 END) as UnreadEmails,
    SUM(CASE WHEN eu.IsStarred = TRUE THEN 1 ELSE 0 END) as StarredEmails,
    COUNT(DISTINCT f.FolderID) as TotalFolders
FROM User u
LEFT JOIN EmailUser eu ON u.UserID = eu.UserID AND eu.IsDeleted = FALSE
LEFT JOIN Folder f ON u.UserID = f.UserID
GROUP BY u.UserID, u.Name;

-- ========== SAMPLE COMPLEX QUERIES ==========

-- Query 1: Get all unread emails with sender details
-- SELECT * FROM EmailWithDetails WHERE ReceiverID = 1 AND IsRead = FALSE AND IsDeleted = FALSE;

-- Query 2: Get email count by priority for a user
-- SELECT Priority, COUNT(*) as Count FROM Email e
-- JOIN EmailUser eu ON e.EmailID = eu.EmailID
-- WHERE eu.UserID = 1 AND eu.IsDeleted = FALSE
-- GROUP BY Priority;

-- Query 3: Get users with most unread emails
-- SELECT Username, UnreadEmails FROM UserEmailStatistics ORDER BY UnreadEmails DESC LIMIT 10;

-- Query 4: Full-text search example
-- SELECT * FROM Email WHERE MATCH(Subject, Body) AGAINST('meeting' IN NATURAL LANGUAGE MODE);

-- ============================================
-- Triggers and Views created successfully
-- ============================================
