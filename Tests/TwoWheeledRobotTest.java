package Tests;
import Hardware.LightPoller;
import Hardware.TwoWheeledRobot;
import Hardware.UltrasonicPoller;
import Hardware.UltrasonicScanner;
import IntermediateLogic.BlockDifferentiator;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class TwoWheeledRobotTest {

	
		
		static void main() 
		{
			//create sensors
			ColorSensor csLeft= new ColorSensor(SensorPort.S1);
			ColorSensor csRight=new ColorSensor(SensorPort.S4);
			UltrasonicSensor usTop=new UltrasonicSensor(SensorPort.S1);
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
			BlockDifferentiator bd = new BlockDifferentiator(null,null);
			Odometer odo = new Odometer(robot);
			Navigation nav = new Navigation(robot, odo, bd);
			
			RConsole.openBluetooth(20000);
			RConsole.println("connected");
			Button.waitForAnyPress();

			
			robot.startLeftLP();
			robot.startRightLP();
			nav.travelSetDistanceStraight(100);
			//robot.stopLeftLP();
			//robot.stopRightLP();
		
		}

	}


