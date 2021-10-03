import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {
	// Login swing stuff
	private static JFrame loginFrame;
	private static JPanel login;
	private static JLabel loginStatus;
	private static JLabel usernameLabel;
	private static JTextField userEntry;
	private static JButton loginButton;

	// Messaging swing stuff
	private static JFrame messageFrame;
	private static JPanel messager;
	private static JTextArea textDisplay;
	private static JScrollPane textScroll;
	private static JTextField messageEntry;
	private static JButton sendMessage;

	// Networking stuff
	private static String screenName;
	private static String IP;
	private static int port = 8010;
	private static Socket socket;
	private static OutputStream out;
	private static InputStream in;
	private static PrintStream printStream;
	private static BufferedReader bufferedReader;

	// Misc.
	private static boolean connected = false;

	public ChatClient() {
		createLogin();
		createMessager();
		try {
			IP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.listen();
	}

	private static class login implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			screenName = userEntry.getText();
			// Displays error if no username is provided
			if (screenName.length() == 0) {
				loginStatus.setText("Invalid Username");
			} else {
				try {
					// Opens messaging interface if connection is available
					connect(IP, port);
					if (connected == true) {
						loginFrame.setVisible(false);
						messageFrame.setVisible(true);
						messageFrame.setTitle("Seby's Chat Client: [" + screenName + "]");
					}
				} catch (Exception e1) {
					// Displays error if server is down
					loginStatus.setText("Connection Unavailable");
				}
			}
		}
	}

	public static class send implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Prints message with name tag to stream and clears entry box
			printStream.println("[" + screenName + "]: " + messageEntry.getText());
			messageEntry.setText("");
			messageEntry.requestFocusInWindow();
		}
	}

	// Connects to provided IP and port
	public static void connect(String IP, int port) throws Exception {
		try {
			socket = new Socket(IP, port);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			printStream = new PrintStream(out);
			bufferedReader = new BufferedReader(new InputStreamReader(in));
			connected = true;
		} catch (Exception ex) {
			loginStatus.setText("Connection Unavailable");
		}
	}

	// Listen to bufferedReader and displays everything that the server broadcasts
	public void listen() {
		String s;
		// Prevents listen() from being called when connection is not established
		while(connected == false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Listens for incoming messages and displays them
		try {
			while ((s = bufferedReader.readLine()) != null) {
				textDisplay.insert(s + "\n", textDisplay.getText().length());
				textDisplay.setCaretPosition(textDisplay.getText().length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createMessager() {
		// Initializes window
		messageFrame = new JFrame();
		messager = new JPanel();

		// Defines window
		messageFrame.setSize(800, 800);
		messageFrame.setLocation(560, 140);
		messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		messageFrame.setVisible(false);
		messageFrame.add(messager);
		messager.setLayout(null);

		// Message Entry
		messageEntry = new JTextField();
		messageEntry.setBounds(10, 675, 650, 75);
		messageEntry.addActionListener(new send());
		messager.add(messageEntry);

		// Message button
		sendMessage = new JButton("Send");
		sendMessage.setBounds(660, 675, 110, 75);
		sendMessage.addActionListener(new send());
		messager.add(sendMessage);

		// Message Display
		textDisplay = new JTextArea();
		textDisplay.setEditable(false);
		textDisplay.setBackground(Color.LIGHT_GRAY);
		textDisplay.setBounds(10, 10, 755, 650);

		// Scrolling text
		textScroll = new JScrollPane(textDisplay);
		textScroll.setBounds(10, 10, 755, 650);
		messager.add(textScroll);

		// Set Close Operation
		messageFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
					out.close();
					in.close();
					connected = false;
					System.out.println("Connection Terminated");
				} catch (IOException e1) {
					System.out.println("Socket does not exist.");
				}

			}
		});
	}

	private static void createLogin() {
		// Initializes winodw
		loginFrame = new JFrame();
		login = new JPanel();

		// Defines window and inital tab
		loginFrame.setSize(300, 150);
		loginFrame.setLocation(760, 440);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.add(login);
		login.setLayout(null);

		// Username label
		usernameLabel = new JLabel("Display Name: ");
		usernameLabel.setBounds(10, 5, 100, 25);
		login.add(usernameLabel);

		// Username Entry
		userEntry = new JTextField(20);
		userEntry.setBounds(10, 30, 200, 25);
		userEntry.addActionListener(new login());
		login.add(userEntry);

		// Login Button
		loginButton = new JButton("Connect");
		loginButton.setBounds(10, 80, 200, 25);
		loginButton.addActionListener(new login());
		login.add(loginButton);

		// Login status
		loginStatus = new JLabel();
		loginStatus.setBounds(10, 55, 200, 25);
		login.add(loginStatus);

		loginFrame.setVisible(true);
	}
}