import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class MainGUI extends Application {
	private Stage primaryStage;
	private UserService userService = new UserService();
	private EmailService emailService = new EmailService();
	private User currentUser = null;

	// Main containers
	private BorderPane mainLayout;
	private VBox loginContainer;
	private BorderPane emailClientContainer;

	// Login/Signup components
	private TextField loginUsername, loginPassword;
	private TextField signupUsername, signupEmail, signupPassword;
	private Label statusLabel;

	// Email client components
	private Label welcomeLabel;
	private ListView<String> emailListView;
	private TextArea emailContentArea;
	private TextField composeToField, composeSubjectField;
	private TextArea composeBodyArea;
	private TabPane mainTabPane;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Email Client - Professional Edition");

		// Initialize database
		DatabaseHelper.initializeDatabase();

		// Create main layout
		mainLayout = new BorderPane();

		// Show login screen initially
		showLoginScreen();

		Scene scene = new Scene(mainLayout, 1000, 700);
		// Comment out CSS loading for now to avoid file path issues
		 scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	private void showLoginScreen() {
		loginContainer = new VBox(20);
		loginContainer.setAlignment(Pos.CENTER);
		loginContainer.setPadding(new Insets(50));
		loginContainer.setStyle("-fx-background-color: #f0f8ff;");

		// Title
		Label titleLabel = new Label("Email Client");
		titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

		// Status label for messages
		statusLabel = new Label();
		statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

		// Login Section
		VBox loginSection = createLoginSection();

		// Signup Section
		VBox signupSection = createSignupSection();

		// Add all components
		loginContainer.getChildren().addAll(
				titleLabel,
				statusLabel,
				createSeparator("Login"),
				loginSection,
				createSeparator("Sign Up"),
				signupSection
		);

		mainLayout.setCenter(loginContainer);
	}

	private VBox createLoginSection() {
		VBox loginBox = new VBox(10);
		loginBox.setAlignment(Pos.CENTER);
		loginBox.setMaxWidth(300);

		loginUsername = new TextField();
		loginUsername.setPromptText("Username");
		loginUsername.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

		loginPassword = new PasswordField();
		loginPassword.setPromptText("Password");
		loginPassword.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
		loginButton.setOnAction(e -> handleLogin());

		// Enter key support
		loginPassword.setOnAction(e -> handleLogin());

		loginBox.getChildren().addAll(loginUsername, loginPassword, loginButton);
		return loginBox;
	}

	private VBox createSignupSection() {
		VBox signupBox = new VBox(10);
		signupBox.setAlignment(Pos.CENTER);
		signupBox.setMaxWidth(300);

		signupUsername = new TextField();
		signupUsername.setPromptText("Username");
		signupUsername.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

		signupEmail = new TextField();
		signupEmail.setPromptText("Email");
		signupEmail.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

		signupPassword = new PasswordField();
		signupPassword.setPromptText("Password");
		signupPassword.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

		Button signupButton = new Button("Sign Up");
		signupButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
		signupButton.setOnAction(e -> handleSignup());

		signupBox.getChildren().addAll(signupUsername, signupEmail, signupPassword, signupButton);
		return signupBox;
	}

	private Separator createSeparator(String text) {
		Separator separator = new Separator();
		separator.setStyle("-fx-background-color: #bdc3c7;");
		return separator;
	}

	private void handleLogin() {
		String username = loginUsername.getText().trim();
		String password = loginPassword.getText();

		if (username.isEmpty() || password.isEmpty()) {
			showStatus("Please enter both username and password.", "error");
			return;
		}

		currentUser = userService.login(username, password);
		if (currentUser != null) {
			showEmailClient();
		} else {
			showStatus("Invalid username or password!", "error");
			loginPassword.clear();
		}
	}

	private void handleSignup() {
		String username = signupUsername.getText().trim();
		String email = signupEmail.getText().trim();
		String password = signupPassword.getText();

		if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
			showStatus("Please fill in all signup fields.", "error");
			return;
		}

		User newUser = new User(username, email, password);
		boolean success = userService.signup(newUser);

		if (success) {
			showStatus("Account created successfully! Please login.", "success");
			clearSignupFields();
		} else {
			showStatus("Signup failed! Username might already exist.", "error");
		}
	}

	private void showStatus(String message, String type) {
		statusLabel.setText(message);
		if (type.equals("success")) {
			statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
		} else {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
		}
	}

	private void clearSignupFields() {
		signupUsername.clear();
		signupEmail.clear();
		signupPassword.clear();
	}

	private void showEmailClient() {
		emailClientContainer = new BorderPane();

		// Create top bar with welcome message and logout
		HBox topBar = createTopBar();
		emailClientContainer.setTop(topBar);

		// Create main tab pane
		mainTabPane = new TabPane();
		mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		// Create tabs
		Tab inboxTab = createInboxTab();
		Tab sentTab = createSentTab();
		Tab composeTab = createComposeTab();

		mainTabPane.getTabs().addAll(inboxTab, sentTab, composeTab);

		emailClientContainer.setCenter(mainTabPane);

		mainLayout.setCenter(emailClientContainer);

		// Load inbox initially
		refreshInbox();
	}

	private HBox createTopBar() {
		HBox topBar = new HBox();
		topBar.setPadding(new Insets(10, 20, 10, 20));
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setStyle("-fx-background-color: #34495e;");

		welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
		welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Button refreshButton = new Button("Refresh");
		refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
		refreshButton.setOnAction(e -> refreshCurrentTab());

		Button logoutButton = new Button("Logout");
		logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
		logoutButton.setOnAction(e -> handleLogout());

		topBar.getChildren().addAll(welcomeLabel, spacer, refreshButton, logoutButton);
		return topBar;
	}

	private Tab createInboxTab() {
		Tab inboxTab = new Tab("Inbox");

		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);

		// Email list
		emailListView = new ListView<>();
		emailListView.setPrefWidth(400);
		emailListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				displaySelectedEmail(newVal);
			}
		});

		// Email content area
		emailContentArea = new TextArea();
		emailContentArea.setEditable(false);
		emailContentArea.setWrapText(true);
		emailContentArea.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace;");

		splitPane.getItems().addAll(emailListView, emailContentArea);
		splitPane.setDividerPositions(0.4);

		inboxTab.setContent(splitPane);
		return inboxTab;
	}

	private Tab createSentTab() {
		Tab sentTab = new Tab("Sent");

		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);

		ListView<String> sentListView = new ListView<>();
		sentListView.setPrefWidth(400);
		sentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				displaySelectedSentEmail(newVal);
			}
		});

		TextArea sentContentArea = new TextArea();
		sentContentArea.setEditable(false);
		sentContentArea.setWrapText(true);
		sentContentArea.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace;");

		splitPane.getItems().addAll(sentListView, sentContentArea);
		splitPane.setDividerPositions(0.4);

		// Store references for refresh
		sentTab.setUserData(new Object[]{sentListView, sentContentArea});

		sentTab.setContent(splitPane);
		return sentTab;
	}

	private Tab createComposeTab() {
		Tab composeTab = new Tab("Compose");

		VBox composeContainer = new VBox(15);
		composeContainer.setPadding(new Insets(20));

		// To field
		HBox toBox = new HBox(10);
		toBox.setAlignment(Pos.CENTER_LEFT);
		Label toLabel = new Label("To:");
		toLabel.setMinWidth(60);
		toLabel.setStyle("-fx-font-weight: bold;");
		composeToField = new TextField();
		composeToField.setPromptText("Recipient username");
		HBox.setHgrow(composeToField, Priority.ALWAYS);
		toBox.getChildren().addAll(toLabel, composeToField);

		// Subject field
		HBox subjectBox = new HBox(10);
		subjectBox.setAlignment(Pos.CENTER_LEFT);
		Label subjectLabel = new Label("Subject:");
		subjectLabel.setMinWidth(60);
		subjectLabel.setStyle("-fx-font-weight: bold;");
		composeSubjectField = new TextField();
		composeSubjectField.setPromptText("Email subject");
		HBox.setHgrow(composeSubjectField, Priority.ALWAYS);
		subjectBox.getChildren().addAll(subjectLabel, composeSubjectField);

		// Body area
		Label bodyLabel = new Label("Message:");
		bodyLabel.setStyle("-fx-font-weight: bold;");
		composeBodyArea = new TextArea();
		composeBodyArea.setPromptText("Type your message here...");
		composeBodyArea.setWrapText(true);
		composeBodyArea.setPrefRowCount(15);
		VBox.setVgrow(composeBodyArea, Priority.ALWAYS);

		// Send button
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		Button sendButton = new Button("Send Email");
		sendButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
		sendButton.setOnAction(e -> handleSendEmail());

		Button clearButton = new Button("Clear");
		clearButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #95a5a6; -fx-text-fill: white;");
		clearButton.setOnAction(e -> clearComposeFields());

		buttonBox.getChildren().addAll(clearButton, sendButton);

		composeContainer.getChildren().addAll(toBox, subjectBox, bodyLabel, composeBodyArea, buttonBox);
		composeTab.setContent(composeContainer);

		return composeTab;
	}

	private void handleSendEmail() {
		String to = composeToField.getText().trim();
		String subject = composeSubjectField.getText().trim();
		String body = composeBodyArea.getText().trim();

		if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
			showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
			return;
		}

		Email email = new Email(subject, body, currentUser.getUserId());
		boolean success = emailService.sendEmail(email, to);

		if (success) {
			showAlert("Success", "Email sent successfully to " + to + "!", Alert.AlertType.INFORMATION);
			clearComposeFields();
			// Switch to sent tab and refresh
			mainTabPane.getSelectionModel().select(1);
			refreshSent();
		} else {
			showAlert("Error", "Failed to send email. Please check if recipient exists.", Alert.AlertType.ERROR);
		}
	}

	private void clearComposeFields() {
		composeToField.clear();
		composeSubjectField.clear();
		composeBodyArea.clear();
	}

	private void refreshCurrentTab() {
		int selectedIndex = mainTabPane.getSelectionModel().getSelectedIndex();
		switch (selectedIndex) {
			case 0: refreshInbox(); break;
			case 1: refreshSent(); break;
			case 2: break; // Compose tab doesn't need refresh
		}
	}

	private void refreshInbox() {
		List<Email> emails = emailService.getEmailsByRole(currentUser.getUserId(), "Receiver");
		ObservableList<String> emailItems = FXCollections.observableArrayList();

		for (Email email : emails) {
			String item = String.format("[%s] From: %s - %s",
					email.getTimestamp().toString().substring(0, 16),
					email.getSenderName() != null ? email.getSenderName() : "Unknown",
					email.getSubject());
			emailItems.add(item);
		}

		emailListView.setItems(emailItems);
	}

	private void refreshSent() {
		Tab sentTab = mainTabPane.getTabs().get(1);
		Object[] components = (Object[]) sentTab.getUserData();
		if (components != null) {
			ListView<String> sentListView = (ListView<String>) components[0];

			List<Email> emails = emailService.getEmailsByRole(currentUser.getUserId(), "Sender");
			ObservableList<String> emailItems = FXCollections.observableArrayList();

			for (Email email : emails) {
				String item = String.format("[%s] To: %s - %s",
						email.getTimestamp().toString().substring(0, 16),
						email.getReceiverName() != null ? email.getReceiverName() : "Unknown",
						email.getSubject());
				emailItems.add(item);
			}

			sentListView.setItems(emailItems);
		}
	}

	private void displaySelectedEmail(String selectedItem) {
		// Parse the selected item to get email details
		List<Email> emails = emailService.getEmailsByRole(currentUser.getUserId(), "Receiver");
		if (!emails.isEmpty()) {
			int index = emailListView.getSelectionModel().getSelectedIndex();
			if (index >= 0 && index < emails.size()) {
				Email email = emails.get(index);
				String content = String.format(
						"From: %s\nTo: %s\nSubject: %s\nDate: %s\n\n%s",
						email.getSenderName() != null ? email.getSenderName() : "Unknown",
						currentUser.getName(),
						email.getSubject(),
						email.getTimestamp(),
						email.getBody()
				);
				emailContentArea.setText(content);
			}
		}
	}

	private void displaySelectedSentEmail(String selectedItem) {
		Tab sentTab = mainTabPane.getTabs().get(1);
		Object[] components = (Object[]) sentTab.getUserData();
		if (components != null) {
			ListView<String> sentListView = (ListView<String>) components[0];
			TextArea sentContentArea = (TextArea) components[1];

			List<Email> emails = emailService.getEmailsByRole(currentUser.getUserId(), "Sender");
			if (!emails.isEmpty()) {
				int index = sentListView.getSelectionModel().getSelectedIndex();
				if (index >= 0 && index < emails.size()) {
					Email email = emails.get(index);
					String content = String.format(
							"From: %s\nTo: %s\nSubject: %s\nDate: %s\n\n%s",
							currentUser.getName(),
							email.getReceiverName() != null ? email.getReceiverName() : "Unknown",
							email.getSubject(),
							email.getTimestamp(),
							email.getBody()
					);
					sentContentArea.setText(content);
				}
			}
		}
	}

	private void handleLogout() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Logout");
		alert.setHeaderText("Are you sure you want to logout?");
		alert.setContentText("You will be returned to the login screen.");

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				currentUser = null;
				clearLoginFields();
				showLoginScreen();
			}
		});
	}

	private void clearLoginFields() {
		loginUsername.clear();
		loginPassword.clear();
		statusLabel.setText("");
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}