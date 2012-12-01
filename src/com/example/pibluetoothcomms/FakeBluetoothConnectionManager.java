package com.example.pibluetoothcomms;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FakeBluetoothConnectionManager implements IBluetoothConnectionManager {
		
	private Handler mConnectionHandler;

	public FakeBluetoothConnectionManager(Handler connectionHandler) {
		mConnectionHandler = connectionHandler;
	}
	
	public void connectToDevice(BluetoothDevice device) {
		Bundle bundle = new Bundle();
		bundle.putString("EventName", "BluetoothDeviceConnected");
		bundle.putString("DeviceName", "MockDevice");
		Message msg = new Message();
		msg.setData(bundle);
		mConnectionHandler.sendMessage(msg);
	}
	
	public void disconnectFromDevice()  {	
	}
	
	public BluetoothSocket getSocketToDevice() {
		return null;
	}	
}
