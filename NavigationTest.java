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
		//Sound.beep();
		LCD.clear();
		
		int [] xCords = {30,60,60,30,0,0,30,60,60,30,0};
		int [] yCords = {0,0,30,30,30,60,60,60,90,90,90};
		//double x,y,theta;
		
		//create sensors
		ColorSensor csLeft= new ColorSensor(SensorPort.S1);
		ColorSensor csRight=new ColorSensor(SensorPort.S4);
		UltrasonicSensor Us= new UltrasonicSensor(SensorPort.S2);
		
		//Create pollers
		LightPoller leftWheelLP= new LightPoller(csLeft);
		LightPoller rightWheelLP=new LightPoller(csRight);
		UltrasonicPoller USP = new UltrasonicPoller(Us);
		
		TwoWheeledRobot robo = new TwoWheeledRobot(Motor.A, Motor.B,Motor.C, null, null,null, USP, leftWheelLP, rightWheelLP);
		robo.pickUpBlock(100);
		Odometer odo = new Odometer(robo,true);
		Navigation nav = new Navigation(robo, odo, null);
		LcdDisplay lcd = new LcdDisplay(odo);
		Localizer loc = new Localizer(odo, robo);
		LocalizerBernie locb= new LocalizerBernie(robo, odo);
		//loc.localize();
		locb.localizeTheta();
		//Button.waitForAnyPress();
		for(int i =0; i<xCords.length;i++)
		{
			RConsole.println("enterd loop");
			nav.travelTo(false, xCords[i], yCords[i]);
			
		}
		Button.waitForAnyPress();
			
		
	}
}
