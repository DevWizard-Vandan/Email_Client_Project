import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService = new UserService();
    private static EmailService emailService = new EmailService();
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Welcome to Email Client ===");

        // Initialize database
        DatabaseHelper.initializeDatabase();

        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- Login/Signup Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                signup();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Welcome, " + currentUser.getName() + "!");
        System.out.println("1. Compose Email");
        System.out.println("2. View Inbox");
        System.out.println("3. View Sent Items");
        System.out.println("4. Logout");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                composeEmail();
                break;
            case 2:
                viewInbox();
                break;
            case 3:
                viewSentItems();
                break;
            case 4:
                logout();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentUser = userService.login(username, password);
        if (currentUser != null) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private static void signup() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User newUser = new User(username, email, password);
        boolean success = userService.signup(newUser);

        if (success) {
            System.out.println("Sign up successful! Please login.");
        } else {
            System.out.println("Sign up failed! Username might already exist.");
        }
    }

    private static void composeEmail() {
        System.out.print("To (username): ");
        String toUsername = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();

        Email email = new Email(subject, body, currentUser.getUserId());
        boolean success = emailService.sendEmail(email, toUsername);

        if (success) {
            System.out.println("Email sent successfully!");
        } else {
            System.out.println("Failed to send email! Check if recipient exists.");
        }
    }

    private static void viewInbox() {
        System.out.println("\n--- Inbox ---");
        emailService.viewInbox(currentUser.getUserId());
    }

    private static void viewSentItems() {
        System.out.println("\n--- Sent Items ---");
        emailService.viewSentItems(currentUser.getUserId());
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
}