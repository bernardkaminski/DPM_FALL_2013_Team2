/** 
Name: 
    Localizer Class 
Authors: 
    GROUP 6 
    Connor Stein (260457550) 
    Miguel Aroca-Ouellette (260454364) 
Purpose: 
    Has two types of ultrasound localization: 
        1)Falling Edge: Based on the robot not facing the wall. However, has cases to handle when it starts facing the wall. 
                        Uses the detection of the beginning of walls to the left and behind the robot in order to localize. 
        2)Rising Edge: Based on the robot facing the wall. However, has cases to handle when it stars not facing the wall. 
                        Uses the detection of the end of walls to the right and in front of the robot in order to localize. 
    Also has a private filter method to clip large values returned by the ultrasonic sensor. 
**/
package IntermediateLogic;  
import Hardware.TwoWheeledRobot;
import lejos.nxt.LCD; 
import lejos.nxt.Sound; 
import lejos.nxt.UltrasonicSensor; 
import lejos.nxt.comm.RConsole;
  
public class Localizer { 
    public enum LocalizationType { FALLING_EDGE, RISING_EDGE }; 
    public static double ROTATION_SPEED = 100; //Speed of rotation of robot 
  
    private Odometer odo; 
    private TwoWheeledRobot robot; 
    private UltrasonicSensor us; 
    private LocalizationType locType; 
    private final int WALL_FILTER=10; 
    //Description of WALL_FILTER: 
    //Falling Edge: Number of no wall values which must be detected in a row to know it has stopped seeing a wall when rotating. 
    //Rising Edge: Number of wall values which must be detected in a row to know it has detected a wall when rotating. 
      
    //constructor 
    public Localizer(Odometer odo, TwoWheeledRobot robot) { 
        this.odo = odo; 
        this.robot = robot;  
        this.locType = LocalizationType.FALLING_EDGE; 
    } 
      
    public void doLocalization() { 
        double angleA, angleB, dTheta, margin_temp, margin_avg; 
        double currDis; //current distance from the wall 
        int wall_count=0; //Falling Edge: counts number of times a lack of wall is detected. Rising Edge: counts number of time a wall is detected 
        int noiseTop=50; //Noise Band Top 
        int noiseBottom=35; //Noise Band Bottom 
          
        if (locType == LocalizationType.FALLING_EDGE) { 
            //Falling Edge 
              
            //Step 1: rotate the robot until it sees no wall 
            robot.rotateClockwise(); 
            while(wall_count<WALL_FILTER){ //rotate until wall_count is greater than wall_filter 
                //keep rotating 
                if(getFilteredData()>noiseTop){ //only increment if no wall is detected (in this case >than top of noise band) 
                    wall_count++; 
                } 
                
            } 
            //Step 2: Reset the odometer as falling edge detection is based off robot starting facing away from the wall - Which it now is. 
            odo.setPosition(new double [] {0.0,0.0,0.0}, new boolean [] {true, true, true}); 
            wall_count=0; 
            
              
            //Step 3: Rotate until the robot sees a wall, then latch the angle 
            while(true){  
                currDis=getFilteredData(); //get filtered data on ultrasound detected distance 
                if(currDis<=noiseTop&&currDis>noiseBottom){ //if the current US distance is within the noise band then enter the loop 
                    margin_temp=odo.getTheta(); //store the entry angle 
                    while(currDis<=noiseTop&&currDis>noiseBottom){ //loop while the US distance stays within the noise band 
                        currDis=getFilteredData(); 
                    } 
                    //at this point the US distance has broken out of the noise band 
                    if(currDis<=noiseBottom){ 
                        /* 
                        if the US distance which broke out of the noise band is < than the bottom of the noise band then store and average. 
                        Else the noise value was > and no wall has been detected so keep looping 
                        */
                        margin_avg=(margin_temp+odo.getTheta())/2; //take the average of the entry and exit (current) angle 
                        break; //the wall has been correctly detected so break out of the loop 
                    } 
                } 
            } 
            Sound.beep(); 
            angleA=margin_avg; //set angleA as the above average 
          
  
            //Step 4: Switch the rotation direction and wait until it sees no wall (same as first filter) 
            //*Note: Prevents angleB from being detected immediately after angleA has been deteced. 
            robot.rotateCounterClockwise(); 
            while(wall_count<WALL_FILTER){ //rotate until wall_count is greater than wall_filter 
                //keep rotating 
            	
                if(getFilteredData()>noiseTop){ //only increment if no wall is detected (in this case >than top of noise band) 
                    wall_count++; 
                } 
                LCD.drawString("W: "+wall_count, 0,5);
            } 
            wall_count=0; //reset counter 
              
            //Step 5: keep rotating until the robot sees a wall, then latch the angle 
            //NOTE: Same while loop as Step 3, see that while loop for comments 
            while(true){ 
                currDis=getFilteredData(); 
                if(currDis<=noiseTop&&currDis>noiseBottom){ 
                    margin_temp=odo.getTheta(); 
                    while(currDis<=noiseTop&&currDis>noiseBottom){ 
                        currDis=getFilteredData(); 
                    } 
                      
                    if(currDis<=noiseBottom){ 
                        margin_avg=(margin_temp+odo.getTheta())/2; 
                        break; 
                    } 
                } 
            } 
            Sound.beep(); 
            angleB=margin_avg; //store angle B 
              
            //Step 6: Calulate dTheta. Based off the trigonometry explained in the tutorial slides. 
            if(Math.abs(angleB)>Math.abs(angleA)){ 
                dTheta=45-(angleA+angleB)/2; 
            } 
            else{ 
                dTheta=225-(angleA+angleB)/2; 
            } 
              
            //Step 7: Update the odometer position 
            //was odo.getTheta()+180 + dTheta-2.329
            odo.setPosition(new double [] {odo.getX(), odo.getY(), odo.getTheta()+180.0+dTheta+17}, new boolean [] {true, true, true}); 
            /*The value of 2.329 above corresponds to the mean error we found when we took results. JUST CHANGED TO +13 WHEN ROBOT STARTS AT ORIGIN
             * We have subtracted it in order to compensate for this mean error. 
            */ 
            robot.stopMotors();  
            return; 
        } else { 
            /* 
             * The robot should turn until it sees the wall, then look for the 
             * "rising edges:" the points where it no longer sees the wall. 
             * This is very similar to the FALLING_EDGE routine, but the robot 
             * will face toward the wall for most of it. 
             */
            //detect a little further 
            noiseTop+=3; 
            noiseBottom+=3; 
            //Step 1: rotate the robot until it sees a wall 
            robot.rotateClockwise(); //start rotating 
            while(wall_count<WALL_FILTER){ //rotate until a wall has been detected enough times to overcome WALL_FILTER 
                //keep rotating 
                if(getFilteredData()<noiseTop){ 
                    wall_count++;   //increment wall_count only if it the filtered US value is smaller than the bottom of the noise band 
                } 
            } 
              
            //Step 2: Reset odometer as Rising Edge only works when robot starts facing a wall - Which it now is. 
            odo.setPosition(new double [] {0.0,0.0,0.0}, new boolean [] {true, true, true}); 
            wall_count=0; //reset counter 
              
            //Step 3: Keep rotating until the robot stops seeing a wall, then latch the angle 
            while(true){  
                currDis=getFilteredData(); //store current filtered data 
                if(currDis<noiseTop&&currDis>=noiseBottom){ //if the current US distance is within the noise band then enter loop 
                    margin_temp=odo.getTheta(); //store the entry angle 
                    while(currDis<noiseTop&&currDis>=noiseBottom){ //loop as long as the US distance is within the noise band 
                        currDis=getFilteredData(); 
                    } 
                    //at this point the US distance has broken out of the noise band 
                    if(currDis>=noiseTop){ 
                        /* 
                        if the US distance which broke out of the noise band is > than the top of the noise band then store and average. 
                        Else the noise value was < and a wall is still being detected so keep looping. 
                        */
                        margin_avg=(margin_temp+odo.getTheta())/2;//take average of entry and exit (current) angle 
                        break; //the end of the wall has been correctly detected so break out of the loop 
                    } 
                } 
            } 
            Sound.beep(); 
            angleA=margin_avg; //store angleA 
  
            //Step 4: Switch rotation direction and wait until it sees a wall 
            //*Note: Prevents angleB from being detected immediately after angleA has been deteced. 
            robot.rotateClockwise(); 
            while(wall_count<WALL_FILTER){ //rotate until enough wall detections break the filter 
                //keep rotating 
                if(getFilteredData()<noiseTop){ //only increment if a wall is being detected (in this case <than top of noise band) 
                    wall_count++; 
                } 
            } 
            wall_count=0; //reset counter 
              
            //Step 5: Keep rotating until the robot stops seeing a wall, then latch the angle 
            //NOTE: Same while loop as Step 3, see that while loop for comments 
            while(true){  
                currDis=getFilteredData(); 
                if(currDis<noiseTop&&currDis>=noiseBottom){ 
                    margin_temp=odo.getTheta(); 
                    while(currDis<noiseTop&&currDis>=noiseBottom){ 
                        currDis=getFilteredData(); 
                    } 
                    if(currDis>=noiseTop){ 
                        margin_avg=(margin_temp+odo.getTheta())/2;  
                        break; 
                    } 
                } 
            } 
            Sound.beep(); 
            angleB=margin_avg; //store angleB 
              
            //Step 6: Calculate the change in theta. Based off the trigonometry in the slides. 
            if(Math.abs(angleB)>Math.abs(angleA)){ 
                dTheta=45-(angleA+angleB)/2; 
            } 
            else{ 
                dTheta=225-(angleA+angleB)/2; 
            } 
              
            //Step 7: Update the odometer position 
            odo.setPosition(new double [] {odo.getX(), odo.getY(), odo.getTheta()+180.0+dTheta-2.51}, new boolean [] {true, true, true}); 
            /*The value of 2.51 above corresponds to the mean error we found when we took results. 
             * We have subtracted it in order to compensate for this mean error. 
            */ 
        } 
    } 
      
    private double getFilteredData() { 
        //Acts as a rough filter which removes too large values. 
        double distance; 
          
        // there will be a delay here 
        distance = robot.scanWithBottomsensor(2); 
         RConsole.println(""+distance); 
        // filter out large values and replace with max value. 60 in this case. 
        if (distance > 100){ 
            distance = 100; 
        } 
          
        return distance; 
    } 
}