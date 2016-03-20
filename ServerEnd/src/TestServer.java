import java.io.*;
import java.net.*;

public class TestServer {
	public static int workerThreadCount = 0;
	public static String[] onlineUsers = new String[100];
	public static Socket[] s = new Socket[100];
	private static ServerSocket ss;

	public static void main(String args[]) {
		int id = 0;
		for (int i = 0; i < 100; i++) {
			onlineUsers[i] = null;
		}

		try {
			ss = new ServerSocket(5555);
			System.out.println("Server has been started successfully.");

			while (true) {
				s[id] = ss.accept(); // TCP Connection
				WorkerThread wt = new WorkerThread(s[id], id);
				Thread t = new Thread(wt);
				t.start();
				workerThreadCount++;
				System.out
						.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
				id++;
			}
		} catch (Exception e) {
			System.err.println("Problem in ServerSocket operation. Exiting main.");
		}
	}
}

class WorkerThread implements Runnable {
	private Socket socket;
	private InputStream is;
	private OutputStream os;

	private int id = 0;

	public WorkerThread(Socket s, int id) {
		this.socket = s;

		try {
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		} catch (Exception e) {
			System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
		}

		this.id = id;
	}

	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
		PrintWriter pr = new PrintWriter(this.os);
		ReadFile registeredUsers = new ReadFile("registeredUsers");

		pr.println("Your id is: " + this.id);
		pr.flush();

		String str;
		boolean loggedIn = false;
		while (true) {
			try {
				if ((str = br.readLine()) != null) {
					if (loggedIn == false) {
						if (str.indexOf('#') != -1) {
							registeredUsers.writeFile(str.substring(9));

							pr.println("%REGISTERED%");
							pr.flush();
							System.out.println("\n" + str);

							String temp = str.substring(9);
							String arr[] = temp.split(" ", 2);
							String temp0 = new String();
							temp0 = "C:\\Users\\Nayeem Reza\\Documents\\JAVA\\Server-Messenger\\src\\" + arr[0]
									+ ".txt";
							File file = new File(temp0);
							file.createNewFile();
						} else if (str.indexOf('$') != -1) {
							System.out.println("LOGGING IN::");
							System.out.println(str.substring(9));
							if(str.substring(9)==null) continue;
							if (registeredUsers.readFile(str.substring(9)) == 1) {
								pr.println("$APPROVED$");
								pr.flush();

								System.out.println("Dhukchi: " + str.indexOf('$'));
								String temp = str.substring(9);
								String arr[] = temp.split(" ", 2);
								if (arr[0] != null)
									TestServer.onlineUsers[id] = arr[0];
								
								ReadFile r = new ReadFile();
								String temp2 = new String();
								temp2 = r.offlineMessageRead(TestServer.onlineUsers[id]);
								pr.println("|" + temp2);
								pr.flush();
								
								
								for (int i = 0; i <= id; i++) {
									if (TestServer.onlineUsers[i] != null) {
										pr.println("@" + TestServer.onlineUsers[i]);
										pr.flush();
									}
								}
								
								pr.println("@LL");
								pr.flush();
								
								ReadFile reader = new ReadFile(TestServer.onlineUsers[id]);
								reader.allFriends();
								for(int i=0;i<reader.noOfFriends;i++){
									if (reader.allFriends[i] != null) {
										pr.println(reader.allFriends[i]);
										pr.flush();
									}
								}
								
								pr.println("WLL");
								pr.flush();
								
								loggedIn = true;
							} else {
								pr.println("$WRONG Username or Password$");
								pr.flush();
							}
						}
					}
					if (str.equals("[AC]")) {
						str = br.readLine();
						ReadFile frnds1 = new ReadFile(TestServer.onlineUsers[id]);
						frnds1.addFriend(str);
						ReadFile frnds2 = new ReadFile(str);
						frnds2.addFriend(TestServer.onlineUsers[id]);
						
						pr.println("@LL");
						pr.flush();
						
						ReadFile reader = new ReadFile(TestServer.onlineUsers[id]);
						reader.allFriends();
						for(int i=0;i<reader.noOfFriends;i++){
							if (reader.allFriends[i] != null) {
								pr.println(reader.allFriends[i]);
								pr.flush();
							}
						}
						
						pr.println("WLL");
						pr.flush();
					}
					if(str.indexOf("}") != -1){
						String temp = str.substring(1);
						ReadFile r = new ReadFile();
						r.offlineMessageWrite(temp);
						pr.println("{");
						pr.flush();
					}
					if(str.indexOf("|") != -1){
						boolean sent = false;
						String temp = str.substring(1);
						String arr[] = temp.split(" ", 2);
						System.out.println("=======CLIENT: Online message pathaichi...");
						for (int i = 0; i <= TestServer.workerThreadCount; i++) {
							System.out.println("+++++CLIENT: Online message pathaichi...");
							if (TestServer.onlineUsers[i] == null)
								continue;
							System.out.println(TestServer.onlineUsers[i]);
							System.out.println(">_< "+arr[0]);
							if (arr[0].equals(TestServer.onlineUsers[i].toUpperCase())) {
								OutputStream osN = TestServer.s[i].getOutputStream();
								PrintWriter prN = new PrintWriter(osN);
								prN.println("|"+TestServer.onlineUsers[id]+" :"+arr[1]);
								prN.flush();
								System.out.println("CLIENT: Online message pathaichi...");
								sent = true;
							}
						}
						if(sent == false){
							System.out.println("CLIENT: Offline message pathaichi...");
						}
					}
					if (str.indexOf("[") != -1) {
						String temp = str.substring(2);
						System.out.println("Thank You: " + temp);
						for (int i = 0; i <= TestServer.workerThreadCount; i++) {
							if (TestServer.onlineUsers[i] == null)
								continue;
							System.out.println(TestServer.onlineUsers[i]);
							if (temp.equals(TestServer.onlineUsers[i])) {
								OutputStream osN = TestServer.s[i].getOutputStream();
								PrintWriter prN = new PrintWriter(osN);
								prN.println("[" + TestServer.onlineUsers[id]);
								prN.flush();
								System.out.println("CLIENT: Friend Request pathaichi...");
							}
						}
					}
					if (str.equals(">>")) {
						System.out.println("Sendeing You the new List!");
						pr.println("<<");
						pr.flush();
						for (int i = 0; i <= TestServer.workerThreadCount; i++) {
							if (TestServer.onlineUsers[i] != null) {
								ReadFile reader = new ReadFile(TestServer.onlineUsers[id]);
								int p = reader.checkIfFriendOrNot(TestServer.onlineUsers[i]);
								String temp0;
								System.out.println("FRIENDSHIP STATUS: " + p);
								if(p==1){
									temp0 = TestServer.onlineUsers[i] + " " + 1;
									System.out.println("Pathaichi WTF:: " + temp0);
									pr.println(temp0);
									pr.flush();
								}
								else{
									temp0 = TestServer.onlineUsers[i] + " " + 0;
									System.out.println("Pathaichi WTF:: " + temp0);
									pr.println(temp0);
									pr.flush();
								}
							}
						}
						pr.println(">>");
						pr.flush();
					} else {
						System.out.println("\n" + str);
					}

					if (str.equals("BYE")) {
						System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
						break; // terminate the loop; it will terminate the
								// thread also
					}

					else if (str.equals("DL")) {
						try {
							File file = new File("capture.jpg");
							FileInputStream fis = new FileInputStream(file);
							@SuppressWarnings("resource")
							BufferedInputStream bis = new BufferedInputStream(fis);
							OutputStream os = socket.getOutputStream();
							byte[] contents;
							long fileLength = file.length();
							pr.println(String.valueOf(fileLength)); // These two
																	// lines are
																	// used
							pr.flush(); // to send the file size in bytes.

							long current = 0;

							@SuppressWarnings("unused")
							long start = System.nanoTime();
							while (current != fileLength) {
								int size = 10000;
								if (fileLength - current >= size)
									current += size;
								else {
									size = (int) (fileLength - current);
									current = fileLength;
								}
								contents = new byte[size];
								bis.read(contents, 0, size);
								os.write(contents);
								// System.out.println("Sending file ...
								// "+(current*100)/fileLength+"% complete!");
							}
							os.flush();
							System.out.println("File sent successfully!");
						} catch (Exception e) {
							System.err.println("Could not transfer file.");
						}
						pr.println("Downloaded.");
						pr.flush();

					} else {
						System.out.println("[" + id + "] says: " + str);
					}
				} else {
					for (int i = 0; i <= TestServer.workerThreadCount; i++) {
						ReadFile update = new ReadFile(TestServer.onlineUsers[i]);
						update.markOnlineOffline(TestServer.onlineUsers[id], 0);
					}
					System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
					TestServer.onlineUsers[id] = "";
					break;
				}
			} catch (Exception e) {
				System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
				break;
			}
		}

		try {
			this.is.close();
			this.os.close();
			this.socket.close();
		} catch (Exception e) {

		}

		TestServer.workerThreadCount--;
		System.out.println(
				"Client [" + id + "] is now terminating. No. of worker threads = " + TestServer.workerThreadCount);
	}
}