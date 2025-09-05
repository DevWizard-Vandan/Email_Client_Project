-- ============================================
-- Contains SQL queries used in EmailService.java
-- ============================================

-- 1. Insert a new email
INSERT INTO Email (Subject, Body, Timestamp)
VALUES (?, ?, NOW());

-- 2. Insert sender relation into EmailUser
INSERT INTO EmailUser (EmailID, UserID, Role)
VALUES (?, ?, 'Sender');

-- 3. Insert receiver relation into EmailUser
INSERT INTO EmailUser (EmailID, UserID, Role)
VALUES (?, ?, 'Receiver');

-- 4. View Inbox (Emails received by a user)
SELECT e.EmailID, e.Subject, e.Body, e.Timestamp,
       sender.Name AS SenderName,
       receiver.Name AS ReceiverName
FROM Email e
JOIN EmailUser eu ON e.EmailID = eu.EmailID
LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
LEFT JOIN User sender ON senderEU.UserID = sender.UserID
LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
WHERE eu.UserID = ? AND eu.Role = 'Receiver'
ORDER BY e.Timestamp DESC;

-- 5. View Sent Items (Emails sent by a user)
SELECT e.EmailID, e.Subject, e.Body, e.Timestamp,
       sender.Name AS SenderName,
       receiver.Name AS ReceiverName
FROM Email e
JOIN EmailUser eu ON e.EmailID = eu.EmailID
LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
LEFT JOIN User sender ON senderEU.UserID = sender.UserID
LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
WHERE eu.UserID = ? AND eu.Role = 'Sender'
ORDER BY e.Timestamp DESC;

-- 6. Search emails by subject or body (Inbox + Sent)
-- If role is null → search both Inbox and Sent
-- If role = 'Sender' or 'Receiver' → search in that folder only
SELECT e.EmailID, e.Subject, e.Body, e.Timestamp,
       sender.Name AS SenderName,
       receiver.Name AS ReceiverName
FROM Email e
JOIN EmailUser eu ON e.EmailID = eu.EmailID
LEFT JOIN EmailUser senderEU ON e.EmailID = senderEU.EmailID AND senderEU.Role = 'Sender'
LEFT JOIN User sender ON senderEU.UserID = sender.UserID
LEFT JOIN EmailUser receiverEU ON e.EmailID = receiverEU.EmailID AND receiverEU.Role = 'Receiver'
LEFT JOIN User receiver ON receiverEU.UserID = receiver.UserID
WHERE eu.UserID = ?
  AND (e.Subject LIKE ? OR e.Body LIKE ?)
  -- AND eu.Role = ?  (optional filter)
ORDER BY e.Timestamp DESC;

-- 7. Get total email count (Inbox + Sent)
SELECT COUNT(*) AS total
FROM EmailUser
WHERE UserID = ?;
