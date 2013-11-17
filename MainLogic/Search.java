package MainLogic;
import Hardware.*;
import IntermediateLogic.*;
/*Two methods:
 * 
 * (1) GrabBlock:
 * Input a heading in degrees.
 * The robot will rotate at its current location to that heading.
 * The robot will then move forward while polling with the bottom US Sensor.
 * Once the grab distance+threshold is reached the robot will stop.
 * The claw will then lower.
 * The robot will then move forward with the claw lowered, slightly pushing the block.
 * The claw will then close and raise in one motion.
 * (If time permits, the robot will then check if a block is still on the ground in front of it.
 */

//changed two wheeled robot to asbolute degree

/**
 * 
 * @author Miguel
 * @version 1.0
 *
 */

import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

public class Search {

	public static final double DEFAULT_GRAB_DISTANCE = 13.5; //CM. Distance from wheel axle to closest part of block when block is pushed agains the robot.
	
	public final double SCAN_ROTATE_THRESH=4.0; //slightly greater than TURNTO_THRESHOLD in navigation
	public final double SCAN_DIST_THRESH=26; //70
	public final double SCAN_DIST_ERROR=5;
	public final int SCAN_ROTATE_SPEED=50;
	public final int BLOCK_COUNT_THRESH=10;//20 //3 @70
	private final int BLOCK_MAX=20; //20 @70
	private int ANGLE_OFFSET = 3;//4
	
	public final int CLAW_RAISE_ANGLE=85;
	public final int CLAW_RAISE_SPEED=250;
	public final int CLAW_RAISE_ACC=150;
	public final int CLAW_LOWER_ANGLE=-55;
	public final int CLAW_LOWER_SPEED=50;
	public final int CLAW_LOWER_ACC=80;
	
	//check scan point - assign cardinal directions
	public final double NORTH;
	public final double SOUTH;
	public final double EAST;
	public final double WEST;
	public final double POINT_BLOCKED_ERROR=25; //angle
	public final double POINT_BLOCKED_DIST=56;
	public final double POINT_BLOCKED_CHECK_DIST=POINT_BLOCKED_DIST+5;
	
	private TwoWheeledRobot robo;
	private Navigation nav;
	private Odometer odo;
	private BlockDifferentiator block_diff;
	
	
	public Search(TwoWheeledRobot robo,Navigation nav,Odometer odo,BlockDifferentiator block_diff){
		this.robo=robo;
		this.nav=nav;
		this.odo=odo;
		this.block_diff=block_diff;
		
		//assign cardinal directions
		this.NORTH=nav.NORTH;
		this.EAST=nav.EAST;
		this.SOUTH=nav.SOUTH;
		this.WEST=nav.WEST;
	}
	
	/**
	 * output:
	 * 0: NORTH scan point (0 if clear) (1 if blocked)
	 * 1: EAST scan point (0 if clear) (1 if blocked)
	 * 2: SOUTH scan point (0 if clear) (1 if blocked)
	 * 3: WEST scan point (0 if clear) (1 if blocked)
	 * 4: found block? (0 if no) (1 if yes)
	 */
	public int[] Scan(){
		double goalAngle=odo.getTheta()+150;
		double currScanDist,avgAngle;
		boolean onBlock=false;
		double BlockBeginning=0, BlockEnd,savedAngle;
		int blockType,blockCount=0;
		boolean atGoal=false;
		int[] result_output={0,0,0,0,0};
		int currAng;
		
		//handle boundary cases
		if(goalAngle>360){
			goalAngle-=360;
		}
		
		robo.setRotationSpeed(SCAN_ROTATE_SPEED);
		robo.rotateClockwise();
		Sound.beep();
		while(!atGoal||blockCount!=0){ //rotate until goal passed and blockCount=0
			
			if(Math.abs(odo.getTheta()-goalAngle)<SCAN_ROTATE_THRESH){ //at goal
				atGoal=true;
			}
			
			currScanDist=robo.scanWithBottomsensor(5);//get distance
			
			RConsole.println("S: "+currScanDist+"    "+blockCount);
			
			currAng=(int)odo.getTheta()+3;
			
			if(currAng>=360){
				currAng-=360;
			}
			
			//check if a scan point is blocked
			if(Math.abs(currAng-NORTH)<POINT_BLOCKED_ERROR&&currScanDist<POINT_BLOCKED_DIST&&result_output[0]==0){
				result_output[0]=currAng;
				if(result_output[0]==0){
					result_output[0]+=1;
				}
			}
			if(Math.abs(currAng-EAST)<POINT_BLOCKED_ERROR&&currScanDist<POINT_BLOCKED_DIST&&result_output[1]==0){
				result_output[1]=currAng;
				if(result_output[1]==0){
					result_output[1]+=1;
				}
			}
			if(Math.abs(currAng-SOUTH)<POINT_BLOCKED_ERROR&&currScanDist<POINT_BLOCKED_DIST&&result_output[2]==0){
				result_output[2]=currAng;
				if(result_output[2]==0){
					result_output[2]+=1;
				}
			}
			if(Math.abs(currAng-WEST)<POINT_BLOCKED_ERROR&&currScanDist<POINT_BLOCKED_DIST&&result_output[3]==0){
				result_output[3]=currAng;
				if(result_output[3]==0){
					result_output[3]+=1;
				}
			}
		
			//check for blocks
			if((onBlock&&currScanDist>SCAN_DIST_THRESH&&blockCount>BLOCK_COUNT_THRESH)||blockCount>BLOCK_MAX){ //end of block
				onBlock=false;	//end of block reached
				blockCount=0;	//reset block count
				BlockEnd=odo.getTheta(); //store block end angle, accountts for sensor eye offset (might have to readd -5)
				Sound.buzz();
				
				//stop rotation
				robo.stopMotors();
				//stop poller

				//turn to middle of block
				if(BlockBeginning>=270 && BlockEnd<=90)
					{
						avgAngle = ((BlockBeginning+BlockEnd)/2)+180;
						if(avgAngle>360)
							avgAngle-=360;
					
					}
				else{
				avgAngle=(BlockBeginning+BlockEnd)/2;
				
				}
				savedAngle=odo.getTheta(); //save angle
				nav.turnTo(avgAngle-ANGLE_OFFSET,true);
				blockType=block_diff.identifyBlock(SCAN_DIST_THRESH*1.5); //changed distance to 25,added start and stop to poller
				if(blockType==1){
					Sound.beepSequenceUp();
					GrabBlock();
					result_output[4]=1;
					break;
				}else{
					Sound.buzz();
				}
				
				//turn back to previous angle to avoid redetection
				nav.turnTo(savedAngle, true);
				
				//start poller again
				robo.startUsBottom();
				robo.setRotationSpeed(SCAN_ROTATE_SPEED);
				robo.rotateClockwise();
			}
			else if(!onBlock&&currScanDist<=SCAN_DIST_THRESH){ //start block
				onBlock=true;//detected a block
				BlockBeginning=odo.getTheta(); //store block start angle
				Sound.beep();
				
			}
			else if(onBlock){//&&(Math.abs(currScanDist-SCAN_DIST_THRESH)<SCAN_DIST_ERROR||currScanDist<=SCAN_DIST_THRESH)){ //on block
				blockCount++;
			}
			
			//RConsole.println(""+currScanDist+"      "+blockCount);
		}
		robo.stopBottomUsPoller(); //stop polling
		//RConsole.open();
		
		//double check
		if(result_output[0]!=0){
			//check north
			result_output[0]=checkCardinal(result_output[0]);
		}
		if(result_output[1]!=0){
			//check east
			result_output[1]=checkCardinal(result_output[1]);
		}
		if(result_output[2]!=0){
			//check south
			result_output[2]=checkCardinal(result_output[2]);
		}
		if(result_output[3]!=0){
			//check west
			result_output[3]=checkCardinal(result_output[3]);
		}
		
		return result_output;
	}
	
	private int checkCardinal(double input){
		nav.turnTo(input,true);
		if(block_diff.identifyBlock(POINT_BLOCKED_CHECK_DIST)==1){
			return 0;
		}
		return 1;
	}
	
	/**
	 * 
	 * @param goes straight
	 */
	public void GrabBlock(){
		double pushBlockDist=25;
		double blockLeeway=15; //must be smaller than pushblockdist
		double blockDist;
		double currX,currY;
		
		//raise claw to void interference with bottom US sensor
		robo.setClawAcc(CLAW_RAISE_ACC);
		robo.setclawSpeed(CLAW_RAISE_ACC);
		robo.rotateClawAbsolute(CLAW_RAISE_ANGLE);
		
		//get initial block distance
		blockDist=robo.scanWithBottomsensor(10);
		robo.setForwardSpeed(100);
		
		
		//too close
		//if(blockDist<=DEFAULT_GRAB_DISTANCE+blockLeeway){
			nav.travelSetDistanceBackwards(DEFAULT_GRAB_DISTANCE);
			
			
			/*while(blockDist<DEFAULT_GRAB_DISTANCE+blockLeeway){
				blockDist=robo.scanWithBottomsensor(15);
				robo.goBackward();*/
			//}
			//robo.stopMotors();
		//}
		
		//too far
		/*else if(blockDist>DEFAULT_GRAB_DISTANCE+blockLeeway){
			robo.goForward();
			while(blockDist>DEFAULT_GRAB_DISTANCE+blockLeeway){
				blockDist=robo.scanWithBottomsensor(5);
				robo.goForward();
			}
			robo.stopMotors();
		}*/
		
		//lower claw
		robo.rotateClawAbsolute(CLAW_LOWER_ANGLE);

		//push block
		nav.travelSetDistanceStraight(pushBlockDist);
		
		//raise claw and grab block
		robo.rotateClawAbsolute(CLAW_RAISE_ANGLE);
		robo.stopClaw();
		double temp=robo.scanWithBottomsensor(5);
		if(temp<DEFAULT_GRAB_DISTANCE+blockLeeway)
		{
			RConsole.println("RECUSRION GRAB "+temp);
			GrabBlock();
		}
		
		
		
		//robo.rotateClawAbsolute(CLAW_LOWER_ANGLE);
	}
	
}