package com.example.pibluetoothcomms.DialogFragments;

import java.util.Set;

import com.example.pibluetoothcomms.R;
import com.example.pibluetoothcomms.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectDiscoveredDeviceDialogFragment extends DialogFragment {
	
	private String[] mDiscoveredDevices;
	
	public SelectDiscoveredDeviceDialogFragment(String[] discoveredDevices) {
		this.mDiscoveredDevices = discoveredDevices;
	}
	
	public SelectDiscoveredDeviceDialogFragment(Set<BluetoothDevice> discoveredDevices) {
		this.mDiscoveredDevices = new String[discoveredDevices.size()];
		int i = 0;
		for(BluetoothDevice device : discoveredDevices) {
			this.mDiscoveredDevices[i] = device.getName();
			i++;
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());		
		builder
			.setTitle(R.string.SelectPreviousBtDeviceTitle)
			.setItems(this.mDiscoveredDevices, new DialogInterface.OnClickListener() {
		
				public void onClick(DialogInterface dialog, int which) {
				}
				
			});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			// this.mDialogListener = (SelectBluetoothDeviceDialogListener)activity;
		}
		catch (ClassCastException e) {
			String errorMessage = "The activity '%s' must implement 'SelectBluetoothDeviceDialogListener";
			throw new RuntimeException(String.format(errorMessage, activity.toString()));
		}
	}	
}
