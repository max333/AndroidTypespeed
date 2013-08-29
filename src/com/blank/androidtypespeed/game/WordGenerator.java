package com.blank.androidtypespeed.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.util.Log;

/**
 * Generates words over time.
 */
public interface WordGenerator {
	public static final String TAG = "WordGenerator";

	/**
	 * 
	 */
	public List<String> generateWordsIfNeeded(float time);

	/**
	 * Pace(t) = ln(a * t + b).
	 * So b = exp(pace) and a = (exp(pace * paceMultiplierAfterOneMinute) - b)
	 */
	public static class Logarithm implements WordGenerator {
		private Iterator<String> randomWordsIterator;
		private String nextWord;
		private int nCharactersGeneratedSoFar;
		private float integralAtTZero;
		private LogarithmicTimeProcess logarithmTimeProcess;

		/**
		 * 
		 */
		public Logarithm(Iterator<String> randomWords, float pace, float paceMultiplierAfterOneMinute) {
			super();
			logarithmTimeProcess = new LogarithmicTimeProcess(pace, paceMultiplierAfterOneMinute);
			nCharactersGeneratedSoFar = 0;
			this.randomWordsIterator = randomWords;
			nextWord = this.randomWordsIterator.next();
			integralAtTZero = logarithmTimeProcess.integralOfPaceAtT(0f);
		}

		

		/**
		 * 
		 */
		private float integralFromTZero(float t) {
			return logarithmTimeProcess.integralOfPaceAtT(t) - integralAtTZero;
		}

		/**
		 * 
		 */
		@Override
		public List<String> generateWordsIfNeeded(float time) {
			List<String> generatedWords = new ArrayList<String>();
			{
				int nCharactersToGenerate = (int) integralFromTZero(time) - nCharactersGeneratedSoFar;
				while (nextWord.length() <= nCharactersToGenerate) {
					nCharactersToGenerate -= nextWord.length();
					nCharactersGeneratedSoFar += nextWord.length();

					generatedWords.add(nextWord);
					
					nextWord = randomWordsIterator.next();
				}
			}
			return generatedWords;
		}
	}
}
