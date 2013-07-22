package com.blank.androidtypespeed;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.SystemClock;
import android.util.Log;

/**
 * Runs some Runner activity at periodic intervals.  If the Runner does not finish
 * within its allocated time interval, the next run will happen only after it finishes.
 * 
 * Caution: the Runner runs on some timer threads, not the UI thread.
 * The timer thread might change from one run to the next.
 */
public class SimulationClock {
	private static final String TAG = "SimulationClock";
	private volatile long timeIntervalAnimationMs;
	private volatile long animationTimeMs;
	private volatile long animationTimePreviousMs;
	// volatile since this might be modified from other threads.
	private volatile boolean isStopped;
	private Runner runner;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private volatile ScheduledFuture<?> currentlyScheduledTask;

	/**
	 * Caution: the Runner runs on some timer thread, not the UI thread.
	 */
	public interface Runner {
		/**
		 * 
		 * @param dt the time elapsed since the last call.
		 * @return false if it fails, which will stop the clock.
		 */
		public boolean run(float dt);
	}

	/**
	 * 
	 */
	public SimulationClock(long timeIntervalAnimationMs, Runner runner) {
		super();
		this.timeIntervalAnimationMs = timeIntervalAnimationMs;
		this.runner = runner;

		initializeClock();
	}

	/**
	 * Can be called from many threads. 
	 */
	public void stop() {
		isStopped = true;
		if (currentlyScheduledTask != null)
			currentlyScheduledTask.cancel(true);
	}

	/**
	 * 
	 */
	public void initializeClock() {
		isStopped = false;
		animationTimeMs = SystemClock.elapsedRealtime();
		animationTimePreviousMs = animationTimeMs;
	}

	/**
	 * Use getDtForLastMark() to get the result.
	 */
	public void markTimeStep() {
		animationTimePreviousMs = animationTimeMs;
		animationTimeMs = SystemClock.elapsedRealtime();
	}

	/**
	 * 
	 */
	public long getDTForLastMark() {
		return (animationTimeMs - animationTimePreviousMs);
	}

	/**
	 * 
	 */
	public float getDTForLastMarkInSeconds() {
		return getDTForLastMark() / 1000.f;
	}

	/**
	 * @return the target time interval.
	 */
	public long getTimeIntervalAnimationMs() {
		return timeIntervalAnimationMs;
	}

	/**
	 * 
	 */
	public void setTimeIntervalAnimationMs(long timeIntervalAnimationMs) {
		this.timeIntervalAnimationMs = timeIntervalAnimationMs;
	}

	/**
	 * @return true if the dt is greater than half the timeIntervalAnimationMs.
	 *         This can be useful to avoid generating too many frames.
	 *         // TODO was deactivate; see run()
	 */
	public boolean wasTimeStepLongEnoughForLastMark() {
		long dt = getDTForLastMark();
		return (dt >= timeIntervalAnimationMs / 2L);
	}

	/**
	 * Computes the time interval since it was last called and runs the Runner.
	 * Calls itself recursively, with a delay.
	 */
	public void runAndScheduleNextRun() {
		try {
		if (!isStopped) {
			//Log.i(TAG, "dt = " + getDTForLastMark());
			// Log.i("SimulationClock", "target dt = " + timeIntervalAnimationMs);
			markTimeStep();

			// TODO could reactivate this, but need major modifications because
			// it is not setting the next animation as it is.
			// if (!wasTimeStepLongEnoughForLastMark()) {
			// Log.i("SimulationClock", "dt was too short... skipping");
			// return;
			// }
			float dtElapsed = getDTForLastMarkInSeconds();

			boolean runSuccess = runner.run(dtElapsed);

			//Log.d(TAG, "just finished the runner");
			
			if (runSuccess) {
				long currentTime = SystemClock.elapsedRealtime();
				long timeToNextRunMS = timeIntervalAnimationMs - (currentTime - animationTimeMs);
				{
					long minMS = 10;
					if (timeToNextRunMS < minMS)
						timeToNextRunMS = minMS;
				}
				// recursive call to itself, in the future.
//				Log.d(TAG, "Scheduling task in ms: " + timeToNextRunMS);
				currentlyScheduledTask = scheduler.schedule(runnableRunAndScheduleNextRun, timeToNextRunMS, TimeUnit.MILLISECONDS);
			} else {
				// something failed in the Runner, so we stop the clock.
				throw new IllegalStateException("Runner failed.");
			}
		} else {
			Log.i(TAG, "not running the simulation step because it was stopped.");
		}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private Runnable runnableRunAndScheduleNextRun = new Runnable() {

		@Override
		public void run() {
			runAndScheduleNextRun();
		}
	};

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "SimulationClock [timeIntervalAnimationMs=" + timeIntervalAnimationMs + ", animationTimeMs=" + animationTimeMs
				+ ", animationTimePreviousMs=" + animationTimePreviousMs + ", runner=" + runner + "]";
	}
}