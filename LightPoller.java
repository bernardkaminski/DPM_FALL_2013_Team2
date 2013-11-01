import lejos.*;
import lejos.nxt.ColorSensor;
import lejos.robotics.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LightPoller implements TimerListener
{
        //all class variables
        //note that they are private as no other class needs to know about these variables, just use them.
        private int lightValue;
        private ColorSensor cs;
        private Timer clock;
        private Object lock;
        
        //all constants go here, they are denoted with the final keyword convention is to use all caps with underscores
        private final int PERIOD = 20;//period of filter. timeout out will be called every this many milliseconds
        private final double FILTER_CONSTANT=0;

        //constructor
        public LightPoller(ColorSensor cs)
        {
                this.cs=cs;
                clock = new Timer(PERIOD, this);
                
        }
        
        //starting method
        public void startLightPoller()
        {
                cs.setFloodlight(true);
                cs.setFloodlight(Color.RED);
                clock.start();
        }
        
        //stoping method
        public void stopLightPoller()
        {
                cs.setFloodlight(false);
                clock.stop();
        }
        
        
        // the getter of this poller this is how other classes will get the light value from the sensor
        public int returnLightValue()
        {
                synchronized (lock) 
                {
                        //passed by value so lightvalue can be private 
                        return lightValue;        
                }
                
        }
        
        // what is done repeatedly
        @Override
        public void timedOut() 
        
        {
                // some type of filter needs to be added     
        		
                synchronized (lock) 
                {
                        lightValue = cs.getLightValue();
                }
        }
}