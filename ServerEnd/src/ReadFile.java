import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadFile {
	private File file;
	public ReadFile(String fileName) {
		String temp0 = new String();
		temp0 = "C:\\Users\\Nayeem Reza\\Documents\\JAVA\\Server-Messenger\\src\\" + fileName + ".txt";
		file = new File(temp0);
	}
	
	public ReadFile() {
		
	}

	public void addFriend(String name) {
		try (FileWriter fw = new FileWriter(file, true)) {
			fw.write(name + " 1\n"); // appends the string to the file
		} catch (Exception e) {
			System.out.println("Cannot create a File Writer!");
		}
	}
	
	
	public int checkIfFriendOrNot(String name)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				String arr[] = line.split(" ", 2);
				if (arr[0].equals(name)) {
					return 1;
				}
			}
		} catch (Exception e) {
			System.out.println("+++++Cannot create a Buffer Reader!++++++");
		}
		return 0;
	}
	
	
	public void markOnlineOffline(String name, int status){
		StringBuilder stringBuilder = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			try {
				while ((line = reader.readLine()) != null) {
					String arr[] = line.split(" ", 2);
					if (name.equals(arr[0])) {
						if (line != null && line.length() > 0) {
							line = line.substring(0, line.length() - 1);
							line = line + status;
						}
					}
					stringBuilder.append(line);
					stringBuilder.append(ls);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try (FileWriter fw = new FileWriter(file, false)) {
				fw.write(stringBuilder.toString()); // appends the string to the file
			} catch (Exception e) {
				System.out.println("Cannot create a File Writer!");
			}
			
			
			
			System.out.println(stringBuilder.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int readFile(String a) {
		System.out.println(a);
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			System.out.println("Ami Registered User der read kortechi...");
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				if (a.equals(line)) {
					//System.out.println("MATCHED");
					return 1;
				}
			}
		} catch (Exception e) {
			System.out.println("Cannot create a Buffer Reader!");
		}
		//System.out.println("DIDN'T MATCH");
		return 0;
	}

	public void writeFile(String a) {
		try (FileWriter fw = new FileWriter(file, true)) {
			fw.write(a + "\n"); // appends the string to the file
		} catch (Exception e) {
			System.out.println("Cannot create a File Writer!");
		}
	}
}
