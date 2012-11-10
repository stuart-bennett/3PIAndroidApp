package com.example.pibluetoothcomms.Threading;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.example.pibluetoothcomms.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class EstablishBluetoothConnectionThread extends Thread {
	
	private final BluetoothAdapter mBtAdapter;
	private final BluetoothDevice mBtDevice;
	private final BluetoothSocket mBtSocket;
	private final Handler mBtConnectionHandler;
	private OutputStream mOutStream;
	
	public EstablishBluetoothConnectionThread(
			BluetoothAdapter bluetoothAdapter,
			BluetoothDevice deviceToConnectTo,
			String uuid,
			Handler connectionHandler) {
		
		this.mOutStream = null;
		this.mBtAdapter = bluetoothAdapter;
		this.mBtDevice = deviceToConnectTo;
		this.mBtConnectionHandler = connectionHandler;
		
		BluetoothSocket tmpSocket = null;
		try {
    		tmpSocket = mBtDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.mBtSocket = tmpSocket;
	}
	
	public void run() {
		
		// Always ensure that discovery is not active before attempting a connection
		this.mBtAdapter.cancelDiscovery();
		
		try {
			this.mBtSocket.connect();
		}
		catch (Exception e)
		{
			try {
				this.mBtSocket.close();
			} catch (Exception innerE) { innerE.printStackTrace(); }
			return;
		}

		Bundle bundle = new Bundle();
		bundle.putString("EventName", "BluetoothDeviceConnected");
		bundle.putString("DeviceName", this.mBtDevice.getName());
		Message msg = new Message();
		msg.setData(bundle);
		this.mBtConnectionHandler.sendMessage(msg);
	}
	
	public boolean hasSocketBeenEstablished() {
		return this.mBtAdapter != null;
	}
	
	public BluetoothSocket getSocket() {
		return this.mBtSocket;
	}
	
	public void cancel() {
		try {
			this.mBtSocket.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
