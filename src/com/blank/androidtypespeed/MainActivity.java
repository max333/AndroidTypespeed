package com.blank.androidtypespeed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 */
public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	private TypespeedView typespeedView;
	private SimulationClock simulationClock;
	private SimulationClock.Runner runner;
	private Game game;
	private EditText userInput;
	private BlockingQueue<UserKeyEvent> userInputEventsQueue = new LinkedBlockingQueue<UserKeyEvent>();
	private TextWatcher userEditListener;

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
		userInput.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // TODO could put in xml
																			// instead?
		initUserEditListener();
		userInput.addTextChangedListener(userEditListener);
	}

	/**
	 * 
	 */
	private void initUserEditListener() {
		userEditListener = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence string, int start, int before, int count) {
				for (int index = start; index < (start + count); index++) {
					char cchar = string.charAt(index);
					Log.d(TAG, "adding to queue: " + Character.toString(cchar));
					UserKeyEvent userKeyEvent = new UserKeyEvent(null, cchar);
					userInputEventsQueue.offer(userKeyEvent);
					Log.d(TAG, "adding to queue done.");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
	}

	/**
	 * 
	 */
	private void initializeRunner() {
		// Careful: this does not run on the UI thread.
		runner = new SimulationClock.Runner() {

			@Override
			public boolean run(float dt) {
				List<UserKeyEvent> userInput = new ArrayList<UserKeyEvent>();
				while (!userInputEventsQueue.isEmpty()) {
					UserKeyEvent temp = userInputEventsQueue.poll();
					Log.d(TAG, "polling from queue: one element: " + Character.toString(temp.getKey()));
					userInput.add(temp);
				}
				game.update(dt, userInput);
				updateTypespeedView(dt);
				return true;
			}
		};
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
