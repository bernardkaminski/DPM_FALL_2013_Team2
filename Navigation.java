
import lejos.nxt.comm.RConsole;

/**
 * the class that allows the robot to move tospecific locations. this class also corrects the odometer while traveling to locations. 
 * @author Bernie, Connor
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
             
        private final int SLOW=100;
        private final int STANDARD=200;
        private final int FAST=300;
        private final double NORTH=0;
        private final double SOUTH=180;
        private final double WEST=270;
        private final double EAST=90;     
        private final double GRIDLINE_ANGLE_THRESHOLD=35;
        private final double TRAVELTO_GOAL_THRESHOLD=4;
        private final double TURNTO_THRESHOLD=1.0; 
        private final double TRAVELTO_TURN_THRESHOLD=4;
        private final int LINE_LIGHTVALUE_MAX=515;
        private final int LINE_LIGHTVALUE_MIN=400;   
        private int leftLightValue;
        private int rightLightValue;
 
            
        //Constructor
        public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd){
                this.robo=robo;
                this.odo=odo;
                this.bd=bd;
                robo.turnOnRightLight();               
            	robo.turnOnLeftLight();
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
                
                else{   
                        while(keepGoing(odo.getX(), odo.getY(), x, y))
                        {
                        		
                        		double minAng = (Math.atan2((x - odo.getX()), (y - odo.getY()))) * (180.0 / Math.PI); 
                                if (minAng < 0) minAng += 359.0; //correct heading 
                                double thetaCorrection=Math.abs(minAng-odo.getTheta()); //handle exceptions over 0-360 line 
                                
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
                        fineTune();                                             
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
        
        public void fineTune(){
        	robo.setForwardSpeed(SLOW);
        	robo.stopMotors();     	
        	leftLightValue=robo.getLeftLightValue();
        	rightLightValue=robo.getRightLightValue();       	
        
        	while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN)){        		        			
    			robo.startLeftMotor();    
    			leftLightValue=robo.getLeftLightValue();			
    		}
        	
    		robo.stopLeftMotor();
    		RConsole.println("On left line");
    		while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN)){        		        			
    			robo.startRightMotor();
            	rightLightValue=robo.getRightLightValue();     	
    		}
    		
    		robo.stopRightMotor();
    		RConsole.println("On right line"); 	
        	String heading=getCardinalHeading();
        	RConsole.println("H:"+heading);
        	correctOdometer(heading);
        	
        }
        //Investigate block
        /**
         * this method uses the BlockDifferentiator class to distinguish blocks 
         */
        public void investigateBlock(){
                
        }
               
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
                else if(odo.getTheta()<(NORTH + GRIDLINE_ANGLE_THRESHOLD) || odo.getTheta()>(NORTH - GRIDLINE_ANGLE_THRESHOLD + 360)){
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
       
        public void correctOdometer(String heading){
     	
        	int y=(int) odo.getY();
        	int x=(int) odo.getX();
        	
        	//North travelling correction
            if(heading.equals("NORTH")){
            	double[] position=new double[3];           
            	int factorY= ((y+15)/30);           	
            	int factorX= ((x+15)/30);
            	position[0]=factorX*30;
            	position[1]=factorY*30;            
            	position[2]=NORTH;
            	boolean[]update={false,true,true};
            	odo.setPosition(position,update);                        
            }
    
            //South travelling correction
            if(heading.equals("SOUTH")){
            	double[] position=new double[3];
            	int factorY= ((y+15)/30);           	
            	int factorX= ((x+15)/30);
            	position[0]=factorX*30;
            	position[1]=factorY*30;           
            	position[2]=SOUTH;
            	boolean[]update={false,true,true};
            	odo.setPosition(position,update);
                               
            }
    
            //West travelling correction
            if(heading.equals("WEST")){
            	double[] position=new double[3];           
            	int factorY= ((y+15)/30);           	
            	int factorX= ((x+15)/30);
            	position[0]=factorX*30;
            	position[1]=factorY*30;               
            	position[2]=WEST;
            	boolean[]update={true,false,true};
            	odo.setPosition(position,update);                   
            }
    
            //East travelling correction
            if(heading.equals("EAST")){
            	 double[] position=new double[3];           
            	 int factorY= ((y+15)/30);           	
             	 int factorX= ((x+15)/30);
             	 position[0]=factorX*30;
            	 position[1]=factorY*30;               
                 position[2]=EAST;
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