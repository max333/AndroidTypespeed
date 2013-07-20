package com.blank.androidtypespeed;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * 
 */
public class Game {
	private static final String TAG = "Game";
	// TODO convert to dp
	private float velocity = 5.0f; // pixels / second
	private final List<WordWithCoordinates> words = new ArrayList<WordWithCoordinates>();

	/**
	 * 
	 */
	public Game() {
		words.add(new WordWithCoordinates("hello", 0f, 0f));
		words.add(new WordWithCoordinates("world", 0f, 30f));
	}

	/**
	 * @param submittedWords 
	 * @param userInput 
	 * 
	 */
	public void update(float dt, List<CharSequence> submittedWords, List<UserKeyEvent> userInput) {
		for (WordWithCoordinates word : words) {
			word.setX(word.getX() + velocity * dt);
		}
		// Check if word(s) reached the end.  Might terminate game.
		// Will modify StatusView.
		
		
		// Check if user got word(s) right.
		if (userInput != null && !userInput.isEmpty())
			Log.d(TAG, "Game.update got chars: " + userInput);


		if (submittedWords != null && !submittedWords.isEmpty())
			Log.d(TAG, "User submitted words: " + submittedWords);


		
		// Check if new words need to be added.
	}

	/**
	 * 
	 */
	// TODO return copy? Guava.
	public List<WordWithCoordinates> getWords() {
		return words;
	}

}
