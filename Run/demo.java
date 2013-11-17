package Run;
import MainLogic.*;

import MainLogic.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import Hardware.LightPoller;
import Hardware.TwoWheeledRobot;
import Hardware.UltrasonicPoller;
import Hardware.UltrasonicScanner;
import IntermediateLogic.BlockDifferentiator;
import IntermediateLogic.LcdDisplay;
import IntermediateLogic.Localizer;
import IntermediateLogic.LocalizerBernie;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import MainLogic.Map;
import MainLogic.Search;
import bluetooth.BluetoothConnection;
import bluetooth.Transmission;


public class demo {

	
	public static void main()
	{
		
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
		
		
		BluetoothConnection conn = new BluetoothConnection();
		 //as of this point the bluetooth connection is closed again, and you can pair to another NXT (or PC) if you wish
		
		// example usage of Tranmission class
		Transmission t = conn.getTransmission();
		if (t == null) 
		{
			LCD.drawString("Failed to read transmission", 0, 5);
		} 
		
		else
		{
			Point BottomLeftGreenZone= new Point(t.greenZone[0]*30,t.greenZone[1]*30);
			
			
			int xdivs = (int)BottomLeftGreenZone.getx()/30;
			int ydivs = (int)BottomLeftGreenZone.gety()/30;
			int [] xCords = new int[xdivs+ydivs];
			int [] yCords = new int[xdivs+ydivs];
			generatexPath((int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords);
			
			
			//LCD.drawString(""+t.role, 0, 0);
			
			//double x,y,theta;
			
			//create sensors
			//int x=2*30;
			//int y = 6*30;
			
			//int [] xCords = new int[x+y];
			//int [] yCords = new int[x+y];
			
			//generatexPath(x, y, xCords, yCords);
			
			int [] scanResults = new int[5];
			//int [] xCords = {0,30,30,60,60,90,90,120};
			//int [] yCords = {30,30,60,60,90,90,120,120};
			
			Sound.beep();
			//RConsole.openAny(20000);
			//Button.waitForAnyPress();
			LCD.clear();
			
			LcdDisplay lcd = new LcdDisplay(odo);
			Localizer loc = new Localizer(odo, robo);
			LocalizerBernie locb= new LocalizerBernie(robo, odo);
			BlockDifferentiator bd = new BlockDifferentiator(odo, robo);
			Search search = new Search(robo, nav, odo, bd);
			
			
			robo.setClawAcc(search.CLAW_RAISE_ACC);
			robo.setclawSpeed(search.CLAW_RAISE_ACC);
			robo.rotateClawAbsolute(search.CLAW_RAISE_ANGLE);
			
			//loc.doLocalization();
			locb.localizeTheta();
			//Button.waitForAnyPress();
			for(int i =0; i<xCords.length;i++)
			{
				RConsole.println("enterd loop");
				nav.travelTo(true,false, xCords[i], yCords[i]);
				nav.turnTo(320, true);	
				scanResults = search.Scan();
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
					search.Scan();
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
					
					search.Scan();
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
			nav.travelTo(false, true, (int)BottomLeftGreenZone.getx(),(int)BottomLeftGreenZone.gety() );//make not hardcoded
			nav.turnTo(45, true);
			robo.dropBlock(search.CLAW_LOWER_ANGLE);
			
			
				
		}
	}

	
	public static void generatexPath(int x,int y,int [] xList, int[] yList)
	{
		
		for (int i =0 ; i< xList.length;i++)
		{
			
			
			
			if(i==0)
			{
				xList[i]=0;
				yList[i]=30;
			}
			else
			{
				
				if((i%2)==0)
				{	
					
						xList[i]=xList[i-1];
						yList[i]=yList[i-1]+30;
						
				}
				else
				{
						xList[i]=xList[i-1]+30;
						yList[i]=yList[i-1];
				}
			
				
			}
			
			if(xList[i]>x)
			{
				for(int j=i;j<xList.length;j++)
				{
					xList[j]=x;
					yList[j]=yList[j-1]+30;
				}
				break;
			}
			if(yList[i]>y)
			{
				for(int j=i;j<xList.length;j++)
				{
					yList[j]=y;
					xList[j]=xList[j-1]+30;
				}
				break;
			}
		
		}
	}
	
	
	
	
}
