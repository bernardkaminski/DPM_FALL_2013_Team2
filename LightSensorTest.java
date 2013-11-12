import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXT;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;


public class LightSensorTest {

	
	static void main() 
	{
		//create sensors
		ColorSensor csLeft= new ColorSensor(SensorPort.S1);
		ColorSensor csRight=new ColorSensor(SensorPort.S4);
		NXTRegulatedMotor rightMotor= new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		rightMotor.setSpeed(200);
		leftMotor.setSpeed(200);
		
		
		//Create pollers
		LightPoller leftWheelLP= new LightPoller(csLeft);
		LightPoller rightWheelLP=new LightPoller(csRight);
		
	
		RConsole.openBluetooth(20000);
		RConsole.println("connected");
		Button.waitForAnyPress();
		
		leftWheelLP.startLightPoller();
		rightWheelLP.startLightPoller();
		
		double displacement =0;
		int lLight =0;
		int rLight =0;
		long tStart = System.currentTimeMillis();
		while(displacement < 100 )
		{
			lLight = leftWheelLP.returnLightValue();
			rLight = rightWheelLP.returnLightValue();
			RConsole.println("left\t"+Integer.toString(lLight) +"\t"+"right\t"+Integer.toString(rLight));
			leftMotor.forward();
			rightMotor.forward();
			displacement =  (leftMotor.getTachoCount() * 2.1 + rightMotor.getTachoCount() * 2.1) * Math.PI / 360.0;
		}
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		RConsole.println("*************"+Long.toString(tDelta));
		leftWheelLP.stopLightPoller();
		rightWheelLP.stopLightPoller();
		
	}

}
