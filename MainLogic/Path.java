package MainLogic;


import java.util.ArrayList;

import IntermediateLogic.Navigation;

public class Path 
{
        
	 
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
    		else{
    			up=false;
    			if(xZone<xStart){
    				right=true;
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
                            	   
                            }
                            else{
                                    xCords.add(xCords.get(i-1));
                                    yCords.add(yCords.get(i-1)-30);}                                
                                    
                        }
                        else
                        {  
                        	if(up){
                        		if(right)
                                {
                                     xCords.add(xCords.get(i-1)+30);
                                     yCords.add(yCords.get(i-1));                                
                                }
                             	else{ xCords.add(xCords.get(i-1)-30);
                                   yCords.add(yCords.get(i-1));
                                   }                       		
                        		}
                        	else{
                        		if(right)
                                {
                                     xCords.add(xCords.get(i-1)-30);
                                     yCords.add(yCords.get(i-1));                                
                                }
                             	else{ xCords.add(xCords.get(i-1)+30);
                                   yCords.add(yCords.get(i-1));} 
                        	}
                        
                        }
                    
                            
                    }
                 //Calculated point has exceeded one of the goal coordinate, can no longer zig zag 
                    if(up)
                    {
                    	if(right){
                    		if(xCords.get(i)>xZone)
                            {		//Remove that bad point
                                    yCords.remove(i);
                                    xCords.remove(i);
                                    int j=i;
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                            xCords.add(xZone);
                                            yCords.add(yCords.get(j-1)+30);        
                                            j++;
                                    }
                                  //Coordinate array complete
                                    break;
                            }
                    		else if(yCords.get(i)>yZone)
                        		{ //Remove that bad point
                                	yCords.remove(i);
                                	xCords.remove(i);
                                	int j=i;
                                	while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                      xCords.add(xCords.get(j-1)+30);
                                      yCords.add(yZone);       
                                      j++;
                                }
                                //Coordinate array complete
                                break;
                        		}
                    		}
                    	else{
                    		if(xCords.get(i)<xZone)
                            {		//Remove that bad point
                                    yCords.remove(i);
                                    xCords.remove(i);
                                    int j=i;
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                            xCords.add(xZone);
                                            yCords.add(yCords.get(j-1)+30);        
                                            j++;
                                    }
                                  //Coordinate array complete
                                    break;
                            }
                    		else if(yCords.get(i)>yZone)
                        		{ //Remove that bad point
                                	yCords.remove(i);
                                	xCords.remove(i);
                                	int j=i;
                                	while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                      xCords.add(xCords.get(j-1)-30);
                                      yCords.add(yZone);       
                                      j++;
                                }
                                //Coordinate array complete
                                break;
                        	}
                    		
                    	}
                    }
                    //Going down
                    else
                    {	
                    	if(right){
                    		
                            if(xCords.get(i)<xZone)
                            {
                                    yCords.remove(i);
                                    xCords.remove(i);
                                    int j=i;
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
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
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
                                            
                                            yCords.add(yZone);        
                                            xCords.add(xCords.get(j-1)-30);        
                                            j++;
                                    }
                                    break;
                            }
                         
                    	}
                    	else{
                    		if(xCords.get(i)>xZone)
                            {
                                    yCords.remove(i);
                                    xCords.remove(i);
                                    int j=i;
                                    while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
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


        public static boolean processData(int[]scanResults,ArrayList<Integer>xCords, ArrayList<Integer>yCords, int i, Navigation nav, Map map){        
                
                //North blocked and next point is north
                if(scanResults[0]==1&&yCords.get(i+1)>yCords.get(i)|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1)))
                {
                        //West travelling zig zag
                        if(xCords.get(i+2)<xCords.get(i)){
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
                
                //East blocks and next point east
                if(scanResults[1]==1&&xCords.get(i+1)>xCords.get(i)|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1)))
                {
                        
                        if(yCords.get(yCords.size()-1)==yCords.get(i))
                        {
                                nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                                nav.travelTo(true, false, xCords.get(i)+30, yCords.get(i)+30);
                                generatePath(xCords.get(i)+30,yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                                return true;
                        }
                        nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                        generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                        return true;
                        
                }
                
                //West blocked and next point west
                if(scanResults[3]==1&&xCords.get(i+1)<xCords.get(i)|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1)))
                {
                        
                        if(yCords.get(yCords.size()-1)==yCords.get(i))
                        {
                                nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                                nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i)+30);
                                generatePath(xCords.get(i)-30,yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                                return true;
                        }
                        nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
                        generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords);
                        return true;
                }
                
                //South blocked and next point is south
                if(scanResults[2]==1&&yCords.get(i+1)<yCords.get(i)|| map.isInDeadZone(xCords.get(i+1), yCords.get(i+1)))
                {
                        //West travelling zig zag
                        if(xCords.get(i+2)>xCords.get(i))
                        {
                                 
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

                return false;//path was not altered
        }

        public static Point[] generateMockGreen(Point BottomLeftGreenZone, Point TopRightGreenZone)
        {
                if(BottomLeftGreenZone.getx()>=3*30)
                {        
                        if(BottomLeftGreenZone.gety()>=3*30)
                        {
                                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-2*30,BottomLeftGreenZone.gety()-2*30);
                                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-2*30,TopRightGreenZone.gety()-2*30);
                                Point[]mockGreen= new Point[2];
                                mockGreen[0]=mockGreenBottomLeft;
                                mockGreen[1]=mockGreenTopRight;
                                return mockGreen;
                        }
                        else
                        {
                                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()-2*30,BottomLeftGreenZone.gety()+2*30);
                                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()-2*30,TopRightGreenZone.gety()+2*30);
                                Point[]mockGreen= new Point[2];
                                mockGreen[0]=mockGreenBottomLeft;
                                mockGreen[1]=mockGreenTopRight;
                                return mockGreen;
                        }
                        
                }
                if(BottomLeftGreenZone.getx()<3*30)
                {
                        if(BottomLeftGreenZone.gety()>=3*30)
                        {
                                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+2*30,BottomLeftGreenZone.gety()-2*30);
                                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+2*30,TopRightGreenZone.gety()-2*30);
                                Point[]mockGreen= new Point[2];
                                mockGreen[0]=mockGreenBottomLeft;
                                mockGreen[1]=mockGreenTopRight;
                                return mockGreen;
                        }
                        else
                        {
                                Point mockGreenBottomLeft= new Point(BottomLeftGreenZone.getx()+2*30,BottomLeftGreenZone.gety()+2*30);
                                Point mockGreenTopRight= new Point(TopRightGreenZone.getx()+2*30,TopRightGreenZone.gety()+2*30);
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