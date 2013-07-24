package com.blank.androidtypespeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * 
 */
public class Game {
	private static final String TAG = "Game";
	private int maxNumFailedWords = 10;
	private WordGenerator wordGenerator;
	private ScrollingSpeed scrollingSpeed;
	private Multimap<String, WordWithCoordinates> words = HashMultimap.create();
	private List<String> successfulWords;
	private List<String> erroneousWords;
	private List<String> reachedOutOfBoundsWords;
	private int counterOutOfBoundsWords;
	private float totalElapsedTime;
	private WordReachedEndListener wordReachEndListener;
	private GameOverListener gameOverListener;
	private Random randomGenerator = new Random(829347);

	/**
	 * To be called when a word goes out of bound.
	 */
	public interface WordReachedEndListener {
		public void onWordReachedEnd(int numberOfWordsToHaveReachedEnd);
	}

	/**
	 * To be called when the game is over.
	 */
	public interface GameOverListener {
		public void onGameOver(GameStatistics gameStatistics); // TODO pass the whole Game object back?
	}
	
	/**
	 * 
	 */
	public class GameStatistics {
		private final float totalCPS;
		private final float validCPS;
		
		public GameStatistics() {
			int successfulChars = 0;
			{
				for (String successfulWord : successfulWords) {
					successfulChars += successfulWord.length();
				}
			}
			validCPS = (float) successfulChars / totalElapsedTime;
			
			int allSubmittedChars = successfulChars;
			{
				for (String erroneousWord : erroneousWords) {
					allSubmittedChars += erroneousWord.length();
				}
			}
			totalCPS = (float) allSubmittedChars / totalElapsedTime;
		}

		public float getTotalCPS() {
			return totalCPS;
		}

		public float getValidCPS() {
			return validCPS;
		}
	}

	/**
	 * 
	 */
	public Game(WordGenerator wordGenerator, ScrollingSpeed scrollingSpeed, WordReachedEndListener wordReachEndListener, GameOverListener gameOverListener) {
		this.wordGenerator = wordGenerator;
		this.scrollingSpeed = scrollingSpeed;
		this.wordReachEndListener = wordReachEndListener;
		this.gameOverListener = gameOverListener;
		initialize();
	}

	/**
	 * 
	 */
	private void initialize() {
		successfulWords = new ArrayList<String>();
		erroneousWords = new ArrayList<String>();
		reachedOutOfBoundsWords = new ArrayList<String>();
		counterOutOfBoundsWords = 0;
		totalElapsedTime = 0f;
	}

	/**
	 * 
	 */
	public void update(float dt, List<String> submittedWords, List<UserKeyEvent> userInput) {
		totalElapsedTime += dt;

		for (WordWithCoordinates word : words.values()) {
			float distanceTraveled = scrollingSpeed.distanceTraveled(word.getStartTime(), totalElapsedTime);
			word.setX(distanceTraveled);
		}

		updateWordsOutOfBounds();
		updateSubmittedWords(submittedWords);
		updateGenerateNewWords();
	}

	/**
	 * Check if word(s) reached the end. Might terminate game.
	 * Will modify StatusView.
	 */
	private void updateWordsOutOfBounds() {
		List<WordWithCoordinates> wordsOutOfBounds = new ArrayList<WordWithCoordinates>();
		{
			for (WordWithCoordinates word : words.values()) {
				boolean isOutOfBounds = checkIfWordOutOfBound(word);
				if (isOutOfBounds) {
					wordsOutOfBounds.add(word);
				}
			}
		}
		for (WordWithCoordinates wordOutOfBound : wordsOutOfBounds) {
			words.remove(wordOutOfBound.getWord(), wordOutOfBound);
			counterOutOfBoundsWords++;
			if (counterOutOfBoundsWords >= maxNumFailedWords)
				gameOver();
		}
		if (!wordsOutOfBounds.isEmpty()) {
			for (WordWithCoordinates wordOutOfBounds : wordsOutOfBounds) {
				reachedOutOfBoundsWords.add(wordOutOfBounds.getWord());
			}
			wordReachEndListener.onWordReachedEnd(reachedOutOfBoundsWords.size());
		}
	}

	/**
	 * Check if user got word(s) right.
	 * @param submittedWords 
	 */
	private void updateSubmittedWords(List<String> submittedWords) {

		// if (userInput != null && !userInput.isEmpty()) {
		//
		// }
		if (submittedWords != null && !submittedWords.isEmpty()) {
			for (String submittedWord : submittedWords) {
				Log.d(TAG, "processing submitted word: >>" + submittedWord + "<<");
				// TODO CharSequence conversion to String create subtle bugs if done wrong. Should
				// go all String.
				Collection<WordWithCoordinates> matchedWords = words.get(submittedWord.toString()); // returns
																									// empty
																									// on
																									// fail.
				if (matchedWords.isEmpty()) {
					erroneousWords.add(submittedWord);
				} else {
					Log.d(TAG, "matched word: " + submittedWord);
					successfulWords.add(submittedWord);
					WordWithCoordinates wordToRemove = null;
					{
						for (WordWithCoordinates matchedWord : matchedWords) {
							if (wordToRemove == null) { // initialize
								wordToRemove = matchedWord;
							} else {
								if (matchedWord.getX() >= wordToRemove.getX()) {
									wordToRemove = matchedWord;
								}
							}
						}
					}
					boolean isSuccessRemove = words.remove(submittedWord.toString(), wordToRemove);
					if (!isSuccessRemove)
						Log.d(TAG, "FAIL: could not remove " + submittedWord + ": " + wordToRemove);
				}
			}
		}
	}

	/**
	 * Might add new words if it is required.
	 */
	private void updateGenerateNewWords() {
		List<String> generatedWords = wordGenerator.generateWordsIfNeeded(totalElapsedTime);
		for (String word : generatedWords) {
			float y = randomGenerator.nextFloat();
			words.put(word, new WordWithCoordinates(word, 0f, y, totalElapsedTime));
		}
	}

	/**
	 *
	 */
	private boolean checkIfWordOutOfBound(WordWithCoordinates word) {
		return (word.getX() >= 1.0f);
	}

	/**
	 * 
	 */
	private void gameOver() {
		GameStatistics gameStatistics = new GameStatistics();
		gameOverListener.onGameOver(gameStatistics);
	}

	/**
	 * @return an immutable copy.
	 */
	public Collection<WordWithCoordinates> getWords() {
		return new ImmutableList.Builder<WordWithCoordinates>().addAll(words.values()).build();
	}

	/**
	 * 
	 */
	public int getMaxNumFailedWords() {
		return maxNumFailedWords;
	}
	
	

}
