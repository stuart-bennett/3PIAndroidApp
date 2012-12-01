package com.example.pibluetoothcomms;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface IBluetoothConnectionManager {
	void connectToDevice(BluetoothDevice device);
	void disconnectFromDevice();
	BluetoothSocket getSocketToDevice();
}