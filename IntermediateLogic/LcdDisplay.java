package IntermediateLogic;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;
//not our code
/**
 * This class displays the Odometer readings on the robots lcd screen
 * @author Bernie
 * @version 1.0
 * @see Odometer 
 */
public class LcdDisplay implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	
	// arrays for displaying data
	private double [] pos;
	
	/**
	 * 
	 * @param odo the odometer that the lcd screen will display on the robpt
	 */
	public LcdDisplay(Odometer odo) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	/**
	 * Repeatedly updates lcd screen 
	 */
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
	}
}
