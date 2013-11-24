package Tests;
import Hardware.*;
import MainLogic.*;
import IntermediateLogic.*;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import bluetooth.*;
import java.util.ArrayList;
import java.util.Timer;

import bluetooth.BluetoothConnection;
import bluetooth.Transmission;
public class FinalCompetition{
        
public static void main()
{
        //Create sensors
        ColorSensor csLeft= new ColorSensor(SensorPort.S1);
        ColorSensor csRight=new ColorSensor(SensorPort.S4);
        UltrasonicSensor UsBottom= new UltrasonicSensor(SensorPort.S2);
        UltrasonicSensor UsTop= new UltrasonicSensor(SensorPort.S3);
        
        //Create pollers
        LightPoller leftWheelLP= new LightPoller(csLeft);
        LightPoller rightWheelLP=new LightPoller(csRight);
        UltrasonicPoller USPbottom = new UltrasonicPoller(UsBottom);
        UltrasonicPoller USPtop = new UltrasonicPoller(UsTop);
        UltrasonicScanner USStop = new UltrasonicScanner(UsTop);
        UltrasonicScanner USSBottom = new UltrasonicScanner(UsBottom);
        
        //Create objects
        TwoWheeledRobot robo = new TwoWheeledRobot(Motor.A, Motor.B, Motor.C, USStop, USSBottom, USPtop, USPbottom, leftWheelLP, rightWheelLP);
        Odometer odo = new Odometer(robo,true);
        Navigation nav = new Navigation(robo, odo, null);
        lightlocalization loc = new lightlocalization( robo,odo,nav);
        Search search = new Search(robo, nav, odo);
        //Clock timeLimit = new Clock(300000);
        
        //RConsole
        //RConsole.openAny(20000);
        
        //Set up bluetooth
        /*BluetoothConnection conn = new BluetoothConnection();
        Transmission t = conn.getTransmission();
        if (t == null) 
        {
                LCD.drawString("Failed to read transmission", 0, 5);
        } 
        else
        { 
                //Set up map, where starting corner is 0,0
                int[] greenZone= {t.greenZone[0],t.greenZone[1],t.greenZone[2],t.greenZone[3]};
                int[] redZone={t.redZone[0],t.redZone[1],t.redZone[2],t.redZone[3]};
                StartCorner startingCorner=t.startingCorner;
                int startingCornerID=startingCorner.getId(); //1=bottom left, 2=bottom right, 3=top right, 4=top left
                convertMap(greenZone,redZone,startingCornerID);
                Map map=new Map(12*30,12*30);        
                Point BottomLeftRedZone= new Point(redZone[0]*30,redZone[1]*30);
                Point TopRightRedZone=new Point(redZone[2]*30,redZone[3]*30);
                map.setDeadZone(BottomLeftRedZone,TopRightRedZone);
                Point BottomLeftGreenZone= new Point(greenZone[0]*30,greenZone[1]*30);
                Point TopRightGreenZone=new Point(greenZone[2]*30,greenZone[3]*30);
                map.setDropZone(BottomLeftGreenZone, TopRightGreenZone);
                if(t.role.equals("Builder")){
                        //Build
                }
                else{
        
                }*/
        
                //Builder therefore treat red zone as obstacle
        
                Map map=new Map(12*30,12*30);        
                Point BottomLeftRedZone= new Point(150,150);
                Point TopRightRedZone=new Point(180,180);
                map.setDeadZone(BottomLeftRedZone,TopRightRedZone);
                Point BottomLeftGreenZone= new Point(60,120);
                Point TopRightGreenZone=new Point(90,150);
                map.setDropZone(BottomLeftGreenZone, TopRightGreenZone);
                
                ArrayList<Integer> xCords = new ArrayList<Integer>();
                ArrayList<Integer> yCords = new ArrayList<Integer>();
                int [] scanResults = new int[5];
               
                RConsole.openAny(20000);
                Button.waitForAnyPress();                
                
                //lift claw
                robo.pickUpBlock();
                
                //generate initial path to desired zone
                Path.generatePath(0,0,(int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords); //boolean set to true because upward zig zag
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
//                        if(i==0)
//                        {
//                                nav.travelTo(false, false,xCords.get(i),yCords.get(i));
//                                continue;
//                        }
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
                RConsole.println(""+ (int)Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].getx()+", "+(int) Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].gety() );
                int xMock=(int)Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].getx();
                int yMock=(int)Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].gety();
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
                                continue;
                        }
                        else
                        {
                                nav.travelTo(true, false,xCords.get(i),yCords.get(i));
                        }
                		
                        //Remember path for returning to actual green zone
                        map.MemAddCoordinates(xCords.get(i),yCords.get(i));
                        
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
                                for(int k=0;k<xCords.size();k++){RConsole.println(""+map.getXMemory().get(k)+", " +map.getYMemory().get(k));}//printing
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
