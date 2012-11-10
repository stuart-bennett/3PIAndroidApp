package com.example.pibluetoothcomms.Fragments;

import com.example.pibluetoothcomms.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ControlRobotFragment extends Fragment {

	@Override
	public View onCreateView(
			LayoutInflater layoutInflater,
			ViewGroup container,
			Bundle savedInstance) {
		
		return layoutInflater.inflate(R.layout.control_robot, container, false);
	}

}
