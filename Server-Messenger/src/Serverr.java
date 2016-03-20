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

import org.omg.CORBA.PUBLIC_MEMBER;

import java.awt.Color;
import java.awt.Font;

public class Serverr extends JFrame {
	private Socket connection;
	private ReadFile registeredUsers = new ReadFile();
	public static int workerThreadCount = 0;
	/**
	 * Create the frame.
	 */
	public Serverr() {
		
	}
	
	
	public static void main(String[] args) {
		int id = 1;
		Serverr S = new Serverr();
		ServerSocket server;
		try {
			while(true){
				server = new ServerSocket(6789, 100);
				// wait for connection, then display connection information
				S.connection = server.accept();
				WorkerThread wt = new WorkerThread(S.connection, id);
				Thread t = new Thread(wt);
				t.start();
				workerThreadCount++;
				System.out.println("Client ["+id+"]"+" is now connected to "+S.connection.getInetAddress().getHostName());
				id++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


class WorkerThread extends Serverr implements Runnable{
	public JPanel contentPane;
	public JTextArea chatWindow;
	public JScrollPane scrollPane;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private ReadFile registeredUsers = new ReadFile();
	
	private int id = 0;
	
	public WorkerThread(Socket connection, int id)
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 11, 390, 239);
		contentPane.add(scrollPane);
		
		chatWindow = new JTextArea();
		scrollPane.setViewportView(chatWindow);
		chatWindow.setTabSize(4);
		chatWindow.setFont(new Font("cmr10", Font.PLAIN, 15));
		chatWindow.setEditable(false);
		chatWindow.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
		
		
		
		this.connection = connection;
		
		try
		{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("\n Streams are now good to go! \n");
		}
		catch(Exception e)
		{
			System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
		}
		
		this.id = id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
				try {
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
		boolean first = false;
		do{
			// have a conversation
			try {
				message = (String) input.readObject();
				if(first == false){
					if(message.indexOf('#')!=-1){
						registeredUsers.writeFile(message.substring(9));
						sendMessage("%REGISTERED%");
						showMessage("\n"+message);
					}
					else if(message.indexOf('$')!=-1){
						if(registeredUsers.readFile(message.substring(9))==1){
							sendMessage("$APPROVED$");
							first = true;
						}
						else sendMessage("$WRONG Username or Password$");
					}
				}
				else showMessage("\n"+message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n IDK WTF that user sent! :| \n");
			}
		}while(!message.equals("CLIENT: END"));
	}


	//Closing All the streams and sockets after you are done chatting
	private void closeCrap() {
		showMessage("\n Closing Connections... \n");
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
			if(message.equals("$APPROVED$") || message.equals("$WRONG Username or Password$") || message.equals("$REGISTERED$")) output.writeObject(message);
			else output.writeObject("SERVER: " + message);
			output.flush();
			showMessage("\nSERVER: " + message);
		} catch (IOException ioException) {
			chatWindow.append("\nERROR: DUDE I CAN'T SEND THAT MESSAGE!!");
		}
	}

	
	//Show message on the text area
	public void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}	
}