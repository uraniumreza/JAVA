import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args) {
		Client C = new Client("127.0.0.1");
		C.startRunning();
	}

}
