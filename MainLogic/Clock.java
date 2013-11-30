package MainLogic;
import lejos.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class Clock implements TimerListener
{
	private Timer clock;
	private int time;
	public Clock(int time)
	{
		this.time = time;
		this.clock = new Timer(this.time, this);
		this.clock.start();
	}
	public void timedOut() 
	{
		System.exit(0);
	}
	

}
