package com.blank.androidtypespeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 */
public class Game {
	private static final String TAG = "Game";
	private static final int MAX_FAILED_WORDS = 10;
	// TODO convert to dp
	private float velocity = 5.0f; // pixels / second
	private Multimap<CharSequence, WordWithCoordinates> words = HashMultimap.create();
	private List<CharSequence> successfulWords;
	private List<CharSequence> erroneousWords;
	private int counterFailedWords;

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
		counterFailedWords = 0;
		
		String word = "hello";
		words.put(word, new WordWithCoordinates(word, 0f, 0f));
		word = "world";
		words.put(word, new WordWithCoordinates(word, 0f, 30f));
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
			counterFailedWords++;
			if (counterFailedWords >= MAX_FAILED_WORDS)
				gameOver();
		}
		
		// Check if user got word(s) right.
		// if (userInput != null && !userInput.isEmpty()) {
		//
		// }
		if (submittedWords != null && !submittedWords.isEmpty()) {
			for (CharSequence submittedWord : submittedWords) {

			}
		}

		// Check if word(s) reached the end. Might terminate game.
		// Will modify StatusView.

		// Check if new words need to be added.
	}

	private boolean checkIfWordOutOfBound(WordWithCoordinates word) {
		// TODO Auto-generated method stub
		return false;
	}

	private void gameOver() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	// TODO return copy? Guava.
	public Collection<WordWithCoordinates> getWords() {
		return words.values();
	}

}
