import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;
/**
 * * This is a class that implements the TimerListener interface so that the ulstrasonic sensor will continuously update distance values(cm)
 * using the UltrasonicSensor class the LeJos library.
 * @author Bernie
 * @version 1.0
 * @see TimerListener LeJos API
 *  
 */
public class UltrasonicPoller implements TimerListener{
	private UltrasonicSensor us;
	private Timer clock;
    private Object lock;
    private int PERIOD=100;
    private int distance=255;
	
    /**
     * 
     * @param us Ultrasonic sensor that the class will use to update distance values.
     */
	public UltrasonicPoller(UltrasonicSensor us)
	{
		this.us=us;
		this.clock = new Timer(PERIOD, this);
		lock = new Object();
	}
	/**
	 * starts Thread, start updating distance
	 */
	public void startUsPoller()
	{
		clock.start();
	}
	/**
	 * Pause Thread, stop updating distance
	 */
	public void stopUsPoller()
	{
		clock.stop();
	}
	/**
	 * Use to get the current 
	 * @return he current distance in cm (up to 255)
	 */
	public int returnDistance()
    {
            synchronized (lock) 
            {
                    //passed by value so lightvalue can be private 
                    return distance;        
            }
            
    }
	
	/**
     * method that repeatedly updates the distance 
     */
	public void timedOut() 
    {
            // some type of filter needs to be added     
    		
            synchronized (lock) 
            {
                  distance = us.getDistance();  
            }
    }	
}
