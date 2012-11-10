package com.example.pibluetoothcomms.DialogFragments;

import java.util.Set;

import com.example.pibluetoothcomms.R;
import com.example.pibluetoothcomms.SelectBluetoothDeviceDialogListener;
import com.example.pibluetoothcomms.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectBluetoothDeviceDialogFragment extends DialogFragment {
	
	private String[] mDevices;
	private SelectBluetoothDeviceDialogListener mDialogListener;
		
	public SelectBluetoothDeviceDialogFragment(String[] discoveredDevices) {
		this.mDevices = discoveredDevices;
	}
	
	public SelectBluetoothDeviceDialogFragment(Set<BluetoothDevice> discoveredDevices) {
		
		this.mDevices = new String[discoveredDevices.size() + 1];
		int i = 0;
		for(BluetoothDevice device : discoveredDevices) {
			String friendlyDeviceName = device.getName();
			this.mDevices[i] = friendlyDeviceName != null ? friendlyDeviceName : "<Unknown>";
			i++;
		}
	}	
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		this.mDevices[this.mDevices.length - 1] = getString(R.string.SelectPreviousBtDeviceScanForNewDevicesOption);
		builder
			.setTitle(R.string.SelectPreviousBtDeviceTitle)
			.setItems(this.mDevices, new DialogInterface.OnClickListener() {
		
				public void onClick(DialogInterface dialog, int which) {
					mDialogListener.onDialogDeviceSelected(mDevices[which]);
				}
				
			});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			this.mDialogListener = (SelectBluetoothDeviceDialogListener)activity;
		}
		catch (ClassCastException e) {
			String errorMessage = "The activity '%s' must implement 'SelectBluetoothDeviceDialogListener";
			throw new RuntimeException(String.format(errorMessage, activity.toString()));
		}
	}	
}
