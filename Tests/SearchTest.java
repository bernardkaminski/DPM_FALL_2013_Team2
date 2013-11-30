package Tests;
import IntermediateLogic.*;

import Hardware.LightPoller;
import Hardware.TwoWheeledRobot;
import Hardware.UltrasonicPoller;
import Hardware.UltrasonicScanner;
import IntermediateLogic.BlockDifferentiator;
import IntermediateLogic.LcdDisplay;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import MainLogic.Search;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class SearchTest {

	
		
		static void main() 
		{
			//create sensors
			ColorSensor csLeft= new ColorSensor(SensorPort.S1);
			ColorSensor csRight=new ColorSensor(SensorPort.S4);
			UltrasonicSensor usTop=new UltrasonicSensor(SensorPort.S3);
			UltrasonicSensor usBottom=new UltrasonicSensor(SensorPort.S2);
			
			
			//Create pollers and scanners
			LightPoller leftWheelLP= new LightPoller(csLeft);
			LightPoller rightWheelLP=new LightPoller(csRight);
			
			
			UltrasonicScanner USSTop= new UltrasonicScanner(usTop);
			UltrasonicScanner USSBottom= new UltrasonicScanner(usBottom); 
			UltrasonicPoller USPTop=new UltrasonicPoller(usTop);
			UltrasonicPoller USPBottom=new UltrasonicPoller(usBottom);
			
			
			//Create Robot and higher level classes 
			TwoWheeledRobot robot =new TwoWheeledRobot(Motor.A,Motor.B,Motor.C,USSTop,USSBottom,USPTop,USPBottom,leftWheelLP,rightWheelLP);
			Odometer odo = new Odometer(robot,true);
			BlockDifferentiator bd = new BlockDifferentiator(odo,robot);
			Navigation nav = new Navigation(robot, odo, bd);
			
			LcdDisplay display=new LcdDisplay(odo);
			
			//initialize Search
			//Search search=new Search(robot,nav,odo,bd);
			//search.Scan();
		}

	}


