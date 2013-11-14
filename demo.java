import lejos.nxt.LCD;
import bluetooth.BluetoothConnection;
import bluetooth.Transmission;


public class demo {

	/**
	 * @param args
	 */
	static void main(String[] args) 
	{

		BluetoothConnection conn = new BluetoothConnection();
		// as of this point the bluetooth connection is closed again, and you can pair to another NXT (or PC) if you wish
		
		// example usage of Tranmission class
		Transmission t = conn.getTransmission();
		if (t == null) 
		{
			LCD.drawString("Failed to read transmission", 0, 5);
		} 
		else
		{
			Map map = new Map(8*(30), 8*(30)); 
		}

	}

}
