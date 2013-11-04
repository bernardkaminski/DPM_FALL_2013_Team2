import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;


public class UltrasonicScannerTest {

	/**
	 * 
	 */
	
	static void main() 
	{
		RConsole.openBluetooth(20000);
		RConsole.println("connected");
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		UltrasonicScanner usS = new UltrasonicScanner(us);
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE)
		{
			double distance = usS.getFilteredDistance(10);
			RConsole.println(Double.toString(distance));
		}
		

	}

}
