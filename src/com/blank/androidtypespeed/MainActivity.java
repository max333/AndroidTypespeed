package com.blank.androidtypespeed;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 */
public class MainActivity extends Activity {
	private TypespeedView typespeedView;
	private SimulationClock simulationClock;
	private SimulationClock.Runner runner;
	private Game game;
	private EditText userInput;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int timeIntervalAnimationMs = 30;
		initializeRunner();
		simulationClock = new SimulationClock(timeIntervalAnimationMs, runner);
		typespeedView = (TypespeedView) findViewById(R.id.typespeed_view);

		game = new Game();

		Button goButton = (Button) findViewById(R.id.go_button);
		goButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				simulationClock.initializeClock();
				simulationClock.runAndScheduleNextRun();
			};
		});
		
		userInput = (EditText) findViewById(R.id.user_input);
	}

	/**
	 * 
	 */
	private void initializeRunner() {
		// Careful: this does not run on the UI thread.
		runner = new SimulationClock.Runner() {

			@Override
			public boolean run(float dt) {
				List<UserKeyEvent> userInput = fetchUserInputOnUIThread();
				game.update(dt, userInput);
				updateTypespeedView(dt);
				return true;
			}
		};
	}


	/**
	 * 
	 * @return
	 */
	private List<UserKeyEvent> fetchUserInputOnUIThread() {
		// TODO use a thread-safe list.  Must empty the list and clear it atomically.  Use a lock with normal list?
		// Or just a volatile list, creating a new one each time.
		return null;
	}

	/**
	 * 
	 */
	private void updateTypespeedView(float dt) {
		typespeedView.setWordsWithCoordinates(game.getWords());
		typespeedView.postInvalidate(); // must be called from UI thread
	}
	
	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		simulationClock.initializeClock();
		simulationClock.runAndScheduleNextRun();
	}
	
	/**
	 * 
	 */
	@Override
	protected void onPause() {
		simulationClock.stop();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
