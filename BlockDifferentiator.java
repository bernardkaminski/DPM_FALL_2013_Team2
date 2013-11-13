/**
 * This class is what is used to differentiate between an obstacle and a building block. 
 * @author Deepak
 * @version 1.0
 * @see TwoWheeledRobot
 */

import lejos.nxt.*;
public class BlockDifferentiator extends Thread {

	/**
	 * 
	 * @param robo the robot that has the top and bottom Ultrasobic sensors
	 * @return 0 if a obstacle and 1 if a building block
	 */	
	
	TwoWheeledRobot robo;
	Odometer odo;
		
	static int difference = 5;
	
	public BlockDifferentiator(Odometer odo, TwoWheeledRobot robo){		
		this.odo = odo;
		this.robo = robo;		
	}
	
	//enum to identify which object is detected blue or beige
	
	public enum object { BLUE, BEIGE };	
	public static object OBJECT;
	
	public int identifyBlock()
	{
		boolean detecting = true;
		
		robo.goForward();	
		
		while(detecting){
					
			//start topsensoring the color sensor for detection
			if(robo.getBottomUsPollerDistance() <=15){							
				robo.stopMotors();
				
				//wait for half a second to get good readings
				try{Thread.sleep(500);}catch(Exception e){}
				
				if(Math.abs(robo.scanWithTopsensor(100)-robo.scanWithBottomsensor(100))>difference){					
					LCD.drawString("Object", 0, 3);					
					OBJECT = object.BLUE;					
					Sound.beep();						
					try{Thread.sleep(1000);}catch(Exception e){	}					
					Sound.beep();					
					detecting = false;
					return 0;
				}
							
				else{					
					OBJECT = object.BEIGE;					
					Sound.beep();
					detecting = false;
					return 1;
				}
				
			}		
						
		}
		
		return 0;
			
	}
	
}
