package IntermediateLogic;
import Hardware.TwoWheeledRobot;
import lejos.nxt.LCD;
   
import lejos.util.Timer; 
import lejos.util.TimerListener; 
   /**
    * this class is a thread that keeps track of the robots position on the field using the rotation of the wheels. 
    * @author Bernie
    *
    */
public class Odometer implements TimerListener { 
    public static final int DEFAULT_PERIOD = 15; 
    private TwoWheeledRobot robot; 
    private Timer odometerTimer; 
    // position data 
    private Object lock; 
    private double x, y, theta; 
    private double [] oldDH, dDH; 
     
    /**
     * 
     * @param robot the TwoWheeledRobot that gives the odometer access to the motor counts.
     * @param period the refresh rate
     * @param start whether to start the thread or not 
     */
    public Odometer(TwoWheeledRobot robot, int period, boolean start) { 
        // initialise variables 
        this.robot = robot; 
        odometerTimer = new Timer(period, this); 
        x = 0.0; 
        y = 0.0; 
        theta = 0.0; 
        oldDH = new double [2]; 
        dDH = new double [2]; 
        lock = new Object(); 
           
        // start the odometer immediately, if necessary 
        if (start) 
            odometerTimer.start(); 
    } 
    
    public Odometer(TwoWheeledRobot robot) { 
        this(robot, DEFAULT_PERIOD, false); 
    } 
       
    public Odometer(TwoWheeledRobot robot, boolean start) { 
        this(robot, DEFAULT_PERIOD, start); 
    } 
       
    public Odometer(TwoWheeledRobot robot, int period) { 
        this(robot, period, false); 
    } 
    
    /**
     * update the position and heading
     */
    public void timedOut() { 
        robot.getDisplacementAndHeading(dDH); 
        dDH[0] -= oldDH[0]; 
        dDH[1] -= oldDH[1]; 
           
        // update the position in a critical region 
        synchronized (lock) { 
            theta += dDH[1]; 
            theta = fixDegAngle(theta); 
               
            x += dDH[0] * Math.sin(Math.toRadians(theta)); 
            y += dDH[0] * Math.cos(Math.toRadians(theta)); 
            
        } 
           
        oldDH[0] += dDH[0]; 
        oldDH[1] += dDH[1]; 
    } 
       
    // accessors 
    public void getPosition(double [] pos) { 
        synchronized (lock) { 
            pos[0] = x; 
            pos[1] = y; 
            pos[2] = theta; 
        } 
    } 
       
   
    // mutators 
    public void setPosition(double [] pos, boolean [] update) { 
        synchronized (lock) { 
            if (update[0]) x = pos[0]; 
            if (update[1]) y = pos[1]; 
            if (update[2]) theta = pos[2]; 
        } 
    } 
       
    // static 'helper' methods 
    public static double fixDegAngle(double angle) {         
        if (angle < 0.0) 
            angle = 360.0 + (angle % 360.0); 
           
        return angle % 360.0; 
    } 
       
    public static double minimumAngleFromTo(double a, double b) { 
        double d = fixDegAngle(b - a); 
           
        if (d < 180.0) 
            return d; 
        else
            return d - 360.0; 
    } 
       
    // return X value 
    public double getX() { 
        synchronized (lock) { 
            return x; 
        } 
    } 
   
    // return Y value 
    public double getY() { 
        synchronized (lock) { 
            return y; 
        } 
    } 
   
    // return theta value 
    public double getTheta() { 
        synchronized (lock) { 
            return theta; 
        }
    
    }
    public void setTheta(double theta)
	{
		synchronized (lock) {
			this.theta=theta;
		}
		
	}
	public void setX(double x)
	{
		synchronized (lock) {
			this.x=x;
		}
		
	}
	public void setY(double y)
	{
		synchronized (lock) {
			this.y=y;
		}
		
	}
}
