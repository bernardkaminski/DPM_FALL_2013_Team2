package MainLogic;
import java.util.ArrayList;

import lejos.*;
import lejos.nxt.comm.RConsole;
import Hardware.*;
import IntermediateLogic.*;

public class Search {
	
	public static final double DEFAULT_GRAB_DISTANCE = 13.5; //CM. Distance from wheel axle to closest part of block when block is pushed agains the robot.
	
	//SCAN VARIABLES
	public final double ANGLE_INCREASE=160;
	private final double SCAN_ROTATE_THRESH=1.0; //slightly greater than TURNTO_THRESHOLD in navigation
	private final int SCAN_ROTATE_SPEED=50;
	private final int NUM_US_SCANS=2; //will have to empirically determine
	
	//PROCESS DATA VARIABLES
	private final double REQ_BLUE_CERT=0.75;//0.85 //percentage to be sure its a blue
	private final double REQ_WOOD_CERT=0.5; //0.5 //percent. lower than this value to be sure its a wood block
	
	//SECTION VARIABLES
	private final double SCAN_DIST_THRESH=26;
	private final double SCAN_DIST_BAND=1; //5
	private final int BLOCK_COUNT_THRESH=40; //might have to change this
	private final int BLOCK_COUNT_MIN=10;
	
	//BLOCK DIFFERENTIATE VARIABLES
	private final double DIFFERENTIATE_DIFF=4;
	private final double DIFFERENTIATE_RANGE=50;
	
	//CARDINAL HEADING VARIABLES
	private final double NORTH;
	private final double SOUTH;
	private final double EAST;
	private final double WEST;
	private final double POINT_BLOCKED_ERROR=20; //angle
	private final double POINT_BLOCKED_DIST=50;
	private final int WOOD_COUNT_MIN=5;//5
	
	//GRAB BLOCK VARIABLES
    private final int CLAW_RECUR_LIM=4;
    private int recursion_count=0;
    private final double PUSH_BLOCK_DIST=25;
    private final double BLOCK_LEEWAY=15; //must be smaller than pushblockdist
    private final double SAFE_PUSH=5;
    
	//objects
	private TwoWheeledRobot robo;
	private Navigation nav;
	private Odometer odo;
	
	
	public Search(TwoWheeledRobot robo,Navigation nav,Odometer odo){
		this.robo=robo;
		this.nav=nav;
		this.odo=odo;
		
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
	public int[] Scan(boolean hasBlock){
		double goalAngle=odo.getTheta()+ANGLE_INCREASE;
		int[] output_array={0,0,0,0,0};
		double most_blue=0;
		int most_blue_index=0;
		
		//handle boundary cases
		if(goalAngle>=360){
			goalAngle-=360;
		}
		
		//raise claw
		robo.pickUpBlock();
		
		//variables
		ArrayList<Double> headings = new ArrayList<Double>();
		ArrayList<Double> topData = new ArrayList<Double>();
		ArrayList<Double> bottomData = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> blocks = new ArrayList<ArrayList<Double>>();
		
		//RConsole.open();
		RConsole.println("GOAL ANG: "+goalAngle);
		//rotate and gather data
		robo.setRotationSpeed(SCAN_ROTATE_SPEED);
		robo.rotateClockwise();		
		while(Math.abs(odo.getTheta()-goalAngle)>SCAN_ROTATE_THRESH){
			topData.add(robo.scanWithTopsensor(NUM_US_SCANS));
			bottomData.add(robo.scanWithBottomsensor(NUM_US_SCANS));
			headings.add(odo.getTheta());
		}
		robo.stopMotors();
		
		//------------processData---------------
		
		blocks=processScanData(headings,topData,bottomData,output_array);
		
		//--------------------------------------
		
		//print data
//		for(int i=0;i<headings.size();i++){
//			RConsole.print(headings.get(i)+"\t");
//		}
//		RConsole.println("");
//		for(int i=0;i<topData.size();i++){
//			RConsole.print(topData.get(i)+"\t");
//		}
//		RConsole.println("");
//		for(int i=0;i<bottomData.size();i++){
//			RConsole.print(bottomData.get(i)+"\t");
//		}
		
		//print blocks
		RConsole.println("num of blocks: "+blocks.size());
		for(int i=0;i<blocks.size();i++){
			RConsole.println("start: "+blocks.get(i).get(0)+" end: "+blocks.get(i).get(1)+" avg: "+blocks.get(i).get(2)+" type: "+blocks.get(i).get(3));
		}
		
		//find most likely blue block
		for(int i=0;i<blocks.size();i++){
			if(blocks.get(i).get(3)>most_blue){
				most_blue=blocks.get(i).get(3);
				most_blue_index=i;
			}
		}
		
		//maybe grab block
		if(most_blue>REQ_BLUE_CERT&&!hasBlock){
			//GRABBING METHOD HERE
			//WILL PROBABLY WANT OFFSET
			RConsole.println("\nGrabbing: "+blocks.get(most_blue_index).get(0)+" end: "+blocks.get(most_blue_index).get(1)+" avg: "+blocks.get(most_blue_index).get(2)+" type: "+blocks.get(most_blue_index).get(3));
			output_array[4]=1;
			nav.turnTo(blocks.get(most_blue_index).get(2), true);
			GrabBlock();
			if(recursion_count>CLAW_RECUR_LIM){
				output_array[4]=0;
			}
		}
		
		//print output array
		RConsole.println("\noutput_array: ");
		for(int i=0;i<output_array.length;i++)
			RConsole.print(" "+output_array[i]);
		
		
		//RConsole.close();
		return output_array;
	}
	
	private ArrayList<ArrayList<Double>> processScanData(ArrayList<Double> headings,ArrayList<Double> topData,ArrayList<Double> bottomData,int[] main_output){
		ArrayList<Integer> startIndexes = new ArrayList<Integer>();
		ArrayList<Integer> endIndexes = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> tempIndexes= new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> blockDiff = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> output_blocks = new ArrayList<ArrayList<Double>>();
		double currAvg;
			//INFO ABOUT BLOCKS:
			//0:start heading
			//1:end heading
			//2:avg heading
			//3:certainty of blue block
		
		//sectioning
		tempIndexes=SectionData(headings,bottomData);
		startIndexes=tempIndexes.get(0);
		endIndexes=tempIndexes.get(1);
		
		//blockDifferentiating
		blockDiff=blockDifferentiate(topData,bottomData);
		
		RConsole.println("blockDiff size: "+blockDiff.size());
		RConsole.println("topData size: "+topData.size());
		RConsole.println("bottomData size: "+bottomData.size());
		
		//create blocks
		for(int i=0;i<startIndexes.size();i++){
			//needs to recreate each time
			ArrayList<Double> temp_block = new ArrayList<Double>();
			
			temp_block.add(headings.get(startIndexes.get(i)));
			temp_block.add(headings.get(endIndexes.get(i)));
			
			//handles average cases
			if(temp_block.get(0)>=270 && temp_block.get(1)<=90){
				currAvg=((temp_block.get(0)+temp_block.get(1))/2)+180;
				if(currAvg>360)
				{
					currAvg-=360;
				}
			}
			else
			{
				currAvg=(temp_block.get(0)+temp_block.get(1))/2;
			}
			
			temp_block.add(currAvg);
			
			
			//blue certainty
			temp_block.add(getListAvg(blockDiff,startIndexes.get(i),endIndexes.get(i)));
			
			//append
			output_blocks.add(temp_block);
			
		}
		
		//check cardinals -done by referencing
		main_output[0]=checkCardinals(headings,topData,bottomData,blockDiff,NORTH);
		main_output[1]=checkCardinals(headings,topData,bottomData,blockDiff,EAST);
		main_output[2]=checkCardinals(headings,topData,bottomData,blockDiff,SOUTH);
		main_output[3]=checkCardinals(headings,topData,bottomData,blockDiff,WEST);
		
		
		return output_blocks;
	}
	
	//called by processData
	private ArrayList<ArrayList<Integer>> SectionData(ArrayList<Double> headings,ArrayList<Double> bottomData){
		//to output
		ArrayList<Integer> startIndexes = new ArrayList<Integer>();
		ArrayList<Integer> endIndexes = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
		
		//method vars
		int currStartIndex=0;
		boolean inSection=false;
		
		
		//section using bottomData
		for(int i=0;i<bottomData.size();i++){
			//start sectioning if not in a section already and dist drops below threshold
			if(!inSection&&bottomData.get(i)<=SCAN_DIST_THRESH){
				currStartIndex=i;
				inSection=true;
				
				//RConsole.println("----------start-----------");
			}
			
			//RConsole.println(""+bottomData.get(i));
			
			//if in sectioning and breaks above band with enough points then end section, or if too many indexes have passed
			if((((inSection&&bottomData.get(i)>=(SCAN_DIST_THRESH+SCAN_DIST_BAND))&&(i-currStartIndex)>=BLOCK_COUNT_MIN))||(inSection&&(i-currStartIndex)>BLOCK_COUNT_THRESH)){
				//store
				startIndexes.add(currStartIndex);
				endIndexes.add(i-1);
				
				//reset
				currStartIndex=i;
				inSection=false;
				
				//RConsole.println("-----------end-----------");
			}
			
		}
		//section off last section
//		if(inSection&&(bottomData.size()-currStartIndex)>=BLOCK_COUNT_MIN){
//			startIndexes.add(currStartIndex);
//			endIndexes.add(bottomData.size()-1);
//		}
		
		RConsole.print("\n start indexes: " );
		for(int i=0;i<startIndexes.size();i++)
			RConsole.print(" "+startIndexes.get(i));
		
		RConsole.print("\n end indexes: " );
		for(int i=0;i<endIndexes.size();i++)
			RConsole.print(" "+endIndexes.get(i));
		RConsole.print("\n");
		
		
		output.add(startIndexes);
		output.add(endIndexes);
		return output;
	}
	
	
	//called by processScanData - in array a 1 is a Blue Block, a 0 is not
	//probably need to test this individually
	private ArrayList<Integer> blockDifferentiate(ArrayList<Double> topData,ArrayList<Double> bottomData){
		ArrayList<Integer> diffOutput = new ArrayList<Integer>();
		
		for(int i=0; i<bottomData.size();i++){
			if((topData.get(i)-bottomData.get(i))>DIFFERENTIATE_DIFF&&bottomData.get(i)<=DIFFERENTIATE_RANGE){
				diffOutput.add(1);
				RConsole.print("1 ");
			}else{
				diffOutput.add(0);
				RConsole.print("0 ");
			}
		}
		RConsole.println("");
		
		return diffOutput;
	}
	
	//called by processScanData
	public static double getListAvg(ArrayList<Integer> input,int start, int end){
		double output=0,count=0;
		//Section to differentiate
		
		//RConsole.println("avgerage for s: "+start+" e: "+end+" ");
		for(int i=start;i<=end;i++){
			//RConsole.print(""+input.get(i));
			output+=input.get(i);
			count++;
		}
		//RConsole.println("\n");
		
		return output/(count);
	}
	
	//called by scan
	/**
	 * Returns 1 if blocked, 0 if free
	 * @param angle
	 * @param cardinal
	 * @return
	 */
	private int checkCardinals(ArrayList<Double> headings,ArrayList<Double> topData, ArrayList<Double> bottomData, ArrayList<Integer> blockDiff,double cardinal){
		int diff_count=0;
		double diff_sum=0;
		int num_wood=0;
		
		
		for(int i=0;i<bottomData.size();i++){
			//if within angle error and close enough then add data
			if(Math.abs(headings.get(i)-cardinal)<POINT_BLOCKED_ERROR&&bottomData.get(i)<POINT_BLOCKED_DIST&&topData.get(i)<POINT_BLOCKED_DIST){
				diff_count++;
				diff_sum+=blockDiff.get(i);
				if(blockDiff.get(i)==0)
					num_wood++;
				RConsole.println("head: "+headings.get(i)+" type: "+blockDiff.get(i));
			}
		}
		
		//avoids division by zero
		//return 1 if avg low enough and point blocked
		if(diff_count!=0)
			RConsole.println("CARDINAL: "+cardinal+" type: "+(diff_sum/diff_count)+ " num wood: "+num_wood);
			if((diff_sum/diff_count)<REQ_WOOD_CERT&&num_wood>=WOOD_COUNT_MIN)
				return 1;
		
		return 0;
		
	}
	
    /**
     * 
     * @param goes straight
     */
    public void GrabBlock(){
            
            //increase recursion
            recursion_count+=1;
            
            //raise claw to void interference with bottom US sensor
            robo.pickUpBlock();
            
            //get initial block distance
            robo.setForwardSpeed(100);
            
            //move back
            nav.travelSetDistanceBackwards(DEFAULT_GRAB_DISTANCE);
                    
            
            //lower claw
            robo.dropBlock();

            //push block
            nav.travelSetDistanceStraight(PUSH_BLOCK_DIST);
            
            //------safe grab-------
            
            //close claw
            robo.pickUpBlock(30);
            
            //open claw
            robo.dropBlock();
            
            //push a little more
            nav.travelSetDistanceStraight(SAFE_PUSH);
            
            //raise claw and grab block
            robo.pickUpBlock();
            robo.stopClaw();
            
            //move back
            nav.travelSetDistanceBackwards(SAFE_PUSH);
            
            //------safe grab-------
            

            double temp=robo.scanWithBottomsensor(5);
            if(temp<(DEFAULT_GRAB_DISTANCE+BLOCK_LEEWAY)&&recursion_count<=CLAW_RECUR_LIM)
            {
                    RConsole.println("RECUSRION GRAB "+temp);
                    GrabBlock();
            }            
            
            //robo.rotateClawAbsolute(CLAW_LOWER_ANGLE);
    }
}