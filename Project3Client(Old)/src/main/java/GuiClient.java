import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import java.util.List;

public class GuiClient extends Application {

	TextField messageInput;
	Button sendButton;
	Button changeUsernameButton;
	Button viewUsersButton;
	ListView<String> messagesListView;
	ListView<String> usersListView;

	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;

	String username = "";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data -> {
			Platform.runLater(() -> {
				if (data instanceof Message) {
					handleMessage((Message) data);
				} else if (data instanceof String) {
					messagesListView.getItems().add((String) data);
				}
			});
		});

		clientConnection.start();

		messagesListView = new ListView<>();
		usersListView = new ListView<>();

		messageInput = new TextField();
		sendButton = new Button("Send");
		sendButton.setOnAction(e -> sendMessage());

		changeUsernameButton = new Button("Change Username");
		changeUsernameButton.setOnAction(e -> changeUsername());

		viewUsersButton = new Button("View Users");
		viewUsersButton.setOnAction(e -> viewUsers());

		VBox inputBox = new VBox(10, messageInput, sendButton, changeUsernameButton, viewUsersButton);
		VBox mainBox = new VBox(10, inputBox, messagesListView, usersListView);
		mainBox.setPadding(new javafx.geometry.Insets(10));

		sceneMap = new HashMap<>();
		sceneMap.put("client", createClientGui(mainBox));

		primaryStage.setOnCloseRequest(t -> {
			Platform.exit();
			System.exit(0);
		});

		primaryStage.setScene(sceneMap.get("client"));
		primaryStage.setTitle("Client");
		primaryStage.show();

		// Initially, prompt user for a username
		changeUsername();
	}

	private Scene createClientGui(VBox content) {
		clientBox = new VBox(content);
		clientBox.setStyle("-fx-background-color: blue;" + "-fx-font-family: 'serif';");
		return new Scene(clientBox, 400, 300);
	}

	private void sendMessage() {
		String messageContent = messageInput.getText().trim();
		if (!messageContent.isEmpty()) {
			Message message;
			if (usersListView.getSelectionModel().getSelectedItems().size() == 1) {
				String recipient = usersListView.getSelectionModel().getSelectedItem();
				message = new Message(Message.MessageType.SEND_TO_INDIVIDUAL, username, messageContent, recipient);
			} else {
				message = new Message(Message.MessageType.SEND_TO_ALL, username, messageContent);
			}
			clientConnection.send(message);
			messageInput.clear();
		}
	}

	private String formatMessage(Message message) {
		switch (message.getType()) {
			case SEND_TO_ALL:
				return message.getSender() + ": " + message.getContent();
			case SEND_TO_INDIVIDUAL:
				return message.getSender() + " (To " + message.getTargetUser() + "): " + message.getContent();
			default:
				return "User connecting!";//when I returned "" there was just two empty new lines
				//original return statement below in case this breaks anything
				//return message.toString();
		}
	}

	private void changeUsername() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Change Username");
		dialog.setHeaderText("Enter Your Username");
		dialog.setContentText("Username:");

		dialog.showAndWait().ifPresent(newUsername -> {
			Message message = new Message(Message.MessageType.CREATE_USER, username, newUsername);
			clientConnection.send(message);
			username = newUsername;
		});
	}

	private void viewUsers() {
		Message message = new Message(Message.MessageType.GET_CONNECTED_USERS, username, "");
		clientConnection.send(message);
	}

	private void handleMessage(Message message) {
		switch (message.getType()) {
			case GET_CONNECTED_USERS:
				updateConnectedUsers(message.getRecipients());
				break;
			case GET_MESSAGES:
				updateMessages(message.getContent());
				break;
			default:
				// For other message types, add them to the messages list
				messagesListView.getItems().add(formatMessage(message));
				break;
		}
	}

	private void updateConnectedUsers(List<String> userList) {
		// Clear and update the connected users list
		usersListView.getItems().clear();
		usersListView.getItems().addAll(userList);
	}

	private void updateMessages(String messages) {
		// Clear and update the messages list
		messagesListView.getItems().clear();
		messagesListView.getItems().addAll(messages.split("\n"));
	}
}
