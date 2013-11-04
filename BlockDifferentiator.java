/**
 * This class is what is used to differentiate between an obstacle and a building block. 
 * @author Bernie
 * @version 1.0
 * @see TwoWheeledRobot
 */
public class BlockDifferentiator {

	/**
	 * 
	 * @param robo the robot that has the top and bottom Ultrasobic sensors
	 * @return 0 if a obstacle and 1 if a building block
	 */
	public int identifyBlock(TwoWheeledRobot robo)
	{
		return 1;
	}
}
