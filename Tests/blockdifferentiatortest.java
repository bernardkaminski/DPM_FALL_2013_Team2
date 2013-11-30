package Tests;

/*
*
*This is the test class used to test block differentiation
*the code uses the difference in heights of the two tyoes of blocks (BEIGE and BLUE) to differentiate between them
*
*@author Deepak Sharma 260468268
*
*for code see: Odometer, Navigation, TwoWheeledRobot
*for testing documents see: blockdifferentiation test data in the appendix of the final report
*
*/

import Hardware.TwoWheeledRobot;
import IntermediateLogic.*;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;


public class blockdifferentiatortest extends Thread{
	
	private static UltrasonicSensor bottomsensor = new UltrasonicSensor(SensorPort.S3);
	
	private static UltrasonicSensor topsensor = new UltrasonicSensor(SensorPort.S2);
		
	Odometer odo;
	
	static Navigation nav;
	
	static TwoWheeledRobot robo ;
	
	static int difference = 5;
	
	public blockdifferentiatortest(Odometer odo, Navigation nav, TwoWheeledRobot robo){
		
		this.odo = odo;
		this.nav = nav;
		this.robo = robo;
		
	}
	
	//enum to identify which object is detected blue or beige
	
	public enum object { BLUE, BEIGE }
	
	public static object OBJECT;
	
	public static object detector() throws Exception{
		
		boolean detecting = true;
		
		//these are the red, green and blue values that the sensor detects
		int red,green,blue;
		
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		
		
		//for detection we found that the red and blue values are similar for the blue object and different for the beige object
		while(detecting){
					
			Motor.A.forward();
			Motor.B.forward();
			
			//start topsensoring the color sensor for detection
			if(bottomsensor.getDistance() <=15){
							
				robo.stopMotors();
				
				//wait for half a second to get good readings
				Thread.sleep(500);
				
				LCD.drawString(bottomsensor.getDistance() + " ", 0 ,6);
					
				if(Math.abs(topsensor.getDistance()-bottomsensor.getDistance())>difference){
					
					LCD.drawString("Object", 0, 3);
					
					OBJECT = object.BLUE;
					
					Sound.beep();	
					
					try{Thread.sleep(1000);}catch(Exception e){
						
						
					}
					
					Sound.beep();
					
					LCD.drawString(topsensor.getDistance() + " ", 0 , 6);
					LCD.drawString(bottomsensor.getDistance() + " ", 0 , 7);
					
					detecting = false;
				}
							
				else{
					
					LCD.drawString("Block", 0, 3);
					
					LCD.drawString(topsensor.getDistance() + " ", 0 , 6);
					LCD.drawString(bottomsensor.getDistance() + " ", 0 , 7);

					OBJECT = object.BEIGE;
					
					Sound.beep();

					//if the object detected is a block then move back by 10 cm
					nav.travelSetDistanceBackwards(10);
					
					detecting = false;
				}
				
			}			
						
		}
		
		//return the object detected
		return OBJECT;
		
	}

}

