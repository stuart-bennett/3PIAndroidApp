package com.example.pibluetoothcomms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothSocket;

public class ThreePiController {
	
	public enum MovementSpeed {
		STOP,
		SLOW,
		MEDIUM,
		FAST
	}
	
	public enum Command {
		LEFT_MOTOR_FORWARD,
		RIGHT_MOTOR_FORWARD,
		CALIBRATE,
		EMERGENCY_STOP
	}
	
	private final BluetoothSocket mSocket;
	private OutputStream mOutStream = null;
	
	private MovementSpeed speed;
	
	private final Map<MovementSpeed, byte[]> mSpeedMap = new HashMap<MovementSpeed, byte[]>() {{
		put(MovementSpeed.STOP, new byte[] { (byte) 0x00 });
		put(MovementSpeed.SLOW, new byte[] { (byte) 0x0E });
		put(MovementSpeed.MEDIUM, new byte[] { (byte) 0x1E });
		put(MovementSpeed.FAST, new byte[] { (byte) 0x2E });
	}};
		
	private final Map<Command, byte[]> mCommandMap = new HashMap<Command, byte[]>() {{
		put(Command.LEFT_MOTOR_FORWARD, new byte[] { (byte) 0xC1 });
		put(Command.RIGHT_MOTOR_FORWARD, new byte[] { (byte) 0xC5 });	
		put(Command.EMERGENCY_STOP, new byte[] { (byte) 0xBC });			
		put(Command.CALIBRATE, new byte[] { (byte) 0xBA });
	}};
	
	public ThreePiController(BluetoothSocket socket) {
		
		this.mSocket = socket;
		try {
			this.mOutStream = socket.getOutputStream();				
		}
		catch (Exception e) { e.printStackTrace(); }
		
		this.speed = MovementSpeed.SLOW;
	}
	
	/**
	 * Control how the fast the 3Pi will move when controlled
	 * @param speed
	 */
	public void setSpeed(MovementSpeed speed) {
		this.speed = speed;
	}
	
	public void moveForward() {
		
		this.writeToOutputStream(new byte[] {
			this.mCommandMap.get(Command.LEFT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(this.speed)[0],
			this.mCommandMap.get(Command.RIGHT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(this.speed)[0]					
		});
	}
		
	public void turnLeft() {
		this.writeToOutputStream(new byte[] {
			this.mCommandMap.get(Command.LEFT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(MovementSpeed.SLOW)[0]
		});
	}
	
	public void turnRight() {
		this.writeToOutputStream(new byte[] {
			this.mCommandMap.get(Command.RIGHT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(MovementSpeed.STOP)[0]
		});
	}		
	
	public void stop() {
		this.speed = MovementSpeed.SLOW;
		this.writeToOutputStream(new byte[] {		
			this.mCommandMap.get(Command.LEFT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(MovementSpeed.STOP)[0],
			this.mCommandMap.get(Command.RIGHT_MOTOR_FORWARD)[0],
			this.mSpeedMap.get(MovementSpeed.STOP)[0],
		});
	}
	
	public void calibrate() {
		this.writeToOutputStream(this.mCommandMap.get("CALIBRATE"));
	}
	
	private void writeToOutputStream(byte[] data) {
		try {
			this.mOutStream.write(data);
		} catch (IOException e) { e.printStackTrace(); }
	}	
}
