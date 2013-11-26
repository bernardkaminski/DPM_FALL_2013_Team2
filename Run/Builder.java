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
	static void main(Odometer odo, TwoWheeledRobot robo,Navigation nav,Search search,lightlocalization loc, Clock clock,Map map,int startingCorner ) 
	{
		robo.pickUpBlock();
		loc.localize();
		//setting odometer according to starting corner
		switch (startingCorner) 
		{
		case 1:
			odo.setX(0);
			odo.setY(0);
			break;
		case 2:
			odo.setX(300);
			odo.setY(0);
			break;
		case 3:
			odo.setX(300);
			odo.setY(300);
			odo.setTheta(180);
			break;
		case 4:
			odo.setX(0);
			odo.setY(300);
			odo.setTheta(180);
			break;
		default:
			break;
		}
		
		ArrayList<Integer> xCords = new ArrayList<Integer>();
        ArrayList<Integer> yCords = new ArrayList<Integer>();
        int [] scanResults = new int[5];
      //generate initial path to desired zone
        Path.generatePath(0,0,(int)map.getDropZoneBottomLeft().getx(), (int)map.getDropZoneBottomLeft().gety(), xCords, yCords); //boolean set to true because upward zig zag
        for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", " +yCords.get(k));}//printing
        
        //Travel to green zone while looking for blocks
        boolean hasBlock=false;
       
        
        for(int i=0;i<xCords.size();++i)
        {        
                if(i==xCords.size()-1)
                { 
                        nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                        break;//done in greenZone
                }
//                if(i==0)
//                {
//                        nav.travelTo(false, false,xCords.get(i),yCords.get(i));
//                        continue;
//                }
                else
                {
                        nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                }   
                //Turn north for scan
                nav.turnTo(0, true);
                
                //Scan
                scanResults = search.Scan(hasBlock);
                
                //Turn back to north for scan
                nav.turnTo(0, true);
                if(scanResults[4]==1){
                        hasBlock=true;
                       
                }
                //Fine tune
                else
                {
                        nav.travelSetDistanceBackwards(5);
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
        }
        robo.stopMotors();
        
        
        //Now in green zone
        if(hasBlock)
        {
                nav.drop();
                hasBlock=false;
        }

        //Generate new path to next "green zone" (point chosen to ensure that robot covers previously uncovered ground
        RConsole.println("start path");
        RConsole.println(""+ (int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDeadZoneTopRight())[0].getx()+", "+(int) Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDeadZoneTopRight())[0].gety() );
        int xMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDeadZoneTopRight())[0].getx();
        int yMock=(int)Path.generateMockGreen(map.getDropZoneBottomLeft(),map.getDeadZoneTopRight())[0].gety();
        RConsole.println(""+xMock+", "+yMock);
        
        Path.generatePath(xCords.get(xCords.size()-1),yCords.get(yCords.size()-1), xMock ,yMock, xCords, yCords);
        for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}//printing
        RConsole.println("end path");
        
        //Travel to next "green zone" looking for blocks
        for(int i=0;i<xCords.size();++i)
        {                
        		if(i==xCords.size()-1)
        		{ 
                    nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                    break;//done in mock greenZone
        		}    
        		if(i==0)
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
                if(yCords.get(yCords.size()-1)>yCords.get(0)){
                	nav.turnTo(0,true);
                }
                else{
                	nav.turnTo(180, true); 
                }
                
                if(i!=0){
                	scanResults=search.Scan(hasBlock);
                } 
                
                if(yCords.get(yCords.size()-1)>yCords.get(0)){
                	nav.turnTo(0,true);
                }
                else{
                	nav.turnTo(180, true); 
                }
                if(scanResults[4]==1)
                {
                        hasBlock=true;
                        
                        //Go back to green zone to stack using exact same path
                        for(int k=0;k<map.getXMemory().size();k++){RConsole.println(""+map.getXMemory().get(k)+", " +map.getYMemory().get(k));}//printing
                        for (int k =0 ; k < map.getXMemory().size();k++)
                        {
                                nav.travelTo(true, false, map.getXMemory().get(k), map.getYMemory().get(k));
                        }
                        //Now in green zone for second time,stack the block
                        nav.stack();  
                        break;
                }
                else
                {
                        nav.travelSetDistanceBackwards(5);
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
        //2 block stacked or in mock green zone
	}

}
