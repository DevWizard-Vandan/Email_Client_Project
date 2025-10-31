import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import entities.*;
import services.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * MainGUI - Professional Email Client Application
 * 
 * This is the primary JavaFX application that provides a complete email client interface
 * with modern UI/UX design, featuring login/signup, email management, folder organization,
 * and file attachment support.
 * 
 * Architecture: Three-tier (Presentation layer)
 * GUI Framework: JavaFX 21+
 * Database: MySQL 8.0+
 * 
 * @version 1.0
 * @since 2025-01-09
 */
public class MainGUI extends Application {
    
    // Service layer instances
    private DatabaseHelper dbHelper;
    private UserService userService;
    private EmailService emailService;
    private FolderService folderService;
    private AttachmentService attachmentService;
    
    // Current session user
    private User currentUser;
    
    // UI Components
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainAppScene;
    
    // Main application components
    private TreeView<Folder> folderTreeView;
    private TableView<Email> emailTableView;
    private TextArea emailContentArea;
    private Label statusLabel;
    private Label welcomeLabel;
    
    // Current state
    private Folder selectedFolder;
    private Email selectedEmail;
    
    /**
     * Application entry point - initializes services and shows login screen
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Professional Email Client");
        
        // Initialize database and services
        initializeServices();
        
        // Show login screen
        showLoginScreen();
        
        primaryStage.show();
    }
    
    /**
     * Initialize all service layer components
     */
    private void initializeServices() {
        try {
            dbHelper = new DatabaseHelper();
            dbHelper.initializeDatabase();
            
            userService = new UserService(dbHelper);
            emailService = new EmailService(dbHelper);
            folderService = new FolderService(dbHelper);
            attachmentService = new AttachmentService(dbHelper);
            
            System.out.println("Services initialized successfully");
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize application services.");
            System.err.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Display the login/signup screen with modern card-based design
     */
    private void showLoginScreen() {
        // Main container with gradient background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");
        
        // Card container
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
        
        // Application title
        Label titleLabel = new Label("Professional Email Client");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // TabPane for Login and Signup
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Login Tab
        Tab loginTab = new Tab("Login");
        loginTab.setContent(createLoginForm());
        
        // Signup Tab
        Tab signupTab = new Tab("Sign Up");
        signupTab.setContent(createSignupForm());
        
        tabPane.getTabs().addAll(loginTab, signupTab);
        
        card.getChildren().addAll(titleLabel, tabPane);
        root.getChildren().add(card);
        
        loginScene = new Scene(root, 1000, 600);
        loginScene.getStylesheets().add("file:src/resources/styles.css");
        primaryStage.setScene(loginScene);
    }
    
    /**
     * Create the login form with username and password fields
     */
    private VBox createLoginForm() {
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(20));
        
        // Username field
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("text-field");
        
        // Password field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("password-field");
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        // Status label for error messages
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        // Login action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password");
                return;
            }
            
            handleLogin(username, password, statusLabel);
        });
        
        // Enter key support
        passwordField.setOnAction(e -> loginButton.fire());
        
        loginForm.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            loginButton, statusLabel
        );
        
        return loginForm;
    }
    
    /**
     * Create the signup form with username, password, and personal details
     */
    private VBox createSignupForm() {
        VBox signupForm = new VBox(15);
        signupForm.setAlignment(Pos.CENTER);
        signupForm.setPadding(new Insets(20));
        
        // Username field
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username (3+ characters)");
        
        // Password field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password (4+ characters)");
        
        // Personal details field
        Label detailsLabel = new Label("Personal Details (Optional):");
        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Email address, phone number, etc.");
        detailsArea.setPrefRowCount(3);
        
        // Signup button
        Button signupButton = new Button("Sign Up");
        signupButton.getStyleClass().add("success-button");
        signupButton.setMaxWidth(Double.MAX_VALUE);
        
        // Status label
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        
        // Signup action
        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String details = detailsArea.getText().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                statusLabel.setText("Username and password are required");
                return;
            }
            
            handleSignup(username, password, details, statusLabel);
        });
        
        signupForm.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            detailsLabel, detailsArea,
            signupButton, statusLabel
        );
        
        return signupForm;
    }
    
    /**
     * Handle login authentication
     */
    private void handleLogin(String username, String password, Label statusLabel) {
        try {
            User user = userService.login(username, password);
            
            if (user != null) {
                currentUser = user;
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
                statusLabel.setText("Login successful! Loading...");
                
                // Update last login
                userService.updateLastLogin(user.getUserId());
                
                // Show main application
                showEmailClient();
            } else {
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                statusLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Login failed. Please try again.");
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle user signup
     */
    private void handleSignup(String username, String password, String details, Label statusLabel) {
        try {
            // Create new user
            User newUser = new User();
            newUser.setName(username);
            newUser.setPassword(password);
            newUser.setPersonalDetails(details);
            
            // Validate and signup
            if (!userService.isValidInput(newUser)) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                statusLabel.setText("Invalid input. Username must be 3+ characters, password 4+ characters.");
                return;
            }
            
            boolean success = userService.signup(newUser);
            
            if (success) {
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
                statusLabel.setText("Signup successful! Please login with your credentials.");
            } else {
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                statusLabel.setText("Signup failed. Username may already exist.");
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Signup failed. Please try again.");
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Display the main email client application
     */
    private void showEmailClient() {
        BorderPane root = new BorderPane();
        
        // Top: Menu bar and toolbar
        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(createMenuBar(), createToolbar());
        root.setTop(topContainer);
        
        // Center: Three-panel layout (folders, emails, content)
        SplitPane mainSplitPane = createMainLayout();
        root.setCenter(mainSplitPane);
        
        // Bottom: Status bar
        root.setBottom(createStatusBar());
        
        mainAppScene = new Scene(root, 1200, 700);
        mainAppScene.getStylesheets().add("file:src/resources/styles.css");
        primaryStage.setScene(mainAppScene);
        
        // Load initial data
        loadFolders();
        loadInboxEmails();
        updateStatusBar();
    }
    
    /**
     * Create the menu bar with File, Edit, View, Help menus
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> refreshCurrentFolder());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);
        
        // Edit menu
        Menu editMenu = new Menu("Edit");
        MenuItem composeItem = new MenuItem("Compose Email");
        composeItem.setOnAction(e -> showComposeDialog());
        editMenu.getItems().add(composeItem);
        
        // View menu
        Menu viewMenu = new Menu("View");
        MenuItem statsItem = new MenuItem("Email Statistics");
        statsItem.setOnAction(e -> showStatistics());
        viewMenu.getItems().add(statsItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
        return menuBar;
    }
    
    /**
     * Create the toolbar with action buttons
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");
        
        // Welcome label
        welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Compose button
        Button composeBtn = new Button("✉ Compose");
        composeBtn.getStyleClass().add("primary-button");
        composeBtn.setOnAction(e -> showComposeDialog());
        
        // Refresh button
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.getStyleClass().add("secondary-button");
        refreshBtn.setOnAction(e -> refreshCurrentFolder());
        
        // Settings button
        Button settingsBtn = new Button("⚙ Settings");
        settingsBtn.getStyleClass().add("secondary-button");
        settingsBtn.setOnAction(e -> showSettingsDialog());
        
        // Logout button
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.getStyleClass().add("danger-button");
        logoutBtn.setOnAction(e -> handleLogout());
        
        toolbar.getChildren().addAll(welcomeLabel, spacer, composeBtn, refreshBtn, settingsBtn, logoutBtn);
        return toolbar;
    }
    
    /**
     * Create the main three-panel layout
     */
    private SplitPane createMainLayout() {
        SplitPane splitPane = new SplitPane();
        
        // Left panel: Folder tree view
        VBox leftPanel = new VBox(5);
        leftPanel.setPadding(new Insets(10));
        Label folderLabel = new Label("Folders");
        folderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        folderTreeView = createFolderTreeView();
        leftPanel.getChildren().addAll(folderLabel, folderTreeView);
        
        // Center panel: Email table view
        VBox centerPanel = new VBox(5);
        centerPanel.setPadding(new Insets(10));
        Label emailLabel = new Label("Emails");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        emailTableView = createEmailTableView();
        centerPanel.getChildren().addAll(emailLabel, emailTableView);
        
        // Right panel: Email content preview
        VBox rightPanel = new VBox(5);
        rightPanel.setPadding(new Insets(10));
        Label contentLabel = new Label("Email Content");
        contentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        emailContentArea = createEmailContentArea();
        rightPanel.getChildren().addAll(contentLabel, emailContentArea);
        
        splitPane.getItems().addAll(leftPanel, centerPanel, rightPanel);
        splitPane.setDividerPositions(0.2, 0.5);
        
        return splitPane;
    }
    
    /**
     * Create folder tree view
     */
    private TreeView<Folder> createFolderTreeView() {
        TreeView<Folder> treeView = new TreeView<>();
        treeView.setShowRoot(false);
        treeView.getStyleClass().add("folder-tree");
        
        // Selection listener
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                selectedFolder = newVal.getValue();
                loadEmailsForFolder(selectedFolder);
            }
        });
        
        return treeView;
    }
    
    /**
     * Create email table view with columns
     */
    private TableView<Email> createEmailTableView() {
        TableView<Email> tableView = new TableView<>();
        tableView.getStyleClass().add("email-table");
        
        // Priority column (icon)
        TableColumn<Email, String> priorityCol = new TableColumn<>("⚡");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(40);
        priorityCol.setCellFactory(col -> new TableCell<Email, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                } else {
                    setText(getPriorityIcon(priority));
                }
            }
        });
        
        // Read status column
        TableColumn<Email, Boolean> readCol = new TableColumn<>("✓");
        readCol.setCellValueFactory(new PropertyValueFactory<>("isRead"));
        readCol.setPrefWidth(40);
        readCol.setCellFactory(col -> new TableCell<Email, Boolean>() {
            @Override
            protected void updateItem(Boolean isRead, boolean empty) {
                super.updateItem(isRead, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(isRead ? "✓" : "✉");
                    setStyle(isRead ? "-fx-text-fill: #95a5a6;" : "-fx-text-fill: #3498db; -fx-font-weight: bold;");
                }
            }
        });
        
        // From/To column (dynamic based on folder)
        TableColumn<Email, String> fromToCol = new TableColumn<>("From/To");
        fromToCol.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        fromToCol.setPrefWidth(150);
        
        // Subject column
        TableColumn<Email, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setPrefWidth(300);
        
        // Attachment column
        TableColumn<Email, Integer> attachCol = new TableColumn<>("📎");
        attachCol.setCellValueFactory(new PropertyValueFactory<>("attachmentCount"));
        attachCol.setPrefWidth(40);
        attachCol.setCellFactory(col -> new TableCell<Email, Integer>() {
            @Override
            protected void updateItem(Integer count, boolean empty) {
                super.updateItem(count, empty);
                if (empty || count == null || count == 0) {
                    setText(null);
                } else {
                    setText("📎" + count);
                }
            }
        });
        
        // Date column
        TableColumn<Email, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setPrefWidth(150);
        
        tableView.getColumns().addAll(priorityCol, readCol, fromToCol, subjectCol, attachCol, dateCol);
        
        // Row selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedEmail = newVal;
                displayEmailContent(newVal);
                
                // Mark as read if unread
                if (!newVal.isRead()) {
                    markEmailAsRead(newVal);
                }
            }
        });
        
        // Context menu for right-click actions
        tableView.setContextMenu(createEmailContextMenu());
        
        return tableView;
    }
    
    /**
     * Create context menu for email table
     */
    private ContextMenu createEmailContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem replyItem = new MenuItem("Reply");
        replyItem.setOnAction(e -> {
            if (selectedEmail != null) {
                showReplyDialog(selectedEmail);
            }
        });
        
        MenuItem starItem = new MenuItem("Star/Unstar");
        starItem.setOnAction(e -> {
            if (selectedEmail != null) {
                toggleStar(selectedEmail);
            }
        });
        
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            if (selectedEmail != null) {
                deleteEmail(selectedEmail);
            }
        });
        
        contextMenu.getItems().addAll(replyItem, starItem, new SeparatorMenuItem(), deleteItem);
        return contextMenu;
    }
    
    /**
     * Create email content display area
     */
    private TextArea createEmailContentArea() {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPromptText("Select an email to view its content");
        textArea.getStyleClass().add("email-content");
        return textArea;
    }
    
    /**
     * Create status bar at the bottom
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("status-bar");
        
        statusLabel = new Label("Status: Ready");
        statusLabel.setFont(Font.font("Arial", 11));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label connectionLabel = new Label("● Connected to database");
        connectionLabel.setStyle("-fx-text-fill: #27ae60;");
        connectionLabel.setFont(Font.font("Arial", 11));
        
        statusBar.getChildren().addAll(statusLabel, spacer, connectionLabel);
        return statusBar;
    }
    
    /**
     * Load folders into tree view
     */
    private void loadFolders() {
        try {
            List<Folder> folders = folderService.getUserFolders(currentUser.getUserId());
            
            TreeItem<Folder> root = new TreeItem<>(new Folder(0, currentUser.getUserId(), "Root", null));
            
            // Separate system and custom folders
            List<Folder> systemFolders = new ArrayList<>();
            List<Folder> customFolders = new ArrayList<>();
            
            for (Folder folder : folders) {
                if (folder.isSystem()) {
                    systemFolders.add(folder);
                } else {
                    customFolders.add(folder);
                }
            }
            
            // Add system folders first
            for (Folder folder : systemFolders) {
                TreeItem<Folder> item = new TreeItem<>(folder);
                root.getChildren().add(item);
            }
            
            // Add separator (represented as a special folder)
            if (!customFolders.isEmpty()) {
                // Add custom folders
                for (Folder folder : customFolders) {
                    TreeItem<Folder> item = new TreeItem<>(folder);
                    root.getChildren().add(item);
                }
            }
            
            folderTreeView.setRoot(root);
            root.setExpanded(true);
            
            // Select inbox by default
            if (!root.getChildren().isEmpty()) {
                folderTreeView.getSelectionModel().select(root.getChildren().get(0));
            }
            
            System.out.println("Loaded " + folders.size() + " folders");
        } catch (Exception e) {
            showError("Folder Load Error", "Failed to load folders.");
            System.err.println("Error loading folders: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load inbox emails
     */
    private void loadInboxEmails() {
        try {
            List<Email> emails = emailService.getInboxEmails(currentUser.getUserId());
            updateEmailTable(emails);
            System.out.println("Loaded " + emails.size() + " inbox emails");
        } catch (Exception e) {
            showError("Email Load Error", "Failed to load inbox emails.");
            System.err.println("Error loading inbox: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load emails for selected folder
     */
    private void loadEmailsForFolder(Folder folder) {
        try {
            List<Email> emails;
            
            // Determine which emails to load based on folder name
            String folderName = folder.getName().toLowerCase();
            
            if (folderName.equals("inbox")) {
                emails = emailService.getInboxEmails(currentUser.getUserId());
            } else if (folderName.equals("sent")) {
                emails = emailService.getSentEmails(currentUser.getUserId());
            } else {
                // Load emails from specific folder
                emails = emailService.getEmailsByFolder(currentUser.getUserId(), folder.getFolderId());
            }
            
            updateEmailTable(emails);
            updateStatusBar();
            System.out.println("Loaded " + emails.size() + " emails for folder: " + folder.getName());
        } catch (Exception e) {
            showError("Email Load Error", "Failed to load emails for folder: " + folder.getName());
            System.err.println("Error loading folder emails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update email table with new data
     */
    private void updateEmailTable(List<Email> emails) {
        ObservableList<Email> emailList = FXCollections.observableArrayList(emails);
        emailTableView.setItems(emailList);
    }
    
    /**
     * Display email content in preview pane
     */
    private void displayEmailContent(Email email) {
        try {
            StringBuilder content = new StringBuilder();
            
            content.append("From: ").append(email.getSenderName()).append("\n");
            content.append("To: ").append(email.getReceiverName()).append("\n");
            content.append("Subject: ").append(email.getSubject()).append("\n");
            content.append("Date: ").append(email.getTimestamp()).append("\n");
            content.append("Priority: ").append(email.getPriority()).append("\n");
            
            // Load attachments if any
            List<Attachment> attachments = attachmentService.getEmailAttachments(email.getEmailId());
            if (!attachments.isEmpty()) {
                content.append("Attachments: ").append(attachments.size()).append(" file(s)\n");
                for (Attachment att : attachments) {
                    content.append("  - ").append(att.getFileName())
                           .append(" (").append(att.getFormattedSize()).append(")\n");
                }
            }
            
            content.append("\n");
            content.append("─".repeat(50)).append("\n\n");
            content.append(email.getBody());
            
            emailContentArea.setText(content.toString());
        } catch (Exception e) {
            emailContentArea.setText("Error loading email content.");
            System.err.println("Error displaying email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show compose email dialog
     */
    private void showComposeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Compose Email");
        dialog.setHeaderText("Send a new email");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField toField = new TextField();
        toField.setPromptText("Recipient username");
        
        TextField subjectField = new TextField();
        subjectField.setPromptText("Email subject");
        
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Email body");
        bodyArea.setPrefRowCount(10);
        bodyArea.setWrapText(true);
        
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Normal", "High");
        priorityCombo.setValue("Normal");
        
        ListView<File> attachmentList = new ListView<>();
        attachmentList.setPrefHeight(80);
        ObservableList<File> selectedFiles = FXCollections.observableArrayList();
        attachmentList.setItems(selectedFiles);
        
        Button attachBtn = new Button("Add Attachment");
        attachBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File");
            List<File> files = fileChooser.showOpenMultipleDialog(dialog.getOwner());
            if (files != null) {
                selectedFiles.addAll(files);
            }
        });
        
        Button removeBtn = new Button("Remove");
        removeBtn.setOnAction(e -> {
            File selected = attachmentList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedFiles.remove(selected);
            }
        });
        
        HBox attachButtons = new HBox(5, attachBtn, removeBtn);
        
        grid.add(new Label("To:"), 0, 0);
        grid.add(toField, 1, 0);
        grid.add(new Label("Subject:"), 0, 1);
        grid.add(subjectField, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityCombo, 1, 2);
        grid.add(new Label("Body:"), 0, 3);
        grid.add(bodyArea, 1, 3);
        grid.add(new Label("Attachments:"), 0, 4);
        grid.add(attachmentList, 1, 4);
        grid.add(attachButtons, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Handle send action
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            handleSendEmail(toField.getText(), subjectField.getText(), 
                          bodyArea.getText(), priorityCombo.getValue(), selectedFiles);
        }
    }
    
    /**
     * Handle sending email
     */
    private void handleSendEmail(String to, String subject, String body, String priority, ObservableList<File> attachments) {
        try {
            // Validate inputs
            if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                showError("Validation Error", "Please fill in all required fields.");
                return;
            }
            
            // Create email object
            Email email = new Email();
            email.setSubject(subject);
            email.setBody(body);
            email.setPriority(priority);
            
            // Send email
            boolean success = emailService.sendEmail(email, currentUser.getUserId(), to);
            
            if (success) {
                // Save attachments if any
                if (!attachments.isEmpty()) {
                    for (File file : attachments) {
                        attachmentService.saveAttachment(email.getEmailId(), file);
                    }
                }
                
                showInfo("Success", "Email sent successfully!");
                refreshCurrentFolder();
            } else {
                showError("Send Failed", "Failed to send email. Recipient may not exist.");
            }
        } catch (Exception e) {
            showError("Send Error", "An error occurred while sending the email.");
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show reply dialog
     */
    private void showReplyDialog(Email originalEmail) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reply to Email");
        dialog.setHeaderText("Reply to: " + originalEmail.getSubject());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label toLabel = new Label("To: " + originalEmail.getSenderName());
        Label subjectLabel = new Label("Subject: RE: " + originalEmail.getSubject());
        
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Your reply...");
        bodyArea.setPrefRowCount(10);
        bodyArea.setWrapText(true);
        
        // Include original message
        bodyArea.setText("\n\n--- Original Message ---\n" + originalEmail.getBody());
        
        grid.add(toLabel, 0, 0);
        grid.add(subjectLabel, 0, 1);
        grid.add(new Label("Body:"), 0, 2);
        grid.add(bodyArea, 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Email reply = new Email();
            reply.setSubject("RE: " + originalEmail.getSubject());
            reply.setBody(bodyArea.getText());
            reply.setPriority("Normal");
            
            emailService.sendEmail(reply, currentUser.getUserId(), originalEmail.getSenderName());
            showInfo("Success", "Reply sent successfully!");
            refreshCurrentFolder();
        }
    }
    
    /**
     * Mark email as read
     */
    private void markEmailAsRead(Email email) {
        try {
            boolean success = emailService.markAsRead(email.getEmailId(), currentUser.getUserId());
            if (success) {
                email.setRead(true);
                emailTableView.refresh();
                updateStatusBar();
                System.out.println("Marked email as read: " + email.getEmailId());
            }
        } catch (Exception e) {
            System.err.println("Error marking email as read: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Toggle star status of email
     */
    private void toggleStar(Email email) {
        try {
            boolean success = emailService.toggleStar(email.getEmailId(), currentUser.getUserId());
            if (success) {
                email.setStarred(!email.isStarred());
                showInfo("Success", email.isStarred() ? "Email starred!" : "Email unstarred!");
                refreshCurrentFolder();
            }
        } catch (Exception e) {
            showError("Error", "Failed to update star status.");
            System.err.println("Error toggling star: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Delete email
     */
    private void deleteEmail(Email email) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete this email?");
        confirm.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = emailService.deleteEmail(email.getEmailId(), currentUser.getUserId());
                if (success) {
                    showInfo("Success", "Email deleted successfully!");
                    refreshCurrentFolder();
                } else {
                    showError("Error", "Failed to delete email.");
                }
            } catch (Exception e) {
                showError("Delete Error", "An error occurred while deleting the email.");
                System.err.println("Error deleting email: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Refresh current folder
     */
    private void refreshCurrentFolder() {
        if (selectedFolder != null) {
            loadEmailsForFolder(selectedFolder);
        } else {
            loadInboxEmails();
        }
        updateStatusBar();
    }
    
    /**
     * Update status bar with email counts
     */
    private void updateStatusBar() {
        try {
            EmailStats stats = emailService.getEmailStats(currentUser.getUserId());
            statusLabel.setText(String.format("Total: %d | Unread: %d | Sent: %d | Received: %d",
                stats.getTotalEmails(), stats.getUnreadEmails(), 
                stats.getSentEmails(), stats.getReceivedEmails()));
        } catch (Exception e) {
            statusLabel.setText("Status: Error loading statistics");
            System.err.println("Error updating status bar: " + e.getMessage());
        }
    }
    
    /**
     * Show statistics dialog
     */
    private void showStatistics() {
        try {
            EmailStats stats = emailService.getEmailStats(currentUser.getUserId());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Email Statistics");
            alert.setHeaderText("Your Email Statistics");
            
            String content = String.format(
                "Total Emails: %d\n" +
                "Unread Emails: %d\n" +
                "Starred Emails: %d\n" +
                "Sent Emails: %d\n" +
                "Received Emails: %d\n" +
                "Total Storage: %s",
                stats.getTotalEmails(),
                stats.getUnreadEmails(),
                stats.getStarredEmails(),
                stats.getSentEmails(),
                stats.getReceivedEmails(),
                stats.getFormattedSize()
            );
            
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            showError("Error", "Failed to load statistics.");
            System.err.println("Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show settings dialog
     */
    private void showSettingsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Application Settings");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label userLabel = new Label("Username: " + currentUser.getName());
        Label emailLabel = new Label("User ID: " + currentUser.getUserId());
        Label createdLabel = new Label("Account Created: " + currentUser.getCreatedAt());
        
        grid.add(userLabel, 0, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(createdLabel, 0, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Professional Email Client");
        alert.setContentText(
            "Version: 1.0\n" +
            "Developed by: Team 5\n" +
            "Technology: JavaFX 21+ with MySQL 8.0+\n\n" +
            "A comprehensive email management system with:\n" +
            "• User authentication\n" +
            "• Email composition and management\n" +
            "• Folder organization\n" +
            "• File attachments\n" +
            "• Search functionality\n\n" +
            "© 2025 All Rights Reserved"
        );
        alert.showAndWait();
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userService.logout(currentUser);
            currentUser = null;
            showLoginScreen();
        }
    }
    
    /**
     * Get priority icon
     */
    private String getPriorityIcon(String priority) {
        switch (priority.toLowerCase()) {
            case "high": return "🔴";
            case "low": return "🟢";
            default: return "🟡";
        }
    }
    
    /**
     * Show error alert
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show information alert
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Main method - launch application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
