import lejos.nxt.*;

public class Project {
	
	public static void main(String[]args){
		
		
		//Create all sensors 
		UltrasonicSensor usTop=new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usBottom=new UltrasonicSensor(SensorPort.S2);
		ColorSensor csLeft= new ColorSensor(SensorPort.S3);
		ColorSensor csRight=new ColorSensor(SensorPort.S4);
		
		//Motors
		//Port A= Left Motor, Port B= Right Motor, Port C= Claw Motor
		
		//Create pollers and scanners
		UltrasonicScanner USSTop= new UltrasonicScanner(usTop);
		UltrasonicScanner USSBottom= new UltrasonicScanner(usBottom); 
		UltrasonicPoller USPTop=new UltrasonicPoller(usTop);
		UltrasonicPoller USPBottom=new UltrasonicPoller(usBottom);
		LightPoller leftWheelLP= new LightPoller(csLeft);
		LightPoller rightWheelLP=new LightPoller(csRight);
		
		//Create Robot
		TwoWheeledRobot robo=new TwoWheeledRobot(Motor.A,Motor.B,Motor.C,USSTop,USSBottom,USPTop,USPBottom,leftWheelLP,rightWheelLP);
			
		
		
	}
}
