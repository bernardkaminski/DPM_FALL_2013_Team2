package MainLogic;
import lejos.*;
import lejos.util.Timer;
import lejos.util.TimerListener;
import Hardware.TwoWheeledRobot;
import IntermediateLogic.*;
public class Clock implements TimerListener
{
	private Timer clock;
	private int time;
	Boolean timeUp;
	
	public Clock(int time, Boolean timeUp)
	{
		this.time = time;
		this.timeUp = timeUp;
		this.clock = new Timer(this.time, this);
		this.clock.start();
	}
	public void timedOut() 
	{
		Boolean t = new Boolean(true);
		timeUp=t;
	
	}
}
