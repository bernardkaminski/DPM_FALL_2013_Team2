package Tests;
import Hardware.TwoWheeledRobot;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;


public class ClawTest 
{
	public void main()
	{
		NXTRegulatedMotor clawMotor = new NXTRegulatedMotor(MotorPort.C);
		TwoWheeledRobot robo = new TwoWheeledRobot(null, null, clawMotor, null, null, null, null, null, null);
		
		int buttonPress ;
		LCD.drawString("left = turn", 0, 0);
		LCD.drawString("right = straight", 0, 1);
		LCD.drawString("bottom = exit", 0, 2);
		buttonPress = Button.waitForAnyPress();
		LCD.clear();
	}
	
	

}
