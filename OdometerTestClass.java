import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;


public class OdometerTestClass 
{
	static void main()
	{
		
		NXTRegulatedMotor leftMotor = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightMotor = new NXTRegulatedMotor(MotorPort.B);
		TwoWheeledRobot robo = new TwoWheeledRobot(leftMotor, rightMotor, null, null, null, null, null, null, null);
		Odometer odo = new Odometer(robo,true);
		LcdDisplay lcd = new LcdDisplay(odo);
		
		int buttonPress ;
		LCD.drawString("left = turn", 0, 0);
		LCD.drawString("right = straight", 0, 1);
		LCD.drawString("bottom = exit", 0, 2);
		buttonPress = Button.waitForAnyPress();
		LCD.clear();
		
		while(buttonPress!=Button.ID_ESCAPE)
		{
			//forward test
			if(buttonPress==Button.ID_RIGHT)
			{
				double distance = 200;
				double startingX = odo.getX();
				double startingY = odo.getY();
				robo.setForwardSpeed(200);
				
				while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(distance, 2))
				{
					robo.goForward();
				}
				robo.stopMotors();
			}
			
			//turn test
			else if(buttonPress==Button.ID_LEFT)
			{
				double ANGLE_THRESHOLD=.5;
				double endHeading=270;
				robo.setRotationSpeed(150);
				
				while(Math.abs(odo.getTheta()-endHeading)>ANGLE_THRESHOLD)
				{
					if(Math.abs(odo.getTheta()-endHeading)<5)
						robo.setRotationSpeed(50);
					robo.rotateCounterClockwise();
					
				}
				robo.stopMotors();
				
			}
			Button.waitForAnyPress();
			LCD.drawString("left = turn", 0, 0);
			LCD.drawString("right = straight", 0, 1);
			LCD.drawString("bottom = exit", 0, 2);
			buttonPress = Button.waitForAnyPress();
			LCD.clear();
		}
		
	}

	
	
	
}
