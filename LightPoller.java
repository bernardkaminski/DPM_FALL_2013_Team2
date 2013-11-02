import lejos.*;
import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This is a class that implements the TimerListener interface so that the light sensor will continuously update values
 * using the ColorSensor class from the LeJos library.
 * @author Bernie
 * @version 1.0
 * @see TimerListener LeJos API
 * 
 *
 */

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

        /**
         * 
         * @param cs the color Sensor that will be used to measure light values.
         * 
         */
        public LightPoller(ColorSensor cs)
        {
                this.cs=cs;
                clock = new Timer(PERIOD, this);
                lock = new Object();
                
        }
        
        /**
         * Starts the thread and as a result starts taking light values.
         */
        public void startLightPoller()
        {
                cs.setFloodlight(true);
                cs.setFloodlight(Color.RED);       
                clock.start();
        }
        
        /**
         * Pauses The Thread the thread and as a result light values stop getting updated. 
         */
        public void stopLightPoller()
        {
                cs.setFloodlight(false);
                clock.stop();
        }
        
        
        // the getter of this poller this is how other classes will get the light value from the sensor
        /**
         * 
         * @return the current light value 
         */
        public int returnLightValue()
        {
                synchronized (lock) 
                {
                        //passed by value so lightvalue can be private
                        return lightValue;        
                }
                
        }
        
        // what is done repeatedly
        //@Override
        /**
         * method that repeatedly updates the light value 
         */
        public void timedOut() 
        
        {
                // some type of filter needs to be added     
        		//RConsole.println("polling");
                synchronized (lock) 
                {
                        lightValue = cs.getLightValue();
                }
        }
}