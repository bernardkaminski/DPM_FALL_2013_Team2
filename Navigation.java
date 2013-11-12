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
        private final double TURNTO_THRESHOLD=1.0;
        private final double ANGLE_THRESHOLD=.5;
        private final double GRIDLINE_LIGHTVALUE_THRESHOLD=15; //When light sensor returns value less than this threshold a line has been detected
        private final int CLOSE=5;
        private final int SLOW=50;
        private final int STANDARD=200;
        private final int FAST=400;
        private final double NORTH=0;
        private final double SOUTH=180;
        private final double WEST=270;
        private final double EAST=90;
        private final double SENSOR_OFFSET=3.0;
        private final int DETECTION_TIME_DIFFERENCE=2;
       
        private final double GRIDLINE_ANGLE_THRESHOLD=20;
        private final double TRAVELTO_GOAL_THRESHOLD=1;
        private final double TRAVELTO_TURN_THRESHOLD=4;
        // the light constant is a constant for difference the line constant is an absolute filter based on data taken 
        private boolean leftLineDetect= false;
        private boolean rightLineDetect=false;
        private int newLeftLightValue;
        private int oldLeftLightValue;
        private int newLeftLightDifference;
        private int oldLeftLightDifference;
        private int newRightLightValue;
        private int oldRightLightValue;
        private int newRightLightDifference;
        private int oldRightLightDifference;
        private boolean clearToDifference;
        private double leftTimeDetection;
        private double rightTimeDetection;
       
        
       
       
            
            
        //Constructor
        public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd){
                this.robo=robo;
                this.odo=odo;
                this.bd=bd;
                robo.startLeftLP();
                robo.startRightLP();
                
                                               
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
                
                else{   double []leftWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by left wheel
                		double []rightWheelLineDetectCoords=new double[2]; //Coordinates of robot [x,y] when line is detected by right wheel 
                		
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
                                	turnTo(minAng,true);
                                	
                                	}
                                robo.goForward();
                                String heading=getCardinalHeading(); //get cardinal heading of robot, required for correction
                                LCD.drawString("Travelling: "+heading,0,5);                                    
                                
                                if(robo.lineLeft()){ 
                                	RConsole.println("Left Line Detected");
                                	leftWheelLineDetectCoords[0]=odo.getX();
                                    leftWheelLineDetectCoords[1]=odo.getY();
                                    RConsole.println("LX"+leftWheelLineDetectCoords[0]);
                                    RConsole.println("LY"+leftWheelLineDetectCoords[1]);
                                    leftLineDetect=true;
                                    leftTimeDetection=System.currentTimeMillis()/Math.pow(10, 3);                                
                                }
                                  
                                if(robo.lineRight()){
                                	RConsole.println("Right Line Detected ");
                                	rightWheelLineDetectCoords[0]=odo.getX();
                                    rightWheelLineDetectCoords[1]=odo.getY();
                                    RConsole.println("RX"+rightWheelLineDetectCoords[0]);
                                    RConsole.println("RY"+rightWheelLineDetectCoords[1]);
                                    rightLineDetect=true;
                                    rightTimeDetection=System.currentTimeMillis()/Math.pow(10, 3);
                                }
                                
                                if(leftLineDetect && rightLineDetect && Math.abs(leftTimeDetection-rightTimeDetection)<DETECTION_TIME_DIFFERENCE){
                                	//detection time difference ensure corrections 
                                	//will only ever occur if robot is somewhat parallel to gridline
                                	//makes sure that even if it misses a line it will not mess up the detection
                                	RConsole.println("Corrected");                               	
                                	correctOdometer(heading,leftWheelLineDetectCoords, rightWheelLineDetectCoords);                               	
                                	rightLineDetect=false;
                                	leftLineDetect=false;
                                	}
                                
                                              
                        }
                        robo.stopMotors();   
                     
                       }   
                                
        }
          
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
                               
                                
                                robo.goForward();
                        }
                        robo.stopMotors();
                }
        
        
        public String getCardinalHeading(){
        		if(odo.getTheta() < (WEST + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta() > (WEST - GRIDLINE_ANGLE_THRESHOLD)){
                //If robot is heading west (within threshold)
                //Use light sensors to update X coordinate as robot travels           
                return "WEST";
                }
                else if(odo.getTheta()< (EAST + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta()> (EAST - GRIDLINE_ANGLE_THRESHOLD)){
                //If robot is heading west (within threshold)
                //Use light sensors to update X coordinate as robot travels
                return "EAST";
                }
                else if(odo.getTheta()<(NORTH + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta()>(NORTH - GRIDLINE_ANGLE_THRESHOLD)){
                //If robot is heading north (within threshold)
                //Use light sensors to update Y coordinate as the robot travels                                
                return "NORTH";
                }        
                else if(odo.getTheta()<(SOUTH + GRIDLINE_ANGLE_THRESHOLD) && odo.getTheta()>(SOUTH - GRIDLINE_ANGLE_THRESHOLD)){
                //If robot is heading north (within threshold)
                //Use light sensors to update Y coordinate as the robot travels                                
                return "SOUTH";
                }
                else {return "NOT CARDINAL";}
                
        }
       
        public void correctOdometer(String heading,double[] leftWheelLineDetectCoords,double[]rightWheelLineDetectCoords){
        	
        	double dXdetectionPoints=Math.abs(leftWheelLineDetectCoords[0]-rightWheelLineDetectCoords[0]);
            double dYdetectionPoints=Math.abs(leftWheelLineDetectCoords[1]-rightWheelLineDetectCoords[1]);
            double dTY=Math.atan2(dXdetectionPoints, dYdetectionPoints)*180/(Math.PI);
            double dTX= Math.atan2(dYdetectionPoints, dXdetectionPoints)*180/(Math.PI);
            RConsole.println("dTY: "+dTY);
            RConsole.println("dTX:"+dTX);
            
        	//North travelling correction
            if(heading.equals("NORTH")){
            double[] position=new double[3];
            int y= (int)((leftWheelLineDetectCoords[1]+rightWheelLineDetectCoords[1])/2.0);
            int absoluteY= 30*((y+15)/30);
            RConsole.println("absoluteY "+absoluteY);
            position[0]=odo.getX();
            position[1]=absoluteY;
            if(leftWheelLineDetectCoords[0]<rightWheelLineDetectCoords[0]){
                    position[2]=NORTH+dTY;
            }
            else{
                    position[2]=NORTH-dTY+360;//+360 to ensure positive
            }
            RConsole.println("CorrectedH:"+position[2]);
            boolean[]update={false,true,true};
            odo.setPosition(position,update);                        
            }
    
            //South travelling correction
            if(heading.equals("SOUTH")){
            double[] position=new double[3];
            int y= (int)((leftWheelLineDetectCoords[1]+rightWheelLineDetectCoords[1])/2.0);
            int absoluteY= 30*((y+15)/30);
            RConsole.println("absoluteY "+absoluteY);
            position[0]=odo.getX();
            position[1]=absoluteY;
            if(leftWheelLineDetectCoords[0]<rightWheelLineDetectCoords[0]){
                    position[2]=SOUTH-dTY;
            }
            else{
                    position[2]=SOUTH+dTY;
            }
            RConsole.println("CorrectedH:"+position[2]);
            boolean[]update={false,true,true};
            odo.setPosition(position,update);                        
            }
    
            //West travelling correction
            if(heading.equals("WEST")){
            double[] position=new double[3];
            int x= (int)((leftWheelLineDetectCoords[0]+rightWheelLineDetectCoords[0])/2.0);
            int absoluteX= 30*((x+15)/30);
            RConsole.println("absoluteX "+absoluteX);
            position[0]=absoluteX;
            position[1]=odo.getY();
            if(leftWheelLineDetectCoords[1]<rightWheelLineDetectCoords[1]){
                    position[2]=WEST+dTX;
            }
            else{
                    position[2]=WEST-dTX;
            }
            RConsole.println("CorrectedH:"+position[2]);
            boolean[]update={true,false,true};
            odo.setPosition(position,update);                        
            }
    
            //East travelling correction
            if(heading.equals("EAST")){
            double[] position=new double[3];
            int x= (int)((leftWheelLineDetectCoords[0]+rightWheelLineDetectCoords[0])/2.0);
                    
            int absoluteX= 30*((x+15)/30);
            RConsole.println("absoluteX "+absoluteX);
            
            position[0]=absoluteX;
            position[1]=odo.getY();
            if(leftWheelLineDetectCoords[1]<rightWheelLineDetectCoords[1]){
                    position[2]=EAST-dTX;
            }
            else{
                    position[2]=EAST+dTX;
            }
            RConsole.println("CorrectedH:"+position[2]);
            boolean[]update={true,false,true};
            odo.setPosition(position,update);                        
            }

        	
        }

        public boolean keepGoing(double xCurrent, double yCurrent, double xDest, double yDest)
            {
                    double distanceFromPoint =Math.sqrt(((xDest-xCurrent)*(xDest-xCurrent))+((yDest-yCurrent)*(yDest-yCurrent)));
                    
                    if(distanceFromPoint<TRAVELTO_GOAL_THRESHOLD)// || (this.distance!=0 && this.distance<distanceFromPoint))//distance should keep getting smaller
                    {
                            
                            
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