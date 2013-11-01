import lejos.nxt.ColorSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

//only here because odometer uses it 
public class TwoWheeledRobot {
	
        public static final double DEFAULT_LEFT_RADIUS = 2.1;
        public static final double DEFAULT_RIGHT_RADIUS = 2.1;
        public static final double DEFAULT_WIDTH = 15.1;
        private NXTRegulatedMotor leftMotor, rightMotor, clawMotor;
        private UltrasonicScanner USSTop;
        private UltrasonicScanner USSBottom;
        private UltrasonicPoller USPTop;
        private UltrasonicPoller USPBottom;
        private LightPoller leftWheelLP;
        private LightPoller rightWheelLP;
        
        
        //
        
        private double leftRadius, rightRadius, width;
        private int forwardSpeed, rotationSpeed;
        
        /*
         * *******************************************************
         * class that controls all motor movements and speeds.
        
        
        */
        public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
                               NXTRegulatedMotor rightMotor, 
                               NXTRegulatedMotor clawMotor,
                               UltrasonicScanner USSTop, 
                               UltrasonicScanner USSBottom,
                               UltrasonicPoller USPTop,
                               UltrasonicPoller USPBottom,
                               LightPoller leftWheelLP,
                               LightPoller rightWheelLP                         
                               ) {
                this.leftMotor = leftMotor;
                this.rightMotor = rightMotor;
                this.clawMotor=clawMotor;
                this.USSTop=USSTop;
                this.USSBottom=USSBottom;
                this.USPTop=USPTop;
                this.USPBottom=USPBottom;
                this.leftWheelLP=leftWheelLP;
                this.rightWheelLP=rightWheelLP;
                
        }
        
        
        // accessors used for odometer
        
        public double getDisplacement() 
        {
                return (leftMotor.getTachoCount() * leftRadius +
                                rightMotor.getTachoCount() * rightRadius) *
                                Math.PI / 360.0;
        }
        
        public double getHeading() 
        {
                return (leftMotor.getTachoCount() * leftRadius -
                                rightMotor.getTachoCount() * rightRadius) / width;
        }
        
        public void getDisplacementAndHeading(double [] data) 
        {
                int leftTacho, rightTacho;
                leftTacho = leftMotor.getTachoCount();
                rightTacho = rightMotor.getTachoCount();
                
                data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *        Math.PI / 360.0;
                data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
        }
        
        
        /***************************[Motor Methods]***************************
         * the motor methods are bellow.
         * everything that the motors need to do can be found below
         * speed of motors can be changed by using the accessors
        */
        public void stopMotors()
        {
                leftMotor.stop();
                rightMotor.stop();
        }
        
        public void goForward()
        {
                leftMotor.setSpeed(forwardSpeed);
                rightMotor.setSpeed(forwardSpeed);
                
                leftMotor.forward();
                rightMotor.forward();
        }
        
        public void goBackward()
        {
                leftMotor.setSpeed(forwardSpeed);
                rightMotor.setSpeed(forwardSpeed);
                
                leftMotor.backward();
                rightMotor.backward();
        }
        
        public void rotateCounterClockwise()
        {
                leftMotor.setSpeed(rotationSpeed);
                rightMotor.setSpeed(rotationSpeed);
                
                leftMotor.backward();
                rightMotor.forward();
        }

        public void rotateClockwise()
        {
                leftMotor.setSpeed(rotationSpeed);
                rightMotor.setSpeed(rotationSpeed);
                
                leftMotor.forward();
                rightMotor.backward();
        }
        
        public void accelerateRotationClockwise(int factor)
        {
                
                rotationSpeed+=(factor*rotationSpeed);
                rotateClockwise();
                
        }
        
        public void deAccelerateRotaionCounterCllockwise(int factor)
        {
                rotationSpeed+=factor;
                rotateCounterClockwise();
        }
        
        public void accelerateForward(int factor)
        {
                forwardSpeed+=factor;
                goForward();
        }
        
        public int getForwardSpeed()
        {
                return this.forwardSpeed;
        }
        
        // mutators
        public void setForwardSpeed(int speed)
        {
                forwardSpeed = speed;
        }
        
        public void setRotationSpeed(int speed)
        {
                rotationSpeed = speed;
        }
        
        /***************[Sensor Methods]****************
        *All sensor related methods are below
        */
        
        public void startRightLP(){
        	rightWheelLP.startLightPoller();
        	
        }
        public void startLeftLP(){
        	leftWheelLP.startLightPoller();
        	
        }
        public void stopRightLP(){
        	rightWheelLP.stopLightPoller();
        	
        }
        public void stopLeftLP(){
        	leftWheelLP.stopLightPoller();
        	
        }
        public int getLeftLightValue(){
        	return leftWheelLP.returnLightValue();
        }
        public int getRightLightValue(){
        	return rightWheelLP.returnLightValue();
        }
        

}