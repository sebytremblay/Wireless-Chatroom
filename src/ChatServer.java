import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	private ArrayList<ChatConnection> connections;
    private static int port = 8010;

    public static void main(String[] args) throws Exception {
    	ArrayList<ChatConnection> connections = new ArrayList<ChatConnection>();
        ServerSocket serverSocket = new ServerSocket(port);
        String myaddr = InetAddress.getLocalHost().getHostAddress();
        
        ConnectionListener connectionListener = new ConnectionListener(connections);

		// Listens for messages to broadcast and terminated clients
		connectionListener.start();
		System.out.println("Server listening on IP addr " + myaddr + " on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            
            ChatConnection connection = new ChatConnection(clientSocket);
			connections.add(connection);
			connection.start();
        }
    }

}