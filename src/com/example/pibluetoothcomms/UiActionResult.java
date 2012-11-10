package com.example.pibluetoothcomms;

public class UiActionResult {
	
	private boolean wasActionSuccessful; 	
	private String messageToUser;
	
	public UiActionResult(boolean wasActionCompletedSuccessfully) {
		this.wasActionSuccessful = wasActionCompletedSuccessfully;
	}

	public UiActionResult(boolean wasActionCompletedSuccessfully, String messageToUser) {
		this(wasActionCompletedSuccessfully);
		this.messageToUser = messageToUser;
	}

	public boolean WasActionSuccessful() {
		return this.wasActionSuccessful;
	}
	
	public String getMessageToUser() {
		return this.messageToUser;
	}
}
