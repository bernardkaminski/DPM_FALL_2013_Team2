package MainLogic;

/**
 * This class keeps track of the board for the robot
 * @author Bernie
 *
 */
public class Map {
	
	private int[] x;
	private int[] y;
	private Point[] deadZone = new Point[2];
	private Point[] dropZone = new Point[2];
	private Point[] towers;
	private Point[] walls= new Point[3];
	
	/**
	 * 
	 * @param length length of the board
	 * @param width width of the  board 
	 */
	public Map(int height,int width)
	{
		x = new int[width+1];
		y = new int[height+1];
		
		int firstx = -30;
		int firsty = -30;
		for (int i=0; i<x.length;i++)
		{
			x[i]=firstx;
			firstx++;
		}
		
		firstx = -30;
		
		for (int i=0; i<y.length;i++)
		{
			y[i]=firsty;
			firsty++;
		}
		
	}	
	
	public boolean isCloseToWall(int x,int y, int bandWidth)
	{
		if(inBandwidth(x,this.x[0],bandWidth) || inBandwidth(x,this.x[this.x.length-1],bandWidth)||x==this.x[0] || inBandwidth(x,this.x[this.x.length-1],bandWidth))
			return true;
		else
			return false;
	}
	
	private boolean inBandwidth(int Given,int ToCompare, int bandWidth)
	{
		if(Math.abs(Given-ToCompare)>=bandWidth)
			return true;
		else
			return false;
	}
	
	
	public void setDropZone(Point bottomLeft,Point topRight)
	{
		dropZone[0]=bottomLeft;
		dropZone[1]=topRight;
	}
	
	public void setDeadZone(Point bottomLeft,Point topRight)
	{
		deadZone[0]=bottomLeft;
		deadZone[1]=topRight;
	}
	/**
	 * 
	 * @param point The point to be tested
	 * @return weather the point being tested is in stored drop zone
	 */
	public boolean isInDropZone(int x, int y)
	{
		return false;
	}
	
	public Point getDropZoneCenter()
	{
		Point p = new Point((dropZone[1].getx()-dropZone[0].getx())/2,(dropZone[1].gety()-dropZone[0].gety())/2 );
		return p;
	}
	
	public Point getDeadZoneCenter()
	{
		Point p = new Point((deadZone[1].getx()-deadZone[0].getx())/2,(deadZone[1].gety()-deadZone[0].gety())/2 );
		return p;
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
