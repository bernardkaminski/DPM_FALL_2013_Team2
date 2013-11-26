
package Hardware;

import lejos.nxt.NXTRegulatedMotor;

/**
 * The class that has all the hardware necessary, it controls the three motors and the four sensors.
 * @author Bernie, Connor, Deepak
 * @version 1.0
 * @see UltrasonicPoller,LightPoller,UltrasonicScanner,NXTRegulatedMotor
 */
 
public class TwoWheeledRobot {
        
               public static final double DEFAULT_LEFT_RADIUS = 2.1;
        public static final double DEFAULT_RIGHT_RADIUS = 2.1;
        public static final double DEFAULT_WIDTH = 16.8;
        private NXTRegulatedMotor leftMotor, rightMotor, clawMotor;
        private UltrasonicScanner USSBottom,USSTop;
        private UltrasonicPoller USPBottom,USPTop;
        private LightPoller rightWheelLP,leftWheelLP;
        private double leftRadius, rightRadius, width;
        private int forwardSpeed=250, rotationSpeed=90;
        
        //Claw constants
        public final int CLAW_RAISE_ANGLE=85;
        public final int CLAW_RAISE_SPEED=100;//250
        public final int CLAW_RAISE_ACC=100;//150
        public final int CLAW_LOWER_ANGLE=-55;
        public final int CLAW_LOWER_SPEED=50;
        public final int CLAW_LOWER_ACC=80;
        
        /*
         * *******************************************************
         * class that controls all motor movements and speeds.
        */
        /**
         * @param leftMotor
         * @param rightMotor
         * @param clawMotor
         * @param USSTop The top ultrasonic Scanner  
         * @param USSBottom The bottom ultrasonic Scanner
         * @param USPTop The top ultrasonic poller
         * @param USPBottom The bottom ultrasonic poller
         * @param leftWheelLP The left wheel light poller
         * @param rightWheelLP The right wheel light poller
         * @param width The width between wheels 
         * @param leftRadius Left wheel radius 
         * @param rightRadius Right wheel radius 
         */
        public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
                               NXTRegulatedMotor rightMotor, 
                               NXTRegulatedMotor clawMotor,
                               UltrasonicScanner USSTop, 
                               UltrasonicScanner USSBottom,
                               UltrasonicPoller USPTop,
                               UltrasonicPoller USPBottom,
                               LightPoller leftWheelLP,
                               LightPoller rightWheelLP,
                               double width,
                                                       double leftRadius,
                                                       double rightRadius) 
        {
                this.leftMotor = leftMotor;
                this.rightMotor = rightMotor;
                this.clawMotor=clawMotor;
                this.USSTop=USSTop;
                this.USSBottom=USSBottom;
                this.USPTop=USPTop;
                this.USPBottom=USPBottom;
                this.leftWheelLP=leftWheelLP;
                this.rightWheelLP=rightWheelLP;
                this.leftRadius = leftRadius;
                        this.rightRadius = rightRadius;
                        this.width = width;
                
        }
        
        /**
         * 
         * @param leftMotor
         * @param rightMotor
         * @param clawMotor
         * @param USSTop The top ultrasonic Scanner  
         * @param USSBottom The bottom ultrasonic Scanner
         * @param USPTop The top ultrasonic poller
         * @param USPBottom The bottom ultrasonic poller
         * @param leftWheelLP The left wheel light poller
         * @param rightWheelLP The right wheel light poller
         */
            public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
                            NXTRegulatedMotor rightMotor,
                            NXTRegulatedMotor clawMotor,
                            UltrasonicScanner USSTop, 
                UltrasonicScanner USSBottom,
                UltrasonicPoller USPTop,
                UltrasonicPoller USPBottom,
                LightPoller leftWheelLP,
                LightPoller rightWheelLP) 
            {
                    this(leftMotor, rightMotor,clawMotor,USSTop,USSBottom,USPTop,USPBottom,leftWheelLP,rightWheelLP, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
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
        
        public void rotateClawAbsoluteWhileBackingUp(int degree)
        {
        	setForwardSpeed(50);
        	while(clawMotor.getPosition()>degree)
        	{
        		clawMotor.backward();
        		goBackward();
        	}
        	stopClaw();
        	stopMotors();
        		//clawMotor.lock(100);
        }
        public void stopClaw()
        {
                clawMotor.stop();
                //clawMotor.flt();
        }
        
        public void floatClaw()
        {
                clawMotor.flt();
        }
        
        public void pickUpBlock()
        {
            	setClawAcc(CLAW_RAISE_ACC);
            	setclawSpeed(CLAW_RAISE_SPEED);
                clawMotor.rotateTo(CLAW_RAISE_ANGLE,false);
        }

        public void pickUpBlock(int degree)
        {
        	setClawAcc(CLAW_RAISE_ACC);
        	setclawSpeed(CLAW_RAISE_SPEED);
                clawMotor.rotateTo(degree,false);
        }
        
        public void pickUpBlock(int degree,int acc,int speed)
        {
            	setClawAcc(acc);
            	setclawSpeed(speed);
                clawMotor.rotateTo(degree,false);
        }
        
        public void dropBlock()
        {
        	setClawAcc(CLAW_LOWER_ACC);
        	setclawSpeed(CLAW_LOWER_SPEED);
            clawMotor.rotateTo(CLAW_LOWER_ANGLE,false);
        }
        
        public void dropBlock(int degree)
        {
        	setClawAcc(CLAW_LOWER_ACC);
        	setclawSpeed(CLAW_LOWER_SPEED);
            clawMotor.rotateTo(degree,false);
        }
        
        public void dropBlock(int degree,int acc,int speed)
        {
        	setClawAcc(acc);
        	setclawSpeed(speed);
            clawMotor.rotateTo(degree,false);
        }
        
        public void setClawAcc(int acc)
        {
                clawMotor.setAcceleration(acc);
        }
        
        public void setclawSpeed (int speed)
        {
                clawMotor.setSpeed(speed);
        }
        
        public void stopLeftMotor()
        {
                leftMotor.stop();
        }
        public void stopRightMotor()
        {
                rightMotor.stop();
        }
        
        public void startLeftMotor()
        {        
                leftMotor.setSpeed(forwardSpeed);
                leftMotor.forward();
        }
        
        public void startRightMotor()
        {        
                rightMotor.setSpeed(forwardSpeed);
                rightMotor.forward();
        }
        
        public void startLeftMotorback()
        {        
                leftMotor.setSpeed(forwardSpeed);
                leftMotor.backward();
        }
        
        public void startRightMotorback()
        {        
                rightMotor.setSpeed(forwardSpeed);
                rightMotor.backward();
        }
        /**
         * stops the robots motors that control movement 
         */
        public void stopMotors()
        {
                leftMotor.stop();
                rightMotor.stop();
        }
        /**
         * starts the motors that control movement and moves the robot forward
         */
        public void goForward()
        {
                leftMotor.setSpeed(forwardSpeed);
                rightMotor.setSpeed(forwardSpeed);
                
                leftMotor.forward();
                rightMotor.forward();
        }
        /**
         * starts the motors that control movement and moves the robot backward
         */
        public void goBackward()
        {
                leftMotor.setSpeed(forwardSpeed);
                rightMotor.setSpeed(forwardSpeed);
                
                leftMotor.backward();
                rightMotor.backward();
        }
        /**
         * starts the motors that control movement and turns the robot counterclockwise
         */
        public void rotateCounterClockwise()
        {
                leftMotor.setSpeed(rotationSpeed);
                rightMotor.setSpeed(rotationSpeed);
                
                leftMotor.backward();
                rightMotor.forward();
        }

        
        /**
         * starts the motors that control movement and turns the robot clockwise
         */
        public void rotateClockwise()
        {
                leftMotor.setSpeed(rotationSpeed);
                rightMotor.setSpeed(rotationSpeed);
                
                leftMotor.forward();
                rightMotor.backward();
        }
        /**
         * 
         * @param factor a factor to increase speed(1=100%)
         */
        public void accelerateRotationClockwise(int factor)
        {
                
                rotationSpeed+=(factor*rotationSpeed);
                rotateClockwise();
                
        }
        /**
         * 
         * @param factor a factor to increase speed(1=100%)
         */
        public void deAccelerateRotaionCounterCllockwise(int factor)
        {
                rotationSpeed+=(factor*rotationSpeed);;
                rotateCounterClockwise();
        }
        /**
         * 
         * @param factor a factor to increase speed(1=100%)
         */
        public void accelerateForward(int factor)
        {
                forwardSpeed+=(factor*forwardSpeed);
                goForward();
        }
        
        public void accelerateLeftWheel(double factor)
        {
                factor = (factor*forwardSpeed);
                leftMotor.setSpeed(forwardSpeed+(int)factor);
            rightMotor.setSpeed(forwardSpeed);
            
            leftMotor.forward();
            rightMotor.forward();
        }
        
        public void accelerateRightWheel(double factor)
        {
                factor = (factor*forwardSpeed);
                leftMotor.setSpeed(forwardSpeed);
            rightMotor.setSpeed(forwardSpeed+(int)factor);
            
            leftMotor.forward();
            rightMotor.forward();
        }
        /**
         * 
         * @return the current forward speed
         */
        public int getForwardSpeed()
        {
                return this.forwardSpeed;
        }
        
        /**
         * set the current forward speed
         * @param speed
         */
        public void setForwardSpeed(int speed)
        {
                forwardSpeed = speed;
        }
        /**
         * set the current Rotation speed
         * @param speed
         */
        public void setRotationSpeed(int speed)
        {
                rotationSpeed = speed;
        }
        
       /**
        * start the right light sensor 
        */
        public void startRightLP()
        {
                rightWheelLP.startLightPoller();
       
        /**
         * start the left light sensor 
         */
        }
        public void startLeftLP()
        {
                leftWheelLP.startLightPoller();
        }
        
        /**
         * stop right light sensor        
         */
        public void stopRightLP()
        {
                rightWheelLP.stopLightPoller();
                
        }
        /**
         * stop left light sensor        
         */
        public void stopLeftLP()
        {
                leftWheelLP.stopLightPoller();
                
        }
        public int getLeftLightValue()
        {
                return leftWheelLP.returnLightValue();
        }
        public int getRightLightValue()
        {
                return rightWheelLP.returnLightValue();
        }
        public void turnOnLeftLight(){
                leftWheelLP.turnOnlights();
                 
                 
        }
        public void turnOnRightLight(){
                rightWheelLP.turnOnlights();
                 
                 
        }       
        
        public void startTopUsPoller()
        {
                USPTop.startUsPoller();
        }
        
        public void startUsBottom()
        {
                USPBottom.startUsPoller();
        }
        
        public void stopTopUsPoller()
        {
                USPTop.stopUsPoller();
        }
        
        public void stopBottomUsPoller()
        {
                USPBottom.stopUsPoller();
        }
        
        public int getBottomUsPollerDistance()
        {
                return USPBottom.returnDistance();
        }
        
        public int getTopUsPollerDistance()
        {
                return USPTop.returnDistance();
        }
        
        //note for ultrasonic scanner the respectice poller should be stopped
        public double scanWithBottomsensor(int amountOfScans)
        {
                return USSBottom.getFilteredDistance(amountOfScans);
        }
        public double scanWithTopsensor(int amountOfScans)
        {
                return USSTop.getFilteredDistance(amountOfScans);
        }
        
}