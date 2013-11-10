import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;


public class UltrasonicPollerTest {

	
	static void main() 
	{
		NXTRegulatedMotor rightMotor= new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		rightMotor.setSpeed(200);//degrees per second
		leftMotor.setSpeed(200);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		UltrasonicPoller usP = new UltrasonicPoller(us);
		double displacement =0;
		double distance;
		usP.startUsPoller();
		long tStart = System.currentTimeMillis();
		while(displacement < 100 )
		{
			distance = usP.returnDistance();
			RConsole.println(Double.toString(distance));
			leftMotor.forward();
			rightMotor.forward();
			displacement =  (leftMotor.getTachoCount() * 2.1 + rightMotor.getTachoCount() * 2.1) * Math.PI / 360.0;
		}
		long tEnd = System.currentTimeMillis();
		usP.stopUsPoller();
		long tDelta = tEnd - tStart;
		RConsole.println("*************"+Long.toString(tDelta));
		
		
	
		
	}

}
