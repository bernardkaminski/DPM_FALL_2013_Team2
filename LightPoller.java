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
        private int newLightValue;
        private int oldLightValue;
        private int newLightDifference;
        private int oldLightDifference;
        private boolean clearToDifference;
        private final int LIGHT_DIFFERENCE_CONSTANT=30;
        private ColorSensor cs;
        private Timer clock;
        private Object lock;
        
        //all constants go here, they are denoted with the final keyword convention is to use all caps with underscores
<<<<<<< HEAD
        private final int PERIOD = 15;//period of filter. timeout out will be called every this many milliseconds
        
=======
        private final int PERIOD = 5;//period of filter. timeout out will be called every this many milliseconds
        private final double FILTER_CONSTANT=0;
>>>>>>> ff9ec29784288ec9b2c364d8ca9a638f92f577a5

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
                newLightValue=1000;
                newLightDifference=2000;
                
        }
        
        /**
         * Starts the thread and as a result starts taking light values.
         */
        public void startLightPoller()
        {
<<<<<<< HEAD
                
                cs.setFloodlight(true);       
                
=======
                cs.setFloodlight(true);
                cs.setFloodlight(Color.RED);       
>>>>>>> ff9ec29784288ec9b2c364d8ca9a638f92f577a5
                
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
                        return newLightValue;        
                }
                
        }
        public int deltaLightValue(){
        		synchronized (lock) 
        		{	
        			return (newLightValue-oldLightValue);
        		}
        }
        public boolean line(){
        	synchronized (lock) 
    		{	
    			if(oldLightDifference<0 && newLightDifference>0 && (newLightDifference-oldLightDifference)>LIGHT_DIFFERENCE_CONSTANT){
    				
    				return true;
    			}
    			else{return false;}
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
                	
                	if(newLightValue!=1000){ 
                    oldLightValue=newLightValue;   //if not on the first iteration set the old light value to the one from the previous iteration               
                	clearToDifference=true;
                	}
                	newLightValue = cs.getRawLightValue(); //update the new light value
                	
                	if(clearToDifference){
                		if(newLightDifference!=2000){ 
                		oldLightDifference=newLightDifference;                		
                			}               	
                		newLightDifference=(newLightValue-oldLightValue);
                	}
                }
        }
}