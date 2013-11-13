
public class Localizer extends Thread{
	
	TwoWheeledRobot robo;
	Odometer odo;
	
	int minimumDistance = 30;
	int openArea = 50;
	int dTheta;
	int ROTATIONSPEED = 100;
	
	boolean isWall = true;//facing the wall?
	boolean noWall = true;//facing an open area?
	
	public Localizer(Odometer odo, TwoWheeledRobot robo)
	{
		this.odo = odo;
		this.robo= robo;
	}
	
	public void localize()
	{
		
		int firstAngle, secondAngle;
		
		double []pos= new double[3];
		
		robo.rotateClockwise();
		
			while(isWall)//robot is in front of a wall so move to open area
			{
				 if(robo.getBottomUsPollerDistance() >= openArea){
					 
					 isWall=false;
					 noWall=true;
				 }
			}
			
			while(noWall)//robot is in a open area so start the actual localization routine 
			{
				 if(robo.getBottomUsPollerDistance() < minimumDistance)//stop if the distance is less than 30
				 {
					  noWall=false;
					  isWall=true;
				 }
			}
			
			firstAngle = (int)odo.getTheta();
			
			//latch onto the first angle
			
			robo.setRotationSpeed(-ROTATIONSPEED);//start rotating backwards to latch onto second angle
			
			/*
			*
			* ROBOT NOW MOVES IN OPPOSITE DIRECTION TO LATCH ONTO SECOND ANGLE
			*
			*/
			
			
			try{Thread.sleep(500);}catch(Exception e){}
			
			while(isWall)//robot facing the wall
			{
				 if(robo.getBottomUsPollerDistance() >= openArea){
					 isWall=false;
					 noWall=true;
				 }
			}
			
			while(noWall)//robot not facing a wall
			{
				 if(robo.getBottomUsPollerDistance() < minimumDistance)
				 {
					  noWall=false;
					  isWall=true;
				 }
			}
			
			secondAngle = (int)odo.getTheta();
			
			//get the second angle
									
			if(secondAngle < firstAngle)
				dTheta= 45-(firstAngle + secondAngle)/2;
			else if(secondAngle > firstAngle)
				dTheta= 225-(firstAngle + secondAngle)/2;
			else
				dTheta= 45;
			
			odo.getPosition(pos);

			
			int actualOrientation = (int) (360 - (pos[2] - dTheta)); //actual orientation of the robot when it has latched onto the second value
					
			odo.setPosition(new double [] {0.0, 0.0, actualOrientation}, new boolean [] {false, false, true});
			
			nav.turnTo(0);
					
	}

}
