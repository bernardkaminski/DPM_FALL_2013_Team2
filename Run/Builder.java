package Run;

import java.util.ArrayList;

import lejos.nxt.comm.RConsole;

import bluetooth.Transmission;
import Hardware.TwoWheeledRobot;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import IntermediateLogic.lightlocalization;
import MainLogic.*;

public class Builder {

	/**
	 * @param args
	 */
	static void main(Odometer odo, TwoWheeledRobot robo,Navigation nav,Search search,lightlocalization loc,Map map,int startingCorner ) 
	{
		Boolean timeUp= new Boolean(false);
		//Clock timeLimit = new Clock(290000,map.getDropZoneBottomLeft(),nav,robo);
		//Clock timeLimit = new Clock(290000,timeUp);
		
		robo.raiseClaw();
		loc.localize();
		int Ystart=0;
		int Xstart=0;
		//setting odometer according to starting corner
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
			odo.setX(300);//300
			odo.setY(300);//300
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
		//nav.travelTo(false,false,Xstart,Ystart);
		
		ArrayList<Integer> xCords = new ArrayList<Integer>();
        ArrayList<Integer> yCords = new ArrayList<Integer>();
        int [] scanResults = new int[5];
      //generate initial path to desired zone
        Path.generatePath(Xstart,Ystart,(int)map.getDropZoneBottomLeft().getx(), (int)map.getDropZoneBottomLeft().gety(), xCords, yCords); //boolean set to true because upward zig zag
        for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", " +yCords.get(k));}//printing
        
        //Travel to green zone while looking for blocks
        boolean hasBlock=false;
        boolean correct= true;
        
        for(int i=0;i<xCords.size();++i)
        {        
                if(i==xCords.size()-1)
                { 
                        nav.travelTo(correct, false,xCords.get(i),yCords.get(i));
                        break;//done in greenZone
                }
                /*if(i==0)
                {
                        nav.travelTo(false, false,xCords.get(i),yCords.get(i));
                        continue;
               }
                else*/
                {
                        nav.travelTo(correct, false,xCords.get(i),yCords.get(i));
                        correct=true;
                }   
                //Turn  for scan
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
                
                //Turn  for scan
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
               
                if(scanResults[4]==1||scanResults[4]==2){
                        hasBlock=true;
                        correct=false;
                       
                }
                //Fine tune
                else
                {
                        nav.travelSetDistanceBackwards(7);
                        nav.fineTune();
                }
                //Process data to determine subsequent path
                if(Path.processData(scanResults, xCords,yCords,i,nav,map))
                {
                        i=(-1);//reset the loop
                        RConsole.println("recalculated");
                        for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}
                        RConsole.println("end of path");
                }
                else
                {RConsole.println("Path unaltered");}
                if (timeUp)
                {
                	nav.travelTo(false, false, map.getDropZoneBottomLeft().getx(), map.getDropZoneBottomLeft().gety());
                	nav.turnTo(45, true);
                	System.exit(0);
                }
        }
        robo.stopMotors();
        
        
        //Now in green zone
        if(hasBlock)
        {
                nav.stack();
                if(map.getDropZoneBottomLeft().getx()<50){
                nav.travelTo(false, false, map.getDropZoneBottomLeft().getx(), map.getDropZoneBottomLeft().gety()-15);
                nav.travelTo(false, false, map.getDropZoneBottomLeft().getx()+30, map.getDropZoneBottomLeft().gety()-30);}
                hasBlock=false;
        }

        //Generate new path to next "green zone" (point chosen to ensure that robot covers previously uncovered ground
        RConsole.println("start path");
        
      
        
       	int xMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDropZoneTopRight())[0].getx();
    	int yMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDropZoneTopRight())[0].gety();
    	map.MemAddCoordinates(xCords.get(xCords.size()-1),yCords.get(yCords.size()-1));
    	Path.generatePath(xCords.get(xCords.size()-1),yCords.get(yCords.size()-1), xMock ,yMock, xCords, yCords);
        
        
        for(int k=0;k<xCords.size();k++){
        	RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}//printing
        RConsole.println("end path");
        
        //Travel to next "green zone" looking for blocks
        for(int i=0;i<xCords.size();++i)
        {                
        		if(i==xCords.size()-1)
        		{ 
                    nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                    break;//done in mock greenZone
        		}    
        		if(xCords.get(i)==map.getDropZoneBottomLeft().getx()&& yCords.get(i)==map.getDropZoneBottomLeft().gety())
                {
                        nav.travelTo(false, false,xCords.get(i),yCords.get(i));
                        //Remember path for returning to actual green zone
                        map.MemAddCoordinates(xCords.get(i),yCords.get(i));
                        continue;
                }
                else
                {
                        nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                        //Remember path for returning to actual green zone
                        map.MemAddCoordinates(xCords.get(i),yCords.get(i));
                }
        		
             
                
                //Determine turning angle for scan
        		  //Turn  for scan
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
                
                if(i!=0){
                	scanResults=search.Scan(hasBlock);
                } 
                
                //Turn  for scan
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
                if(scanResults[4]==1)
                {
                        hasBlock=true;
                        
                        //Go back to green zone to stack using exact same path
                        for(int k=0;k<map.getXMemory().size();k++){RConsole.println(""+map.getXMemory().get(k)+", " +map.getYMemory().get(k));}//printing
                        for (int k =map.getXMemory().size()-1 ; k > 0;k--)
                        {
                                nav.travelTo(true, false, map.getXMemory().get(k), map.getYMemory().get(k));
                        }
                        //Now in green zone for second time,stack the block
                        
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
                        i=-1;
                        RConsole.println("recalculated");
                        for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}
                        RConsole.println("end of path");
                }
        	}
        if (timeUp)
        {
        	nav.travelTo(false, false, map.getDropZoneBottomLeft().getx(), map.getDropZoneBottomLeft().gety());
        	nav.turnTo(45, true);
        	System.exit(0);
        }
        //2 block stacked or in mock green zone
	}

}
