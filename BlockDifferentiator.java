<<<<<<< HEAD
import lejos.nxt.*;

=======
/**
 * This class is what is used to differentiate between an obstacle and a building block. 
 * @author Bernie
 * @version 1.0
 * @see TwoWheeledRobot
 */
>>>>>>> 4b1b350df68753866eece57df9b9c73af4c0d9e0
public class BlockDifferentiator {
	
	private static UltrasonicPoller usPoller = new UltrasonicPoller(new UltrasonicSensor(SensorPort.S3));//bottom sensor uses the uspoller
	private static UltrasonicScanner usScanner = new UltrasonicScanner(new UltrasonicScanenr(SensorPort.S2));//topsensor uses the usscanner
	private static TwoWheeledRobot robo;
	
	static int difference = 5;
	
	public BlockDifferentiator(Odometer odo, Navigation nav, TwoWheeledRobot robo){
		
		this.odo = odo;
		this.nav = nav;
		this.robo = robo;
		
	}
	
	//enum to identify whether object is blue or beige
	
	public enum object { BLUE, BEIGE }
	
	public static object OBJECT;
	
	public static object identifyBlock() throws Exception{
		
		boolean detecting = true;
		
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		
		//for detection we found that the red and blue values are similar for the blue object and different for the beige object
		while(detecting){
					
			Motor.A.forward();
			Motor.B.forward();
			
			usPoller.startUsPoller();
			
			//start bottomsensor
			if(usPoller.returnDistance() <=15){
				
				robo.stopMotors();
									
				//wait for half a second to get good readings
				try{Thread.sleep(1000);}catch(Exception e){	}
				
				LCD.drawString(usPoller.returnDistance() + " ", 0 ,6);
					
				if(Math.abs(usScanner.getFilteredDistance()-usPoller.returnDistance())>difference){
					
					LCD.drawString("Object", 0, 3);
					
					OBJECT = object.BLUE;
					
					Sound.beep();	
					
					try{Thread.sleep(1000);}catch(Exception e){	}
					
					Sound.beep();
					
					LCD.drawString(usScanner.getFilteredDistance() + " ", 0 , 6);
					LCD.drawString(usPoller.returnDistance() + " ", 0 , 7);
					
					detecting = false;
				}
							
				else{
					
					LCD.drawString("Block", 0, 3);
					
					LCD.drawString(usScanner.getFilteredDistance() + " ", 0 , 6);
					LCD.drawString(usPoller.returnDistance() + " ", 0 , 7);

					OBJECT = object.BEIGE;
					
					Sound.beep();

					//if the object detected is a block then move back by 10 cm				
					detecting = false;
				}
				
			}
							
			
						
		}
		
		//return the object detected
		return OBJECT;
		
	}


	/**
	 * 
	 * @param robo the robot that has the top and bottom Ultrasobic sensors
	 * @return 0 if a obstacle and 1 if a building block
	 */
	public int identifyBlock(TwoWheeledRobot robo)
	{
		return 1;
	}
}
