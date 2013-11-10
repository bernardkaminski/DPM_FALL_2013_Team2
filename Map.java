/**
 * This class keeps track of the board for the robot
 * @author Bernie
 *
 */
public class Map {
	
	private double[] x;
	private double[] y;
	private Point[] deadZone = new Point[4];
	private Point[] dropZone = new Point[4];
	private Point[] towers;
	private double walls[];
	
	/**
	 * 
	 * @param length length of the board
	 * @param width width of the  board 
	 */
	public Map(double length,double width)
	{
		
	}
	
	/**
	 * 
	 * @param point The point to be tested
	 * @return weather the point being tested is in stored drop zone
	 */
	public boolean isInDropZone(Point point)
	{
		return false;
	}
	
	/**
	 * 
	 * @param point The point to be tested
	 * @return weather the point being tested is in stored dead zone
	 */
	public boolean isInDeadZone(Point point)
	{
		return false;
	}

}
