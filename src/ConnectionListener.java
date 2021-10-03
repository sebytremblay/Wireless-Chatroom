import java.util.ArrayList;

public class ConnectionListener extends Thread {
	private ArrayList<ChatConnection> connections;

	public ConnectionListener(ArrayList<ChatConnection> connections) {
		this.connections = connections;
	}
	
	public void run() {
		while (true) {
			for (int i = 0; i < connections.size(); i++) {
				ChatConnection connectionInstance = connections.get(i);

				// If connection terminated, remove from list of active connections
				if (!connectionInstance.isAlive())
					connections.remove(i);

				// Broadcast messages to everyone
				String message = connectionInstance.getMessage();
				if (message != null)
					for (ChatConnection connection : connections)
						connection.println(message); 
			}

			// Only check every 0.1s instead of constantly
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

