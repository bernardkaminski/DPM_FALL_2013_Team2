package IntermediateLogic;
/**
 * This class is what is used to differentiate between an obstacle and a building block. 
 * @author Deepak
 * @version 1.0
 * @see TwoWheeledRobot
 */


import Hardware.TwoWheeledRobot;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
public class BlockDifferentiator extends Thread {

	/**
	 * 
	 * @param robo the robot that has the top and bottom Ultrasobic sensors
	 * @return 0 if a obstacle and 1 if a building block
	 */	
	
	TwoWheeledRobot robo;
	Odometer odo;
	private int AMOUNT_OF_SCANS=35;
	
	final static int difference = 5;
	
	public BlockDifferentiator(Odometer odo, TwoWheeledRobot robo){		
		this.odo = odo;
		this.robo = robo;		
	}
	
	//enum to identify which object is detected blue or beige
	
	public enum object { BLUE, BEIGE };	
	public static object OBJECT;
	
	public int identifyBlock(double range){
		double topSensorData=robo.scanWithTopsensor(AMOUNT_OF_SCANS);		
		double bottomSensorData=robo.scanWithBottomsensor(AMOUNT_OF_SCANS);
		RConsole.println("T: " +topSensorData);
		RConsole.println("B: " +bottomSensorData);
			if(Math.abs(topSensorData-bottomSensorData)>difference&&bottomSensorData<=range){
				
				OBJECT = object.BLUE;										
				return 1;
			}
						
			else{
				
				OBJECT = object.BEIGE;					
				return 0;
			}			
	}
	
}
