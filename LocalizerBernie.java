import javax.microedition.sensor.UltrasonicSensorInfo;

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
	
	
	private final int LATCH_DISTANCE=50;
	
	//contructor
	public LocalizerBernie(TwoWheeledRobot robo,Odometer odo)
	{
		this.robo = robo;
		this.odo = odo;
	}
	
	public void localizeTheta()
	{
		int k=0;
		robo.startUsBottom();
		int distance1=robo.getBottomUsPollerDistance();
		while (k <500)
		{
			distance1=robo.getBottomUsPollerDistance();
			k++;
		}
		double firstAngle,secondAngle,wideAngle;
		if(distance1>LATCH_DISTANCE)
		{
			while(distance1>LATCH_DISTANCE)
			{
				RConsole.println("D:"+distance1);
				robo.rotateClockwise();
				distance1=robo.getBottomUsPollerDistance();
			}
			robo.stopMotors();
			Sound.beep();
			firstAngle=odo.getTheta();
			
			while (distance1<LATCH_DISTANCE)
			{
				RConsole.println("D:"+distance1);
				robo.rotateClockwise();
				distance1=robo.getBottomUsPollerDistance();
			}
			secondAngle=odo.getTheta();
			wideAngle= secondAngle-firstAngle;
			robo.stopMotors();
			Sound.beep();
		}
		else
		{
			while(distance1<LATCH_DISTANCE)
			{
				RConsole.println("D:"+distance1);
				robo.rotateCounterClockwise();
				distance1=robo.getBottomUsPollerDistance();
			}
			firstAngle=360-odo.getTheta();
			robo.stopMotors();
			Sound.beep();
			//Button.waitForAnyPress();
			while(distance1 >LATCH_DISTANCE)
			{
				RConsole.println(Integer.toString(distance1));
				robo.rotateClockwise();
				distance1=robo.getBottomUsPollerDistance();
			}
			Sound.beep();
			robo.stopMotors();
			
			while(distance1 <LATCH_DISTANCE)
			{
				RConsole.println(Integer.toString(distance1));
				robo.rotateClockwise();
				distance1=robo.getBottomUsPollerDistance();
			}
			
			secondAngle=odo.getTheta();
			wideAngle= secondAngle+firstAngle;
			robo.stopMotors();
			Sound.beep();
			//Button.waitForAnyPress();
		}
		robo.stopMotors();
		robo.stopBottomUsPoller();
		double currentAngle = 225 +(wideAngle/2);
		odo.setTheta(currentAngle);
		
	}
	
	
}
