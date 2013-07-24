package com.blank.androidtypespeed;

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
		private float pace; // char / second
		// 1.5 would mean the pace is 50% faster after a minute.
		private float paceMultiplierAfterOneMinute;
		private float a;
		private float b;
		private float integralAtTZero;

		/**
		 * 
		 */
		public Logarithm(Iterator<String> randomWords, float pace, float paceMultiplierAfterOneMinute) {
			super();
			nCharactersGeneratedSoFar = 0;
			this.randomWordsIterator = randomWords;
			nextWord = this.randomWordsIterator.next();
			this.pace = pace;
			this.paceMultiplierAfterOneMinute = paceMultiplierAfterOneMinute;

			b = (float) Math.exp(pace);
			a = (float) (Math.exp(pace * paceMultiplierAfterOneMinute) - b) / 60.f;
			integralAtTZero = integralOfPaceAtT(0f);
		}

		/**
		 * Integral of ln(ax + b) = ((ax + b) ln(ax + b) - ax) / a
		 */
		private float integralOfPaceAtT(float t) {
			float atpb = a * t + b;
			return (atpb * (float) Math.log(atpb) - a * t) / a;
		}

		/**
		 * 
		 */
		private float integralFromTZero(float t) {
			return integralOfPaceAtT(t) - integralAtTZero;
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
