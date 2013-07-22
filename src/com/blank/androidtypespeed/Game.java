package com.blank.androidtypespeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 */
public class Game {
	private static final String TAG = "Game";
	private static final int MAX_FAILED_WORDS = 10;
	private float velocity = 0.02f; // percent of screen / second
	private Multimap<String, WordWithCoordinates> words = HashMultimap.create();
	private List<CharSequence> successfulWords;
	private List<CharSequence> erroneousWords;
	private int counterOutOfBoundsWords;

	/**
	 * 
	 */
	public Game() {
		init();
	}

	/**
	 * 
	 */
	private void init() {
		successfulWords = new ArrayList<CharSequence>();
		erroneousWords = new ArrayList<CharSequence>();
		counterOutOfBoundsWords = 0;

		String word = "hello";
		words.put(word, new WordWithCoordinates(word, 0f, 0f));
		word = "world";
		words.put(word, new WordWithCoordinates(word, 0f, 0.3f));
	}

	/**
	 * @param submittedWords 
	 * @param userInput 
	 * 
	 */
	public void update(float dt, List<CharSequence> submittedWords, List<UserKeyEvent> userInput) {
		List<WordWithCoordinates> wordsOutOfBounds = new ArrayList<WordWithCoordinates>();
		{
			for (WordWithCoordinates word : words.values()) {
				word.setX(word.getX() + velocity * dt);
				boolean isOutOfBounds = checkIfWordOutOfBound(word);
				if (isOutOfBounds) {
					wordsOutOfBounds.add(word);
				}
			}
		}
		for (WordWithCoordinates wordOutOfBound : wordsOutOfBounds) {
			words.remove(wordOutOfBound.getWord(), wordOutOfBound);
			counterOutOfBoundsWords++;
			if (counterOutOfBoundsWords >= MAX_FAILED_WORDS)
				gameOver();
		}

		// Check if user got word(s) right.
		// if (userInput != null && !userInput.isEmpty()) {
		//
		// }
		if (submittedWords != null && !submittedWords.isEmpty()) {
			for (CharSequence submittedWord : submittedWords) {
				Log.d(TAG, "processing submitted word: >>" + submittedWord + "<<");
				// TODO CharSequence conversion to String create subtle bugs if done wrong.  Should go all String.
				Collection<WordWithCoordinates> matchedWords = words.get(submittedWord.toString()); // returns empty on fail.
				Collection<WordWithCoordinates> dummy = words.get("hello");
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

		// Check if word(s) reached the end. Might terminate game.
		// Will modify StatusView.

		// Check if new words need to be added.
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
		// TODO call registered callback from MainActivity to stop clock and display result.
		Log.d(TAG, "Game Over: not implemented yet.");
	}

	/**
	 * 
	 */
	// TODO return copy? Guava.
	public Collection<WordWithCoordinates> getWords() {
		return words.values();
	}

}
