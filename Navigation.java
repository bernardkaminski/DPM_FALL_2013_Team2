import javax.xml.stream.events.Attribute;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

/**
 * the class that allows the robot to move tospecific locations. this class also corrects the odometer while traveling to locations. 
 * @author Bernie
 *
 */
public class Navigation {
        //Properties of navigation
        private TwoWheeledRobot robo; 
        private Odometer odo;
        private BlockDifferentiator bd;
        
        //Variables
        private boolean hasBlock=false;
        
        //Constants
        private final double TURNTO_THRESHOLD=2.0;
        private final double ANGLE_THRESHOLD=.5;
        private final double GRIDLINE_LIGHTVALUE_THRESHOLD=20; //When light sensor returns value less than this threshold a line has been detected
        private final int CLOSE=5;
        private final int SLOW=50;
        private final int STANDARD=200;
        private final int FAST=400;
        private final double NORTH=0;
        private final double SOUTH=180;
        private final double WEST=270;
        private final double EAST=90;
        private final double SENSOR_OFFSET=4.0;
       
        private final double GRIDLINE_ANGLE_THRESHOLD=20;
        private final double TRAVELTO_GOAL_THRESHOLD=2;
        private final double TRAVELTO_TURN_THRESHOLD=15;
        private final int LIGHT_DIFFERENCE_CONSTANT=70;// the light constant is a constant for difference the line constant is an absolute filter based on data taken 
    	private final int LEFT_LINE_VALUE_CONSTANT=535;
    	private final int RIGHT_LINE_VALUE_CONSTANT=586;
    	private byte lineCounter = 0;
    	private int rightLightValue=0;
    	private int leftLightValue=0;
    	
    	
        //Constructor
        public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd){
                this.robo=robo;
                this.odo=odo;
                this.bd=bd;
                robo.startRightLP();
                robo.startLeftLP();
                rightLightValue=robo.getRightLightValue();
            	leftLightValue=robo.getLeftLightValue();
                
                
        }
        
        //Travel To Method
        /**
         * this class that controls the robots traveling 
         * @param hasBlock weather or not the robot currently has a block 
         * @param x the x coordinate to travel to 
         * @param y the y coordinate to travel to 
         */
        public void travelTo(boolean hasBlock, double x, double y){
        	
       
        	if(hasBlock){
                        //Special code for navigating straight to green/red zone
                        }
                
                else{                        
                        //Travel to given coordinate while using light sensors to update position
                	
                 
                 
                	while(keepGoing(odo.getX(), odo.getY(), x, y))
                        {
                        double minAng = (Math.atan2((x - odo.getX()), (y - odo.getY()))) * (180.0 / Math.PI); 
                		if (minAng < 0) minAng += 359.0; //correct heading 
                		double thetaCorrection=Math.abs(minAng-odo.getTheta()); //handle exceptions over 0-360 line 
                		
                		if(thetaCorrection>180)
                		{ 
                			thetaCorrection-=360;
                		} 
                		if(Math.abs(thetaCorrection)>TRAVELTO_TURN_THRESHOLD)
                		{ 
                			//robo.stopLeftLP();
                            //robo.stopRightLP();
                        	turnTo(minAng,true);
                        	//robo.startLeftLP();
                        	//robo.startRightLP();
                        }
                		
                		//Decide whether to turn, 1st iteration will turn to destination angle
                		//travelingThetaUpdater(goalTheta);//turnTo needs to be changed to turnBy that bernie wrote
                		//RConsole.println(Double.toString(goalTheta));
                		//Restart light poller
                		//Sound.beep();
                		//Robot is pointed in the right direction, move forward
	                		robo.goForward(); 
	                		updateOdometry();                              
                        }
                        robo.stopMotors();      
                }                
        }
          
        //calculate the destination angle of the point
       
       public void turnTo(double angle, boolean stop) { 
    	   double error = angle - odo.getTheta(); 
    	   while (Math.abs(error) > TURNTO_THRESHOLD) {  		     			   		   
    		   error = angle - odo.getTheta(); 
    		   if (error < -180.0) { 
    			   robo.rotateClockwise(); } 
    		   else if (error < 0.0) {
    			   robo.rotateCounterClockwise(); } 
    		   else if (error > 180.0) { 
    			   robo.rotateCounterClockwise(); } 
    		   else { 
    			   robo.rotateClockwise(); } 
    		   } 
    	   if (stop) { robo.stopMotors();} 
    	   }
	   
        //Avoid obstacle method
        /**
         * the method that avoids obstacles 
         */
        public void avoidObstacle(){
                
        }
        
        //Investigate block
        /**
         * this method uses the BlockDifferentiator class to distinguish blocks 
         */
        public void investigateBlock(){
                
        }
        
        //use pythogorem to know if the robot has travel the set distance
        /**
         * 
         * @param d distance in cm to be traveled 
         */
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
        
        public void updateOdometry(){

        	double []leftWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by left wheel
        	double []rightWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by right wheel 
        
        	boolean travellingNorth=false; 
        	boolean travellingSouth=false; 
        	boolean travellingEast=false;
        	boolean travellingWest=false;
        	
        	
        	boolean leftEnteredLine = checkLineLeft();
        	boolean rightEnteredLine = checkLineRight();
        	if (leftEnteredLine)
        	{
        		lineCounter++;
        	}
        	if (rightEnteredLine)
        	{
        		lineCounter++;
        	}
        
        	if(odo.getTheta() < WEST + GRIDLINE_ANGLE_THRESHOLD && odo.getTheta() > WEST - GRIDLINE_ANGLE_THRESHOLD){
                //If robot is heading west (within threshold)
                //Use light sensors to update X coordinate as robot travels
                travellingWest=true;
        	}
        	else if(odo.getTheta()< EAST + GRIDLINE_ANGLE_THRESHOLD && odo.getTheta()> EAST - GRIDLINE_ANGLE_THRESHOLD){
                //If robot is heading west (within threshold)
                //Use light sensors to update X coordinate as robot travels
                travellingEast=true;
        	}
        	else if(odo.getTheta()<NORTH + GRIDLINE_ANGLE_THRESHOLD && odo.getTheta()>NORTH - GRIDLINE_ANGLE_THRESHOLD){
                //If robot is heading north (within threshold)
                //Use light sensors to update Y coordinate as the robot travels                                
                travellingNorth=true;
        	}        
        	else if(odo.getTheta()<SOUTH + GRIDLINE_ANGLE_THRESHOLD && odo.getTheta()>SOUTH - GRIDLINE_ANGLE_THRESHOLD){
                //If robot is heading north (within threshold)
                //Use light sensors to update Y coordinate as the robot travels                                
                travellingSouth=true;
        	}        
        
        	//Get robots position at each of the two detections
        	//Left wheel light sensor detects a line
        	if(leftEnteredLine){
                leftWheelLineDetectCoords[0]=odo.getX();
                leftWheelLineDetectCoords[1]=odo.getY()+SENSOR_OFFSET ;
                RConsole.println("crossed left line");
        	}
        	//Right wheel light sensor detects a line
        	if(rightEnteredLine){
                rightWheelLineDetectCoords[0]=odo.getX();
                rightWheelLineDetectCoords[1]=odo.getY()+SENSOR_OFFSET;
                RConsole.println("crossed right line");
        	}
        	
        	//Corrections cannot miss a line 
        	double dXdetectionPoints=Math.abs(leftWheelLineDetectCoords[0]-rightWheelLineDetectCoords[0]);
        	double dYdetectionPoints=Math.abs(leftWheelLineDetectCoords[1]-rightWheelLineDetectCoords[1]);
        	double dT=Math.atan2(dXdetectionPoints, dYdetectionPoints);
               
        	//North travelling correction
        	if(travellingNorth && (lineCounter==2)){
                double[] position=new double[3];
                int absoluteY= ((int)(((leftWheelLineDetectCoords[1]+rightWheelLineDetectCoords[1]))/2.0)/30)*30;
                position[0]=odo.getX();
                position[1]=absoluteY;
                if(leftWheelLineDetectCoords[0]<rightWheelLineDetectCoords[0]){
                        position[2]=NORTH+dT;
                }
                else{
                        position[2]=NORTH-dT+360;//+360 to ensure positive
                }
                boolean[]update={false,true,true};
                odo.setPosition(position,update);                        
        	}
        
        	//South travelling correction
        	if(travellingSouth && (lineCounter==2)){
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
        	if(travellingWest && (lineCounter==2)){
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
        	if(travellingEast && (lineCounter==2)){
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
        	if(lineCounter==2)
        		lineCounter=0;
        }

        public boolean checkLineLeft()
        {
        	int lLight=robo.getLeftLightValue();
        	if(leftLightValue-lLight>(LIGHT_DIFFERENCE_CONSTANT)){//)&& rLight<RIGHT_LINE_VALUE_CONSTANT)
        		leftLightValue=lLight;
        		return true;}
        	else{
        		leftLightValue=lLight;
        		return false;}
        	}
        	//otherwise actually check
        
        public boolean checkLineRight(){
        	int rLight=robo.getRightLightValue();
        	if(rightLightValue-rLight>(LIGHT_DIFFERENCE_CONSTANT)){//)&& rLight<RIGHT_LINE_VALUE_CONSTANT)
        		rightLightValue=rLight;
        		return true;}
        	else{
        		rightLightValue=rLight;
        		return false;}
        	
        	
        	
  
        	
        }

        public boolean keepGoing(double xCurrent, double yCurrent, double xDest, double yDest)
    	{
    		double distanceFromPoint =Math.sqrt(((xDest-xCurrent)*(xDest-xCurrent))+((yDest-yCurrent)*(yDest-yCurrent)));
    		LCD.drawString("D:"+distanceFromPoint, 0, 4);
    		if(distanceFromPoint<TRAVELTO_GOAL_THRESHOLD)// || (this.distance!=0 && this.distance<distanceFromPoint))//distance should keep getting smaller
    		{
    			RConsole.println("keep going false");
    			
    			//this.distance=distanceFromPoint;
    			
    			return false;
    		}
    		else
    		{
    			//this.distance=distanceFromPoint;Sound.buzz();
    			
    			return true;
    		}
    	}
}
