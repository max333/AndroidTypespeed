package com.blank.androidtypespeed;

/**
 * X and y are between 0 and 1.
 */
public class WordWithCoordinates {
	private String word;
	private float x;
	private float y;

	/**
	 * 
	 */
	public WordWithCoordinates(String word, float x, float y) {
		super();
		this.word = word;
		this.x = x;
		this.y = y;
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

	@Override
	public String toString() {
		return "WordWithCoordinates [word=" + word + ", x=" + x + ", y=" + y + "]";
	}
}
