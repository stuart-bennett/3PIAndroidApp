package com.example.pibluetoothcomms.Fragments;

import com.example.pibluetoothcomms.MainActivity;
import com.example.pibluetoothcomms.R;
import com.example.pibluetoothcomms.UiActionResult;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FindRobotFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.find_robot, container, false);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
}
