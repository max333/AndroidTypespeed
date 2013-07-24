package com.blank.androidtypespeed;

/**
 * 
 */
public interface ScrollingSpeed {

	public float distanceTraveled(float startTime, float currentTime);
	
	/**
	 * 
	 */
	public static class ConstantVelocity implements ScrollingSpeed {
		private final float velocity;
		
		public ConstantVelocity(float velocity) {
			super();
			this.velocity = velocity;
		}



		@Override
		public float distanceTraveled(float startTime, float currentTime) {
			return (currentTime - startTime) * velocity;
		}
	}
	
	/**
	 * 
	 */
	public static class Logarithm implements ScrollingSpeed {
		private LogarithmTimeProcess logarithmTimeProcess;
		
		/**
		 * 
		 */
		public Logarithm(float pace, float paceMultiplierAfterOneMinute) {
			logarithmTimeProcess = new LogarithmTimeProcess(pace, paceMultiplierAfterOneMinute);
		}

		/**
		 * 
		 */
		@Override
		public float distanceTraveled(float startTime, float currentTime) {
			return logarithmTimeProcess.integralOfPaceAtT(currentTime) - logarithmTimeProcess.integralOfPaceAtT(startTime);
		}
		
	}
}
