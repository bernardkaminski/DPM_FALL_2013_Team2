package Run;
import Tests.NavigationTest;
import Tests.PathTest;
import lejos.nxt.LCD;
import lejos.nxt.rcxcomm.PacketHandler;


public class Main {

	
	public static void main(String[] args) 
	{
		//uncomment to run the scanner test
		//UltrasonicScannerTest.main();
		
		
		//uncomment this for light sensor test
		//LightSensorTest.main();
		
		//uncomment this for ultrasonic poller test
		//UltrasonicPollerTest.main();
		
		//OdometerTestClass.main();
		//NavigationTest.main();
		//UltrasonicPollerTest.main();
		//bluetoothTest.main();
		//BTTest.main(null);
		//demo.main();
		PathTest.main();
	}

}
