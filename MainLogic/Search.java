package MainLogic;
import java.util.ArrayList;

import lejos.*;
import lejos.nxt.comm.RConsole;
import Hardware.*;
import IntermediateLogic.*;

public class Search {
        
        public static final double DEFAULT_GRAB_DISTANCE = 13.5; //CM. Distance from wheel axle to closest part of block when block is pushed against the robot.
        
        //SCAN VARIABLES
        public final double ANGLE_DECREASE=20;
        public final double ANGLE_INCREASE=135;
        private final double SCAN_ROTATE_THRESH=5.0; //slightly greater than TURNTO_THRESHOLD in navigation
        private final int SCAN_ROTATE_SPEED=100; //59
        private final int NUM_US_SCANS=1; //2 //will have to empirically determine
        
        //QUICK SCAN VARIABLES
        private final int QUICK_SCAN_GO_SPEED=200;
        private final int QUICK_SCAN_SCAN_SPEED=100;
        private final int QUICK_SCAN_PLUS=60;//60
        private final int QUICK_SCAN_MINUS=25;//10
        private final int QUICK_SCAN_DIFF=10;
        private final int QUICK_SCAN_CLIP=20; //10 //35
        private final int QUICK_SCAN_NUM_SCAN=1;

        
        //PROCESS DATA VARIABLES
        private final double REQ_BLUE_CERT=0.75;//0.85 //percentage to be sure its a blue
        private final double REQ_WOOD_CERT=0.2; //0.5 //percent. higher than this value to be sure its a wood block
        
        //ALIGN VARIABLES
        private final int NUM_INDEX_CLIP=20; //10 //35 //45
        
        //SECTION VARIABLES
        private final double SCAN_DIST_THRESH=26;
        private final double SCAN_DIST_BAND=1; //5
        private final int BLOCK_COUNT_THRESH=100;//60 //40 //might have to change this
        private final int BLOCK_COUNT_MIN=10;
        //private final double BLOCK_DIFF_RANGE=2;
        
        //BLOCK DIFFERENTIATE VARIABLES
        private final double DIFFERENTIATE_DIFF=4;
        
        //CARDINAL HEADING VARIABLES
        private final double NORTH;
        private final double SOUTH;
        private final double EAST;
        private final double WEST;
        private final double POINT_BLOCKED_ERROR=20; //angle
        private final double POINT_BLOCKED_DIST=45;//50
        private final int WOOD_COUNT_MIN=5;//5
        
        //GRAB BLOCK VARIABLES
	    private final int CLAW_RECUR_LIM=3;
	    private int recursion_count=0;
	    private final double PUSH_BLOCK_DIST=25;
	    private final double BLOCK_LEEWAY=15; //must be smaller than pushblockdist
	    private final double SAFE_PUSH=10;
	    private final double RECUR_SCAN_DIST=25;
	    private final double RECUR_MOVE_DIST=10;
    
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
         * 4: found block? (0 if no) (1 if yes) (2 if failed)
         */
        public int[] Scan(boolean hasBlock){
        		double startAngle=odo.getTheta()-ANGLE_DECREASE;
                double goalAngle=odo.getTheta()+ANGLE_INCREASE;
                int[] output_array={0,0,0,0,0};
                double most_blue=0;
                int most_blue_index=0;
                int quick_result=1;
                double temp_ang;
                String temp_card;
                
                //handle boundary cases
                if(startAngle<0){
                	startAngle+=360;
                }
                if(goalAngle>=360){
                    goalAngle-=360;
                }
                
                //variables
                ArrayList<Double> headings = new ArrayList<Double>();
                ArrayList<Double> topData = new ArrayList<Double>();
                ArrayList<Double> bottomData = new ArrayList<Double>();
                ArrayList<ArrayList<Double>> blocks = new ArrayList<ArrayList<Double>>();
                
                //--------------QUICK SCAN
//                if(hasBlock==true){ //handle multiple blocked cases
//                	RConsole.println("HAS BLOCK");
//                	for(int i=0;i<4;i++)
//                	{
//                		temp_card=nav.getCardinalHeading();
//                		quick_result=quickScan();
//                		if(quick_result==0)
//                		{
//                			break;
//                		}
//                		else
//                		{
//                			if(temp_card.equals("NORTH"))
//                				output_array[0]=1;
//                			if(temp_card.equals("EAST"))
//                				output_array[1]=1;
//                			if(temp_card.equals("SOUTH"))
//                				output_array[2]=1;
//                			if(temp_card.equals("WEST"))
//                				output_array[3]=1;
//                		}
//                		temp_ang=odo.getTheta()+90-QUICK_SCAN_PLUS;
//                		
//                		if(temp_ang>=360)
//                			temp_ang-=360;
//                		
//                		robo.setRotationSpeed(QUICK_SCAN_GO_SPEED);
//                		nav.turnTo(temp_ang,true);
//                			
//                	}
//                	return output_array;
//                }
                //---------------END OF QUICK SCAN
                
                //RConsole.open();
                RConsole.println("GOAL ANG: "+goalAngle);
                //rotate and gather data
                nav.turnTo(startAngle, true);
                robo.setRotationSpeed(SCAN_ROTATE_SPEED);
                robo.rotateClockwise();                
                while(Math.abs(odo.getTheta()-goalAngle)>SCAN_ROTATE_THRESH){
                        topData.add(robo.scanWithTopsensor(NUM_US_SCANS));
                        bottomData.add(robo.scanWithBottomsensor(NUM_US_SCANS));
                        headings.add(odo.getTheta());
                }
                robo.stopMotors();
                
                //print data
//                for(int i=0;i<headings.size();i++){
//                        RConsole.print(headings.get(i)+"\t");
//                }
//                RConsole.println("");
//                for(int i=0;i<topData.size();i++){
//                        RConsole.print(topData.get(i)+"\t");
//                }
//                RConsole.println("");
//                for(int i=0;i<bottomData.size();i++){
//                        RConsole.print(bottomData.get(i)+"\t");
//                }
                
                //------------processData---------------
                
                blocks=processScanData(headings,topData,bottomData,output_array);
                
                //--------------------------------------
                
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
                		RConsole.println("BLUE BLOCK DETECTED");
                        RConsole.println("\nGrabbing start: "+blocks.get(most_blue_index).get(0)+" end: "+blocks.get(most_blue_index).get(1)+" avg: "+blocks.get(most_blue_index).get(2)+" type: "+blocks.get(most_blue_index).get(3));
                        output_array[4]=1;
                        nav.turnTo(blocks.get(most_blue_index).get(2), true);
                        GrabBlock();
                        if(recursion_count>CLAW_RECUR_LIM)
                        {
                                output_array[4]=2;
                        }
                }
                
                //print output array
                RConsole.println("\noutput_array: ");
                for(int i=0;i<output_array.length;i++)
                        RConsole.print(" "+output_array[i]);
                RConsole.println("");
                
                
                //RConsole.close();
                return output_array;
        }
        
        
        //ASSUME YOU'RE CURRENTLY FACING A CARDINAL
        private int quickScan()
        {
        	RConsole.println("QUICK CALLED");
            ArrayList<Double> headings = new ArrayList<Double>();
            ArrayList<Double> topData = new ArrayList<Double>();
            ArrayList<Double> bottomData = new ArrayList<Double>();
        	ArrayList<Integer> blockDiff=new ArrayList<Integer>();
        	double startAngle=odo.getTheta()-QUICK_SCAN_MINUS;
        	double goalAngle=odo.getTheta()+QUICK_SCAN_PLUS;
            //handle boundary cases
            if(startAngle<0){
            	startAngle+=360;
            }
            
            if(goalAngle>=360){
            	goalAngle-=360;
            }
            
            RConsole.println("start ang: "+startAngle);
            RConsole.println("goal ang: "+goalAngle);
            
            nav.turnTo(startAngle,true);
            robo.setRotationSpeed(QUICK_SCAN_SCAN_SPEED);
            robo.rotateClockwise();
            
            while(Math.abs(odo.getTheta()-goalAngle)>SCAN_ROTATE_THRESH)
            {
                topData.add(robo.scanWithTopsensor(QUICK_SCAN_NUM_SCAN));
                bottomData.add(robo.scanWithBottomsensor(QUICK_SCAN_NUM_SCAN));
                headings.add(odo.getTheta());
            }
            robo.stopMotors();
            
//        	RConsole.println("blockDiff size: "+blockDiff.size());
//            RConsole.println("topData size: "+topData.size());
//            RConsole.println("bottomData size: "+bottomData.size());
            
            //align data
            alignData(headings,topData,bottomData,QUICK_SCAN_CLIP);
        	
//        	RConsole.println("blockDiff size: "+blockDiff.size());
//            RConsole.println("topData size: "+topData.size());
//            RConsole.println("bottomData size: "+bottomData.size());
             
            //print data
           /* for(int i=0;i<headings.size();i++)
            {
                    RConsole.print(headings.get(i)+"\t");
            }
            RConsole.println("");
            for(int i=0;i<topData.size();i++)
            {
                    RConsole.print(topData.get(i)+"\t");
            }
            RConsole.println("");
            for(int i=0;i<bottomData.size();i++)
            {
                    RConsole.print(bottomData.get(i)+"\t");
            }
            
            RConsole.println("");
            RConsole.println("");*/
//            
            return woodDifferentiate(topData, bottomData);
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
                
                RConsole.println("Process Data Called");
                
                //RConsole.println("blockDiff size: "+blockDiff.size());
                //RConsole.println("topData size: "+topData.size());
                //RConsole.println("bottomData size: "+bottomData.size());
                
                //align data
                alignData(headings,topData,bottomData,NUM_INDEX_CLIP);
                
                //print data
//                for(int i=0;i<headings.size();i++){
//                        RConsole.print(headings.get(i)+"\t");
//                }
//                RConsole.println("");
//                for(int i=0;i<topData.size();i++){
//                        RConsole.print(topData.get(i)+"\t");
//                }
//                RConsole.println("");
//                for(int i=0;i<bottomData.size();i++){
//                        RConsole.print(bottomData.get(i)+"\t");
//                }
                
                //sectioning
                tempIndexes=SectionData(headings,bottomData);
                startIndexes=tempIndexes.get(0);
                endIndexes=tempIndexes.get(1);
                
                //blockDifferentiating
                blockDiff=blueDifferentiate(topData,bottomData);
                
//                RConsole.println("blockDiff size: "+blockDiff.size());
//                RConsole.println("topData size: "+topData.size());
//                RConsole.println("bottomData size: "+bottomData.size());
                
                //create blocks
                for(int i=0;i<startIndexes.size();i++){
                        //needs to recreate each time
                        ArrayList<Double> temp_block = new ArrayList<Double>();
                        
                        temp_block.add(headings.get(startIndexes.get(i)));
                        temp_block.add(headings.get(endIndexes.get(i)));
                        
                        //handles average cases
                        if(temp_block.get(0)>=270 && temp_block.get(1)<=180){
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
                main_output[0]=checkCardinals(headings,topData,bottomData,NORTH);
                if (main_output[0]==1)
                	RConsole.println("NORTH BLOCKED");
                main_output[1]=checkCardinals(headings,topData,bottomData,EAST);
                if (main_output[1]==1)
                	RConsole.println("EAST BLOCKED");
                main_output[2]=checkCardinals(headings,topData,bottomData,SOUTH);
                if (main_output[2]==1)
                	RConsole.println("SOUTH BLOCKED");
                main_output[3]=checkCardinals(headings,topData,bottomData,WEST);
                if (main_output[3]==1)
            		RConsole.println("WEST BLOCKED");
                
                
                return output_blocks;
        }
        
        //called by processData - uses referencing
        private void alignData(ArrayList<Double> headings,ArrayList<Double> topData, ArrayList<Double> bottomData,int clip)
        {
        	for(int i=0;i<clip;i++)
        	{
        		bottomData.remove(bottomData.size()-1);
        		headings.remove(headings.size()-1);
        		topData.remove(0);
        	}
        }
        
        //called by processData
        private ArrayList<ArrayList<Integer>> SectionData(ArrayList<Double> headings,ArrayList<Double> bottomData)
        {
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
                if(inSection&&(bottomData.size()-currStartIndex)>=BLOCK_COUNT_MIN){
                        startIndexes.add(currStartIndex);
                        endIndexes.add(bottomData.size()-1);
                }
                
//                RConsole.print("\n start indexes: " );
//                for(int i=0;i<startIndexes.size();i++)
//                        RConsole.print(" "+startIndexes.get(i));
//                
//                RConsole.print("\n end indexes: " );
//                for(int i=0;i<endIndexes.size();i++)
//                        RConsole.print(" "+endIndexes.get(i));
//                RConsole.print("\n");
                
                
                output.add(startIndexes);
                output.add(endIndexes);
                return output;
        }
        
        
        //called by processScanData - in array a 1 is a Blue Block, a 0 is not
        //probably need to test this individually
        private ArrayList<Integer> blueDifferentiate(ArrayList<Double> topData,ArrayList<Double> bottomData){
                ArrayList<Integer> diffOutput = new ArrayList<Integer>();
                
                for(int i=0; i<bottomData.size();i++){
                        if((topData.get(i)-bottomData.get(i))>DIFFERENTIATE_DIFF&&bottomData.get(i)<=SCAN_DIST_THRESH){
                                diffOutput.add(1);
                                //RConsole.print("1 \t");
                        }else{
                                diffOutput.add(0);
                                //RConsole.print("0 \t");
                        }
                }
                RConsole.println("");
                
                return diffOutput;
        }
        
        
        //returns possibility probability of being a wood block
        //returns 1 if wood
        //returns 0 if not wood
        private int woodDifferentiate(ArrayList<Double> topData,ArrayList<Double> bottomData)
        {
        	double avgWood=0;
        	double avg_count=0;
	        for(int i=0; i<bottomData.size();i++)
	        {
	        	avg_count++;
	            if((topData.get(i)-bottomData.get(i))<QUICK_SCAN_DIFF&&bottomData.get(i)<=POINT_BLOCKED_DIST){
	                    avgWood+=1;
	                    //RConsole.print("1 \t"); //wood block
	            }else{
	            		//add 0
	                    //RConsole.print("0 \t"); //not wood bock
	            }
	        }
	        
	        RConsole.println("wood diff: "+avgWood/avg_count);
	        
	        if(avgWood/avg_count>REQ_WOOD_CERT)
	        	return 1;
	        return 0;
        }
        
        //called by processScanData
        public static double getListAvg(ArrayList<Integer> input,int start, int end){
                double output=0,count=0;
                //Section to differentiate
                
                //RConsole.println("avgerage for s: "+start+" e: "+end+" ");
                for(int i=start;i<=end;i++){
//                        RConsole.print(""+input.get(i));
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
        private int checkCardinals(ArrayList<Double> headings,ArrayList<Double> topData, ArrayList<Double> bottomData,double cardinal){
                ArrayList<Double> cardinalTop = new ArrayList<Double>();
                ArrayList<Double> cardinalBottom = new ArrayList<Double>();
                
                RConsole.println("Checking Cardinal: "+cardinal);
                
                
                for(int i=0;i<bottomData.size();i++){
                        //if within angle error and close enough then add data
                        if(Math.abs(headings.get(i)-cardinal)<POINT_BLOCKED_ERROR||Math.abs(headings.get(i)-cardinal)>(360-POINT_BLOCKED_ERROR)){
                        	cardinalTop.add(topData.get(i));
                        	cardinalBottom.add(bottomData.get(i));
                        }
                }
                
                //avoids division by zero
                //return 1 if avg low enough and point blocked

                return woodDifferentiate(cardinalTop,cardinalBottom);
                
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
            //nav.travelSetDistanceBackwards(SAFE_PUSH);
            
            //------safe grab-------
            
            double temp_bot=robo.scanWithBottomsensor(5);
            double temp_top=robo.scanWithTopsensor(5);
            if(temp_bot<(RECUR_SCAN_DIST)&&(temp_top-temp_bot)>DIFFERENTIATE_DIFF&&recursion_count<=CLAW_RECUR_LIM)
            {
                    RConsole.println("RECURSION GRAB -> dist: "+temp_bot);
                    GrabBlock();
            }
            if(temp_bot<(RECUR_SCAN_DIST)&&(temp_top-temp_bot)<DIFFERENTIATE_DIFF)
            {
            	recursion_count=CLAW_RECUR_LIM+1;
            }
            
            //robo.rotateClawAbsolute(CLAW_LOWER_ANGLE);
    }
}