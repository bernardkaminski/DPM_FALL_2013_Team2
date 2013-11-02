import javax.xml.stream.events.Attribute;

import lejos.nxt.comm.RConsole;


public class Navigation {
	//Properties of navigation
	private TwoWheeledRobot robo; 
	private Odometer odo;
	private BlockDifferentiator bd;
	
	//Variables
	private boolean hasBlock=false;
	
	//Constants
	private final double TURNTO_THRESHOLD=1.0;
	private final double GRIDLINE_LIGHTVALUE_THRESHOLD=20; //When light sensor returns value less than this threshold a line has been detected
	private final int CLOSE=5;
	private final int SLOW=50;
	private final int STANDARD=200;
	private final int FAST=400;
	private enum Heading
	{
		NORTH(0),
		SOUTH(180),
		EAST(90),
		WEST(270);
		
		private int value;
		
		private Heading(int value)
		{
			this.value=value;
		}
		public int getAngle()
		{
			return value;
		}
	}
	private final double NORTH=0;
	private final double SOUTH=180;
	private final double WEST=270;
	private final double EAST=90;
	private final double GRIDLINE_ANGLE_THRESHOLD=20;
	private final double TRAVELTO_GOAL_THRESHOLD=1;
	private final double TRAVELTO_TURN_THRESHOLD=5;
	//Constructor
	public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd){
		this.robo=robo;
		this.odo=odo;
		this.bd=bd;	
		
	}
	
	//Travel To Method
	
	public void travelTo(boolean hasBlock, double x, double y){
		boolean atPosition = false;
		if(hasBlock){
			//Special code for navigating straight to green/red zone
			}
		
		else{			
			//Travel to given coordinate while using light sensors to update position
			while(!atPosition)
			{
				//Calculate x and y differences
	            double dX=x-odo.getX();
	            double dY=y-odo.getY();
	             
	            //Stop if reached goal
	            if(Math.abs(dX)<TRAVELTO_GOAL_THRESHOLD && Math.abs(dY)<TRAVELTO_GOAL_THRESHOLD)
	            {
	                atPosition=true;
	            }
	            
	            //Calculate goal theta (desired heading)
	            double goalTheta=Math.atan2(dX,dY)*(180/Math.PI);
	            if (goalTheta<0){//Make sure theta is always positive
	                goalTheta+=360;
	            }
	            travelingThetaUpdater(goalTheta);
	            
	            //Robot is pointed in the right direction, move forward
	            robo.goForward(); 
			
	            robo.startLeftLP();
	            robo.startRightLP();
	            double []leftWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by left wheel
	            double []rightWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by right wheel 
			
				int detectionCount=0;
				boolean travellingNorth=false; 
				boolean travellingSouth=false; 
				boolean travellingEast=false;
				boolean travellingWest=false;
				boolean detectionComplete=false;
				
				//*************************************************i like the west,north,south,est keywords**********************
				if(odo.getAng() < WEST + GRIDLINE_ANGLE_THRESHOLD && odo.getAng() > WEST - GRIDLINE_ANGLE_THRESHOLD){
					//If robot is heading west (within threshold)
					//Use light sensors to update X coordinate as robot travels
					travellingWest=true;
				}
				else if(odo.getAng()< EAST + GRIDLINE_ANGLE_THRESHOLD && odo.getAng()> EAST - GRIDLINE_ANGLE_THRESHOLD){
					//If robot is heading west (within threshold)
					//Use light sensors to update X coordinate as robot travels
					travellingEast=true;
				}
				else if(odo.getAng()<NORTH + GRIDLINE_ANGLE_THRESHOLD && odo.getAng()>NORTH - GRIDLINE_ANGLE_THRESHOLD){
					//If robot is heading north (within threshold)
					//Use light sensors to update Y coordinate as the robot travels				
					travellingNorth=true;
				}	
				else if(odo.getAng()<SOUTH + GRIDLINE_ANGLE_THRESHOLD && odo.getAng()>SOUTH - GRIDLINE_ANGLE_THRESHOLD){
					//If robot is heading north (within threshold)
					//Use light sensors to update Y coordinate as the robot travels				
					travellingSouth=true;
				}	
				
				//Get robots position at each of the two detections
				//Left wheel light sensor detects a line
				if(robo.getLeftLightValue()<GRIDLINE_LIGHTVALUE_THRESHOLD){
					leftWheelLineDetectCoords[0]=odo.getX();
					leftWheelLineDetectCoords[1]=odo.getY();
					//NEED TO PAUSE POLLER SOMEHOW TO AVOID REDECTION
					detectionCount++;
				}
				//Right wheel light sensor detects a line
				if(robo.getRightLightValue()<GRIDLINE_LIGHTVALUE_THRESHOLD){
					rightWheelLineDetectCoords[0]=odo.getX();
					rightWheelLineDetectCoords[1]=odo.getY();
					//NEED TO PAUSE POLLER SOMEHOW TO AVOID REDECTION
					detectionCount++;
				}
				if(detectionCount==2){
					detectionComplete=true;
				}
				//Corrections connot miss a line 
				double dXdetectionPoints=Math.abs(leftWheelLineDetectCoords[0]-rightWheelLineDetectCoords[0]);
				double dYdetectionPoints=Math.abs(leftWheelLineDetectCoords[1]-rightWheelLineDetectCoords[1]);
				double dT=Math.atan2(dXdetectionPoints, dYdetectionPoints);
				
				
				
				//************* these four cases should be put into one method that takes either north,west,south or east
				
				
				//North travelling correction
				if(travellingNorth && detectionComplete){
					double[] position=new double[3];
					int absoluteY= ((int)(((leftWheelLineDetectCoords[1]+rightWheelLineDetectCoords[1]))/2.0)/30)*30;
					position[0]=odo.getX();
					position[1]=absoluteY;
					if(leftWheelLineDetectCoords[0]<rightWheelLineDetectCoords[0]){
						position[2]=NORTH+dT;
					}
					else{
						position[2]=NORTH-dT;
					}
					boolean[]update={false,true,true};
					odo.setPosition(position,update);			
				}
				
				//South travelling correction
				if(travellingSouth && detectionComplete){
					double[] position=new double[3];
					int absoluteY= ((int)(((leftWheelLineDetectCoords[1]+rightWheelLineDetectCoords[1]))/2.0)/30)*30;
					position[0]=odo.getX();
					position[1]=absoluteY;
					if(leftWheelLineDetectCoords[0]<rightWheelLineDetectCoords[0]){
						position[2]=SOUTH-dT;
					}
					else{
						position[2]=SOUTH+dT;
					}
					boolean[]update={false,true,true};
					odo.setPosition(position,update);			
				}
				
				//West travelling correction
				if(travellingWest && detectionComplete){
					double[] position=new double[3];
					int absoluteX= ((int)(((leftWheelLineDetectCoords[0]+rightWheelLineDetectCoords[0]))/2.0)/30)*30;
					position[0]=absoluteX;
					position[1]=odo.getY();
					if(leftWheelLineDetectCoords[1]<rightWheelLineDetectCoords[1]){
						position[2]=WEST+dT;
					}
					else{
						position[2]=WEST-dT;
					}
					boolean[]update={true,false,true};
					odo.setPosition(position,update);			
				}
				
				//East travelling correction
				if(travellingEast && detectionComplete){
					double[] position=new double[3];
					int absoluteX= ((int)(((leftWheelLineDetectCoords[0]+rightWheelLineDetectCoords[0]))/2.0)/30)*30;
					position[0]=absoluteX;
					position[1]=odo.getY();
					if(leftWheelLineDetectCoords[1]<rightWheelLineDetectCoords[1]){
						position[2]=EAST-dT;
					}
					else{
						position[2]=EAST+dT;
					}
					boolean[]update={true,false,true};
					odo.setPosition(position,update);			
				 }						
			}
			robo.stopMotors();
		}		
	}
	
	public boolean middleLine()
	{
		//light 
		/*if(Currentlight-Lastlight>(LIGHT_DIFFERENCE_CONSTANT)&& light<GRIDLINE_LIGHTVALUE_THRESHOLD)
		{
		
		}
		return false;*/
		return false;
	}
	
	
	private void travelingThetaUpdater(double goalTheta)
	{
		
        //Heading correction
        double thetaCorrection=Math.abs(goalTheta-odo.getAng());
         
        //handle exceptions over 0-360 line 
        if(thetaCorrection>180){ 
            thetaCorrection-=360; 
        } 
         
        //Correct theta while moving between gridlines only if error is > than threshold
        if(Math.abs(thetaCorrection)>TRAVELTO_TURN_THRESHOLD){
            turnTo(goalTheta,false);
        }
	}
	
	//calculate the destination angle of the point 
	public double calDestinationAngle(double xDest, double yDest)
	{
		double odomAngle = odo.getAng();//current position
		double dAngle = 0 ;//angle to get to
		double x = odo.getX();
		double y = odo.getY();
		double changeY = yDest-y;
		double changeX = xDest-x;
		if(changeX==0)
		{
			if(changeY>0)
			{
				dAngle=0;
			}
			else
			{
				dAngle=Math.PI;
			}
		}
		else
		{
			if(changeY==0)
			{
				if(changeX>0)
				{
					dAngle=(Math.PI/2);
				}
				else
				{
					dAngle=(3*Math.PI/2);
				}
			}
			else
			{
				dAngle = Math.atan(( changeY/changeX));//math.atan returns radians
				if((changeX>0 && changeY>0)||(changeX>0 && changeY<0))
				{
					dAngle = (Math.PI/2) - dAngle;
				}
				if((changeX<0 && changeY<0)||(changeX<0 && changeY>0))
				{
					dAngle =(3*Math.PI/2)-dAngle;
				}
				
				
			}
		}
		
		return Math.toDegrees(dAngle)-odomAngle;
	}
	
	
	//Turn to method  // not sure why you need to pass a boolean here 
	public void turnTo(double angle, boolean stop) { 
        
        double error = angle - odo.getAng(); 
        robo.setRotationSpeed(STANDARD);
        while (Math.abs(error) > TURNTO_THRESHOLD) { 
               
    
            error = angle - odo.getAng(); 
            
            //if close reduce speed 
            if(error <= CLOSE)
            {
            	robo.setRotationSpeed(SLOW);
            }
            
            if (error < -180.0) { //rotate clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else if (error < 0.0) { //rotate counter clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else if (error > 180.0) { //rotate counter clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else { //rotate clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } 
        } 
        robo.setRotationSpeed(STANDARD);
        if (stop) { 
            robo.stopMotors();} 
           
    }
	
	//Avoid obstacle method
	public void avoidObstacle(){
		
	}
	
	//Investigate block
	public void investigateBlock(){
		
	}
	
	//use pythogorem to know if the robot has travel the set distance
	public void travelSetDistanceStraight(double d)
		{
			double startingX = odo.getX();
			double startingY = odo.getY();
			robo.setForwardSpeed(STANDARD);
			while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
			{
				RConsole.println(Integer.toString(robo.getLeftLightValue())+" Left");
				RConsole.println(Integer.toString(robo.getRightLightValue())+" Right");
				
				robo.goForward();
			}
			robo.stopMotors();
		}
		

}
