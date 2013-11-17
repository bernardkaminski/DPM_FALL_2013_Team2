package IntermediateLogic;

import javax.microedition.sensor.UltrasonicSensorInfo;

import Hardware.TwoWheeledRobot;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

/*
 * localization class that implements
 * rising edge localization 


*/
public class LocalizerBernie {
	TwoWheeledRobot robo;
	Odometer odo;
	
	
	private final int LATCH_DISTANCE=40;
	
	//contructor
	public LocalizerBernie(TwoWheeledRobot robo,Odometer odo)
	{
		this.robo = robo;
		this.odo = odo;
	}
	
	public void localizeTheta()
	{
		int k=0;
		//robo.startUsBottom();
		double distance1;
		while (k <10)
		{
			distance1=robo.scanWithBottomsensor(2);
			k++;
		}
		
		distance1=robo.scanWithBottomsensor(2);
		double firstAngle,secondAngle,wideAngle;
		if(distance1>LATCH_DISTANCE)
		{
			while(distance1>LATCH_DISTANCE)
			{
				
				robo.rotateClockwise();
				distance1=robo.scanWithBottomsensor(2);
				RConsole.println("D:"+distance1);
			}
			//robo.stopMotors();
			Sound.beep();
			firstAngle=odo.getTheta();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			while (distance1<LATCH_DISTANCE&&distance1!=(255))
			{
				
				robo.rotateClockwise();
				distance1=robo.scanWithBottomsensor(2);
				RConsole.println("D:"+distance1);
			}
			secondAngle=odo.getTheta();
			wideAngle= secondAngle-firstAngle;
			robo.stopMotors();
			Sound.beep();
		}
		else
		{
			while(distance1<LATCH_DISTANCE&&distance1!=(255))
			{
				RConsole.println("D:"+distance1);
				robo.rotateCounterClockwise();
				distance1=robo.scanWithBottomsensor(2);
			}
			firstAngle=360-odo.getTheta();
			robo.rotateClockwise();
			Sound.beep();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Button.waitForAnyPress();
			while(distance1 >LATCH_DISTANCE)
			{
				RConsole.println(""+distance1);
				robo.rotateClockwise();
				distance1=robo.scanWithBottomsensor(2);
			}
			Sound.beep();
			robo.stopMotors();
			
			while(distance1 <LATCH_DISTANCE&&distance1!=(255))
			{
				RConsole.println(""+distance1);
				robo.rotateClockwise();
				distance1=robo.scanWithBottomsensor(2);
			}
			
			secondAngle=odo.getTheta();
			wideAngle= secondAngle+firstAngle;
			robo.stopMotors();
			Sound.beep();
			//Button.waitForAnyPress();
		}
		robo.stopMotors();
		//robo.stopBottomUsPoller();
		double currentAngle = 225 +(wideAngle/2);
		odo.setTheta(currentAngle);
		
	}
	
	
}
