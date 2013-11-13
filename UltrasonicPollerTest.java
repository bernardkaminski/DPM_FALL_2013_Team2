import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;


public class UltrasonicPollerTest {

	
	static void main() 
	{
		RConsole.openAny(2000000);
		RConsole.println("connected");
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		UltrasonicPoller usP = new UltrasonicPoller(us);
		 TwoWheeledRobot robo = new TwoWheeledRobot(null, null, null, null, null, null, usP, null, null);
		double distance;
		robo.startUsBottom();
		
		while(true )
		{
			distance = robo.getBottomUsPollerDistance();
			RConsole.println(Double.toString(distance));
			
		}
		
		
	
		
	}

}
