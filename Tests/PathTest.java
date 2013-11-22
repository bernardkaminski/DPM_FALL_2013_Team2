package Tests;
import Hardware.*;
import MainLogic.*;
import IntermediateLogic.*;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import bluetooth.*;
import java.util.ArrayList;

import javax.microedition.sensor.HeadingChannelInfo;

import bluetooth.BluetoothConnection;
import bluetooth.Transmission;
public class PathTest {
	public static void main(){
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
		Localizer loc = new Localizer(odo, robo);
		Search search = new Search(robo, nav, odo);
		
		
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
				/
			}*/
		
			//Builder therefore treat red zone as obstacle
			Map map=new Map(12*30,12*30);	
			Point BottomLeftRedZone= new Point(30,60);
			Point TopRightRedZone=new Point(60,90);
			map.setDeadZone(BottomLeftRedZone,TopRightRedZone);
			Point BottomLeftGreenZone= new Point(90,90);
			Point TopRightGreenZone=new Point(120,120);
			map.setDropZone(BottomLeftGreenZone, TopRightGreenZone);
			ArrayList<Integer> xCords = new ArrayList<Integer>();
			ArrayList<Integer> yCords = new ArrayList<Integer>();
			int [] scanResults = new int[5];
			RConsole.openAny(20000);
			Button.waitForAnyPress();		
			
			//variable to remember where first block was placed
			double stackHeading;
			double xStack;
			double yStack;
			
			//generate initial path to desired zone
			generatePath(0,0,(int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords,true);	
			for(int k=0;k<xCords.size();k++){
			RConsole.println(""+xCords.get(k)+", ");
			}
			for(int k=0;k<xCords.size();k++){
				RConsole.println(""+yCords.get(k)+", ");
				}
			//Lift claw
			robo.rotateClawAbsolute(80);
			//Travel to green zone while looking for blocks
			boolean hasBlock=false;
			for(int i=0;i<xCords.size();i++){				
				nav.travelTo(true, false,xCords.get(i),yCords.get(i));
				//nav.turnTo(270, true);
				/*scanResults = search.Scan(hasBlock);
				if(scanResults[4]==1){
					hasBlock=true;
				}
				processData(scanResults, xCords,yCords,i,nav,BottomLeftRedZone,TopRightRedZone);*/
				
			}
			robo.stopMotors();
			
			//Now in green zone
			if(hasBlock){
				//Drop block
				nav.turnTo(45, true);
				drop(robo,nav);
				//robo.rotateClawAbsolute(search.CLAW_LOWER_ANGLE);
				stackHeading = odo.getTheta();
				xStack= odo.getX();
				yStack= odo.getY();
				hasBlock=false;
				}
			
			//Generate new path to next "green zone" (point chosen to ensure that robot covers previously uncovered ground
			//just back to origin for testing
			generatePath((int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(),0,0, xCords, yCords,false);
			for(int k=0;k<xCords.size();k++){
				RConsole.println(""+xCords.get(k)+", ");
				}
			for(int k=0;k<xCords.size();k++){
				RConsole.println(""+yCords.get(k)+", ");
				}
			for(int i=0;i<xCords.size();i++){				
				nav.travelTo(true, false,xCords.get(i),yCords.get(i));
				nav.turnTo(90, true); //now travelling south so want to turn to 90 instead of 270 for scans
				scanResults = search.Scan(hasBlock);
				if(scanResults[4]==1){
					hasBlock=true;
					//Go back to green zone to stack
					generatePath(xCords.get(i),yCords.get(i), (int)BottomLeftGreenZone.getx(), (int)BottomLeftGreenZone.gety(), xCords, yCords,true);
					for(int j=0;j<xCords.size();j++){				
						nav.travelTo(true, false,xCords.get(j),yCords.get(j));
						nav.turnTo(270, true);
						scanResults = search.Scan(hasBlock);
						if(scanResults[4]==1){
							hasBlock=true;
						}
						processData(scanResults, xCords,yCords,i,nav,BottomLeftRedZone,TopRightRedZone,false);
						
						}
						robo.stopMotors();
						stack(robo,nav);
						//Back in green zone with second block, ready to stack
				}
				processData(scanResults, xCords,yCords,i,nav,BottomLeftRedZone,TopRightRedZone,true);				
			}
			
	
		}
	

	public static void generatePath( int xStart, int yStart, int xZone,int yZone,ArrayList<Integer>xCords, ArrayList<Integer>yCords, boolean up)
	{	
		//Remove all points
		for(int i=0;i<xCords.size();i++){
			xCords.remove(i);
			yCords.remove(i);
		}
		xCords.add(0);
		yCords.add(0);
		int i=0;
		while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
			
		//generate new list of points
			
			if(i==0)
			{  
				xCords.remove(0);
				yCords.remove(0);
				xCords.add(i,xStart);
				if(up){
				yCords.add(i,yStart+30);}
				yCords.add(i,yStart-30);
			}
			else
			{
				
				if((i%2)==0)
				{	
					if(yStart>yZone)
					{
						xCords.add(xCords.get(i-1));
						yCords.add(yCords.get(i-1)-30);				
					}
						xCords.add(xCords.get(i-1));
						yCords.add(yCords.get(i-1)+30);				
						
				}
				else
				{       
					if(xStart>xZone)
					{
						xCords.add(xCords.get(i-1)-30);
						yCords.add(yCords.get(i-1));				
					}
					xCords.add(xCords.get(i-1)+30);
					yCords.add(yCords.get(i-1));
				
				}
			
				
			}
			if(up){
			if(xCords.get(i)>xZone)
			{
				yCords.remove(i);
				xCords.remove(i);
				int j=i;
				while((xCords.get(xCords.size()-1)!=xZone)||(yCords.get(yCords.size()-1)!=yZone)){
					xCords.add(xZone);
					yCords.add(yCords.get(j-1)+30);	
					j++;
				}
				break;
			}
			if(yCords.get(i)>yZone)
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
			else{
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
				if(yCords.get(i)<yZone)
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
		 i++;
		}
	}

	//still needs to add in red zone as an obstacle
public static void processData(int[]scanResults,ArrayList<Integer>xCords, ArrayList<Integer>yCords, int i, Navigation nav, Point badZoneBL, Point badZoneBR, boolean up){	
	
	//North blocked and next point is north
	if(scanResults[0]==1&&yCords.get(i+1)>yCords.get(i)){
		//West travelling zig zag
		if(xCords.get(i+2)<xCords.get(i)){
			nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
			generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords,up);	
		}
		//East travelling zig zag
		nav.travelTo(true, false, xCords.get(i)+30, yCords.get(i));
		generatePath(xCords.get(i)+30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords,up);
				
	}
	
	//East blocks and next point east
	if(scanResults[1]==1&&xCords.get(i+1)>xCords.get(i)){
		nav.travelTo(true, false, xCords.get(i),yCords.get(i)+30);
		generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords,up);
		

	}
	
	//West blocked and next point west
	if(scanResults[2]==1&&xCords.get(i+1)<xCords.get(i)){	
		nav.travelTo(true, false, xCords.get(i), yCords.get(i)+30);
		generatePath(xCords.get(i),yCords.get(i)+30,xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords, up);
	
	}
	
	//South blocked and next point is south
	if(scanResults[3]==1&&yCords.get(i+1)<yCords.get(i)){
		//West travelling zig zag
		if(xCords.get(i+2)>xCords.get(i)){
			nav.travelTo(true, false, xCords.get(i)-30, yCords.get(i));
			generatePath(xCords.get(i)-30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords,up);	
		}
		//East travelling zig zag
		nav.travelTo(true, false, xCords.get(i)+30, yCords.get(i));
		generatePath(xCords.get(i)+30,yCords.get(i),xCords.get(xCords.size()-1),yCords.get(yCords.size()-1),xCords,yCords,up);
				
	}

	
}


public static void convertMap(int[] greenZone,int[] redZone,int startingCorner){
	int x1Green=greenZone[0];
	int y1Green=greenZone[1];
	int x2Green=greenZone[2];
	int y2Green=greenZone[3];
	int x1Red=greenZone[0];
	int y1Red=greenZone[1];
	int x2Red=greenZone[2];
	int y2Red=greenZone[3];
	if(startingCorner==1){
	//Map remains the same	
	}
	if(startingCorner==2){
		greenZone[0]=y1Green;
		greenZone[1]=10-x1Green;
		greenZone[2]=y2Green;
		greenZone[3]=10-x2Green;
		redZone[0]=y1Red;
		redZone[1]=10-x1Red;
		redZone[2]=y2Red;
		redZone[3]=10-x2Red;
	}
	if(startingCorner==3){
		greenZone[0]=10-x1Green;
		greenZone[1]=10-y1Green;
		greenZone[2]=10-x2Green;
		greenZone[3]=10-y2Green;
		redZone[0]=10-x1Red;
		redZone[1]=10-y1Red;
		redZone[2]=10-x2Red;
		redZone[3]=10-y2Red;
	}
	if(startingCorner==4){
		greenZone[0]=10-y1Green;
		greenZone[1]=x1Green;
		greenZone[2]=10-y2Green;
		greenZone[3]=x2Green;
		redZone[0]=10-y1Red;
		redZone[1]=x1Red;
		redZone[2]=10-y2Red;
		redZone[3]=x2Red;
	}
}

<<<<<<< HEAD

public static void stack(TwoWheeledRobot robo, Navigation nav,Search search)
{
        int LINE_LIGHTVALUE_MAX=515;
        int LINE_LIGHTVALUE_MIN=400;
        int SLOW =75; 
        robo.setForwardSpeed((int)(SLOW*1.5));
        robo.stopMotors();    
        int leftLightValue=robo.getLeftLightValue();
        int rightLightValue=robo.getRightLightValue();      
        nav.turnTo(45, true);
         while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
         {               
                 robo.startLeftMotor();   
                 leftLightValue=robo.getLeftLightValue();
         }
            
         robo.stopLeftMotor();
         
         while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
         {               
                 robo.startRightMotor();
                 rightLightValue=robo.getRightLightValue();    
         }
        
         robo.stopRightMotor();
         robo.rotateClawAbsolute(search.CLAW_LOWER_ANGLE);
         nav.travelSetDistanceBackwards(10);
         nav.turnTo(0, true);
         nav.fineTune();
 

        
}

public static void drop(TwoWheeledRobot robo, Navigation nav, Search search)
{
        int LINE_LIGHTVALUE_MAX=515;
    int LINE_LIGHTVALUE_MIN=400;
    int SLOW =75; 
        robo.setForwardSpeed((int)(SLOW*1.5));
=======
public static void stack(TwoWheeledRobot robo, Navigation nav)
{
	int LINE_LIGHTVALUE_MAX=515;
	int LINE_LIGHTVALUE_MIN=400;
	int SLOW =75; 
	robo.setForwardSpeed((int)(SLOW*1.5));
	robo.stopMotors();    
	int leftLightValue=robo.getLeftLightValue();
	int rightLightValue=robo.getRightLightValue();      
	nav.turnTo(45, true);
	 while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
	 {               
		 robo.startLeftMotor();   
		 leftLightValue=robo.getLeftLightValue();
	 }
	    
	 robo.stopLeftMotor();
	 
	 while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
	 {               
		 robo.startRightMotor();
		 rightLightValue=robo.getRightLightValue();    
	 }
	
	 robo.stopRightMotor();
	 robo.rotateClawAbsolute(search.CLAW_LOWER_ANGLE);
	 nav.travelSetDistanceBackwards(10);
	 nav.turnTo(0, true);
	 nav.fineTune();
 

	
}

public static void drop(TwoWheeledRobot robo, Navigation nav)
{
	int LINE_LIGHTVALUE_MAX=515;
    int LINE_LIGHTVALUE_MIN=400;
    int SLOW =75; 
	robo.setForwardSpeed((int)(SLOW*1.5));
>>>>>>> 95e36f62f208cd2c885b5924c00fa7cc37f0d876
    robo.stopMotors();    
    int leftLightValue=robo.getLeftLightValue();
    int rightLightValue=robo.getRightLightValue();      
    nav.turnTo(45, true);
     while(!(leftLightValue<LINE_LIGHTVALUE_MAX && leftLightValue > LINE_LIGHTVALUE_MIN))
     {               
<<<<<<< HEAD
             robo.startLeftMotor();   
             leftLightValue=robo.getLeftLightValue();
         }
            
         robo.stopLeftMotor();
         
         while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
         {               
                 robo.startRightMotor();
                 rightLightValue=robo.getRightLightValue();    
         }
        
         robo.stopRightMotor();
         robo.rotateClawAbsolute(search.CLAW_LOWER_ANGLE);
         nav.travelSetDistanceBackwards(10);
         nav.turnTo(0, true);
         nav.fineTune();
         
         
        }

}
=======
    	 robo.startLeftMotor();   
    	 leftLightValue=robo.getLeftLightValue();
	 }
	    
	 robo.stopLeftMotor();
	 
	 while(!(rightLightValue<LINE_LIGHTVALUE_MAX && rightLightValue > LINE_LIGHTVALUE_MIN))
	 {               
		 robo.startRightMotor();
		 rightLightValue=robo.getRightLightValue();    
	 }
	
	 robo.stopRightMotor();
	 robo.rotateClawAbsolute(search.CLAW_LOWER_ANGLE);
	 nav.travelSetDistanceBackwards(10);
	 nav.turnTo(0, true);
	 nav.fineTune();
	 
	 
	}

}
>>>>>>> 95e36f62f208cd2c885b5924c00fa7cc37f0d876
