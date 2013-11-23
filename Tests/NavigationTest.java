package Tests;
import bluetooth.BluetoothConnection;
import bluetooth.Transmission;
import Hardware.LightPoller;
import Hardware.TwoWheeledRobot;
import Hardware.UltrasonicPoller;
import Hardware.UltrasonicScanner;
import IntermediateLogic.BlockDifferentiator;
import IntermediateLogic.LcdDisplay;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import IntermediateLogic.lightlocalization;
import MainLogic.Search;
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

	public static void main()
	{
		/*BluetoothConnection conn = new BluetoothConnection();
		 //as of this point the bluetooth connection is closed again, and you can pair to another NXT (or PC) if you wish
		
		// example usage of Tranmission class
		Transmission t = conn.getTransmission();
		if (t == null) 
		{
			LCD.drawString("Failed to read transmission", 0, 5);
		} else*/
		{
			//LCD.drawString(""+t.role, 0, 0);
			
			//double x,y,theta;
			
			//create sensors
			ColorSensor csLeft= new ColorSensor(SensorPort.S1);
			ColorSensor csRight=new ColorSensor(SensorPort.S4);
			UltrasonicSensor UsBottom= new UltrasonicSensor(SensorPort.S2);
			UltrasonicSensor UsTop= new UltrasonicSensor(SensorPort.S3);
			
			//Create pollers
			LightPoller leftWheelLP= new LightPoller(csLeft);
			LightPoller rightWheelLP=new LightPoller(csRight);
			UltrasonicPoller USPbottom = new UltrasonicPoller(UsBottom);
			UltrasonicPoller USPtop = new UltrasonicPoller(UsTop);
			UltrasonicScanner USStop = new UltrasonicScanner(UsTop);
			UltrasonicScanner USSBottom = new UltrasonicScanner(UsBottom);
			
			TwoWheeledRobot robo = new TwoWheeledRobot(Motor.A, Motor.B, Motor.C, USStop, USSBottom, USPtop, USPbottom, leftWheelLP, rightWheelLP);
			Odometer odo = new Odometer(robo,true);
			Navigation nav = new Navigation(robo, odo, null);
			
			int [] scanResults = new int[5];
			int [] xCords = {0,30,30,60,60,90,90,120};
			int [] yCords = {30,30,60,60,90,90,120,120};
			Sound.beep();
			RConsole.openAny(20000);
			//Button.waitForAnyPress();
			LCD.clear();
			
			LcdDisplay lcd = new LcdDisplay(odo);
			lightlocalization loc = new lightlocalization(robo,odo,nav);
			BlockDifferentiator bd = new BlockDifferentiator(odo, robo);
			Search search = new Search(robo, nav, odo);
			
			
			robo.setClawAcc(search.CLAW_RAISE_ACC);
			robo.setclawSpeed(search.CLAW_RAISE_ACC);
			robo.rotateClawAbsolute(search.CLAW_RAISE_ANGLE);
			
			//loc.doLocalization();
			loc.localize();
			//Button.waitForAnyPress();
			for(int i =0; i<xCords.length;i++)
			{
				RConsole.println("enterd loop");
				nav.travelTo(true,false, xCords[i], yCords[i]);
				nav.turnTo(320, true);	
				scanResults = search.Scan(false);
				if(scanResults[4]==1)
				{
					break;
					
				}
				
				nav.turnTo(90, true);
				
				for(int j=0 ; j<scanResults.length;j++)
					RConsole.println(""+scanResults[j]);
				if(scanResults[0]==1&&yCords[i+1]>yCords[i]){
					//North blocked and next point is north
					nav.travelTo(true, false, xCords[i]+30, yCords[i]);
					nav.turnTo(320, true);
					search.Scan(false);
					nav.turnTo(90, true);
					i++;
					
				}
				if(scanResults[4]==1)
				{
					break;
					
				}
				if(scanResults[1]==1&&xCords[i+1]>xCords[i]){
					//East blocks and next point is east
					nav.travelTo(true, false, xCords[i],yCords[i]+30);
					
					search.Scan(false);
					nav.turnTo(90, true);
					i++;
				}
				if(scanResults[4]==1)
				{
					break;
					
				}
				//nav.travelTo(true,false, xCords[i], yCords[i]);
				
				
				nav.travelSetDistanceBackwards(4);
				nav.fineTune();
			}
			nav.travelTo(false, false, 4*30, 4*30);
			nav.turnTo(45, true);
			robo.dropBlock(search.CLAW_LOWER_ANGLE);
			
			Button.waitForAnyPress();	
		}
	}
}
