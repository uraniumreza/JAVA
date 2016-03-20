import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

@SuppressWarnings("serial")
public class Clientt extends JFrame {
	private String[] onlineAnonymousUsers = new String[100];
	private String[] friendRequests = new String[100];
	@SuppressWarnings("unused")
	private String[] allFriends = new String[100];
	private String[] onlineFriends = new String[100];
	int noOfOnlineFriends = 0;
	int noOfAllFriends = 0;
	int noOfFriendRequests = 0;
	int noOfAnonymousUsers = 0;
	private String name;
	private JPanel contentPane;
	private static BufferedReader br = null;
	private static PrintWriter pr = null;
	private Socket connection;
	private JTextField userText;
	private JTextArea chatWindow;
	private String message = null;
	private String serverIP;
	private JScrollPane scrollPane;
	private JTextField userName;
	private JPasswordField password;
	private JTextField signUpNam;
	private JPasswordField signUpPass;
	private JButton logIn;
	private JButton signUp;
	private JButton logOut;
	private JList<String> list_2;
	private JButton btnSendFriendrequest;
	private JButton btnRefresh;
	private JButton button_2;
	private JButton button;
	private JButton btnReject;
	private JButton btnAccept;
	private JButton btnSendMsg;
	private JList<String> list;
	private JList<String> list_3;
	private JList<String> list_1;
	DefaultListModel<String> anonymous = new DefaultListModel<String>();
	DefaultListModel<String> frndRqst = new DefaultListModel<String>();
	DefaultListModel<String> onlineFrnds = new DefaultListModel<String>();

	/**
	 * Create the frame.
	 */
	public Clientt(String host) {
		super("Q-Messanger: CLIENT");
		serverIP = host;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 663, 671);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		userText = new JTextField();
		userText.setFont(new Font("CMU Typewriter Text", Font.PLAIN, 20));
		userText.setBackground(Color.WHITE);
		userText.setBounds(9, 218, 402, 32);
		contentPane.add(userText);
		userText.setColumns(10);
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					sendMessage(event.getActionCommand(), '1');
				} catch (IOException ioException) {
					// TODO Auto-generated catch block
					ioException.printStackTrace();
				}
				userText.setText("");
			}
		});

		scrollPane = new JScrollPane();
		scrollPane.setBounds(9, 26, 402, 181);
		contentPane.add(scrollPane);

		chatWindow = new JTextArea();
		scrollPane.setViewportView(chatWindow);
		chatWindow.setTabSize(4);
		chatWindow.setFont(new Font("cmr10", Font.PLAIN, 15));
		chatWindow.setEditable(false);
		chatWindow.setBackground(Color.LIGHT_GRAY);

		userName = new JTextField();
		userName.setEditable(true);
		userName.setBounds(503, 542, 134, 20);
		contentPane.add(userName);
		userName.setColumns(10);

		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(432, 545, 74, 14);
		contentPane.add(lblUserName);

		password = new JPasswordField();
		password.setEditable(true);
		password.setBounds(503, 570, 134, 20);
		contentPane.add(password);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(432, 573, 74, 14);
		contentPane.add(lblPassword);

		logIn = new JButton("LOG IN");
		logIn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessage(userName.getText() + " " + password.getText(), 'l');
					System.out.println("LOG IN BUTTON");
				} catch (IOException e1) {
					System.out.println("Cannot LOG IN");
				}
			}
		});
		logIn.setBounds(540, 599, 97, 23);
		contentPane.add(logIn);

		JLabel label = new JLabel("User Name:");
		label.setBounds(217, 545, 74, 14);
		contentPane.add(label);

		signUpNam = new JTextField();
		signUpNam.setColumns(10);
		signUpNam.setBounds(288, 542, 134, 20);
		contentPane.add(signUpNam);

		JLabel label_1 = new JLabel("Password:");
		label_1.setBounds(217, 573, 74, 14);
		contentPane.add(label_1);

		signUpPass = new JPasswordField();
		signUpPass.setBounds(288, 570, 134, 20);
		contentPane.add(signUpPass);

		signUp = new JButton("SIGN UP");
		signUp.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessage(signUpNam.getText() + " " + signUpPass.getText(), 's');
					System.out.println("SIGN UP BUTTON");
				} catch (IOException e1) {
					System.out.println("Cannot SIGN UP");
				}
			}
		});
		signUp.setBounds(217, 599, 205, 23);
		contentPane.add(signUp);

		logOut = new JButton("LOG OUT");
		logOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeCrap();
			}
		});
		logOut.setBounds(432, 599, 97, 23);
		logOut.setEnabled(false);
		contentPane.add(logOut);

		list = new JList();
		list.setEnabled(false);
		list.setBackground(SystemColor.activeCaption);
		list.setBounds(431, 276, 206, 197);
		contentPane.add(list);

		JLabel lblFriends = new JLabel("ALL FRIENDS");
		lblFriends.setFont(new Font("cmr10", Font.PLAIN, 15));
		lblFriends.setBounds(484, 260, 153, 14);
		contentPane.add(lblFriends);

		list_1 = new JList(onlineFrnds);
		list_1.setFont(new Font("cmr10", Font.PLAIN, 15));
		list_1.setEnabled(false);
		list_1.setBackground(SystemColor.activeCaption);
		list_1.setBounds(431, 25, 206, 181);
		contentPane.add(list_1);

		JLabel lblActiveFriends = new JLabel("ONLINE   FRIENDS");
		lblActiveFriends.setFont(new Font("cmr10", Font.PLAIN, 15));
		lblActiveFriends.setBounds(457, 11, 180, 14);
		contentPane.add(lblActiveFriends);

		btnSendMsg = new JButton("SEND MSG");
		btnSendMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					sendMessage("|"+list_1.getSelectedValue().toUpperCase()+" "+userText.getText(), '1');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSendMsg.setEnabled(false);
		btnSendMsg.setBounds(431, 217, 97, 32);
		contentPane.add(btnSendMsg);

		JButton button_1 = new JButton("SEND FILE");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button_1.setEnabled(false);
		button_1.setBounds(540, 217, 96, 32);
		contentPane.add(button_1);

		list_2 = new JList(anonymous);
		list_2.setEnabled(false);
		list_2.setFont(new Font("cmr10", Font.PLAIN, 14));
		list_2.setValueIsAdjusting(true);
		list_2.setBackground(SystemColor.inactiveCaption);
		list_2.setBounds(9, 276, 196, 305);
		contentPane.add(list_2);

		btnSendFriendrequest = new JButton("REQUEST");
		btnSendFriendrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					pr.println("[ " + list_2.getSelectedValue());
					pr.flush();
					System.out.println("Send Friend Request Button Pressed!");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btnSendFriendrequest.setEnabled(false);
		btnSendFriendrequest.setBounds(9, 590, 89, 32);
		contentPane.add(btnSendFriendrequest);

		list_3 = new JList(frndRqst);
		list_3.setFont(new Font("cmr10", Font.PLAIN, 15));
		list_3.setEnabled(false);
		list_3.setBackground(SystemColor.inactiveCaption);
		list_3.setBounds(215, 276, 196, 197);
		contentPane.add(list_3);

		btnAccept = new JButton("ACCEPT");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					pr.println("[AC]");
					pr.flush();
					pr.println(list_3.getSelectedValue());
					pr.flush();

					int selectedIndex = list_3.getSelectedIndex();
					if (selectedIndex != -1) {
						frndRqst.remove(selectedIndex);
						friendRequests[selectedIndex] = null;
					}
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		});
		btnAccept.setEnabled(false);
		btnAccept.setBounds(314, 481, 97, 32);
		contentPane.add(btnAccept);

		JSeparator separator = new JSeparator();
		separator.setBounds(431, 524, 226, 2);
		contentPane.add(separator);

		btnReject = new JButton("REJECT");
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int selectedIndex = list_3.getSelectedIndex();
					if (selectedIndex != -1) {
						frndRqst.remove(selectedIndex);
						friendRequests[selectedIndex] = null;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btnReject.setEnabled(false);
		btnReject.setBounds(215, 481, 97, 32);
		contentPane.add(btnReject);

		JLabel lblAnonymousUsers = new JLabel("ANONYMOUS USERS");
		lblAnonymousUsers.setFont(new Font("cmr10", Font.PLAIN, 15));
		lblAnonymousUsers.setBounds(31, 261, 174, 14);
		contentPane.add(lblAnonymousUsers);

		JLabel lblFriendRequests = new JLabel("FRIEND REQUESTS");
		lblFriendRequests.setFont(new Font("cmr10", Font.PLAIN, 15));
		lblFriendRequests.setBounds(235, 261, 174, 14);
		contentPane.add(lblFriendRequests);

		JLabel lblMessageBox = new JLabel("MESSAGE BOX");
		lblMessageBox.setFont(new Font("cmr10", Font.PLAIN, 15));
		lblMessageBox.setBounds(155, 11, 121, 14);
		contentPane.add(lblMessageBox);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(215, 524, 226, 2);
		contentPane.add(separator_2);

		button = new JButton("SEND MSG");
		button.setEnabled(false);
		button.setBounds(431, 481, 97, 32);
		contentPane.add(button);

		button_2 = new JButton("SEND FILE");
		button_2.setEnabled(false);
		button_2.setBounds(540, 481, 96, 32);
		contentPane.add(button_2);

		btnRefresh = new JButton("REFRESH");
		btnRefresh.setEnabled(false);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					pr.println(">>");
					pr.flush();
					System.out.println("Refresh Button Pressed!");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btnRefresh.setBounds(118, 590, 89, 32);
		contentPane.add(btnRefresh);

		setVisible(true);
	}

	public static void main(String[] args) {
		Clientt C = new Clientt("127.0.0.1");
		C.startRunning();
	}

	public void startRunning() {
		try {
			// connect and have converstaion
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException eofException) {
			// TODO: handle exception
			showMessage("\n Client ended the connection! ");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeCrap();
		}
	}

	// wait for connection, then display connection information
	private void connectToServer() throws IOException {
		showMessage("Attempting Connection.... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 5555);
		showMessage("Conneccted to: " + connection.getInetAddress().getHostName());
	}

	// get stream to send and recieve data
	private void setupStreams() throws IOException {
		br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		pr = new PrintWriter(connection.getOutputStream());
		showMessage("\n Streams are now good to go! \n");
	}

	// during the chat conversation
	private void whileChatting() throws IOException {
		message = "You are now Connected! ";
		sendMessage(message, '1');
		do {
			message = (String) br.readLine();
			if (message == null)
				continue;
			// message = "Ami message dekhate pari na keno!";
			System.out.println("paichi mamma: " + message);
			
			if(message.indexOf("|") != -1){
				String temp = message.substring(1);
				temp = "\n" + temp;
				showMessage(temp);
			} else if (message.equals("$APPROVED$")) {
				name = (String) userName.getText();
				ableToType(true);
				logInEditable(false);
				signUpEditable(false);
				showMessage("\n" + message + " " + userName.getText());
			} else if (message.equals("$WRONG Username or Password$")) {
				userName.setText("");
				password.setText("");
				showMessage("\n" + message);
			} else if (message.equals("%REGISTERED%")) {
				showMessage("\n" + message);
			} else if (message.equals("<<")) {
				noOfAnonymousUsers = 0;
				noOfOnlineFriends = 0;
				while (true) {
					message = br.readLine();
					if (message != null) {
						try {
							if (message.equals(">>"))
								break;
							System.out.println("-----------------" + message);
							String arr[] = message.split(" ", 2);
							System.out.println("+++++++++++++++++" + arr[0]);
							if (!arr[0].equals(name)) {
								if (arr[1].equals("0")) {
									onlineAnonymousUsers[noOfAnonymousUsers++] = arr[0];
								} else {
									System.out.println("+++++ ONLINE FRIENDS +++++++" + arr[0]);
									onlineFriends[noOfOnlineFriends++] = arr[0];
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (noOfAnonymousUsers > 0)
					btnSendFriendrequest.setEnabled(true);
				else
					btnSendFriendrequest.setEnabled(false);

				if (noOfOnlineFriends > 0) {
					list_1.setEnabled(true);
					btnSendMsg.setEnabled(true);
					button_2.setEnabled(true);
				} else {
					list_1.setEnabled(false);
					btnSendMsg.setEnabled(false);
					button_2.setEnabled(false);
				}

				anonymous.removeAllElements();
				for (int j = 0; j < noOfAnonymousUsers; j++) {
					anonymous.addElement(onlineAnonymousUsers[j]);
				}

				onlineFrnds.removeAllElements();
				for (int j = 0; j < noOfOnlineFriends; j++) {
					onlineFrnds.addElement(onlineFriends[j]);
				}

				frndRqst.removeAllElements();
				for (int j = 0; j < noOfFriendRequests; j++) {
					if (friendRequests[j] != null)
						frndRqst.addElement(friendRequests[j]);
				}
				if (frndRqst.isEmpty()) {
					btnAccept.setEnabled(false);
					btnReject.setEnabled(false);
				} else {
					btnAccept.setEnabled(true);
					btnReject.setEnabled(true);
				}
			}

			else if (message.indexOf("[") != -1) {
				System.out.println("CLIENT: Paichi Request...");
				showMessage(" " + message.substring(1));
				friendRequests[noOfFriendRequests] = message.substring(1);
				noOfFriendRequests++;
			}

			else if (message.indexOf("@") != -1) {
				if (!message.equals("@")) {
					System.out.println(name);
					System.out.println(message.substring(1));
					if (!name.equals(message.substring(1))) {
						onlineAnonymousUsers[noOfAnonymousUsers++] = message.substring(1);
						anonymous.addElement(message.substring(1));
					}
				}
			} else if (message != null)
				showMessage("\n" + message);
		} while (message!=null && !message.equals("SERVER: END"));
	}

	// Turn the text field editable
	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(b);
			}
		});
	}

	// SIGN UP table editable or not!
	public void signUpEditable(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				signUpNam.setEditable(b);
				signUpPass.setEditable(b);
				signUp.setEnabled(b);
				signUp.setText("You Are Registered...");
				System.out.println("DONE");
			}
		});
	}

	// LOG IN table editable or not!
	public void logInEditable(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userName.setEditable(b);
				password.setEditable(b);
				logOut.setEnabled(true);
				list_2.setEnabled(true);
				btnRefresh.setEnabled(true);
				list_3.setEnabled(true);
				list_1.setEnabled(true);
				btnAccept.setEnabled(true);
				btnReject.setEnabled(true);
			}
		});
	}

	// Closing All the streams and sockets after you are done chatting
	private void closeCrap() {
		showMessage("\n Closing Connections... \n");
		ableToType(false);
		try {
			pr.close();
			br.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// Send message to Server
	private void sendMessage(String message, char mode) throws IOException {
		if (mode == 's') {
			pr.println("CLIENT: #" + message+"\n");
			pr.flush();
		} else if (mode == 'l') {
			pr.println("CLIENT: $" + message+"\n");
			pr.flush();
		} else {
			pr.println(message);
			pr.flush();
			if(name == null) showMessage("\nCLIENT: " + message);
			else showMessage("\n"+ name.toUpperCase() + message);
		}
	}

	// Show message on the text area
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}
}
