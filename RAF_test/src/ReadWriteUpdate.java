import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReadWriteUpdate {
	private final int RECORD = 20;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadWriteUpdate obj = new ReadWriteUpdate();
		//obj.writeInFile();
		obj.readFromFile();
		obj.updateInFile();
	}

	public void writeInFile() {
		RandomAccessFile file;
		byte status = 0;
		String name = new String("Mottakin");
		String fileName = new String(
				"C:\\Users\\Nayeem Reza\\Documents\\JAVA\\RAF_test\\src" + "Foysal" + ".txt");
		try {
			file = new RandomAccessFile(new File(fileName), "rw");
			long fileSize = file.length();
			file.seek(fileSize);
			file.writeUTF(name);
			for (int i = 0; i < 19 - name.length(); i++) {
				file.writeByte(19);
			}
			file.write(status);

			file.close();
		} catch (IOException e) {
			System.out.println("Cannot Write!");
		}
	}
	
	public void readFromFile(){
		RandomAccessFile file;
		String fileName = new String(
				"C:\\Users\\Nayeem Reza\\Documents\\JAVA\\RAF_test\\src" + "Foysal" + ".txt");
		String n = null;
		String name = new String("Uranium");
		try {
			file = new RandomAccessFile(new File(fileName), "rw");
			long fileSize = file.length();
			file.seek(0);
			long noOfRecords = fileSize / RECORD;
			System.out.println("No of Records: "+noOfRecords);
			for (int j = 0; j < noOfRecords; j++) {
				n = file.readUTF();
				for (int i = 0; i < 19 - n.length(); i++) {
					file.readByte();
				}
				byte s = file.readByte();
				System.out.println("STATUS:" + s);

				if (name.equals(n)) {
					System.out.println("Name: "+n+" Status: "+s);
				}
			}

			file.close();
		} catch (IOException e) {
			System.out.println("Cannot Read!");
		}
	}
	
	public void updateInFile(){
		RandomAccessFile file1, file2;
		String fileName = new String(
				"C:\\Users\\Nayeem Reza\\Documents\\JAVA\\RAF_test\\src" + "Foysal" + ".txt");
		String n = new String("");
		byte status = 1;
		String name = new String("Uranium");
		try {
			file1 = new RandomAccessFile(new File(fileName), "rw");
			file2 = new RandomAccessFile(new File(fileName), "rw");
			long fileSize = file1.length();
			file1.seek(0);
			long noOfRecords = fileSize / RECORD;
			System.out.println("No of Records: "+noOfRecords);
			for (int j = 0; j < noOfRecords; j++) {
				n = file1.readUTF();
				for (int i = 0; i < 19 - n.length(); i++) {
					file1.readByte();
				}
				byte s = file1.readByte();
				System.out.println("STATUS:" + s);

				if (name.equals(n)) {
					file2.seek(RECORD*j+21);
					file2.write(status);
				}
			}
			file1.close();
			file2.close();
			System.out.println(">>>>>>>>>>>>>>>");
		} catch (IOException e) {
			System.out.println("Cannot Update!");
			e.printStackTrace();
		}
		
	}

}
