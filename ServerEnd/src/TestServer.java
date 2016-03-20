import java.io.*;
import java.net.*;

import javax.xml.transform.Templates;

public class TestServer {
	public static int workerThreadCount = 0;
	public static String[] onlineUsers = new String[100];
	public static Socket[] s = new Socket[100];

	public static void main(String args[]) {
		int id = 1;
		for (int i = 0; i < 100; i++) {
			onlineUsers[i] = null;
		}

		try {
			ServerSocket ss = new ServerSocket(5555);
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

	public void run()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
		PrintWriter pr = new PrintWriter(this.os);
		ReadFile registeredUsers = new ReadFile("registeredUsers");
		
		pr.println("Your id is: " + this.id);
		pr.flush();
		
		String str;
		boolean loggedIn = false;
		while(true)
		{
			try
			{
				if( (str = br.readLine()) != null )
				{
					if(loggedIn == false){
						if(str.indexOf('#')!=-1){
							registeredUsers.writeFile(str.substring(9));
							
							pr.println("%REGISTERED%");
							pr.flush();
							System.out.println("\n"+str);
							
							
							String temp = str.substring(9);
							String arr[] = temp.split(" ", 2);
							String temp0 = new String();
							temp0 = "C:\\Users\\Nayeem Reza\\Documents\\JAVA\\Server-Messenger\\src\\"+arr[0]+".txt";
							File file = new File(temp0);
							file.createNewFile();
						}
						else if(str.indexOf('$')!=-1){
							System.out.println(str.substring(9));
							if(registeredUsers.readFile(str.substring(9))==1){
								pr.println("$APPROVED$");
								pr.flush();
								System.out.println("Dhukchi: "+str.indexOf('$'));
								String temp = str.substring(9);
								String arr[] = temp.split(" ", 2);
								TestServer.onlineUsers[id] = arr[0];
								
								// Online Users
								
								for(int i=0;i<=id;i++){
									if(TestServer.onlineUsers[i]!=null){
										pr.println("@"+TestServer.onlineUsers[i]);
										pr.flush();
									}
								}
								loggedIn = true;
							}
							else{
								pr.println("$WRONG Username or Password$");
								pr.flush();
							}
						}
					}
					if(str.equals("[AC]")){
						str = br.readLine();
						ReadFile frnds1 = new ReadFile();
						frnds1.addFriend(TestServer.onlineUsers[id], str);
						ReadFile frnds2 = new ReadFile();
						frnds2.addFriend(str, TestServer.onlineUsers[id]);
					}
					if(str.indexOf("[")!=-1){
						String temp = str.substring(2);
						System.out.println("Thank You: "+temp);
						for(int i=0;i<=TestServer.workerThreadCount;i++){
							if(TestServer.onlineUsers[i]==null) continue;
							System.out.println(TestServer.onlineUsers[i]);
							if(temp.equals(TestServer.onlineUsers[i])){
								OutputStream osN = TestServer.s[i].getOutputStream();
								PrintWriter prN = new PrintWriter(osN);
								prN.println("["+TestServer.onlineUsers[id]);
								prN.flush();
								System.out.println("CLIENT: Friend Request pathaichi...");
							}
						}
					}
					if(str.equals(">>")){
						System.out.println("Sendeing You the new List!");
						pr.println("<<");
						pr.flush();
						for(int i=0;i<=TestServer.workerThreadCount;i++){
							if(TestServer.onlineUsers[i]!=null){
								String arr[] = TestServer.onlineUsers[i].split(" ", 2);
								pr.println(arr[0]);
								pr.flush();
							}
						}
						pr.println(">>");
						pr.flush();
					}
					else{
						System.out.println("\n"+str);
					}
					
					if(str.equals("BYE"))
					{
						System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
						break; // terminate the loop; it will terminate the thread also
					}
					
					else if(str.equals("DL"))
					{
						try
						{
							File file = new File("capture.jpg");
							FileInputStream fis = new FileInputStream(file);
							BufferedInputStream bis = new BufferedInputStream(fis);
							OutputStream os = socket.getOutputStream();
							byte[] contents;
							long fileLength = file.length();
							pr.println(String.valueOf(fileLength));		//These two lines are used
							pr.flush();									//to send the file size in bytes.
							
							long current = 0;
							 
							long start = System.nanoTime();
							while(current!=fileLength){ 
								int size = 10000;
								if(fileLength - current >= size)
									current += size;    
								else{ 
									size = (int)(fileLength - current); 
									current = fileLength;
								} 
								contents = new byte[size]; 
								bis.read(contents, 0, size); 
								os.write(contents);
								//System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
							}   
							os.flush(); 
							System.out.println("File sent successfully!");
						}
						catch(Exception e)
						{
							System.err.println("Could not transfer file.");
						}
						pr.println("Downloaded.");
						pr.flush();

					}
					else
					{
						System.out.println("[" + id + "] says: " + str);
					}
				}
				else
				{
//					for(int i=0;i<=TestServer.workerThreadCount;i++){
//						String arr[] = TestServer.onlineUsers[i].split(" ", 2);
//					}
					System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
					TestServer.onlineUsers[id] = "";
					break;
				}
			}
			catch(Exception e)
			{
				System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
				break;
			}
		}
		
		try
		{
			this.is.close();
			this.os.close();
			this.socket.close();
		}
		catch(Exception e)
		{
		
		}
		
		TestServer.workerThreadCount--;
		System.out.println("Client [" + id + "] is now terminating. No. of worker threads = " 
				+ TestServer.workerThreadCount);
	}
}