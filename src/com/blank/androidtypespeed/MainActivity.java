package com.blank.androidtypespeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blank.androidtypespeed.game.Game;
import com.blank.androidtypespeed.game.RandomWordIterator;
import com.blank.androidtypespeed.game.ScrollingSpeed;
import com.blank.androidtypespeed.game.WordGenerator;
import com.blank.androidtypespeed.game.WordLengthMeasurer;
import com.blank.androidtypespeed.game.WordWithCoordinates;

/**
 * 
 * Note: in the manifest.xml, this activity is declared with android:windowSoftInputMode="adjustResize"
 * so the software keyboard won't just hide the TypespeedView, but resize it.
 */
public class MainActivity extends FragmentActivity {
	protected static final String TAG = "MainActivity";
	private TypespeedView typespeedView;
	private StatusView statusView;
	private SimulationClock simulationClock;
	private SimulationClock.Runner runner;
	private Game game;
	private EditText userInput;
	private BlockingQueue<UserKeyEvent> userInputEventsQueue = new LinkedBlockingQueue<UserKeyEvent>();
	private BlockingQueue<String> userSubmitWordEventsQueue = new LinkedBlockingQueue<String>();
	private TextWatcher userEditListener;
	private MediaPlayer errorMediaPlayer;

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int timeIntervalAnimationMs = 30;
		initializeWorkerThreadRunner();
		simulationClock = new SimulationClock(timeIntervalAnimationMs, runner);
		typespeedView = (TypespeedView) findViewById(R.id.typespeed_view);
		statusView = (StatusView) findViewById(R.id.statusView1);

		Game.GameOverListener gameOverListner = new Game.GameOverListener() {

			@Override
			public void onGameOver(final Game.GameStatistics gameStatistics) {
				simulationClock.stop();

				// need to run all this is some other thread.
				(new AsyncTask<Void, Void, Integer>() {

					@Override
					protected Integer doInBackground(Void... voidd) {
						return saveGameResultToDB();
					}

					@Override
					protected void onPostExecute(Integer gameId) {
						// TODO not sure if this really needs to run on the UI thread.
						Intent gameOverIntent = new Intent(getBaseContext(), GameOverActivity.class);
						gameOverIntent.putExtra(GameOverActivity.GAME_ID, gameId);
					}
				}).execute();

				// MainActivity.this.runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// GameOverDialogFragment gameOverDialogFragment = new GameOverDialogFragment();
				// gameOverDialogFragment.initialize(gameStatistics);
				// gameOverDialogFragment.show(getSupportFragmentManager(), "game_over_dialog");
				// Log.i(TAG, "Game Over");
				// }
				// });
			}
		};
		Game.WordReachedEndListener wordReachedEndListener = new Game.WordReachedEndListener() {

			@Override
			public void onWordReachedEnd(final int numberOfWordsToHaveReachedEnd) {
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						int numMistakes = Math.min(numberOfWordsToHaveReachedEnd, game.getMaxNumFailedWords());
						statusView.setText("mistakes: " + numMistakes);
						errorMediaPlayer.start();
					}
				});
			}
		};

		// TODO doesn't that belong more in the Game part??
		Iterator<String> randomWordsIterator = null;
		try {
			randomWordsIterator = new RandomWordIterator.Scowl10(getAssets());
		} catch (IOException e) {
			// TODO fail for real
			e.printStackTrace();
		}
		float pace = 2f; // char / second
		float paceMultiplierAfterOneMinute = 2.0f;
		WordGenerator wordGenerator = new WordGenerator.Logarithm(randomWordsIterator, pace, paceMultiplierAfterOneMinute);
		// ScrollingSpeed scrollingSpeed = new ScrollingSpeed.ConstantVelocity(0.02f);
		// ScrollingSpeed scrollingSpeed = new ScrollingSpeed.Logarithm(0.02f,
		// paceMultiplierAfterOneMinute);
		ScrollingSpeed scrollingSpeed = new ScrollingSpeed.Logarithm(0.2f, paceMultiplierAfterOneMinute);

		/**
		 * Does not always return the right length since it needs to compute the result asynchronously on the UI thread.
		 * But it should not really matter since the word length will most likely have been computed by the time
		 * the word reaches the end.  The length is set to 0 until it is really computed. 
		 * 
		 */
		// Warning: there can be problems if the measurer is not ready when the game starts.
		// TODO maybe the words which hit the wall should be a value returned by the view.
		// TODO does not delete the removed words from the hash map.
		WordLengthMeasurer wordLengthMeasurer = new WordLengthMeasurer() {
			private final static float DEFAULT_LENGTH_IF_NOT_COMPUTED = 0f;
			// WordWithCoordinates is OK as a hash key since its hashCode does not use the
			// (changing) coordinates.
			private ConcurrentHashMap<WordWithCoordinates, Float> mapWordToLength = new ConcurrentHashMap<WordWithCoordinates, Float>();

			/**
			 * From WordLengthMeasurer interface.
			 * @return the ratio of the word as drawn with the current font relative to the total width.
			 *         If it is not present in the hash map, returns 0, but the result will be computed later.
			 */
			@Override
			public float getLengthRatio(WordWithCoordinates word) {
				Float length = mapWordToLength.get(word);
				if (length != null) {
					return length;
				} else {
					computeLengthOnUIThreadAndSetInConcurrentHashMap(word);
					return DEFAULT_LENGTH_IF_NOT_COMPUTED;
				}
			}

			/**
			 * 
			 */
			private void computeLengthOnUIThreadAndSetInConcurrentHashMap(final WordWithCoordinates word) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						int width = typespeedView.getWidth();
						if (width != 0) { // might be 0 if typespeedView is not yet displayed.
							int wordWidth = typespeedView.computeTextLength(word.getWord());
							float lengthRatio = (wordWidth / (float) width);
							mapWordToLength.put(word,  lengthRatio);
						}
					}
				});
			}
		};

		game = new Game(wordGenerator, scrollingSpeed, wordLengthMeasurer, wordReachedEndListener, gameOverListner);

		Button goButton = (Button) findViewById(R.id.go_button);
		goButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				simulationClock.initializeClock();
				simulationClock.runAndScheduleNextRun();
			};
		});

		userInput = (EditText) findViewById(R.id.user_input);

		initUserEditListener();
		userInput.addTextChangedListener(userEditListener);

		// TODO should run this in some other thread. Maybe. Not quite wrong the way it is.
		errorMediaPlayer = MediaPlayer.create(this, R.raw.error_135125);
	}

	// "Enter", "Tab" or "Space"
	private final Pattern PATTERN_USER_SUBMIT = Pattern.compile("[\\n\\t ]+");

	/**
	 * 
	 */
	private void initUserEditListener() {
		userEditListener = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			/**
			 * Sends everything to the BlockingQueue, in a non-blocking way.
			 * If "\n" or "\t" or " " are the added values, everything before the last such symbol is 
			 * removed from the EditText.
			 */
			@Override
			public void onTextChanged(CharSequence string, int start, int before, int count) {
				// This loop only entered if text is added, not deleted.
				for (int index = start; index < (start + count); index++) {
					char cchar = string.charAt(index);
					Log.d(TAG, "adding to queue: " + Character.toString(cchar));
					UserKeyEvent userKeyEvent = new UserKeyEvent(null, cchar);
					userInputEventsQueue.offer(userKeyEvent);
					Log.d(TAG, "adding to queue done.");
				}
			}

			/**
			 * Each word followed by "Enter", "Tab" or "Space" is sent to the worker thread.
			 * They are also removed from the display area.  Characters following are untouched.
			 */
			@Override
			public void afterTextChanged(Editable s) {
				int previousValidRegionStart = 0;
				int previousValidRegionEnd = 0;
				int nextRegionStart = 0;
				if (s.length() != 0) {
					Matcher matcher = PATTERN_USER_SUBMIT.matcher(s);
					while (matcher.find()) {
						previousValidRegionEnd = matcher.start();
						nextRegionStart = matcher.end(); // The first char after the matched
															// pattern.
						if (previousValidRegionEnd >= 0) {
							CharSequence previousValidWord = s.subSequence(previousValidRegionStart, previousValidRegionEnd);
							if (previousValidWord != null && previousValidWord.length() != 0) {
								Log.d(TAG, "adding word: " + previousValidWord.toString());
								userSubmitWordEventsQueue.offer(previousValidWord.toString());
							}
						}
					}
				}
				if (nextRegionStart != 0) {
					s.delete(0, nextRegionStart);
				}
			}
		};
	}

	/**
	 * 
	 */
	private void initializeWorkerThreadRunner() {
		// Careful: this does not run on the UI thread.
		runner = new SimulationClock.Runner() {

			@Override
			public boolean run(float dt) {
				List<UserKeyEvent> userInput = new ArrayList<UserKeyEvent>();
				List<String> submittedWords = new ArrayList<String>();
				{
					while (!userInputEventsQueue.isEmpty()) {
						UserKeyEvent temp = userInputEventsQueue.poll();
						Log.d(TAG, "polling from queue: one element: " + Character.toString(temp.getKey()));
						userInput.add(temp);
					}
					while (!userSubmitWordEventsQueue.isEmpty()) {
						String submittedWord = userSubmitWordEventsQueue.poll();
						Log.d(TAG, "reading from BlockingQueue submitted word: " + submittedWord);
						submittedWords.add(submittedWord);
					}
				}
				game.update(dt, submittedWords, userInput);
				updateTypespeedViewOnUIThread(dt);
				return true;
			}
		};
	}

	/**
	 * 
	 */
	private void updateTypespeedViewOnUIThread(float dt) {
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

	/**
	 * 
	 */
	private Integer saveGameResultToDB() {
		// TODO Auto-generated method stub
		return null;
	}

}
