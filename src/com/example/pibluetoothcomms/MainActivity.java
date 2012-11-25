package com.example.pibluetoothcomms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;

import com.example.pibluetoothcomms.ThreePiController.MovementSpeed;
import com.example.pibluetoothcomms.DialogFragments.SelectBluetoothDeviceDialogFragment;
import com.example.pibluetoothcomms.Exceptions.*;
import com.example.pibluetoothcomms.Fragments.FindRobotFragment;
import com.example.pibluetoothcomms.Threading.EstablishBluetoothConnectionThread;

public class MainActivity extends FragmentActivity 
	implements SelectBluetoothDeviceDialogListener {

	private RobotFinder mRobotFinder;
	private EstablishBluetoothConnectionThread mConnectionThread;
	
	// Devices discovered during bluetooth discovery
	private Set<BluetoothDevice> mDiscoveredBtDevices;
	
	// For managing comms with the 3Pi once connected
	private ThreePiController mRobotController;

	// The find robot fragment
	private FindRobotFragment mFindRobotFragment;
	
	// Handles bluetooth device discovery
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String action = intent.getAction();	
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);	
    			mFindRobotFragment.DeviceDiscovered(device);
    			// mDiscoveredBtDevices.add(device);			
    		}
    	}
    };
    
    private final Handler mBluetoothConnectionHandler = new Handler() {
    	public void handleMessage(Message msg) { 		
    		Bundle data = msg.getData();
    		String event = data.getString("EventName");
    		if (event.equalsIgnoreCase(getString(R.string.BluetoothDeviceConnectedEvent))) {
    			doBluetoothDeviceConnected(data.getString(getString(R.string.BluetoothDeviceConnectedEventDeviceNamePropertyKey)));
    		}
    		else {
    			throw new RuntimeException(String.format("Didn't know what to do for event name: '%s'", event));
    		}	
    	}
    };
	
    private void doBluetoothDeviceConnected(String nameOfDeviceConnectedTo) {
    	FragmentManager manager = this.getSupportFragmentManager();
    	FindRobotFragment findRobotFragment = (FindRobotFragment)manager.findFragmentById(R.id.find_robot_fragment);
    	findRobotFragment.BluetoothDeviceConnected(nameOfDeviceConnectedTo);
    	this.showRobotControls();
    	this.mRobotController = new ThreePiController(this.mConnectionThread.getSocket());
    }
    
    /**
     * Shows the controls for the robot
     */
    private void showRobotControls() {
    	FragmentManager manager = this.getSupportFragmentManager();
    	FragmentTransaction ft = manager.beginTransaction();
    	Fragment controlRobotFragment = manager.findFragmentById(R.id.control_robot_fragment);
    	ft.show(controlRobotFragment);
    	ft.commit();  
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_main);         
        
        this.mDiscoveredBtDevices = new HashSet<BluetoothDevice>();
        this.registerBluetoothDiscoveryIntent();
    	FragmentManager manager = this.getSupportFragmentManager();
    	this.mFindRobotFragment = (FindRobotFragment)manager.findFragmentById(R.id.find_robot_fragment);        
        this.initialiseFragments();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }     
    
    /**
     * Called when a Bluetooth device selection dialog is used to specify a device
     */
    public void onDialogDeviceSelected(String nameOfSelectedDevice) {    	
    	if (nameOfSelectedDevice.equalsIgnoreCase(getString(R.string.SelectPreviousBtDeviceScanForNewDevicesOption))) {
    		// Prompt for device discovery
    		this.doDiscoveryOfDevices();
    	}
    	else {
    		BluetoothDevice device = this.mFindRobotFragment.FindDevice(nameOfSelectedDevice);
    		
    		if (device == null) {
    			throw new RuntimeException(String.format("Could not find device '%s'", nameOfSelectedDevice));
    		}
    		
    		this.doConnectToDevice(device);
    	}
    }	    
    
    /** 
     * Helper method for initialising UI state when the activity first loads
     */
    private void initialiseFragments() {
    	FragmentManager manager = this.getSupportFragmentManager();
    	FragmentTransaction ft = manager.beginTransaction();
    	Fragment controlRobotFragment = manager.findFragmentById(R.id.control_robot_fragment);
    	ft.hide(controlRobotFragment);
    	ft.commit();  
    }
 
    /**
     * Register broadcast receiver for discovering bluetooth
     */
    private void registerBluetoothDiscoveryIntent() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(this.mBroadcastReceiver, filter);    	
    }
 
    public void onControlRobotForwardButton(View v) {
    	this.mRobotController.moveForward();
    }
    
	public void onControlRobotBackwardButton(View v) {    
    	this.mRobotController.stop();	    
	}
	
	public void onControlRobotCalibrateButton(View v) {
		this.mRobotController.calibrate();
	}
	
	public void onControlRobotTurnLeftButton (View v) {
		this.mRobotController.turnLeft();
	}

	public void onControlRobotTurnRightButton (View v) {
		this.mRobotController.turnRight();
	}
	
	public void onControlRobotSpeedSlowRadioClicked(View v) {
		this.mRobotController.setSpeed(MovementSpeed.SLOW);
	}
	
	public void onControlRobotSpeedMediumRadioClicked(View v) {
		this.mRobotController.setSpeed(MovementSpeed.MEDIUM);
	}

	public void onControlRobotSpeedFastRadioClicked(View v) {
		this.mRobotController.setSpeed(MovementSpeed.FAST);
	}
			
    public void onFindRobotButtonClick(View v) {
        this.initialiseFragments();
    	this.doFindRobot();
    }
   
	public void onDisconnectFromDeviceButton(View v) {
		this.mConnectionThread.cancel();
    	FragmentManager manager = this.getSupportFragmentManager();
    	FindRobotFragment findRobotFragment = (FindRobotFragment)manager.findFragmentById(R.id.find_robot_fragment);
    	findRobotFragment.BluetoothDeviceDisconnected();
	}
    
    // Confirms if Bluetooth is active. Also prompts to enable if disabled
    private void doFindRobot() {
    	
    	// Is bluetooth enabled on the device?
    	UiActionResult startBluetoothResult = this.checkBluetoothAndPromptIfNotEnabled();
    	if (!startBluetoothResult.WasActionSuccessful()) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    		builder.setMessage(startBluetoothResult.getMessageToUser());
    		builder.show();
    		return;
    	}
    	
    	FragmentManager manager = this.getSupportFragmentManager();
    	FindRobotFragment findRobotFragment = (FindRobotFragment)manager.findFragmentById(R.id.find_robot_fragment);
    	findRobotFragment.setPreviouslyPairedDevices(this.mRobotFinder.getPreviouslyPairedDevices());
    	findRobotFragment.promptToConnectToPreviouslyPairedDevices();
    }
    
    /**
     * 
     * @return True if Bluetooth setup went ok, false if not
     */
    private UiActionResult checkBluetoothAndPromptIfNotEnabled() {
    	try {
        	this.mRobotFinder = new RobotFinder();    		
    	}
    	catch (BluetoothNotAvailableOnDeviceException e) {
    		e.printStackTrace();
    		return new UiActionResult(false, "You do not have Bluetooth available on your device! This application required a Bluetooth enabled device");
    	}
    	catch (BluetoothNotEnabledException e) {
    		// If the device is bluetooth capable then we just need to turn it on...
    		e.printStackTrace();
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBluetoothIntent, 1);
    	}
    	
    	return new UiActionResult(true);
    }
    
    /**
     * Connects to Bluetooth Device
     * @param device
     */
    private void doConnectToDevice(BluetoothDevice device) {
    	
    	this.mConnectionThread = new EstablishBluetoothConnectionThread(
    			BluetoothAdapter.getDefaultAdapter(), 
    			device, 
    			"00001101-0000-1000-8000-00805F9B34FB",
    			this.mBluetoothConnectionHandler);
    	this.mConnectionThread.run();
    }
    
    /**
     * Shows device discovery dialog and makes necessary calls for discovering devices
     */
    private void doDiscoveryOfDevices() {
    	
		// this.mDiscoveredBtDevices.clear();
		this.mRobotFinder.startDiscovery();	
		final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Searching for devices. Please wait", true);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				dialog.cancel();
				mRobotFinder.stopDiscovery();
				mFindRobotFragment.promptToConnectToDiscoveredDevices();
			}
		}, 1000 * 10);
    }
    
}