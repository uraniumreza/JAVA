import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;

public class Server extends JFrame {
	private JPanel contentPane;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private JTextField userText;
	private JTextArea chatWindow;
	private JScrollPane scrollPane;

	/**
	 * Create the frame.
	 */
	public Server() {
		super("Q-Messanger: SERVER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		userText = new JTextField();
		userText.setFont(new Font("CMU Typewriter Text", Font.PLAIN, 20));
		userText.setBackground(Color.WHITE);
		userText.setBounds(21, 203, 390, 47);
		contentPane.add(userText);
		userText.setColumns(10);
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					try {
						sendMessage(event.getActionCommand());
					} catch (IOException ioException) {
						// TODO Auto-generated catch block
						ioException.printStackTrace();
					}
					userText.setText("");
				}
			}
		);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 11, 390, 181);
		contentPane.add(scrollPane);
		
		chatWindow = new JTextArea();
		scrollPane.setViewportView(chatWindow);
		chatWindow.setTabSize(4);
		chatWindow.setFont(new Font("cmr10", Font.PLAIN, 15));
		chatWindow.setEditable(false);
		chatWindow.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
	}
	
	
	public void startRunning(){
		try {
			server = new ServerSocket(6789, 100);
			while(true){
				try {
					// connect and have converstaion
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					// TODO: handle exception
					showMessage("\n Server ended the connection! ");
				}finally{
					closeCrap();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}


	// wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect.... \n");
		connection = server.accept();
		showMessage("Now connected to "+connection.getInetAddress().getHostName());
	}
	
	
	//get stream to send and recieve data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now good to go! \n");
	}
	
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		String message = "You are now Connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			// have a conversation
			try {
				message = (String) input.readObject();
				showMessage("\n"+message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n IDK WTF that user sent! :| \n");
			}
		}while(!message.equals("CLIENT: END"));
	}
	
	
	//Turn the text field editable
	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(b);
				}
			}
		);
	}


	//Closing All the streams and sockets after you are done chatting
	private void closeCrap() {
		showMessage("\n Closing Connections... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	
	//Send message to client
	private void sendMessage(String message) throws IOException{
		try {
			output.writeObject("SERVER: " + message);
			output.flush();
			showMessage("\nSERVER: " + message);
		} catch (IOException ioException) {
			chatWindow.append("\nERROR: DUDE I CAN'T SEND THAT MESSAGE!!");
		}
	}

	
	//Show message on the text area
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
}
