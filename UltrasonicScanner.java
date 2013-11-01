import lejos.nxt.UltrasonicSensor;

/*
this class is a scanning class for the ultrasonic sensor
it is not a thread it is meant to be used while standing still to get accurate readings. 
when the getfiltereddistance ethod is called the scanner is used to get an amount of values indicated
by what argument is passed to the method and then an average is taken disregarding values that are 255 unless all the values
measured are 255.
*/
public class UltrasonicScanner {
	private UltrasonicSensor us;
    
	
	public UltrasonicScanner(UltrasonicSensor us)
	{
		this.us=us;
		
	}

	public double getFilteredDistance(int amountOfData)
	{
		double distance=0;
		int scan;
		int i=0;
		int invalidCounter=0;
		while (i<amountOfData)
		{
			if(invalidCounter==amountOfData)//if the sensor reads 255 the set amount times then theres nothing 
			{
				return 255;
			}
			
			scan =us.getDistance();
			if(scan>=255)
			{
				i--;
				invalidCounter++;
				
			}
			else
			{
			 distance+=scan; 
			}
					 i++;
		}
		
		return (distance/i); 
	}

}
