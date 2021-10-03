import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

class ChatConnection extends Thread {
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private PrintStream printStream;
	private BufferedReader bufferedReader;
	private String message;

	ChatConnection(Socket socket) {
		this.socket = socket;
		try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
            printStream = new PrintStream(out);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
		InetAddress ipaddr = socket.getInetAddress();
		System.out.println("Connection accepted from " + ipaddr);
	}

	public String getMessage() {
		 if (message == null)
	            return null;
		 // Prevents from continuously returning the same message
	     String temp = message;
	     message = null;
	     return temp;
	}
	
	// Sends Message
	public void println(String s) {
        printStream.println(s);
    }
	
	// Listens for messages
	public void run() {
		try {
			do {
				message = bufferedReader.readLine();
			} while (message != null);

			System.out.println("ChatListener finished");

		} catch (IOException e) {
			System.out.println("Listener Exception!!");
			System.out.println(e);
		}
	}
}