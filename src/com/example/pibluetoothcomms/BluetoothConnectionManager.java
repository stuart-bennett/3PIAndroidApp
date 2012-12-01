package com.example.pibluetoothcomms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.example.pibluetoothcomms.Threading.EstablishBluetoothConnectionThread;

public class BluetoothConnectionManager implements  IBluetoothConnectionManager {
    
	// Thread for managing Bluetooth connectivity
	private EstablishBluetoothConnectionThread mConnectionThread;
	
	// Bluetooth Connection Handler for notifying outside classes of events
	private Handler mBluetoothConnectionHandler;
	
	/**
	 * Create a new instance of Bluetooth Connection Manager
	 * @param connectionThread
	 * @param connectionHandler
	 */
	public BluetoothConnectionManager(Handler connectionHandler) {	
		mBluetoothConnectionHandler = connectionHandler;
	}
	
	/**
	 * Connect to @device
	 * param @device The device to connect to
	 */
	public void connectToDevice(BluetoothDevice device) {
    	this.mConnectionThread = new EstablishBluetoothConnectionThread(
    			BluetoothAdapter.getDefaultAdapter(), 
    			device, 
    			"00001101-0000-1000-8000-00805F9B34FB",
    			this.mBluetoothConnectionHandler);
    	this.mConnectionThread.run();    	
	}
	
	/**
	 * Gets a socket to the device connected to
	 */
	public BluetoothSocket getSocketToDevice() {
		return this.mConnectionThread.getSocket();
	}
	
	/**
	 * Disconnects from the device currently connected
	 */
	public void disconnectFromDevice() {
		this.mConnectionThread.cancel();
	}
}
