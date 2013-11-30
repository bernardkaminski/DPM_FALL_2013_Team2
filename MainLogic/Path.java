package MainLogic;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
=======
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85

import java.util.Random;
import java.util.ArrayList;
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85

>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
import java.util.Random;
import java.util.ArrayList;
import IntermediateLogic.Navigation;

/**
 * This class handles all of the path logic for the robot
 * It continuously manages the list of coordinates that the robot will travel to
 * The processData method adjusts the path depending on the location of obstacles
 * @author Connor and Bernie
 *
 */

public class Path 
{ 
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
	/**
	 * The generatPAth method is the main purpose of the path class, it generates a zig zag pattern to the from xStart,yStart to xZone, yZone
	 * When it is no longer possible to zig zag, the path is filled in with a straight line
	 * It lists the coordinates in sequential order (to be traveled to) in the two arrayLists xCords and yCords
	 * Note: the list goes from the initial point (xStart, yStart) to the final point ALL INCLUSIVE
	 * @author Bernie, Connor
	 * @param xStart int corresponding to x coordinate of starting position
	 * @param yStart int corresponding to y coordinate of starting position
	 * @param xZone  int corresponding to x coordinate of desired zone 
	 * @param yZone  int corresponding to y coordinate of desired zone
	 * @param xCords ArrayList of x coordinates in the path (only  grid intersections i.e. multiples of 30)
	 * @param yCords ArrayList of y coordinates in the path (only grid intersections i.e. multiples of 30)
	 */
	public static void generatePath(int xStart, int yStart, int xZone, int yZone, ArrayList<Integer>xCords, ArrayList<Integer>yCords)
    {        
		 //Create up and right booleans, defined with respect to origin on grid
         boolean up;
         boolean right;
         if(yZone>yStart)
         {
	    	 up=true;
	         if(xZone>xStart)
	         {
	        	 //Robots path will be up and to the right (north east)
	        	 right=true;
	         }
	         else
	         {
	        	//Robots path will be up and to the left (north west)
	        	right=false;
	         }
         }
         else if(yZone==yStart)
         {    
        	 //Special case where path will be straight line along a particular y coordinate
        	 if(xZone>xStart)
        	 {   
        		 //Here choice of up boolean is arbitrary, but still needs to be initialized
        		 right = true;
        		 up=false;
        	 }
        	 else
        	 {
        		 //Here choice of up boolean is arbitrary, but still needs to be initialized
        		 right = false;
        		 up = false;
        	 }
        	 //Call the fill method which fills in the remaining coordinates to be traveled to with desired straight line and exit generate path method
        	 fill(xStart, yStart, xZone, yZone, xCords, yCords, up, right);
        	 return;
         }
         else
         {
        	 up=false;
        	 if(xZone<xStart)
        	 {
        		 //Robot path is down and to the right (south west)
        		 right=true;
        	 }
        	 else if(xZone==xStart)
        	 {
        		 //Special case where robots path will be along a particular x coordinate
        		 if(yZone>yStart)
        		 { 
        			 right = true;
        			 up=true;
        		 }
        		 else
        		 {
        			 right = false;
        			 up = false;
        		 }
        		 //Fill in path coordinates with straight line and exit generatePath method
        		 fill(xStart, yStart, xZone, yZone, xCords, yCords, up, right);
        		 return;      
        	 }	
        	 else
        	 {
        		 right=false;
        	 }
         }
         
         //Clear previous points in the arraylist to make the new path        
         xCords.clear();
         yCords.clear();
         //Add at least one point to give the arraylist a size, this point will be removed inside the while loop
         xCords.add(-1);
         yCords.add(-1);
  
         int i=0;
         
         //While the last point in the arrayList of coordinates is not equal to the desired final coordinates, continue adding points to be travelled to
         while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
         {       
             if(i==0)
             { 
            	 //If on the first iteration remove point required to enter while loop
            	 xCords.remove(0);
                 yCords.remove(0);  
                 //Add the starting point to the list
                 yCords.add(yStart);
                 xCords.add(xStart);            
             }
             else
             {
            	 
            	//Not the first iteration, fill in zig zag to zone coordinates
            	 if((i%2)==0)
                 {   
            		 //On the even iterations add the north/south segment of zig zag    
            		 if(up)
                     {   
            			 //Add a new set of coordinates to travel too, x value is unchanged, but increment the y by 30 (one tile size)
            			 xCords.add(xCords.get(i-1));
            			 yCords.add(yCords.get(i-1)+30);                   
                     }
            		 else
            		 {	 
            			 //Add a new set of coordinates to travel too, x value is unchanged, but decrement the y by 30 (one tile size)
            			 xCords.add(xCords.get(i-1));
            			 yCords.add(yCords.get(i-1)-30);
            		 }                                          
                 }
            	 
                 else
                 {	  
            	 //On the odd iterations add the east/west segment of the path to be traveled to ** note that here the east and west is flipped for south traveling
                	 if(up)
                	 {
                		 if(right)
                         {
                			 //Add a new set of coordinates to travel too, y value unchanged, but increment the x by 30
                			 xCords.add(xCords.get(i-1)+30);
                			 yCords.add(yCords.get(i-1));                                
                          }
                         else
                         { 
                        	 //Add new set of coordinates to travel too, y value unchanged, but decrement the x by 30 
                        	 xCords.add(xCords.get(i-1)-30);
                        	 yCords.add(yCords.get(i-1));
                          }                                       
                      }
                	  else
                	  {	  //South traveling so east and west directions are flipped
                          if(right)
                          {	
                        	  //Add new set of coordinates to travel too, y value unchanged, decrement the x by 30
                        	  xCords.add(xCords.get(i-1)-30);
                        	  yCords.add(yCords.get(i-1));                                
                           }
=======
=======
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
=======
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
	public static void generatePath(int xStart, int yStart, int xZone,int yZone,ArrayList<Integer>xCords, ArrayList<Integer>yCords)
    {        
            //Remove all points
    		boolean up;
    		boolean right;
    		if(yZone>yStart){
    			up=true;
    			if(xZone>xStart){
    				right=true;
    			}
    			else{right=false;}
    		}
    		else if(yZone==yStart)
    		{
    			if(xZone>xStart)
    			{
    				right = true;
    				up=false;
    			}
    			else
    			{
    				right = false;
    				up = false;
    			}
    			fill(xStart, yStart, xZone, yZone, xCords, yCords, up, right);
    			return;
    		}
    		else{
    			up=false;
    			if(xZone<xStart){
    				right=true;
    			}
    			else if(xZone==xStart)
    			{
    				if(yZone>yStart)
        			{
        				right = true;
        				up=true;
        			}
        			else
        			{
        				right = false;
        				up = false;
        			}
        			fill(xStart, yStart, xZone, yZone, xCords, yCords, up, right);
        			return;
    				
    			}
    			else{
    				right=false;
    			}
    		}
    		
            xCords.clear();
            yCords.clear();
            xCords.add(-1);
            yCords.add(-1);
            int i=0;
            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                    
            //generate new list of points                
                 if(i==0)
                    {  //Remove point required to enter while loop
                       xCords.remove(0);
                       yCords.remove(0);
                            
                       if(xStart==xZone || yStart==yZone)
                        {	
                    	   //X is already aligned with green zone
                            if(xStart==xZone)
                            {                        	
                                int j=1;
                                xCords.add(-1);
                                yCords.add(-1);
                                
                                while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                                {
                                        if(j==1)
                                        {
                                                xCords.remove(0);
                                                yCords.remove(0);
                                                yCords.add(yStart);
                                                xCords.add(xStart);
                                   
                                        }
                                        //keep x the same
                                        xCords.add(xZone);
                                        //Fill in y coords accordingly 
                                        if(up){
                                        	  yCords.add(yStart+(j*30));  
                                        }
                                        else{
                                        	 yCords.add(yStart-(j*30));  
                                        }
                                        j++;
                                }
                                break;
                             }
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
                          else
                          { 
                        	  //Add new set of coordinates to travel too, y value unchanged, increment the x by 30
                        	  xCords.add(xCords.get(i-1)+30);
                        	  yCords.add(yCords.get(i-1));
                           } 
                	    }
                    }                   
                }
             	//Calculated point has exceeded one of the goal coordinates, can no longer zig zag, fill in the rest of the array with a straight line
                if(up)
                {
                	if(right)
                	{
                		if(xCords.get(i)>xZone)
                        {       
                			//Remove point generated that exceeded one of the goal coordinates
                            yCords.remove(i);
                            xCords.remove(i);
                            //Start index at that point
                            int j=i;
                            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                            {
<<<<<<< HEAD
                            	//Fill in the array with the same x coordinate as the zone and y values that are increasing multiples of 30
                            	xCords.add(xZone);
                            	yCords.add(yCords.get(j-1)+30);        
                            	j++;
                             }
			                 //Coordinate array complete
			                 break;
                         }
	                     else if(yCords.get(i)>yZone)
	                     { 
	                    	  //Remove that point
                              yCords.remove(i);
                              xCords.remove(i);
                              int j=i;
                              while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                              {
                            	  xCords.add(xCords.get(j-1)+30);
                            	  yCords.add(yZone);       
                            	  j++;
                              }
                              //Coordinate array complete
                              break;
	                       }
                	  }
                	  else
                	  {
                		  //Robot traveling up and left (north west)
                			if(xCords.get(i)<xZone)
                			{                
                				//Remove that bad point
                				yCords.remove(i);
                				xCords.remove(i);
                				int j=i;
                				while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                				{
	                                xCords.add(xZone);
	                                yCords.add(yCords.get(j-1)+30);        
	                                j++;
                				}
                				//Coordinate array complete
                				break;
                			 }
                			 else if(yCords.get(i)>yZone)
                             { 
                				 //Remove that bad point
                				 yCords.remove(i);
                				 xCords.remove(i);
                				 int j=i;
                				 while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                				 {
                					 xCords.add(xCords.get(j-1)-30);
                					 yCords.add(yZone);       
                					 j++;
                				 }
			                     //Coordinate array complete
			                     break;
			                 }
                	    }
                 }           
          
                else
                {        
                    //Robot travelling south
                    if(right)
                    {        
                    	if(xCords.get(i)<xZone)
                        {
                            yCords.remove(i);
                            xCords.remove(i);
                            int j=i;
                            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                    xCords.add(xZone);
                                    yCords.add(yCords.get(j-1)-30);        
                                    j++;
=======
                                int j=1;
                                xCords.add(-1);
                                yCords.add(-1);
                                while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                                { 
                                        if(j==1)
                                        {
                                                xCords.remove(0);
                                                yCords.remove(0);  
                                                yCords.add(yStart);
                                                xCords.add(xStart);
                                        }
                                        yCords.add(yZone);
                                        if(right){
                                        	xCords.add(xStart+(j*30)); 
                                        }
                                        else{
                                        	xCords.add(xStart-(j*30)); 
                                        }
                                        j++;
                                }
                                break;
                                    
                            }
                            
                     }
                    else
                    	{                       
                         yCords.add(yStart);
                         xCords.add(xStart);
            
                    	}
                            
                    }
                 //Not the first iteration, fill in zig zag to zone coordinates
                    else
                    {
                        if((i%2)==0)
                        {        
                            if(up)
                            {          	
                            		xCords.add(xCords.get(i-1));
                                    yCords.add(yCords.get(i-1)+30);
                            	   
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
                            }
                            //Coordinate array complete
                            break;
                         }
                         else if (yCords.get(i)<yZone)
                         {
                        	 yCords.remove(i);
                             xCords.remove(i);
                             int j=i;
                             while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                             {
                                    yCords.add(yZone);        
                                    xCords.add(xCords.get(j-1)-30);        
                                    j++;
                             }
                             //Cordinate array complete
                             break;
                        }
                     }
                     else
                     {
                        
                    	if(xCords.get(i)>xZone)
                        {
                    		yCords.remove(i);
                            xCords.remove(i);
                            int j=i;
                            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                            {
<<<<<<< HEAD
                            	xCords.add(xZone);
                                yCords.add(yCords.get(j-1)-30);        
                                j++;
                             }
                             break;
                         }
                         else if (yCords.get(i)<yZone)
                         {
                        	 yCords.remove(i);
                        	 xCords.remove(i);
                        	 int j=i;
                        	 while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
                        	 {
                        		 yCords.add(yZone);        
                        		 xCords.add(xCords.get(j-1)+30);        
                        		 j++;
                        	 }
                        	 break;
                          }
                      }
                    
                //Still able to zig zag
                }
                //Increment i to add another segment to the zig zag
                i++;
         	}
    	}

	/**
	 * The fill method is a helper method for generate path
	 * It fills in the xCords and yCords arrayLists with values corresponding to a straight line from the starting position to the zone
	 * @param xStart int corresponding to x coordinate of starting position
	 * @param yStart int corresponding to y coordinate of starting position
	 * @param xZone  int corresponding to x coordinate of desired zone 
	 * @param yZone  int corresponding to y coordinate of desired zone
	 * @param xCords ArrayList of x coordinates in the path (only  grid intersections i.e. multiples of 30)
	 * @param yCords ArrayList of y coordinates in the path (only grid intersections i.e. multiples of 30)
	 * @param up  boolean whether the robot is traveling north or south (up or down)
	 * @param right boolean whether the robot is traveling east or west (right or left)
	 * @return returns boolean 
	 */
	
    private static boolean fill(int xStart, int yStart, int xZone, int yZone, ArrayList<Integer>xCords, ArrayList<Integer>yCords, boolean up, boolean right)
    {   
    	//Clear the old array
    	xCords.clear();
    	yCords.clear();
        if(xStart==xZone)
        {                                
            int j=1;
            //Add a point in order to give xCords and yCords a size so the while loop can be entered 
            xCords.add(-1);
            yCords.add(-1);
            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
            {
            	if(j==1)
            	{
            		//On the first iteration remove the spurious point and add the starting point
                    xCords.remove(0);
                    yCords.remove(0); 
                    yCords.add(yStart);
                    xCords.add(xStart);
                }
            	//X coordinate remains the same for the straight line
                xCords.add(xZone);
                //Fill in y coords accordingly      
                if(up)
                {
                	yCords.add(yStart+(j*30));  
                }
                else
                {
                	yCords.add(yStart-(j*30));  
                }
                j++;
            }
            return true;
        }
        else
        {
        	//The yStart coordinate is equal to the yZone coordinate
            int j=1;
            xCords.add(-1);
            yCords.add(-1);
            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
            { 
            	if(j==1)
            	{
                    xCords.remove(0);
                    yCords.remove(0);   
                    yCords.add(yStart);
                    xCords.add(xStart);
            	}
            	yCords.add(yZone);
                if(right)
                {
                	xCords.add(xStart+(j*30)); 
                }
                else
                {
                	xCords.add(xStart-(j*30)); 
                }
                j++;
            }
            return true;
                
         }
    }
    
    /**
     * This method processes the results of the scan at each point
     * If an obstacle is located in the scan then a new path is generated which avoids the obstacle
     * @author Connor, Bernie
     * @param scanResults integer array  where index 0: NORTH scan point (0 if clear) (1 if blocked), index 1: EAST scan point (0 if clear) (1 if blocked)
     index 2: SOUTH scan point (0 if clear) (1 if blocked), index 3: WEST scan point (0 if clear) (1 if blocked), index 4: found block? (0 if no) (1 if yes)
     * @param xCords ArrayList of x coordinates in the path
     * @param yCords ArrayList of y coordinates in the path
     * @param i current index of scan points, see builder and collector classes
     * @param nav Navigation object
     * @param map Map object
     * @return always return true
     */

    public static boolean processData(int[]scanResults,ArrayList<Integer>xCords, ArrayList<Integer>yCords, int i, Navigation nav, Map map)
    {            
    	//Create a random boolean object will be used in special cases described below
    	Random rand = new Random();
               
        //If north blocked (either obstacle or red zone) and next point is north
        if((yCords.get(i+1)>yCords.get(i))&&(scanResults[0]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
        {
        	//Handle case where robot wants to travel in a straight line but it is blocked
        	if(xCords.get(i)==xCords.get(xCords.size()-1))
            {
        		//The purpose of the random boolean is to avoid infinite loops where the generatePath method tells the robot to travel in zig zag path
        		//where the first point of the zig zag path is still blocked by the obstacle
        		//This way it will eventually try both east and west directions around the obstacle
                boolean random = rand.nextBoolean();
                if(random)
                {
                	nav.travelTo(true, false, xCords.get(i)+60, yCords.get(i));
                    generatePath(xCords.get(i)+60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                }
                else
                {
                    nav.travelTo(true, false, xCords.get(i)-60, yCords.get(i));
                    generatePath(xCords.get(i)-60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                 }        
             }
        	
	         //North west travelling zig zag
	         if(xCords.get(i+2)<xCords.get(i))
	         {    
	        	 //Avoid obstacle and generate a new path
	        	 nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                 generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);                              
                 return true;
	          }
	          //East travelling zig zag
	          else
	          {              
	             nav.travelTo(true, false, xCords.get(i)+30, yCords.get(i));
	             generatePath(xCords.get(i)+30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
	             return true;
	           }        
         }
        
        //East blocked (either red zone or obstacle) and next point east
        if((xCords.get(i+1)>xCords.get(i))&&(scanResults[1]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
        { 
        	if(yCords.get(yCords.size()-1)==yCords.get(i))
            {      
        		//Special case where robot wants to travel in a straight line and obstacle in the way
        		//Random boolean used to avoid infinite loop of generate path creating a path that is still blocked
        		boolean random = rand.nextBoolean();
        		if(random)
        		{
        			nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                    generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
        		}
        		else
        		{
        			nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
        			generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
        			return true;
        		}
                       
             }   
        	 nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
        	 generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
        	 return true;       
        }
        
        //West blocked and next point west
        if((xCords.get(i+1)<xCords.get(i))&&(scanResults[3]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
        {  
        	if(yCords.get(yCords.size()-1)==yCords.get(i))
            {
        		boolean random = rand.nextBoolean();
                if(random)
                {
                	nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                    generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                }
                else
                {
                    nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
                    generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                }
            }
        	//Other wise travel north one tile and generate a new path to zone
            nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
            generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
            return true;
        }
        
        //South blocked and next point is south
        if((yCords.get(i+1)<yCords.get(i))&&(scanResults[2]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
        {
            if(xCords.get(i)==xCords.get(xCords.size()-1))
            {
            	boolean random = rand.nextBoolean();
                if(random)
                {
                	nav.travelTo(true, false, xCords.get(i)+60, yCords.get(i));
                    generatePath(xCords.get(i)+60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                }
                else
                {
                    nav.travelTo(true, false, xCords.get(i)-60, yCords.get(i));
                    generatePath(xCords.get(i)-60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                }              
            }
            
            //South west traveling zig zag
            if(xCords.get(i+2)>xCords.get(i))
            {             
            	nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                return true;
            }
            
            //South east traveling zig zag
            else if(xCords.get(i+2)<xCords.get(i))
            {
            	nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                return true;
            }
            
        }
        //path was not altered
        return false;
    }

    /**
     * This method creates a new point for the robot to treat as the green zone after it has already placed one block in the green zone
     * @author Connor
     * @param BottomLeftGreenZone Point containing the x and y coordinates of the bottom left corner of the green zone
     * @param TopRightGreenZone Point containing the x and y coordinates of the top right corner of the greeen zone
     * @return returns an array of Points, the first and second indices containing Point objects representing the bottom left and top right corners of the mock green zone, respectively 
     * 
     */
    
    public static Point[] generateMockGreen(Point BottomLeftGreenZone, Point TopRightGreenZone)
    {
    	//Note that mid-point of competition board is 5*30
    	//Given the high blue block density, it is very likely that robot will find a second block before actually reaching this 
    	//"mock zone" point
    	if(BottomLeftGreenZone.getx()>=5*30)
        {                 
    		if(BottomLeftGreenZone.gety()>=5*30)
            {
    			//Here the green zone is in the top right quartile of the board, so we want the mock green zone to be in the opposite quartile of the board
    			//to cover more ground i.e. the bottom left quartile of the board
                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-4*30,BottomLeftGreenZone.gety()-4*30);
                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-4*30,TopRightGreenZone.gety()-4*30);
                Point[]mockGreen= new Point[2];
                mockGreen[0]=mockGreenBottomLeft;
                mockGreen[1]=mockGreenTopRight;
                return mockGreen;
            }
            else
            {
            	//Here the green zone is in the top left quartile of board, set the mock green zone to a point in the bottom right quartile of the board
                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-4*30,BottomLeftGreenZone.gety()+4*30);
                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-4*30,TopRightGreenZone.gety()+4*30);
                Point[]mockGreen= new Point[2];
                mockGreen[0]=mockGreenBottomLeft;
                mockGreen[1]=mockGreenTopRight;
                return mockGreen;
            }
                
        }
        if(BottomLeftGreenZone.getx()<5*30)
        {
            if(BottomLeftGreenZone.gety()>=5*30)
            {
            	//Here the green zone is in the bottom right quartile of the board, set the mock green zone to a point in the top left quartile
                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+4*30,BottomLeftGreenZone.gety()-4*30);
                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+4*30,TopRightGreenZone.gety()-4*30);
                Point[]mockGreen= new Point[2];
                mockGreen[0]=mockGreenBottomLeft;
                mockGreen[1]=mockGreenTopRight;
                return mockGreen;
            }
            else
            {
            	//Here the green zone is in the bottom left quartile of the board, set the mock green zone to a point in the top right quartile of board
                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+4*30,BottomLeftGreenZone.gety()+4*30);
                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+4*30,TopRightGreenZone.gety()+4*30);
                Point[]mockGreen= new Point[2];
                mockGreen[0]=mockGreenBottomLeft;
                mockGreen[1]=mockGreenTopRight;
                return mockGreen;
            }
        }
        //Return null otherwise
        //This code will never be reaches because one of the above cases will be true
        Point[]mockGreen= new Point[2]; 
        return mockGreen;
            
    }

}
=======
                                    
                                    yCords.remove(i);
                                    xCords.remove(i);
                                    int j=i;
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                            
                                            yCords.add(yZone);        
                                            xCords.add(xCords.get(j-1)+30);        
                                            j++;
                                    }
                                    break;
                            }
                    	}
                    }
             i++;
            }
    }

	public static boolean fill(int xStart, int yStart, int xZone,int yZone,ArrayList<Integer>xCords, ArrayList<Integer>yCords,boolean up,boolean right)
	{
		//X is already aligned with green zone
        if(xStart==xZone)
        {                        	
            int j=1;
            xCords.add(-1);
            yCords.add(-1);
            
            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
            {
                    if(j==1)
                    {
                            xCords.remove(0);
                            yCords.remove(0); 
                            yCords.add(yStart);
                            xCords.add(xStart);
               
                    }
                    //keep x the same
                    xCords.add(xZone);
                    //Fill in y coords accordingly 
                    if(up){
                    	  yCords.add(yStart+(j*30));  
                    }
                    else{
                    	 yCords.add(yStart-(j*30));  
                    }
                    j++;
            }
            return true;
         }
      else
        {
            int j=1;
            xCords.add(-1);
            yCords.add(-1);
            while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone))
            { 
                    if(j==1)
                    {
                            xCords.remove(0);
                            yCords.remove(0);   
                            yCords.add(yStart);
                            xCords.add(xStart);
                    }
                    yCords.add(yZone);
                    if(right){
                    	xCords.add(xStart+(j*30)); 
                    }
                    else{
                    	xCords.add(xStart-(j*30)); 
                    }
                    j++;
            }
            return true;
                
        }
   
	}
	

    public static boolean processData(int[]scanResults,ArrayList<Integer>xCords, ArrayList<Integer>yCords, int i, Navigation nav, Map map){        
            
    	Random rand = new Random();
    	boolean up;
		boolean right;
		if(yCords.get(yCords.size()-1)>yCords.get(0)){
			up=true;
			if(xCords.get(xCords.size()-1)>xCords.get(0)){
				right=true;
<<<<<<< HEAD
<<<<<<< HEAD
			}
			else{right=false;}
		}
		else{
			up=false;
			if(xCords.get(xCords.size()-1)<xCords.get(0)){
				right=true;
			}
			else{
				right=false;
			}
=======
			}
			else{right=false;}
		}
		else{
			up=false;
			if(xCords.get(xCords.size()-1)<xCords.get(0)){
				right=true;
			}
			else{
				right=false;
			}
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
=======
			}
			else{right=false;}
		}
		else{
			up=false;
			if(xCords.get(xCords.size()-1)<xCords.get(0)){
				right=true;
			}
			else{
				right=false;
			}
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
		}
    	
    	
            //North blocked and next point is north
            if((yCords.get(i+1)>yCords.get(i))&&(scanResults[0]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
            {
            	
            	if((i+1)==xCords.size()-1)
            	{
            		//do something
            	}
            	
            	if(xCords.get(i)==xCords.get(xCords.size()-1))
            	{
            		boolean random = rand.nextBoolean();
                    if(random)
                    {
                    	 nav.travelTo(true, false, xCords.get(i)+60, yCords.get(i));
                    	 generatePath(xCords.get(i)+60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                         return true;
                    }
                    else
                    {
                    	nav.travelTo(true, false, xCords.get(i)-60, yCords.get(i));
                   	 	generatePath(xCords.get(i)-60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                        return true;
                    }
            		
            	}
            
            	//West travelling zig zag
               if(xCords.get(i+2)<xCords.get(i))
               {
            	   
            	   if(scanResults[3]==1)//west blocked
            	   {
            		   //double block case
            		   nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
                       generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);                              
                       return true;
            	   }
                        nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                        generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);                              
                        return true;
                }
                //East travelling zig zag
                else
                {
                	if(scanResults[1]==1)//east blocked
             	   {
                		nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
                        generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);                              
                        return true;
             	   }
                nav.travelTo(true, false, xCords.get(i)+30, yCords.get(i));
                generatePath(xCords.get(i)+30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                
                return true;
                }        
            }
            
            //East blocks and next point east
            if((xCords.get(i+1)>xCords.get(i))&&(scanResults[1]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
            {
                    
                    if(yCords.get(yCords.size()-1)==yCords.get(i))
                    {
                           
                            //if random
                            boolean random = rand.nextBoolean();
                            if(random)
                            {
                            	 nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                            	 generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                                 return true;
                            }
                            else
                            {
                            	 nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
                            	 generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                                 return true;
                            }
                           
                    }
                    if(scanResults[0]==1)//north blocked
              	   {
              		   //double block case
              	   }
                  //always go up by default?
                    nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                    generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
                    
            }
            
            //West blocked and next point west
            if((xCords.get(i+1)<xCords.get(i))&&(scanResults[3]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
            {
                    
                    if(yCords.get(yCords.size()-1)==yCords.get(i))
                    {
	                    boolean random = rand.nextBoolean();
	                    if(random)
	                    {
	                    	 nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
	                    	 generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
	                         return true;
	                    }
	                    else
	                    {
	                    	 nav.travelTo(true, false, xCords.get(i), yCords.get(i)-30);
	                    	 generatePath(xCords.get(i),yCords.get(i)-30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
	                         return true;
	                    }
                    }
                    if(scanResults[1]==1)//north blocked
              	   {
              		   //double block case
              	   }
                    //always go up by default?
                    nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                    generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                    return true;
            }
            
            //South blocked and next point is south
            if((yCords.get(i+1)<yCords.get(i))&&(scanResults[2]==1|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1))))
            {
//            	if((i+1)==xCords.size()-1)
//            	{
//            		//do something
//            		//maybe regenerate
//            	}
                   
            	if(xCords.get(i)==xCords.get(xCords.size()-1))
            	{
            		boolean random = rand.nextBoolean();
                    if(random)
                    {
                    	 nav.travelTo(true, false, xCords.get(i)+60, yCords.get(i));
                    	 generatePath(xCords.get(i)+60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                         return true;
                    }
                    else
                    {
                    	nav.travelTo(true, false, xCords.get(i)-60, yCords.get(i));
                   	 	generatePath(xCords.get(i)-60,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                        return true;
                    }
            		
            	}
            	//West travelling zig zag
                    if(xCords.get(i+2)>xCords.get(i))
                    {
                    	if(scanResults[3]==1)//west blocked
                  	   	{
                  		   //double block case
                    		nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                            generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                            return true;
                  	   	}
                    	else{
                            nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                            generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                            return true;}
                    }
                    //East travelling zig zag
                    else if(xCords.get(i+2)<xCords.get(i))
                    {
                    	if(scanResults[1]==1)//east blocked
                  	   {
                    		//double block case
                    		nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                            generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                            return true;
                  	   }
                            nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
                            generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                            return true;
                    }        
            }

            return false;//path was not altered
    }

    public static Point[] generateMockGreen(Point BottomLeftGreenZone, Point TopRightGreenZone)
    {
            if(BottomLeftGreenZone.getx()>=5*30)
            {        
                    if(BottomLeftGreenZone.gety()>=5*30)
                    {
                            Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-4*30,BottomLeftGreenZone.gety()-4*30);
                            Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-4*30,TopRightGreenZone.gety()-4*30);
                            Point[]mockGreen= new Point[2];
                            mockGreen[0]=mockGreenBottomLeft;
                            mockGreen[1]=mockGreenTopRight;
                            return mockGreen;
                    }
                    else
                    {
                            Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-4*30,BottomLeftGreenZone.gety()+4*30);
                            Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-4*30,TopRightGreenZone.gety()+4*30);
                            Point[]mockGreen= new Point[2];
                            mockGreen[0]=mockGreenBottomLeft;
                            mockGreen[1]=mockGreenTopRight;
                            return mockGreen;
                    }
                    
            }
            if(BottomLeftGreenZone.getx()<5*30)
            {
                    if(BottomLeftGreenZone.gety()>=5*30)
                    {
                            Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+4*30,BottomLeftGreenZone.gety()-4*30);
                            Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+4*30,TopRightGreenZone.gety()-4*30);
                            Point[]mockGreen= new Point[2];
                            mockGreen[0]=mockGreenBottomLeft;
                            mockGreen[1]=mockGreenTopRight;
                            return mockGreen;
                    }
                    else
                    {
                            Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+4*30,BottomLeftGreenZone.gety()+4*30);
                            Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+4*30,TopRightGreenZone.gety()+4*30);
                            Point[]mockGreen= new Point[2];
                            mockGreen[0]=mockGreenBottomLeft;
                            mockGreen[1]=mockGreenTopRight;
                            return mockGreen;
                    }
            }
            Point[]mockGreen= new Point[2]; 
            return mockGreen;
            
    }

}

<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
=======
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
=======
>>>>>>> a6a3c5fb4f1f3a16a15217ecf0ead7c7aa5f4c85
