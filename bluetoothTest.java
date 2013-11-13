import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;


public class bluetoothTest {
	
	static void main()
	{
		RConsole.openAny(20000);
		Bluetooth.setFriendlyName("NXT");
		RConsole.println("connected");
	}

}
