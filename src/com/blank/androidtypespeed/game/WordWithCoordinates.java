package com.blank.androidtypespeed.game;

/**
 * X and y are between 0 and 1.
 */
public class WordWithCoordinates {
	private String word;
	private float x;
	private float y;
	private float startTime;

	/**
	 * 
	 */
	public WordWithCoordinates(String word, float x, float y, float startTime) {
		super();
		this.word = word;
		this.x = x;
		this.y = y;
		this.startTime = startTime;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getStartTime() {
		return startTime;
	}

	@Override
	public String toString() {
		return "WordWithCoordinates [word=" + word + ", x=" + x + ", y=" + y + "]";
	}
}
