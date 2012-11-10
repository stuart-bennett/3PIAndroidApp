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
	private RobotController mRobotController;
	
	// Handles bluetooth device discovery
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String action = intent.getAction();	
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			mDiscoveredBtDevices.add(device);
    			System.out.println(String.format("Found device: '%s' '%s'", device.getName(), device.getAddress()));
    			
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
    	this.mRobotController = new RobotController(this.mConnectionThread.getSocket());
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
        this.initialiseFragments();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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

	/**
	 * Responsible for sending communication to serial reader on the 3Pi
	 * @author Stu
	 *
	 */
	public class RobotController {
				
		private final BluetoothSocket mSocket;
		private OutputStream mOutStream = null;
		
		private final Map<String, byte[]> mCommandMap = new HashMap<String, byte[]>() {{
			put("START_LEFT_MOVING_FORWARD", new byte[] { (byte) 0xC1 });
			put("START_RIGHT_MOVING_FORWARD", new byte[] { (byte) 0xC5 });
			put("STOP", new byte[] { (byte) 0xBC });			
			put("CALIBRATE", new byte[] { (byte) 0xBA });
		}};
		
		public RobotController(BluetoothSocket socket) {
			this.mSocket = socket;
			try {
				this.mOutStream = socket.getOutputStream();				
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public void moveForward() {
			this.writeToOutputStream(this.mCommandMap.get("START_LEFT_MOVING_FORWARD"));
			this.writeToOutputStream(this.mCommandMap.get("START_RIGHT_MOVING_FORWARD"));		
		}
		
		public void stop() {
			this.writeToOutputStream(this.mCommandMap.get("STOP"));
		}
		
		public void calibrate() {
			this.writeToOutputStream(this.mCommandMap.get("CALIBRATE"));
		}
		
		private void writeToOutputStream(byte[] data) {
			try {
				this.mOutStream.write(data);
			} catch (IOException e) { e.printStackTrace(); }
		}
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
    	
    	UiActionResult startBluetoothResult = this.checkBluetoothAndPromptIfNotEnabled();
    	if (!startBluetoothResult.WasActionSuccessful()) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    		builder.setMessage(startBluetoothResult.getMessageToUser());
    		builder.show();
    		return;
    	}
    
    	this.promptToConnectToPreviouslyPairedDevices();
    }
    
    /**
     * Prompts the user to select a previously known device before initiating an 
     * potentially unnecessary, resource-expensive discovery process
     */
    private void promptToConnectToPreviouslyPairedDevices() {	
    	Set<BluetoothDevice> previouslyPaired = this.mRobotFinder.getPreviouslyPairedDevices();
    	if (previouslyPaired.size() > 0) {
        	this.mDiscoveredBtDevices = previouslyPaired;    		
    		DialogFragment dialog = new SelectBluetoothDeviceDialogFragment(previouslyPaired);
    		dialog.show(getSupportFragmentManager(), "SelectBluetoothDeviceDialogFragment");
    	}
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
    		e.printStackTrace();
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBluetoothIntent, 1);
			return new UiActionResult(false, "Bluetooth could not be enabled on your device. Please try manually enabling Bluetooth and retrying");
    	}
    	
    	return new UiActionResult(true);
    }
    
    /**
     * Called when a Bluetooth device selection dialog is used to specify a device
     */
    public void onDialogDeviceSelected(String nameOfSelectedDevice) {    	
    	if (nameOfSelectedDevice.equalsIgnoreCase(getString(R.string.SelectPreviousBtDeviceScanForNewDevicesOption))) {
    		this.doDiscoveryOfDevices();
    	}
    	else {
    		BluetoothDevice device = null;
    		for (BluetoothDevice d : this.mDiscoveredBtDevices) {
    			if (
    				d.getName() == null && nameOfSelectedDevice.equals(getString(R.string.SelectBtDeviceWhenFriendlyNameIsNull)) ||
    				d.getName().equals(nameOfSelectedDevice)) {	
    				device = d;
    			}
    		}
    		
    		if (device == null) {
    			throw new RuntimeException(String.format("Could not find device '%s'", nameOfSelectedDevice));
    		}
    		
    		this.doConnectToDevice(device);
    	}
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
				DialogFragment selectBluetoothDeviceDialogFragment = new SelectBluetoothDeviceDialogFragment(mDiscoveredBtDevices);
				selectBluetoothDeviceDialogFragment.show(getSupportFragmentManager(), "SelectDiscoveredDeviceDialog");
			}
		}, 1000 * 10);
    }
    
}