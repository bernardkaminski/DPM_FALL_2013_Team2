package Tests;
/*
*
*This is the test class used to test the first iteration of the localization code i.e. localization with light sensors,
*the code uses the FALLING_EDGE method to do localization 
*
*@author Deepak Sharma 260468268
*
*for code see: Odometer, Navigation
*for testing documents see: localization test data in the appendix of the final report
*
*note: this localization is NOT used for the actual competition 
*
*/
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import IntermediateLogic.*;


public class localizationtest {
	
	Odometer odo;
	Navigation nav;
	
	int minimumDistance = 30;//the minimum distance to detect and note the odometer reading
	int openArea = 50;// an openarea is anything greater than 50
	int dTheta;
	int ROTATIONSPEED = 50;
	
	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
	
	boolean isWall = true;//facing the wall?
	boolean noWall = true;//facing an open area?
	
	public localizationtest(Odometer odo, Navigation nav, UltrasonicSensor us)
	{
		this.odo = odo;
		this.nav = nav;
		this.us = us;
	}
	
	public void localize()
	{
		
		int firstAngle, secondAngle;
		
		double []pos= new double[3];
		
		Motor.A.setSpeed(ROTATIONSPEED);
		Motor.B.setSpeed(ROTATIONSPEED);
		
		Motor.A.forward();
		Motor.B.backward();
		
			while(isWall)//robot is in front of a wall so move to open area
			{
				 if(us.getDistance() >= openArea){
					 
					 isWall=false;
					 noWall=true;
				 }
			}
			
			while(noWall)//robot is in an open area so start the actual localization routine 
			{
				 if(us.getDistance() < minimumDistance)//stop if the distance is less than 30
				 {
					  noWall=false;
					  isWall=true;
				 }
			}
			
			firstAngle = (int)odo.getTheta();
			Sound.beep();
			
			RConsole.print(firstAngle + " ");
			
			Motor.A.backward();
			Motor.B.forward();
			
			try{Thread.sleep(500);}catch(Exception e){}
			
			//latch onto the first angle
			
			//start rotating backwards to latch onto second angle
			
			/*
			*
			* ROBOT NOW MOVES IN OPPOSITE DIRECTION TO LATCH ONTO SECOND ANGLE
			*
			*/
			
			while(isWall)//robot facing the wall
			{
				 if(us.getDistance() >= openArea){
					 isWall=false;
					 noWall=true;
				 }
			}
			
			while(noWall)//robot not facing a wall
			{
				 if(us.getDistance() < minimumDistance)
				 {
					  noWall=false;
					  isWall=true;
				 }
			}
			
			secondAngle = (int)odo.getTheta();
			RConsole.print(secondAngle + " ");
			
			Sound.beep();
			
			Motor.A.stop();
			Motor.B.stop();
			
			//get the second angle
									
			if(secondAngle < firstAngle)
				dTheta= 45-(firstAngle + secondAngle)/2;
			else if(secondAngle> firstAngle)
				dTheta= 225-(firstAngle + secondAngle)/2;
			else
				dTheta= 45;
			
			
			odo.getPosition(pos);
			
			RConsole.print(pos[2] + " ");
			
			RConsole.print(dTheta + " ");//calculate the actual initial heading
			
			
			
			
								
	}
	
}

