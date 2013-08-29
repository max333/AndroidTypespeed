package com.blank.androidtypespeed.game;

/**
 * A time-dependent process for a value which increases logarithmicly over time.
 * As opposed to a linear time process, the rate of increase diminishes over time.
 */
public class LogarithmicTimeProcess {
	private float initialPace; // char / second
	private float paceMultiplierAfterOneMinute;
	private float a;
	private float b;

	/**
	 * @param initialPace the pace in characters per second at time 0.
	 * @param paceMultiplierAfterOneMinute 1.5 would mean the pace is 1.5 * initialPace after a minute.
	 */
	public LogarithmicTimeProcess(float initialPace, float paceMultiplierAfterOneMinute) {
		this.initialPace = initialPace;
		this.paceMultiplierAfterOneMinute = paceMultiplierAfterOneMinute;

		b = (float) Math.exp(initialPace);
		a = (float) (Math.exp(initialPace * paceMultiplierAfterOneMinute) - b) / 60.f;
	}

	/**
	 * Integral over t of ln(a * t + b) = ((a * t + b) ln(a * t + b) - a * t) / a
	 */
	public float integralOfPaceAtT(float t) {
		float atpb = a * t + b;
		return (atpb * (float) Math.log(atpb) - a * t) / a;
	}
}
