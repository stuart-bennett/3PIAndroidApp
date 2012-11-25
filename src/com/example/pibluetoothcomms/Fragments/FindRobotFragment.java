package com.example.pibluetoothcomms.Fragments;

import java.util.HashSet;
import java.util.Set;

import com.example.pibluetoothcomms.MainActivity;
import com.example.pibluetoothcomms.R;
import com.example.pibluetoothcomms.SelectBluetoothDeviceDialogListener;
import com.example.pibluetoothcomms.UiActionResult;
import com.example.pibluetoothcomms.DialogFragments.SelectBluetoothDeviceDialogFragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FindRobotFragment extends Fragment {

	// Bluetooth devices that have been discovered
	private Set<BluetoothDevice> mDiscoveredDevices;

	// Bluetooth devices that have been previously paired with this device
	private Set<BluetoothDevice> mPreviouslyPairedDevices;
	
	/**
	 * Setter for mPreviouslyPairedDevices
	 * @param previouslyPairedDevices
	 */
	public void setPreviouslyPairedDevices(Set<BluetoothDevice> previouslyPairedDevices) {
		this.mPreviouslyPairedDevices = previouslyPairedDevices;
	}
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.find_robot, container, false);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.mPreviouslyPairedDevices = new HashSet<BluetoothDevice>();		
		this.mDiscoveredDevices = new HashSet<BluetoothDevice>();		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void BluetoothDeviceConnected(String nameOfDevice) {
		
		// Hide the "Find button"
		LinearLayout findRobotLayout = (LinearLayout)this.getView().findViewById(R.id.FindRobotLinearLayout);
		findRobotLayout.setVisibility(View.INVISIBLE);
		
		// Show the disconnect option
		LinearLayout disconnectDeviceLayout = (LinearLayout)this.getView().findViewById(R.id.DisconnectDeviceLinearLayout);
		disconnectDeviceLayout.setVisibility(View.VISIBLE);
		
		this.setConnectedDeviceText(nameOfDevice);
	}
	
	public void BluetoothDeviceDisconnected() {
		
		this.setConnectedDeviceText(getString(R.string.NoDeviceConnected));
		
		// Hide the disconnect option
		LinearLayout disconnectDeviceLayout = (LinearLayout)this.getView().findViewById(R.id.DisconnectDeviceLinearLayout);
		disconnectDeviceLayout.setVisibility(View.INVISIBLE);
		
		LinearLayout findRobotLayout = (LinearLayout)this.getView().findViewById(R.id.FindRobotLinearLayout);
		findRobotLayout.setVisibility(View.VISIBLE);		
	}
	
	private void setConnectedDeviceText(String value) {
		TextView connectedDeviceTextView = (TextView)this.getView().findViewById(R.id.ConnectedBtDeviceName);
		connectedDeviceTextView.setText(value);		
	}
	
	/**
	 * Give the user a list of previously paired devices and let them
	 * recognise the robot's device name
	 */
	public void promptToConnectToPreviouslyPairedDevices() {
    	if (this.mPreviouslyPairedDevices.size() > 0) {
    		DialogFragment dialog = new SelectBluetoothDeviceDialogFragment(this.mPreviouslyPairedDevices);
    		dialog.show(getFragmentManager(), "SelectBluetoothDeviceDialogFragment");
    	}
	}
	
	public void promptToConnectToDiscoveredDevices() {
    	if (this.mDiscoveredDevices.size() > 0) {
    		DialogFragment dialog = new SelectBluetoothDeviceDialogFragment(this.mDiscoveredDevices);
    		dialog.show(getFragmentManager(), "SelectBluetoothDeviceDialogFragment");
    	}
    	else {
    		// Show no devices found (no previously paired or discovered)
    	}
	}
	
	/** 
	 * Find a device
	 * @param deviceName The name of the device to be found
	 * @return the bluetooth device, or null if it's not found
	 */
	public BluetoothDevice FindDevice(String deviceName) {
		
		for (BluetoothDevice d : this.mPreviouslyPairedDevices) {
			if (d.getName().equals(deviceName)) {	
				return d;
			}
		}
		
		for (BluetoothDevice d : this.mDiscoveredDevices) {
			if (d.getName().equals(deviceName)) {	
				return d;
			}
		}
		
		return null;
	}
	
	public void DeviceDiscovered(BluetoothDevice device)  {
		this.mDiscoveredDevices.add(device);
	}
}
