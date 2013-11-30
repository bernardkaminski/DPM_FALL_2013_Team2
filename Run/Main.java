package Run;
import Hardware.LightPoller;
import Hardware.TwoWheeledRobot;
import Hardware.UltrasonicPoller;
import Hardware.UltrasonicScanner;
import IntermediateLogic.Navigation;
import IntermediateLogic.Odometer;
import IntermediateLogic.lightlocalization;
import MainLogic.Search;
import Tests.NavigationTest;
import Tests.FinalCompetition;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.rcxcomm.PacketHandler;
import bluetooth.*;
import MainLogic.*;

public class Main {

	
	public static void main(String[] args) 
	{
		
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
        
        
        //Set up bluetooth
        BluetoothConnection conn = new BluetoothConnection();
        Transmission t = conn.getTransmission();
        //RConsole.openAny(20000);
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
                
                Map map=new Map(12*30,12*30);        
                
                Point BottomLeftRedZone= new Point(redZone[0]*30,redZone[1]*30);
                Point TopRightRedZone=new Point(redZone[2]*30,redZone[3]*30);
                
                map.setDeadZone(BottomLeftRedZone,TopRightRedZone);
                
                Point BottomLeftGreenZone= new Point(greenZone[0]*30,greenZone[1]*30);
                Point TopRightGreenZone=new Point(greenZone[2]*30,greenZone[3]*30);
                
                PlayerRole role = t.role;
                
                map.setDropZone(BottomLeftGreenZone, TopRightGreenZone);
                
                if(role.getId()==1)
                {
                	Builder.main(odo, robo, nav, search, loc, map, startingCornerID );
                }
                else
                {
                	
                	Collector.main(odo, robo, nav, search, loc, map, startingCornerID);
                }
        
       }
	}

}
