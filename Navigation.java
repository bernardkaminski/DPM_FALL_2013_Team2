
public class Navigation {
	//Properties of navigation
	private TwoWheeledRobot robo; 
	private Odometer odo;
	private BlockDifferentiator bd;
	
	//Variables
	private boolean hasBlock=false;
	
	//Constants
	private final double TURNTO_THRESHOLD=1.0;
	
	//Constructor
	public Navigation(TwoWheeledRobot robo, Odometer odo, BlockDifferentiator bd){
		this.robo=robo;
		this.odo=odo;
		this.bd=bd;	
		
	}
	
	//Travel to method
	public void travelTo(boolean hasBlock, double x, double y){
		if(hasBlock){
			//Code for navigating straight to green/red zone
		}
		else{
			robo.startLightPollers();
			
			//while travelling turn on light sensors
			//Store when light sensor picks up 
				//travel to (x,y) coordinate passed
		}		
	}
	
	//Turn to method
	public void turnTo(double angle, boolean stop) { 
        
        double error = angle - odo.getAng(); 
    
        while (Math.abs(error) > TURNTO_THRESHOLD) { 
               
    
            error = angle - odo.getAng(); 
    
            if (error < -180.0) { //rotate clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else if (error < 0.0) { //rotate counter clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else if (error > 180.0) { //rotate counter clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } else { //rotate clockwise at speed defined in Two Wheeled Robot
                robo.rotateClockwise();
            } 
        } 
    
        if (stop) { 
            robo.stopMotors();} 
           
    }
	
	//Avoid obstacle method
	public void avoidObstacle(){
		
	}
	
	//Investigate block
	public void investigateBlock(){
		
	}
	
	

}
