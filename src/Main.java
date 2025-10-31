import entities.*;
import services.*;

import java.util.List;
import java.util.Scanner;

/**
 * Main - Console-based Email Client Application
 * 
 * This is a text-based interface for testing and backup purposes.
 * Provides all core functionality through console commands.
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class Main {
    
    private static DatabaseHelper dbHelper;
    private static UserService userService;
    private static EmailService emailService;
    private static FolderService folderService;
    private static AttachmentService attachmentService;
    
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    Professional Email Client - Console   ");
        System.out.println("===========================================\n");
        
        // Initialize services
        initializeServices();
        
        // Main application loop
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = handleAuthMenu();
            } else {
                running = handleMainMenu();
            }
        }
        
        System.out.println("\nThank you for using Professional Email Client!");
        scanner.close();
    }
    
    /**
     * Initialize all service layer components
     */
    private static void initializeServices() {
        try {
            dbHelper = new DatabaseHelper();
            dbHelper.initializeDatabase();
            
            userService = new UserService(dbHelper);
            emailService = new EmailService(dbHelper);
            folderService = new FolderService(dbHelper);
            attachmentService = new AttachmentService(dbHelper);
            
            System.out.println("✓ Services initialized successfully\n");
        } catch (Exception e) {
            System.err.println("✗ Error initializing services: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Handle authentication menu (login/signup)
     */
    private static boolean handleAuthMenu() {
        System.out.println("\n--- Authentication Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleSignup();
                break;
            case "3":
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        
        return true;
    }
    
    /**
     * Handle main menu (when user is logged in)
     */
    private static boolean handleMainMenu() {
        System.out.println("\n===========================================");
        System.out.println("Welcome, " + currentUser.getName() + "!");
        System.out.println("===========================================");
        System.out.println("1. View Inbox");
        System.out.println("2. View Sent Emails");
        System.out.println("3. Compose Email");
        System.out.println("4. Search Emails");
        System.out.println("5. View Folders");
        System.out.println("6. Email Statistics");
        System.out.println("7. Logout");
        System.out.println("8. Exit");
        System.out.print("Choose option: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                viewInbox();
                break;
            case "2":
                viewSentEmails();
                break;
            case "3":
                composeEmail();
                break;
            case "4":
                searchEmails();
                break;
            case "5":
                viewFolders();
                break;
            case "6":
                viewStatistics();
                break;
            case "7":
                handleLogout();
                break;
            case "8":
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        
        return true;
    }
    
    /**
     * Handle user login
     */
    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            User user = userService.login(username, password);
            if (user != null) {
                currentUser = user;
                userService.updateLastLogin(user.getUserId());
                System.out.println("✓ Login successful!");
            } else {
                System.out.println("✗ Invalid username or password.");
            }
        } catch (Exception e) {
            System.out.println("✗ Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle user signup
     */
    private static void handleSignup() {
        System.out.println("\n--- Sign Up ---");
        System.out.print("Username (3+ characters): ");
        String username = scanner.nextLine();
        System.out.print("Password (4+ characters): ");
        String password = scanner.nextLine();
        System.out.print("Personal Details (optional): ");
        String details = scanner.nextLine();
        
        try {
            User newUser = new User();
            newUser.setName(username);
            newUser.setPassword(password);
            newUser.setPersonalDetails(details);
            
            if (!userService.isValidInput(newUser)) {
                System.out.println("✗ Invalid input. Check username and password requirements.");
                return;
            }
            
            boolean success = userService.signup(newUser);
            if (success) {
                System.out.println("✓ Signup successful! You can now login.");
            } else {
                System.out.println("✗ Signup failed. Username may already exist.");
            }
        } catch (Exception e) {
            System.out.println("✗ Signup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle logout
     */
    private static void handleLogout() {
        userService.logout(currentUser);
        System.out.println("✓ Logged out successfully.");
        currentUser = null;
    }
    
    /**
     * View inbox emails
     */
    private static void viewInbox() {
        try {
            List<Email> emails = emailService.getInboxEmails(currentUser.getUserId());
            
            System.out.println("\n--- Inbox (" + emails.size() + " emails) ---");
            if (emails.isEmpty()) {
                System.out.println("No emails in inbox.");
                return;
            }
            
            for (int i = 0; i < emails.size(); i++) {
                Email email = emails.get(i);
                System.out.printf("%d. [%s] %s - %s | %s%n",
                    i + 1,
                    email.isRead() ? "✓" : "✉",
                    email.getSenderName(),
                    email.getSubject(),
                    email.getTimestamp()
                );
            }
            
            System.out.print("\nEnter email number to view (0 to go back): ");
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice > 0 && choice <= emails.size()) {
                viewEmailDetails(emails.get(choice - 1));
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading inbox: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * View sent emails
     */
    private static void viewSentEmails() {
        try {
            List<Email> emails = emailService.getSentEmails(currentUser.getUserId());
            
            System.out.println("\n--- Sent Emails (" + emails.size() + " emails) ---");
            if (emails.isEmpty()) {
                System.out.println("No sent emails.");
                return;
            }
            
            for (int i = 0; i < emails.size(); i++) {
                Email email = emails.get(i);
                System.out.printf("%d. To: %s - %s | %s%n",
                    i + 1,
                    email.getReceiverName(),
                    email.getSubject(),
                    email.getTimestamp()
                );
            }
            
            System.out.print("\nEnter email number to view (0 to go back): ");
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice > 0 && choice <= emails.size()) {
                viewEmailDetails(emails.get(choice - 1));
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading sent emails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * View email details
     */
    private static void viewEmailDetails(Email email) {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("From: " + email.getSenderName());
            System.out.println("To: " + email.getReceiverName());
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Date: " + email.getTimestamp());
            System.out.println("Priority: " + email.getPriority());
            
            // Load and display attachments
            List<Attachment> attachments = attachmentService.getEmailAttachments(email.getEmailId());
            if (!attachments.isEmpty()) {
                System.out.println("Attachments:");
                for (Attachment att : attachments) {
                    System.out.println("  - " + att.getFileName() + " (" + att.getFormattedSize() + ")");
                }
            }
            
            System.out.println("\n" + "-".repeat(50));
            System.out.println(email.getBody());
            System.out.println("=".repeat(50));
            
            // Mark as read if unread
            if (!email.isRead()) {
                emailService.markAsRead(email.getEmailId(), currentUser.getUserId());
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading email details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Compose and send email
     */
    private static void composeEmail() {
        System.out.println("\n--- Compose Email ---");
        System.out.print("To (username): ");
        String to = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Priority (Low/Normal/High): ");
        String priority = scanner.nextLine();
        if (priority.isEmpty()) priority = "Normal";
        
        System.out.println("Body (type END on a new line to finish):");
        StringBuilder body = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            body.append(line).append("\n");
        }
        
        try {
            Email email = new Email();
            email.setSubject(subject);
            email.setBody(body.toString());
            email.setPriority(priority);
            
            boolean success = emailService.sendEmail(email, currentUser.getUserId(), to);
            
            if (success) {
                System.out.println("✓ Email sent successfully!");
            } else {
                System.out.println("✗ Failed to send email. Recipient may not exist.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Search emails
     */
    private static void searchEmails() {
        System.out.println("\n--- Search Emails ---");
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        System.out.println("1. Search Inbox");
        System.out.println("2. Search Sent");
        System.out.print("Choose: ");
        String choice = scanner.nextLine();
        
        String role = choice.equals("1") ? "Receiver" : "Sender";
        
        try {
            List<Email> results = emailService.searchEmails(currentUser.getUserId(), searchTerm, role);
            
            System.out.println("\n--- Search Results (" + results.size() + " found) ---");
            if (results.isEmpty()) {
                System.out.println("No emails found matching your search.");
                return;
            }
            
            for (int i = 0; i < results.size(); i++) {
                Email email = results.get(i);
                System.out.printf("%d. %s - %s | %s%n",
                    i + 1,
                    role.equals("Receiver") ? email.getSenderName() : email.getReceiverName(),
                    email.getSubject(),
                    email.getTimestamp()
                );
            }
        } catch (Exception e) {
            System.out.println("✗ Error searching emails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * View folders
     */
    private static void viewFolders() {
        try {
            List<Folder> folders = folderService.getUserFolders(currentUser.getUserId());
            
            System.out.println("\n--- Your Folders ---");
            for (Folder folder : folders) {
                int emailCount = folderService.getFolderEmailCount(folder.getFolderId());
                System.out.printf("%s %s (%d emails)%s%n",
                    folder.getIcon(),
                    folder.getName(),
                    emailCount,
                    folder.isSystem() ? " [System]" : ""
                );
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading folders: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * View email statistics
     */
    private static void viewStatistics() {
        try {
            EmailStats stats = emailService.getEmailStats(currentUser.getUserId());
            
            System.out.println("\n--- Email Statistics ---");
            System.out.println("Total Emails: " + stats.getTotalEmails());
            System.out.println("Unread Emails: " + stats.getUnreadEmails());
            System.out.println("Starred Emails: " + stats.getStarredEmails());
            System.out.println("Sent Emails: " + stats.getSentEmails());
            System.out.println("Received Emails: " + stats.getReceivedEmails());
            System.out.println("Total Storage: " + stats.getFormattedSize());
        } catch (Exception e) {
            System.out.println("✗ Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
