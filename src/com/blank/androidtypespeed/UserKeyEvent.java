package com.blank.androidtypespeed;

import java.util.Date;

/**
 * 
 */
public class UserKeyEvent {
	final private Date time; // TODO not Date
	final private char key;  // TOOD not Char

	public UserKeyEvent(Date time, char key) {
		super();
		this.time = time;
		this.key = key;
	}

	public Date getTime() {
		return time;
	}

	public char getKey() {
		return key;
	}

	@Override
	public String toString() {
		return Character.toString(key);
	}	
}
