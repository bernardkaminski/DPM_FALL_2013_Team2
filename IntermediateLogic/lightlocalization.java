package IntermediateLogic;

import Hardware.TwoWheeledRobot;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class lightlocalization {

	TwoWheeledRobot robo;
	Odometer odo;
	Navigation nav;
	
	private final int LOCALIZATIONSPEED = 100;
	private final int LINE_LIGHTVALUE_MAX=515;
    private final int LINE_LIGHTVALUE_MIN=400;  
    private int leftLightValue;
    private int rightLightValue;
    
    int minChangeToIndicateLine = 12;
    int maxChangeToIndicateLine = 150;
	
	public lightlocalization(TwoWheeledRobot robo, Odometer odo, Navigation nav){
		this.robo = robo;
		this.odo = odo;
		this.nav = nav;
		robo.turnOnRightLight();              
        robo.turnOnLeftLight();
        robo.startLeftLP();
        robo.startRightLP();
	}
	
	
	public void localize(){		
		int usScannerFirstLine, usScannerSecondLine;
		
		robo.rotateClawAbsolute(10);
		
		robo.setRotationSpeed(LOCALIZATIONSPEED);
		        
		getLightValue(2);
		RConsole.println("Robot has detected both lines for the first line");
		
		usScannerFirstLine = (int)robo.scanWithTopsensor(10);
		
		getLightValue(2);
		RConsole.println("Robot has detected both lines for the second line");
		
		robo.stopMotors();	
		RConsole.println("stop");
		
		usScannerSecondLine = (int)robo.scanWithTopsensor(10);
		robo.setRotationSpeed(80);
		
		if((usScannerFirstLine<35)&&(usScannerSecondLine>40)){			
			robo.startRightMotor();
			getLightValue(1);
			odo.setPosition(new double [] {0.0,0.0,0.0}, new boolean [] {true, true, true});
			RConsole.println(usScannerFirstLine+" " + usScannerSecondLine+ "third quadrant");
		}
		
		if((usScannerFirstLine>40)&&(usScannerSecondLine>40)){			
			robo.startRightMotor();
			getLightValue(1);
			odo.setPosition(new double [] {0.0,0.0,90.0}, new boolean [] {true, true, true});
			RConsole.println(usScannerFirstLine+" " + usScannerSecondLine+ "second quadrant");
		}
		
		if((usScannerFirstLine>40)&&(usScannerSecondLine<35)){			
			robo.startRightMotorback();
			getLightValue(1);
			odo.setPosition(new double [] {0.0,0.0,180.0}, new boolean [] {true, true, true});
			RConsole.println(usScannerFirstLine+" " + usScannerSecondLine+ "first quadrant");
		}
		
		if((usScannerFirstLine<35)&&(usScannerSecondLine<35)){			
			robo.startRightMotorback();
			getLightValue(1);
			odo.setPosition(new double [] {0.0,0.0,270.0}, new boolean [] {true, true, true});
			RConsole.println(usScannerFirstLine+" " + usScannerSecondLine+ "fourth quadrant");
		}
		
		robo.setRotationSpeed(80);
		nav.turnTo(0, true);
		nav.travelSetDistanceBackwards(2);
		nav.fineTune();
	}
	
	public boolean getLightValue(int numberofmotors){
		
		int lineCounter=0;
		int changeInLightValue=0, lightValue=0;
		if(numberofmotors==2){
		robo.rotateClockwise();
		while (true) 
			{
				RConsole.println(""+robo.getLeftLightValue());
				changeInLightValue = lightValue - robo.getLeftLightValue();
				lightValue = robo.getLeftLightValue();
				if((Math.abs(changeInLightValue) > minChangeToIndicateLine)&&(Math.abs(changeInLightValue) < maxChangeToIndicateLine)&&(lightValue<520))// this senses the change in colour on the floor . therefore for each line it senses two changes entering and exiting
				{
					RConsole.println(changeInLightValue+" "+lightValue );
					lineCounter++;//increment the amount of times a change in color has been sensed 
					//in order to hear if the sensor sensed the line
						if (lineCounter==1)//exiting first line
						{
							Sound.beep();
							RConsole.println(robo.getLeftLightValue()+" "+robo.getRightLightValue() );
							return true;
						}
						
				}
				
			}
			
		}
		else if(numberofmotors==1){
			
			while (true) 
			{
				RConsole.println(""+robo.getLeftLightValue());
				changeInLightValue = lightValue - robo.getRightLightValue();
				lightValue = robo.getRightLightValue();
				if((Math.abs(changeInLightValue) > minChangeToIndicateLine)&&(Math.abs(changeInLightValue) < maxChangeToIndicateLine)&&(lightValue<570))// this senses the change in colour on the floor . therefore for each line it senses two changes entering and exiting
				{
					RConsole.println(changeInLightValue+" "+lightValue );
					lineCounter++;//increment the amount of times a change in color has been sensed 
					//in order to hear if the sensor sensed the line
						if (lineCounter==1)//exiting first line
						{
							Sound.beep();
							RConsole.println(robo.getLeftLightValue()+" "+robo.getRightLightValue() );
							robo.stopMotors();
							return true;
						}
						
				}
				
			}
			
		}
		
		return false;
	}
	
}

