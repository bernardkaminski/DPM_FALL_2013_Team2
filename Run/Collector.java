package Run;

import java.util.ArrayList;

import lejos.nxt.comm.RConsole;

import bluetooth.Transmission;
import Hardware.TwoWheeledRobot;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import IntermediateLogic.lightlocalization;
import MainLogic.*;

public class Collector {

		/**
		 * This class is responsible for executing the collector function of the robot
		 * It is identical to the builder class except for the flipZone() method at the beggining
		 * @author Connor, Bernie
		 * @param odo Odometer required for keeping track of position
		 * @param robo TwoWheeledRobot containing all hardware related methods
		 * @param nav Navigation used for traveling to points
		 * @param search Search used for scanning to detect blue blocks and obstacles
		 * @param loc lightlocalization used to localize robot
		 * @param map Map containing the locations of the red and green zones
		 * @param startingCorner int repesenting which starting corner the robot is in
		 */
        static void main(Odometer odo, TwoWheeledRobot robo,Navigation nav,Search search,lightlocalization loc,Map map,int startingCorner ) 
        {
        		
                map.flipZones();
                //Create a clock so that robot stops operating after 5 minutes (30,000 milliseconds)
            	Boolean timeUp= new Boolean(false);
                Clock timeLimit = new Clock(300000, timeUp);
                //Raise claw to clear sensors
                robo.raiseClaw();
                //Localize
                loc.localize();
                int Ystart=0;
                int Xstart=0;
                //Setting odometer according to starting corner
                switch (startingCorner) 
                {
                case 1:
                        odo.setX(0);
                        odo.setY(0);
                        Ystart=60;
                        Xstart=60;
                        break;
                case 2:
                        odo.setX(300);
                        odo.setY(0);
                        odo.setTheta(270);
                        Ystart=60;
                        Xstart=240;
                        break;
                case 3:
                        odo.setX(300);
                        odo.setY(300);
                        odo.setTheta(180);
                        Ystart=240;
                        Xstart=240;
                        break;
                case 4:
                        odo.setX(0);
                        odo.setY(300);
                        odo.setTheta(90);
                        Ystart=240;
                        Xstart=60;
                        break;
                default:
                        break;
                }

                //Create the array lists corresponding to the list of points the robot should travel to during its path
                ArrayList<Integer> xCords = new ArrayList<Integer>();
                ArrayList<Integer> yCords = new ArrayList<Integer>();
                int [] scanResults = new int[5];
                
                //Generate initial path to desired zone
                Path.generatePath(Xstart,Ystart,(int)map.getDropZoneBottomLeft().getx(), (int)map.getDropZoneBottomLeft().gety(), xCords, yCords); 
        
    	        //Travel to red zone while looking for blocks
    	        boolean hasBlock=false;
    	        boolean correct= true;
    	        
    	        for(int i=0;i<xCords.size();++i)
    	        {       
    	        	
                    if(i==xCords.size()-1)
                    { 
                    	//Point to travel too is the red zone, travel there and break loop
                    	nav.travelTo(correct, false,xCords.get(i),yCords.get(i));
                        break;
                    }  
                    
                    //Otherwise travel to the next point
                    nav.travelTo(correct, false,xCords.get(i),yCords.get(i));
                    
                    //Once robot gets to that point perform a scan
                    //Turn  for scan (depends which way the robot is zig zagging)
                    if(yCords.get(yCords.size()-1)>yCords.get(0))
                    {
                        if(xCords.get(xCords.size()-1)<xCords.get(0))
                        {
                                nav.turnTo(270,true);
                        }
                        else
                        {
                                nav.turnTo(0,true);
                        }
                    }
                    else
                    {
                        if(xCords.get(xCords.size()-1)<xCords.get(0))
                        {
                                nav.turnTo(180,true);
                        }
                        else
                        {
                                nav.turnTo(90,true);
                        }
                    }
                    //Scan
    	            scanResults = search.Scan(hasBlock);
    	                
                    //Turn back 
                    if(yCords.get(yCords.size()-1)>yCords.get(0))
                    {
                        if(xCords.get(xCords.size()-1)<xCords.get(0))
                        {
                                nav.turnTo(270,true);
                        }
                        else
                        {
                                nav.turnTo(0,true);
                        }
                    }
                    else
                    {
                        if(xCords.get(xCords.size()-1)<xCords.get(0))
                        {
                                nav.turnTo(180,true);
                        }
                        else
                        {
                                nav.turnTo(90,true);
                        }
                    }
                    
                    //If the last value of scanResults is a 1 a blue block has been detected and grabbed
       
                    if(scanResults[4]==1||scanResults[4]==2)
                    {
                        hasBlock=true;
                        //Do not want to correct after grabbing a blue block (robot will not be aligned with grid lines)
                        correct=false;     
                    }
                    //Fine tune
                    else
                    {
                            nav.travelSetDistanceBackwards(7);
                            nav.fineTune();
                    }
                    //Process data received during scan to determine subsequent path
                    if(Path.processData(scanResults, xCords,yCords,i,nav,map))
                    {	
                    	//If the path has been altered, need to reset the loop to start from the beggining of updated lists of coords
                        i=(-1);
                    } 
                    if (timeUp)
    		        {        
    		                System.exit(0);
    		        }
    	        }
    	        robo.stopMotors();
            
    		    
    		    //Now in red zone
    		    if(hasBlock)
    		    {
    		    	//place the block in zone
    	            nav.stack();
    	            if(map.getDropZoneBottomLeft().getx()<5*30)
    	            {
    	            	//This is a hard code to avoid hitting the blue block out of its position when the robot leaves the red zone
    	            	//Only applies if the red zone is in the left half of the field, because of we always go to the bottom left corner of red zone
    	            	nav.travelTo(false, false, map.getDropZoneBottomLeft().getx(), map.getDropZoneBottomLeft().gety()-15);
    	            	nav.travelTo(false, false, map.getDropZoneBottomLeft().getx()+30, map.getDropZoneBottomLeft().gety()-30);}
    	            	hasBlock=false;
    		    	}
    		
    		     //Generate new path to next "red zone" (point chosen to ensure that robot covers previously uncovered ground), starting at current red zone
    		     //Which is last point in the xCords and yCords
    		     int xMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDropZoneTopRight())[0].getx();
    		     int yMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDropZoneTopRight())[0].gety();

    		     Path.generatePath(xCords.get(xCords.size()-1),yCords.get(yCords.size()-1), xMock ,yMock, xCords, yCords);
    	    
    		     //Travel to next "red zone" looking for blocks
    		     for(int i=0;i<xCords.size();++i)
    		     {                
    		    	 if(i==xCords.size()-1)
                     { 
    		    		 //Done in mock redZone
    		    		 //Algorithm can go no further
    		    		 nav.travelTo(true, false,xCords.get(i),yCords.get(i));
    		    		 break;
                     }    
                     
    		    	 if(xCords.get(i)==map.getDropZoneBottomLeft().getx()&& yCords.get(i)==map.getDropZoneBottomLeft().gety())
    		    	 {
    		    		 nav.travelTo(false, false,xCords.get(i),yCords.get(i));
    	                 //Remember path for returning to actual red zone
    	                 map.MemAddCoordinates(xCords.get(i),yCords.get(i));
    	                 //On the first iteration do not scan (to avoid redetection of block already place)
    	                 continue;
    		    	 }
    		    	 else
    		    	 {
                         nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                         //Remember path for returning to actual red zone
                         map.MemAddCoordinates(xCords.get(i),yCords.get(i));
    		    	 }

                    
                     //Turn to scan angle, depends on direction of zig zag, must ensure that scanning for obstacles                   
    		    	 if(yCords.get(yCords.size()-1)>yCords.get(0))
    	             {
    	                    if(xCords.get(xCords.size()-1)<xCords.get(0))
    	                    {
    	                            nav.turnTo(270,true);
    	                    }
    	                    else
    	                    {
    	                            nav.turnTo(0,true);
    	                    }
    	              }
    	              else
    	              {
    	                    if(xCords.get(xCords.size()-1)<xCords.get(0))
    	                    {
    	                            nav.turnTo(180,true);
    	                    }
    	                    else
    	                    {
    	                            nav.turnTo(90,true);
    	                    }
    	               }
    		    	   //Scan
    		    	   scanResults=search.Scan(hasBlock);
    		    	   
    		    	   //Turn back after scan
    	                if(yCords.get(yCords.size()-1)>yCords.get(0))
    	                {
    	                    if(xCords.get(xCords.size()-1)<xCords.get(0))
    	                    {
    	                            nav.turnTo(270,true);
    	                    }
    	                    else
    	                    {
    	                            nav.turnTo(0,true);
    	                    }
    	                }
    	                else
    	                {
    	                    if(xCords.get(xCords.size()-1)<xCords.get(0))
    	                    {
    	                            nav.turnTo(180,true);
    	                    }
    	                    else
    	                    {
    	                            nav.turnTo(90,true);
    	                    }
    	                }
    	                
    	                //Check last coordinate of scan results, which indicates whether another block has been detected
    			        if(scanResults[4]==1)
    			        {
    		                hasBlock=true;
    		                //Go back to red zone to stack using exact same path, because we already know that it is clear
    		                for (int k =map.getXMemory().size()-1 ; k > 0;k--)
    		                {
    		                     nav.travelTo(true, false, map.getXMemory().get(k), map.getYMemory().get(k));
    		                }
    		                //Now in red zone for second time,stack the block
    		                nav.stackSecond();
    		                break;
    			        }
    			        else if(scanResults[4]!=2)
    			        {
    			        	nav.travelSetDistanceBackwards(4);
    			            nav.fineTune();
    			        }
    			        if(Path.processData(scanResults, xCords,yCords,i,nav,map))
    			        {
    			        	//If path has been altered, need to restart the for loop to travel to all points in new path
    			        	i=-1;
    			        }
                    }
    		        if (timeUp)
    		        {        
    		                System.exit(0);
    		        }
            	}
            	//Robot has stacked 2 blue blocks in red zone, assuming it found a second block while navgiating to the mock red zone
}
