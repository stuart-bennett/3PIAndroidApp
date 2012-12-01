package com.example.pibluetoothcomms;

import com.example.pibluetoothcomms.ThreePiController.MovementSpeed;

public interface IControlThreePi {
	void moveForward();
	void turnLeft();
	void turnRight();
	void setSpeed(MovementSpeed speed);
	void stop();
	void calibrate();
}
