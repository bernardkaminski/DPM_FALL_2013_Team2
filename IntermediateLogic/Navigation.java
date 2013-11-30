
package IntermediateLogic;
import lejos.nxt.*;
import Hardware.TwoWheeledRobot;
import MainLogic.Search;
import lejos.nxt.Button;
import lejos.nxt.comm.*;

/**
* the class that allows the robot to move to specific locations. this class also corrects the odometer while traveling to locations.
* @author Bernie, Connor
*
*/
public class Navigation {
        
		//Properties of navigation
        private TwoWheeledRobot robo;
        private Odometer odo;
       
        //Variables
        private boolean hasBlock=false;
       
        //Constants    
        private final int SLOW=100;
        private final int HARD_TRAVEL=30;
        private final int DETECT_DISTANCE=25;
        private final int STANDARD=350;
        private final int FAST=300;
        private final int FAST_ROTATION_SPEED=150;
        public final double NORTH=0;
        public final double SOUTH=180;
        public final double WEST=270;
        public final double EAST=90;        
        private final double GRIDLINE_ANGLE_THRESHOLD=20;//35
        private final double TRAVELTO_GOAL_THRESHOLD=3;
        private final double TURNTO_THRESHOLD=2.0;
        private final double TRAVELTO_TURN_THRESHOLD=4.5;//WAS 4
        private final int LINE_LIGHTVALUE_MAX=515;
        private final int LINE_LIGHTVALUE_MIN=400;  
        private final int SLOW_TURNTHRESHOLD=8;
        private int leftLightValue;
        private int rightLightValue;

           
        //Constructor
        public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd)
        {
            this.robo=robo;
            this.odo=odo;        
            //Turn on light sensors
            robo.turnOnRightLight();              
            robo.turnOnLeftLight();
            robo.startLeftLP();
            robo.startRightLP();                                   
        }
         
       /**
        * Travels to a specific coordinate
        * @author Connor, Bernie
        * @param correct boolean whether robot should align itself with gridline when it it reaches desired point or not
        * @param hasBlock boolean whether robot has a block or not, used for hard coded obstacle avoidance
        * @param x desired x coordinate to travel to (cm)
        * @param y desired y coordinate to travel to (cm)
        */
       public void travelTo(boolean correct ,boolean hasBlock, double x, double y)
       {      
           //This is hard coded obstacle avoidance if the robot has a block and we want it to travel directly to the zone
    	   //Note used in competition, used scan results instead (see Path class)
    	   if(hasBlock)
           {              
            	robo.setForwardSpeed(SLOW);
            	while(keepGoing(odo.getX(), odo.getY(), x, y))
                {
            		if(robo.scanWithTopsensor(2)<DETECT_DISTANCE){
		                robo.stopMotors();
		                double head = odo.getTheta()+90;
		                if(head>=360)
		                {
		                	head-=360;
		                }
		                //Avoid obstacle by turn 90 degrees to the right
		                turnTo(head,true);
		                travelSetDistanceStraight(HARD_TRAVEL);
		                //Recursively call travel to in order to continue avoiding block
		                travelTo(false, true, x, y);  
            		}
            		
	            	//Calculate minimum angle to turn to
	                double minAng = (Math.atan2((x - odo.getX()), (y - odo.getY()))) * (180.0 / Math.PI);
	                //Correct the heading to make it always positive
	                if (minAng < 0)
	                {
	                	minAng += 360; 
	                } 
	                //handle exceptions over 0-360 line
	                double thetaCorrection=Math.abs(minAng-odo.getTheta()); 
	                if(thetaCorrection>180)
	                {
	                   thetaCorrection-=360;
	                }
	                robo.setForwardSpeed(FAST);
	                robo.goForward();
	                if(Math.abs(thetaCorrection)>TRAVELTO_TURN_THRESHOLD)
	                {  
	                	turnTo(minAng,true);
	                }     
                 }
            	
                 if(correct)
                 {
                	 fineTune();  
                 }                                           
                 else
                 {	 
                	 //Robot at destinations
                	 robo.stopMotors(); 
                 }
            }
            
    	   	//Robot does not have a block
            else{  
            	
                while(keepGoing(odo.getX(), odo.getY(), x, y))
                {
                	//Calculate minimum angle to turn to
                	double minAng = (Math.atan2((x - odo.getX()), (y - odo.getY()))) * (180.0 / Math.PI);
                    //Make heading always positive
                	if (minAng < 0)
                    {
                    	minAng += 359.0; 
                    }
                    double thetaCorrection=Math.abs(minAng-odo.getTheta()); 
                    //Handle exceptions over 0-360 line
                    if(thetaCorrection>180)
                    {
                       thetaCorrection-=360;
                    }
                    //Robot is now oriented properly
                    robo.setForwardSpeed(FAST);
                    robo.goForward();
                    if(Math.abs(thetaCorrection)>TRAVELTO_TURN_THRESHOLD)
                    {  
                    	turnTo(minAng,true);
                    }
                       
                }
	            if(correct)
	            {
	            	//Align with grid line
	            	fineTune();
	            }                                       
	            else
	            {
	            	//Robot at its destination
	                robo.stopMotors();
	            }  
            }                          
        }
         
       /**
        * This method turns the robot to a desired angle
        * @author Connor, Bernie
        * @param angle desired angle to turn to in degrees
        * @param stop boolean whether the robot should stop after turning or not
        */
       public void turnTo(double angle, boolean stop) 
       {
    	   //Make all 360 angles equal to zero, arbitrary
    	   if((int)angle==360)
    	   {
    		   angle = 0;
    	   }
    	   
           double error = angle - odo.getTheta();             
           while (Math.abs(error) > TURNTO_THRESHOLD) 
           {   
        	   //Turn at different speeds depending on how far the robot has to turn
        	   if(Math.abs(error)<SLOW_TURNTHRESHOLD)
        	   {
        		   robo.setRotationSpeed(SLOW);
        	   }
        	   else
        	   {
        		   robo.setRotationSpeed(FAST_ROTATION_SPEED);
        	   }
        	   
        	   error = angle - odo.getTheta();
               //Rotate robot in correct direction   
               if (error < -180.0) 
               {
            	   robo.rotateClockwise(); 
               }
               else if (error < 0.0) 
               {
            	   robo.rotateCounterClockwise(); 
               }
               else if (error > 180.0) 
               {
                   robo.rotateCounterClockwise(); 
               }
               else 
               {
            	   robo.rotateClockwise(); 
               }
	       }
	       if (stop) 
	       { 
	    	   robo.stopMotors();
	       }
	  }

       /**
        * This method is our odometry correction, it aligns the robot with a gridline and updates the odometer
        * @author Connor,Bernie
        * 
        */
       public void fineTune()
       {
    	   //Obtain cardinal heading
    	   String card = getCardinalHeading();  
    	   //Turn to current cardinal heading in preparation for fine tune
	       if(card.equals("NORTH"))
	       {
	    	   turnTo(0, true);
	       }
	       if(card.equals("WEST"))
	       {
	    	   turnTo(270, true);
	       }
	       if(card.equals("SOUTH"))
	       {
	           turnTo(180, true);
	       }
	       if(card.equals("EAST"))
	       {
	           turnTo(90, true);
	       }
	       //Set new desired speed (much slower)
	       robo.setForwardSpeed(SLOW);
	       //Stop motors
	       robo.stopMotors();    
	       //Get initial light values
	       leftLightValue=robo.getLeftLightValue();
	       rightLightValue=robo.getRightLightValue();      
	       while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
	       {      
	    	   //Robots left wheel not on the line yet
	           robo.startLeftMotor();   
	    	   leftLightValue=robo.getLeftLightValue();
	       }
	       //Robots left wheel on the line
		   robo.stopLeftMotor();
		   while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
		   {               
			   robo.startRightMotor();
		       rightLightValue=robo.getRightLightValue();    
		   }
		   //Robots right wheel on the line
		   robo.stopRightMotor();
		   String heading=getCardinalHeading();
		   //Correct the odometer
		   correctOdometer(heading);
        }
  
       /**
        * @author Bernie
        * @param d double representing desired distance to travel forward
        */
       public void travelSetDistanceStraight(double d)
       {
	        double startingX = odo.getX();
	        double startingY = odo.getY();
	        robo.setForwardSpeed(STANDARD);
	        while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
	        {
	            robo.goForward();
	        }
	        robo.stopMotors();
        }
       
       /**
        * @author Bernie
        * @param d double representing desired distance to travel backward
        */
       public void travelSetDistanceBackwards(double d)
       {
                double startingX = odo.getX();
                double startingY = odo.getY();
                robo.setForwardSpeed(STANDARD);
                while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
                {
                    robo.goBackward();
                }
                robo.stopMotors();
        }
       
       /**
        * @author Connor
        * @return returns a string representing the cardinal heading "NORTH", "SOUTH", "EAST", "WEST"
        */
       public String getCardinalHeading()
       {
    	   if(odo.getTheta() < (WEST + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta() > (WEST - GRIDLINE_ANGLE_THRESHOLD))
    	   {
                //If robot is heading west (within threshold)
                //Use light sensors to update X coordinate as robot travels          
                return "WEST";
           }
           else if(odo.getTheta()< (EAST + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta()> (EAST - GRIDLINE_ANGLE_THRESHOLD))
           {
        	   //If robot is heading west (within threshold)
        	   //Use light sensors to update X coordinate as robot travels
        	   return "EAST";
           }
           else if(odo.getTheta()<(NORTH + GRIDLINE_ANGLE_THRESHOLD) || odo.getTheta()>(NORTH - GRIDLINE_ANGLE_THRESHOLD + 360))
           {
        	   //If robot is heading north (within threshold)
        	   //Use light sensors to update Y coordinate as the robot travels                               
        	   return "NORTH";
           }       
           else if(odo.getTheta()<(SOUTH + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta()>(SOUTH - GRIDLINE_ANGLE_THRESHOLD))
           {
        	   //If robot is heading north (within threshold)
        	   //Use light sensors to update Y coordinate as the robot travels                               
        	   return "SOUTH";
           }
           else 
           {
        	   return "NOT CARDINAL";
           }       
       }
      
       /**
        * This method updates the corrects the odometer depending which cardinal direction the robot is travelling in
        * @author Connor
        * @param heading String "NORTH", "SOUTH", "EAST", "WEST", used to determine which coordinate to correct
        */
       public void correctOdometer(String heading)
       {
    	   int y=(int) odo.getY();
    	   int x=(int) odo.getX();
    	   //North travelling correction
           if(heading.equals("NORTH"))
           {
	            double[] position=new double[3];
	            //Determine what multiple of the 30 is closest to current position (according to the odometer)
	            int factorY= ((y+15)/30);          
	            int factorX= ((x+15)/30);
	            position[0]=factorX*30;
	            position[1]=factorY*30;           
	            position[2]=NORTH;
	            //Correct y and theta
	            boolean[]update={false,true,true};
	            odo.setPosition(position,update);                       
           }
   
           //South travelling correction
           if(heading.equals("SOUTH"))
           {
	            double[] position=new double[3];
	            //Determine what multiple of the 30 is closest to current position (according to the odometer)
	            int factorY= ((y+15)/30);          
	            int factorX= ((x+15)/30);
	            position[0]=factorX*30;
	            position[1]=factorY*30;          
	            position[2]=SOUTH;
	            //Correct y and theta
	            boolean[]update={false,true,true};
	            odo.setPosition(position,update);              
           }
   
           //West travelling correction
           if(heading.equals("WEST"))
           {
	            double[] position=new double[3];  
	            //Determine what multiple of the 30 is closest to current position (according to the odometer)
	            int factorY= ((y+15)/30);          
	            int factorX= ((x+15)/30);
	            position[0]=factorX*30;
	            position[1]=factorY*30;              
	            position[2]=WEST;
	            //Correct x and theta
	            boolean[]update={true,false,true};
	            odo.setPosition(position,update);                  
           }
   
           //East travelling correction
           if(heading.equals("EAST"))
           {
	            double[] position=new double[3];  
	            //Determine what multiple of the 30 is closest to current position (according to the odometer)
	            int factorY= ((y+15)/30);          
	            int factorX= ((x+15)/30);
	            position[0]=factorX*30;
	            position[1]=factorY*30;              
                position[2]=EAST;
                //Correct x and theta
                boolean[]update={true,false,true};
                odo.setPosition(position,update);                   
           }
        }

       /**
        * This method is a helper method for travelTo is indicates whether the robot is at its destination or not
        * i.e. should the robot keep going or not
        * @author Bernie
        * @param xCurrent
        * @param yCurrent
        * @param xDest
        * @param yDest
        * @return returns a boolean whether the robot should continue traveling or not (is it at the point yet or not)
        */
       public boolean keepGoing(double xCurrent, double yCurrent, double xDest, double yDest)
       {
	        double distanceFromPoint =Math.sqrt(((xDest-xCurrent)*(xDest-xCurrent))+((yDest-yCurrent)*(yDest-yCurrent)));
	        if(distanceFromPoint<TRAVELTO_GOAL_THRESHOLD)
	        {                       
                return false;
	        }
	        else
	        {
                return true;
	        }
       }

       /**
        * This method places a block in the desired zone
        * @author Bernie
        */
       public void stack()
       {
            //Make sure that robot is positioned perfectly for the placing the block, needs to put the blocks in the same place everytime
    	   	superTune();
    	   	
            //Move forward slightly so claw is inside the green zone
            travelSetDistanceStraight(5);
            
            //Move forward slowly 
            robo.setForwardSpeed(SLOW);
            int leftLightValue=robo.getLeftLightValue();
            int rightLightValue=robo.getRightLightValue();      
            //Turn to 45, since robot is always at bottom left corner of the green zone this will always place the block in the bottom left square of green zone
            turnTo(45, true);
            //The below is the same as fine tune, except now we are align robot at exactly 45 degrees
            while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
            {               
	             robo.startLeftMotor();   
	             leftLightValue=robo.getLeftLightValue();
            }    
            robo.stopLeftMotor(); 
            while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
            {               
                 robo.startRightMotor();
                 rightLightValue=robo.getRightLightValue();    
            }
            robo.stopRightMotor();
            //Place block down
            robo.dropBlock();
            travelSetDistanceBackwards(18);
            robo.raiseClaw();
            turnTo(0, true);             
        }
       
       /**
        * This place stacks the second block
        * @author Bernie
        */
       public void stackSecond()
       {
    	    //Essentially the same method as stack() however for the second block
            superTune();
            turnTo(45, true);
            travelSetDistanceStraight(4); 
            robo.setForwardSpeed(SLOW);    
            int leftLightValue=robo.getLeftLightValue();
            int rightLightValue=robo.getRightLightValue();      
            while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
            {               
                 robo.startLeftMotor();   
                 leftLightValue=robo.getLeftLightValue();
            }        
            robo.stopLeftMotor();
            robo.startRightMotor();          
            while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
            {               
                 robo.startRightMotor();
                 rightLightValue=robo.getRightLightValue();    
            }
            
            robo.stopRightMotor();
            travelSetDistanceBackwards(5);
            robo.dropBlock();
            travelSetDistanceBackwards(5);
            travelSetDistanceStraight(5);
            travelSetDistanceBackwards(18);
            robo.raiseClaw();
            turnTo(0, true);
            fineTune();
        }

       /**
        * This method is a additional odometer correct used right before robot desposits/stacks a block
        */
        public void superTune()
        {
        	turnTo(90, true);
            travelSetDistanceBackwards(5);
            fineTune();
            turnTo(0, true);
            travelSetDistanceBackwards(5);
            fineTune();
        }
}