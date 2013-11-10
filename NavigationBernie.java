import lejos.*;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
public class NavigationBernie {
	
	private Odometer odo;
	private TwoWheeledRobot robo;
	private BlockDifferentiator blockDetector;
	
	private final double DISTANCE_THRESHOLD=1;
	private final double ANGLE_THRESHOLD=.5;
	private final int LIGHT_DIFFERENCE_CONSTANT=35;// the light constant is a constant for difference the line constant is an absolute filter based on data taken 
	private final int LEFT_LINE_VALUE_CONSTANT=535;
	private final int RIGHT_LINE_VALUE_CONSTANT=586;
	private double distance=0;
	public  boolean obstacleClose = false;
	private boolean rwCrossedLine = false;
	private boolean lwCrossedLine = false;
	private double  lLight=0;
	private double rLight=0;
	
	/*
	 * class that controls all the navigation of the robot
	*/
	
	//contructor
	public NavigationBernie(Odometer odo, TwoWheeledRobot robot, BlockDifferentiator block)
	{
		this.odo=odo;
		this.robo= robot;
		this.blockDetector = block;
		robo.setForwardSpeed(200);
		robo.setRotationSpeed(100);
	}
	
	//set that their is an obsticle in the navigation route 
	public void setObsticleClose(boolean answer)
	{
		obstacleClose = answer;
	}
	
	// travel to a set point stops when the distance from the set point is close enough
	public void travelTo(double x , double y)
	{
		
		turnBy(calOpitimalTurnAngle(calDestAngle(x, y)));
		
		while(keepGoing(odo.getX(), odo.getY(), x, y))
		{
			
			if(rwCrossedLine)
			{}
			if(lwCrossedLine)
			{}
			
			if(!obstacleClose)
			{
				robo.goForward();
				
			}
			else
			{
				robo.stopMotors();
				break;
			}
		}
		robo.stopMotors();
	}

	private void updateLineValues()
	{
		double lOld=lLight;
		double rOld=rLight;
		
		lLight=robo.getLeftLightValue();
		rLight=robo.getLeftLightValue();
		
		if(lOld-lLight>(LIGHT_DIFFERENCE_CONSTANT)&& lLight<LEFT_LINE_VALUE_CONSTANT)
			lwCrossedLine=true;
		else if(lOld-lLight<(LIGHT_DIFFERENCE_CONSTANT)&& lLight>LEFT_LINE_VALUE_CONSTANT)
			lwCrossedLine=false;
		
		
		if(rOld-rLight>(LIGHT_DIFFERENCE_CONSTANT)&& rLight<RIGHT_LINE_VALUE_CONSTANT)
			rwCrossedLine=true;
		else if(rOld-rLight<(LIGHT_DIFFERENCE_CONSTANT)&& rLight>RIGHT_LINE_VALUE_CONSTANT)
			rwCrossedLine=false;
		
	}
	
	//use pythogorem to know if the robot has travel the set distance
	public void travelSetDistanceStraight(double d)
	{
		double startingX = odo.getX();
		double startingY = odo.getY();
		
		while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
		{
			robo.goForward();
		}
		robo.stopMotors();
	}
	
	//same thing but backwards
	public void travelSetDistanceBack(double d)
	{
		double startingX = odo.getX();
		double startingY = odo.getY();
		
		while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
		{
			robo.goBackward();
		}
		robo.stopMotors();
	}
	
	//the logic that evaluates weather the robot is close enough to the point 
	public boolean keepGoing(double xCurrent, double yCurrent, double xDest, double yDest)
	{
		double distanceFromPoint =Math.sqrt(((xDest-xCurrent)*(xDest-xCurrent))+((yDest-yCurrent)*(yDest-yCurrent)));
		
		if(distanceFromPoint<DISTANCE_THRESHOLD)// || (this.distance!=0 && this.distance<distanceFromPoint))//distance should keep getting smaller
		{
			RConsole.println("keep going false");
			this.distance=distanceFromPoint;
			return false;
		}
		else
		{
			this.distance=distanceFromPoint;
			return true;
		}
	}
	
	
	
	//turn by a given angle
	public void turnBy(double TurnAngle)
	{
		double startingAngle = odo.getTheta();
		double endAngle = odo.getTheta()+TurnAngle;
		if(endAngle<0)
		{
			endAngle=360+endAngle;
		}
		endAngle= Math.round(endAngle);//added this and made angle threshold to 0 
		double diff=calculateOpitimalDiff(Math.abs(endAngle-startingAngle));
		robo.setRotationSpeed(150);
		if(TurnAngle>0)
		{
			while(Math.abs(diff)>ANGLE_THRESHOLD)
			{
				if(Math.abs(diff)<ANGLE_THRESHOLD+5)
				{
					robo.setRotationSpeed(25);
				}
				robo.rotateClockwise();
				diff=calculateOpitimalDiff(Math.abs(endAngle-odo.getTheta()));
			}
		}
		else
		{
			while(Math.abs(diff)>ANGLE_THRESHOLD)
			{
				if(Math.abs(diff)<ANGLE_THRESHOLD+5)
				{
					robo.setRotationSpeed(25);
				}
				robo.rotateCounterClockwise();
				diff=calculateOpitimalDiff(Math.abs(endAngle-odo.getTheta()));
			}
		}
			robo.stopMotors();
	}
	
	//logic used because there are no negative angles defined by the odometer 
	private double calculateOpitimalDiff(double diff)
	{
		if (diff>180)
		{
			diff = 360-diff;
		}
		return diff;
	}
	
	//calculate the destination angle of the point 
	public double calDestAngle(double xDest, double yDest)
	{
		double dAngle = 0 ;//angle to get to
		double x = odo.getX();
		double y = odo.getY();
		double changeY = yDest-y;
		double changeX = xDest-x;
		if(changeX==0)
		{
			if(changeY>0)
			{
				dAngle=0;
			}
			else
			{
				dAngle=Math.PI;
			}
		}
		else
		{
			if(changeY==0)
			{
				if(changeX>0)
				{
					dAngle=(Math.PI/2);
				}
				else
				{
					dAngle=(3*Math.PI/2);
				}
			}
			else
			{
				dAngle = Math.atan(( changeY/changeX));//math.atan returns radians
				if((changeX>0 && changeY>0)||(changeX>0 && changeY<0))
				{
					dAngle = (Math.PI/2) - dAngle;
				}
				if((changeX<0 && changeY<0)||(changeX<0 && changeY>0))
				{
					dAngle =(3*Math.PI/2)-dAngle;
				}
			}
		}
		
		return Math.toDegrees(dAngle);
	}
	
	//rotate to a certain angle relatuve to origin
	public void rotateTo(double angle)
	{
		turnBy(calOpitimalTurnAngle(angle));
	}
	
	//calculate the shortest turn angle 
	public double calOpitimalTurnAngle(double angle)
	{
		double op = angle-odo.getTheta();
		if(op <-180)
		{
			op += 360;
		}
		if (op > 180)
		{
			op -=360;
		}
		
		return op;
		
	}

	public double calGoalTurnAngle(double x, double y )
	{
		return(calOpitimalTurnAngle(calDestAngle(x, y)));
	}
}
