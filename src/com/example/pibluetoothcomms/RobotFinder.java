package com.example.pibluetoothcomms;

import java.util.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pibluetoothcomms.Exceptions.*;

public class RobotFinder {
	
	private BluetoothAdapter mBluetoothAdapter;

	/**
	 * Creates a new instance of RobotFinder - establishes bluetooth comms
	 */
	public RobotFinder() throws BluetoothNotAvailableOnDeviceException, BluetoothNotEnabledException {
		
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (this.mBluetoothAdapter == null) {
			throw new BluetoothNotAvailableOnDeviceException();
		}
		
		if (!this.mBluetoothAdapter.isEnabled()) {
			throw new BluetoothNotEnabledException();
		}	
	}
	
	/**
	 * Gets devices that have previously been paired on this device
	 */
	public Set<BluetoothDevice> getPreviouslyPairedDevices() {
		return this.mBluetoothAdapter.getBondedDevices();		
	}
	
	/**
	 * Begins discovering bluetooth devices
	 */
	public void startDiscovery() {		
		this.mBluetoothAdapter.startDiscovery();	
	}
	
	public void stopDiscovery() {
		this.mBluetoothAdapter.cancelDiscovery();
	}
}