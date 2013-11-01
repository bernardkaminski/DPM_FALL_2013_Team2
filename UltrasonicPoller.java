import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class UltrasonicPoller implements TimerListener{
	private UltrasonicSensor us;
	private Timer clock;
    private Object lock;
    private int PERIOD=100;
    private int distance=255;
	
	public UltrasonicPoller(UltrasonicSensor us)
	{
		this.us=us;
		this.clock = new Timer(PERIOD, this);
	}
	public void startUsPoller()
	{
		clock.start();
	}
	public void stopUsPoller()
	{
		clock.stop();
	}
	public int returnDistance()
    {
            synchronized (lock) 
            {
                    //passed by value so lightvalue can be private 
                    return distance;        
            }
            
    }
	public void timedOut() 
    {
            // some type of filter needs to be added     
    		
            synchronized (lock) 
            {
                  distance = us.getDistance();  
            }
    }	
}
