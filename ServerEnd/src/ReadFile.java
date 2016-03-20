import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReadFile {
	private File file;
	private final int RECORD = 20;
	public ReadFile(String fileName) {
		String temp0 = new String();
		temp0 = "C:\\Users\\Nayeem Reza\\Documents\\JAVA\\Server-Messenger\\src\\" + fileName + ".txt";
		file = new File(temp0);
	}
	
	public ReadFile() {
		
	}

	public void addFriend(String fileName, String name) {
		RandomAccessFile file;
		byte status = '1';
		fileName = "C:\\Users\\Nayeem Reza\\Documents\\JAVA\\Server-Messenger\\src\\" + fileName + ".txt";
		
		try {
			file = new RandomAccessFile(new File(fileName), "rw");
			long fileSize = file.length();
			file.seek(fileSize);
			file.writeUTF(name);
			for(int i=0;i<19-name.length();i++){
				file.writeByte(19);
			}
			file.write(status);
			
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void markOnlineOffline(String fileName, String a, boolean Online) {
		RandomAccessFile file;
		String name;
		char status;
		
		
	}

	public int readFile(String a) {
		System.out.println(a);
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			System.out.println("Ami Registered User der read kortechi...");
			String line;
			line = reader.readLine();
			System.out.println(line);
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				if (a.equals(line)) {
					return 1;
				}
			}
		} catch (Exception e) {
			System.out.println("Cannot create a Buffer Reader!");
		}
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
