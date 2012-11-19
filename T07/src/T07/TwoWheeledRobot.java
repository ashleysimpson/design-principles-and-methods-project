package T07;
import lejos.nxt.*;

public class TwoWheeledRobot {
	
	// this class contains the measurements of the robot
	public static final double DEFAULT_LEFT_RADIUS = 2.70;
	public static final double DEFAULT_RIGHT_RADIUS = 2.70;
	public static final double DEFAULT_WIDTH = 15.8;
	public NXTRegulatedMotor leftMotor, rightMotor, lightSensorMotor;
	public NXTRegulatedMotor leftClawMotor, rightClawMotor, liftRaiseMotor;
	public UltrasonicSensor middleUSSensor, rightUSSensor;
	public LightSensor leftLS, rightLS, middleLS;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	private final int openAndClosingAngle = 60;
	private int distanceClawRaised = 0; // this stores the raised distance of the claws, for use by the lower claws method
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   NXTRegulatedMotor leftClawMotor,
						   NXTRegulatedMotor rightClawMotor,
						   NXTRegulatedMotor liftRaiseMotor,
						   UltrasonicSensor middleUSSensor,
						   UltrasonicSensor rightUSSensor,
						   LightSensor leftLS,
						   LightSensor rightLS,
						   LightSensor middleLS) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftClawMotor = leftClawMotor;
		this.rightClawMotor = rightClawMotor;
		this.liftRaiseMotor = liftRaiseMotor;
		this.leftLS = leftLS;
		this.rightLS = rightLS;
		this.middleUSSensor = middleUSSensor;
		this.rightUSSensor = rightUSSensor;
		this.leftRadius = DEFAULT_LEFT_RADIUS;
		this.rightRadius = DEFAULT_RIGHT_RADIUS;
		this.width = DEFAULT_WIDTH;
		
		leftClawMotor.stop(false); // makes the motors for the claws stay in place, in case the claw comes into contact with something
		rightClawMotor.stop(true);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, 
						   NXTRegulatedMotor rightMotor, 
						   UltrasonicSensor middleUSSensor, 
						   UltrasonicSensor rightUSSensor,
						   NXTRegulatedMotor leftClawMotor,
						   NXTRegulatedMotor rightClawMotor,
						   NXTRegulatedMotor liftRaiseMotor,
						   LightSensor leftLS,
						   LightSensor rightLS,
						   LightSensor middleLS,
						   double width) {
		this(leftMotor, rightMotor, leftClawMotor, rightClawMotor, liftRaiseMotor, middleUSSensor, rightUSSensor, leftLS, rightLS, middleLS);
	}
	
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, 0);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(0, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
		
	}
	
	// 
	
	// method that returns if leftMotor is moving
	public boolean leftMotorMoving() {
		return leftMotor.isMoving();
	}
	
	// method that returns if rightMotor is moving
	public boolean rightMotorMoving() {
		return rightMotor.isMoving();
	}
	
	// returns whether the motors are moving or not
	public boolean motorsMoving() {
		if (rightMotorMoving() || leftMotorMoving()) {
			return true;
		}
		return false;
	}
	
	// method that closes the robot claws
	public void closeClaws() {
		
		leftClawMotor.setSpeed(50); // TODO: check the opening and closing speeds
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(openAndClosingAngle);
		rightClawMotor.rotate(openAndClosingAngle);
		leftClawMotor.stop(true);
		rightClawMotor.stop(false);
	}
	
	// TODO: check the rotation direction of the claws
	// method that opens the robot claws
	public void openClaws() {
		
		leftClawMotor.setSpeed(50);
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(-openAndClosingAngle);
		rightClawMotor.rotate(-openAndClosingAngle);
		leftClawMotor.stop(true);
		rightClawMotor.stop(false);
	}
	
	// method that raises the claw system, passed an int that is converted to a height for distance of raise
	public void raiseClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised + distance; // this takes into account if the claw is raised from some height to another
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180); // TODO: check the distance raised by the input parameter, make it roughly accurate, check direction, check lowerclaws too
		liftRaiseMotor.stop(false);
		
	}
	
	// method that lowers the claw system, passed an int that is converts to a height for distance to lower
	public void lowerClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised - distance; // this takes into account the amount the claw height will be displaced by
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180);
		liftRaiseMotor.stop(false);
	}
	
	// method that stops the leftmotor only
	public void stopLeftMotor () {
		leftMotor.stop();
	}
	
	// method that stops the rightmotor only
	public void stopRightMotor () {
		rightMotor.stop();
	}
	
	/**
	 * both motor rotate a certain degree
	 * @param angle
	 */
	
	// TODO: Check if this turns the desired angle
	public void rotate(int angle) {
		rightMotor.rotate(angle, true);
		leftMotor.rotate(-angle);
	}
	
	/**
	 * stop two motors at the same time
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
	// method that moves the robot forward a specific distance
	public void moveForwardDistance(double distance) {
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), true);
	}
	
	// taken from the square driver class (lab2) converts the turn angle into a distance for the convertDistance method
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	// taken from the square drive class, converts into a usable angle displacement (degrees) for the rotate operation
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}