import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;

	Server(Consumer<Serializable> call) {
		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread {

		public void run() {

			try (ServerSocket mysocket = new ServerSocket(5555)) {
				callback.accept("Server is waiting for a client!");

				while (true) {
					Socket socket = mysocket.accept();
					ClientThread c = new ClientThread(socket, count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;
				}
			} catch (IOException e) {
				callback.accept("Server socket did not launch");
			}
		}
	}

	class ClientThread extends Thread {

		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);
			} catch (IOException e) {
				callback.accept("Streams not open");
			}
		}

		public void updateClients(Message message) {
			for (ClientThread client : clients) {
				try {
					client.out.writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void run() {

			updateClients(new Message(Message.MessageType.CONNECTED, "Server", "client #" + count + " connected"));

			while (true) {
				try {
					Message data = (Message) in.readObject();
					callback.accept("client: " + count + " sent: " + data);
					updateClients(data);

				} catch (IOException | ClassNotFoundException e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count
							+ "....closing down!");
					updateClients(new Message(Message.MessageType.DISCONNECTED, "Server", "Client #" + count + " has left the server!"));
					clients.remove(this);
					break;
				}
			}
		}
	}
}
