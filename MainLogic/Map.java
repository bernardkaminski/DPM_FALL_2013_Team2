package MainLogic;
import java.util.ArrayList;

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
	public ArrayList<Integer> mem_x=new ArrayList<Integer>();
    public ArrayList<Integer> mem_y=new ArrayList<Integer>();
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
	public boolean isInDeadZone(int x, int y)
	{
		if (x>=deadZone[0].getx()&&x<=deadZone[1].getx()&&y>=deadZone[0].gety() && y<=deadZone[1].gety())
		{
			return true;
		}
		else
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

	public static void convertMap(int[] greenZone,int[] redZone,int startingCorner){
		int x1Green=greenZone[0];
		int y1Green=greenZone[1];
		int x2Green=greenZone[2];
		int y2Green=greenZone[3];
		int x1Red=greenZone[0];
		int y1Red=greenZone[1];
		int x2Red=greenZone[2];
		int y2Red=greenZone[3];
		if(startingCorner==1){
		//Map remains the same	
		}
		if(startingCorner==2){
			greenZone[0]=y1Green;
			greenZone[1]=10-x1Green;
			greenZone[2]=y2Green;
			greenZone[3]=10-x2Green;
			redZone[0]=y1Red;
			redZone[1]=10-x1Red;
			redZone[2]=y2Red;
			redZone[3]=10-x2Red;
		}
		if(startingCorner==3){
			greenZone[0]=10-x1Green;
			greenZone[1]=10-y1Green;
			greenZone[2]=10-x2Green;
			greenZone[3]=10-y2Green;
			redZone[0]=10-x1Red;
			redZone[1]=10-y1Red;
			redZone[2]=10-x2Red;
			redZone[3]=10-y2Red;
		}
		if(startingCorner==4){
			greenZone[0]=10-y1Green;
			greenZone[1]=x1Green;
			greenZone[2]=10-y2Green;
			greenZone[3]=x2Green;
			redZone[0]=10-y1Red;
			redZone[1]=x1Red;
			redZone[2]=10-y2Red;
			redZone[3]=x2Red;
		}
	}

	 public void clearMemPath()
     {
     	mem_x.clear();
     	mem_y.clear();
     }
     
     /**
      * 
      * @param x The x value to be added to the path memory
      * @param y The y value to be added to the path memory
      */
     public void MemAddCoordinates(int x,int y)
     {
     	mem_x.add(x);
     	mem_y.add(y);
     }
     
     /**
      * 
      * @return The list of x coordinates stored in memory
      */
     public ArrayList<Integer> getXMemory()
     {
     	return mem_x;
     }
     
     /**
      * 
      * @return The list of y coordinates stored in memory
      */
     public ArrayList<Integer> getYMemory()
     {
     	return mem_y;
     }
}
