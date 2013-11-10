import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;


public class NavigationTest 
{

	static void main()
	{
		RConsole.openAny(200000);
		Bluetooth.setFriendlyName("monster");
		RConsole.println("connected");
		Sound.beep();
		LCD.clear();
		int [] xCords = {0,60,60,0,60};
		int [] yCords = {60,60,120,120,120};
		double x,y,theta;
		
		//create sensors
		ColorSensor csLeft= new ColorSensor(SensorPort.S1);
		ColorSensor csRight=new ColorSensor(SensorPort.S4);
		
		//Create pollers
		LightPoller leftWheelLP= new LightPoller(csLeft);
		LightPoller rightWheelLP=new LightPoller(csRight);
		
		
		TwoWheeledRobot robo = new TwoWheeledRobot(Motor.A, Motor.B,null, null, null, null, null, leftWheelLP, rightWheelLP);
		Odometer odo = new Odometer(robo,true);
		Navigation nav = new Navigation(robo, odo, null);
		LcdDisplay lcd = new LcdDisplay(odo);
		
		Button.waitForAnyPress();
		for(int i =0; i<xCords.length;i++)
		{
			RConsole.println("enterd loop");
			nav.travelTo(false, xCords[i], yCords[i]);
			x=odo.getX();
			y=odo.getY();
			theta = odo.getTheta();
			RConsole.println("x="+Double.toString(x)+"\ty="+Double.toString(y)+"\ttheta="+Double.toString(theta)+"\ttried to get to x="+Double.toString(xCords[i])+"\ty="+Double.toString(yCords[i]));
			Button.waitForAnyPress();
		}
		Button.waitForAnyPress();
			
		
	}
}
