import lejos.nxt.*;

public class BlockDifferentiator {
	
	private static UltrasonicPoller usPoller = new UltrasonicPoller(new UltrasonicSensor(SensorPort.S3));//bottom sensor uses the uspoller
	private static UltrasonicScanner usScanner = new UltrasonicScanner(new UltrasonicScanenr(SensorPort.S2));//topsensor uses the usscanner
	private static TwoWheeledRobot robo;
	
	static int difference = 20;
	
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
			if(usPoller.returnDistance() <=10){
				
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


}
