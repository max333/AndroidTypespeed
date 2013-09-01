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

	// hashCode and equals ignore the coordinates since the words move.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(startTime);
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordWithCoordinates other = (WordWithCoordinates) obj;
		if (Float.floatToIntBits(startTime) != Float.floatToIntBits(other.startTime))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WordWithCoordinates [word=" + word + ", x=" + x + ", y=" + y + "]";
	}
}
