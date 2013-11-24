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
public class FinalCompetition {
	
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
		Point BottomLeftRedZone= new Point(30,60);
		Point TopRightRedZone=new Point(60,90);
		map.setDeadZone(BottomLeftRedZone,TopRightRedZone);
		Point BottomLeftGreenZone= new Point(60,60);
		Point TopRightGreenZone=new Point(90,90);
		map.setDropZone(BottomLeftGreenZone, TopRightGreenZone);
		
		ArrayList<Integer> xCords = new ArrayList<Integer>();
		ArrayList<Integer> yCords = new ArrayList<Integer>();
		int [] scanResults = new int[5];
		robo.pickUpBlock();
		RConsole.openAny(20000);
		Button.waitForAnyPress();		
		
		//lift claw
		
		//generate initial path to desired zone
		Path.generatePath(0,0,(int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords,true);	//boolean set to true because upward zig zag
		for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", " +yCords.get(k));}//printing
		
		//Travel to green zone while looking for blocks
		boolean hasBlock=false;
		boolean picked=false;
		
		for(int i=0;i<xCords.size();++i)
		{	
			if(i==xCords.size()-1)
			{ 
				nav.travelTo(true, false,xCords.get(i),yCords.get(i));
				break;//done in greenZone
			}
			if(picked)//robot has just picked a block
			{
				nav.travelTo(false, false,xCords.get(i),yCords.get(i));
				picked = false;
			}
			else
			{			
				nav.travelTo(true, false,xCords.get(i),yCords.get(i));
			}	
			
			nav.turnTo(300, true);
			scanResults = search.Scan(hasBlock);
			if(scanResults[4]==1){
				hasBlock=true;
				picked = true;
			}
			else
			{
				nav.travelSetDistanceBackwards(5);
				nav.fineTune();
			}
			if(Path.processData(scanResults, xCords,yCords,i,nav,true,map))
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
		Path.generatePath((int)BottomLeftGreenZone.getx(),(int)BottomLeftGreenZone.gety(), (int) Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].getx() ,(int)Path.generateMockGreen(BottomLeftGreenZone,TopRightGreenZone)[0].gety(), xCords, yCords,true);
		for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}//printing
		RConsole.println("end path");
		
		//Path.generatePath((int)BottomLeftGreenZone.getx(),(int)BottomLeftGreenZone.gety(), 0,0, xCords, yCords,false);
		//for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}//printing
		
		for(int i=0;i<xCords.size();++i)
		{		
			if(i==0)
			{
				nav.travelTo(false, false,xCords.get(i),yCords.get(i));
			}
			else
			{
				nav.travelTo(true, false,xCords.get(i),yCords.get(i));
			}
			//should store these 
			map.MemAddCoordinates(xCords.get(i),yCords.get(i));
			nav.turnTo(135, true); //now travelling south so want to turn to 135 instead of 300 for scans
			if(i!=xCords.size()-1)
			{
				scanResults = search.Scan(hasBlock);
			}
			
			if(scanResults[4]==1)
			{
				hasBlock=true;
				//Go back to green zone to stack
				/*
				for (int k =0 ; k < map.getXMemory().size();k++)
				{
					nav.travelTo(true, false, map.getXMemory().get(k), map.getYMemory().get(k));
				}
				*/
				nav.travelTo(false, false,xCords.get(i),yCords.get(i));
				nav.turnTo(0, true);
				nav.travelSetDistanceBackwards(5);
				nav.fineTune();
				Path.generatePath(xCords.get(i),yCords.get(i), (int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords,true);
				for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", " +yCords.get(k));}//printing
				
				for(int j=0;j<xCords.size();j++)
				{				
					nav.travelTo(true, false,xCords.get(j),yCords.get(j));
					
				}
					robo.stopMotors();
					nav.stack();
					robo.pickUpBlock();
					//Back in green zone with second block, ready to stack
			}
			else
			{
				nav.travelSetDistanceBackwards(5);
				nav.fineTune();
			}
			if(Path.processData(scanResults, xCords,yCords,i,nav,true,map))
			{
				i=-1;
				RConsole.println("recalculated");
				for(int k=0;k<xCords.size();k++){RConsole.println(""+xCords.get(k)+", "+yCords.get(k));}
				RConsole.println("end of path");
			}
		}
		

}
}
