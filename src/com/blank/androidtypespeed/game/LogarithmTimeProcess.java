package com.blank.androidtypespeed.game;

/**
 * 
 */
public class LogarithmTimeProcess {
	private float pace; // char / second
	// 1.5 would mean the pace is 50% faster after a minute.
	private float paceMultiplierAfterOneMinute;
	private float a;
	private float b;

	/**
	 * 
	 */
	public LogarithmTimeProcess(float pace, float paceMultiplierAfterOneMinute) {
		this.pace = pace;
		this.paceMultiplierAfterOneMinute = paceMultiplierAfterOneMinute;

		b = (float) Math.exp(pace);
		a = (float) (Math.exp(pace * paceMultiplierAfterOneMinute) - b) / 60.f;
	}

	/**
	 * Integral of ln(ax + b) = ((ax + b) ln(ax + b) - ax) / a
	 */
	public float integralOfPaceAtT(float t) {
		float atpb = a * t + b;
		return (atpb * (float) Math.log(atpb) - a * t) / a;
	}
}
