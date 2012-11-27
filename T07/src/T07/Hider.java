package T07;

import lejos.nxt.*;

// class that focuses on hiding the flag, will also need to deal with depositing the flag
public class Hider {
	private Odometer odometer;
	private Navigation2 navigation;
	private TwoWheeledRobot robot;
	private USPoller usPoller;
	private double fieldLength = 30.48 * 11;
	private double smallDistance = 15;
	
	
	//Constructor
	public Hider(Odometer odometer, Navigation2 navigation, USPoller usPoller) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.usPoller = usPoller;
		this.robot = odometer.getTwoWheeledRobot();
	}
	
	public void pickUpDefender(int x, int y) {
		navigation.travelTo(x * 30.48, y*30.48, true);
		navigation.turnTo(45);
		robot.pickUpFromGround();
	}
	
	// method that focuses on hiding the flag, may need a second method for just placing the flag
	public void hide() {
		// get robot to travel forward
		robot.setForwardSpeed();
		// until ultrasonic reading is less than 17
		while(usPoller.getFilteredData() > 17);
		robot.stop();
		// travel another 8cm using the get displacement because the ultrasonic sensor don't work in a close range
		robot.resetTachoCountBothWheels();
		while (robot.getDisplacement() < 8);
		robot.stop();
		// place down the beacon
		robot.placeOntoGround();
		// exit the field
		exitField();
		Sound.twoBeeps();
	}
	
	public void exitField(){
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		if(currentX >= currentY){
			if(currentX <= fieldLength/2){
				navigation.travelTo(0, 0,true);
			}
			if(currentY >= fieldLength/2){
				navigation.travelTo(fieldLength,fieldLength,true);
			}
			else{
				navigation.travelTo(fieldLength,0,true);
			}
		}
		else{
			if(currentY <= fieldLength/2){
				navigation.travelTo(0,0,true);
			}
			if(currentX >= fieldLength/2){
				navigation.travelTo(fieldLength,fieldLength,true);
			}
			else{
				navigation.travelTo(fieldLength,0,true);
			}
		}
		robot.stop();
	}

}