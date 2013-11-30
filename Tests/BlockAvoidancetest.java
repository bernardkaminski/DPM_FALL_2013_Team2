package Tests;

/*
*
*This is the test class used to test blockavoidance as used in the demo on Friday, 11/15/2013
*the code is a very improvised solution to blockavoidance. the robot is hard coded to move a specific set of distances after detecting a block
*
*@author Deepak Sharma 260468268, Adip Narang 260475577
*
*for code see: Odometer, Navigation, TwoWheeledRobot
*for testing documents see: blockavoidance test data in the appendix of the final report
**
*/

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import Hardware.TwoWheeledRobot;
import IntermediateLogic.*;

public class BlockAvoidancetest {
	
	static final double DETECT_DISTANCE = 30;
	static final double HARD_TRAVEL = 31;
	static final int FAST = 100;
	static final double TRAVELTO_TURN_THRESHOLD = 3;
		
	TwoWheeledRobot robo;
	Odometer odo;
	Navigation nav;
		
	public BlockAvoidancetest(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd, Navigation nav){
    this.robo=robo;
    this.odo=odo;
    this.nav=nav;
    
    robo.turnOnRightLight();              
    robo.turnOnLeftLight();
    robo.startLeftLP();
    robo.startRightLP();                                       
}
	
	public void blockAvoid(double x, double y){
    		RConsole.println("T:"+x+"   "+y);
            RConsole.println("hasblock entered");
            //robo.startUsBottom();
            robo.setForwardSpeed(50);
            
            while(nav.keepGoing(odo.getX(), odo.getY(), x, y))
           {
            
            if(robo.scanWithTopsensor(2)<DETECT_DISTANCE){
            RConsole.print("DETECT DISTANCE");
                robo.stopMotors();
                
                double head =odo.getTheta()+90;
                
                if(head>=360)
                {
                	head-=360;
                }
                
                nav.turnTo(head,true);
                
                nav.travelSetDistanceStraight(HARD_TRAVEL);
                                                
                nav.travelTo(false, true, x, y);
                               
                }
                
                double minAng = (Math.atan2((x - odo.getX()), (y - odo.getY()))) * (180.0 / Math.PI);
                if (minAng < 0) minAng += 360; //correct heading
                double thetaCorrection=Math.abs(minAng-odo.getTheta()); //handle exceptions over 0-360 line
                    if(thetaCorrection>180)
                     {
                        thetaCorrection-=360;
                     }
                    
                     robo.setForwardSpeed(FAST);
                     robo.goForward();
                                         
                     if(Math.abs(thetaCorrection)>TRAVELTO_TURN_THRESHOLD)
                     {  
                     nav.turnTo(minAng,true);
                     }
                    
                 }
                            
            }
}
