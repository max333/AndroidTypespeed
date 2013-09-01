package com.blank.androidtypespeed.game;

/**
 * Gives the length ratio of a word as the ratio of its length to the screen length.
 */
public interface WordLengthMeasurer {

	public float getLengthRatio(WordWithCoordinates word);
}
