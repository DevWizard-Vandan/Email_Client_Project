public class UserService {

    /**
     * Handle user signup
     * @param user User object with signup details
     * @return true if signup successful, false otherwise
     */
    public boolean signup(User user) {
        // Check if user already exists
        User existingUser = DatabaseHelper.getUserByName(user.getName());
        if (existingUser != null) {
            System.out.println("Username already exists!");
            return false;
        }

        // Validate input
        if (!isValidInput(user)) {
            return false;
        }

        // Insert user into database
        boolean success = DatabaseHelper.insertUser(user);
        if (success) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("Registration failed!");
        }

        return success;
    }

    /**
     * Handle user login
     * @param username Username entered by user
     * @param password Password entered by user
     * @return User object if login successful, null otherwise
     */
    public User login(String username, String password) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty!");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty!");
            return null;
        }

        // Validate credentials against database
        User user = DatabaseHelper.validateUser(username.trim(), password);

        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getName());
        } else {
            System.out.println("Invalid username or password!");
        }

        return user;
    }

    /**
     * Validate user input for signup
     * @param user User object to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidInput(User user) {
        // Check username
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            System.out.println("Username cannot be empty!");
            return false;
        }

        if (user.getName().length() < 3) {
            System.out.println("Username must be at least 3 characters long!");
            return false;
        }

        // Check email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.out.println("Email cannot be empty!");
            return false;
        }

        if (!isValidEmail(user.getEmail())) {
            System.out.println("Please enter a valid email address!");
            return false;
        }

        // Check password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("Password cannot be empty!");
            return false;
        }

        if (user.getPassword().length() < 4) {
            System.out.println("Password must be at least 4 characters long!");
            return false;
        }

        return true;
    }

    /**
     * Simple email validation
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Get user by username (utility method)
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        return DatabaseHelper.getUserByName(username);
    }
}