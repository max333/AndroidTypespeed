package com.blank.androidtypespeed;

import java.util.Collection;

import com.blank.androidtypespeed.game.WordWithCoordinates;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 */
public class TypespeedView extends View {
	private final static String TAG = "TypespeedView";
	private TextPaint textPaint;
	private Paint backgroundPaint;
	private Collection<WordWithCoordinates> words;

	/**
	 * 
	 */
	public TypespeedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint = new TextPaint();
		{
			textPaint.setColor(Color.BLACK);
			Typeface typeface = Typeface.create("Roboto", Typeface.NORMAL);
			textPaint.setTypeface(typeface);
			float densityDPI = getResources().getDisplayMetrics().density;
			float textSizeInPixels = 20.0f * densityDPI; // 20 pixels on 160dpi display
			textPaint.setTextSize(textSizeInPixels);
			textPaint.setAntiAlias(true);
			addHintingIfPossible();
		}
		backgroundPaint = new Paint();
		{
			backgroundPaint.setColor(Color.MAGENTA);
		}
	}

	/**
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void addHintingIfPossible() {
		if (Build.VERSION.SDK_INT >= 14)
			textPaint.setHinting(Paint.HINTING_ON);
	}

	/**
	 * This must not be the actual collection of words in the game engine, but a copy
	 * since it can otherwise lead to ConcurrentModificationException.
	 */
	public void setWordsWithCoordinates(Collection<WordWithCoordinates> words) {
		this.words = words;
	}

	/**
	 * 
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// TODO should set this rectangle once, in some appropriate lifecycle callback.\
		Rect canvasRectangle = canvas.getClipBounds();
		canvas.drawRect(canvasRectangle, backgroundPaint);
		float ascent = textPaint.ascent();
		// Log.d(TAG, "ascent = " + ascent);
		if (words != null) {
			for (WordWithCoordinates word : words) {
				canvas.drawText(word.getWord(), word.getX() * canvasRectangle.width(),
						-ascent + word.getY() * canvasRectangle.height(), textPaint);
			}
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d(TAG, "new size: " + w + " x " + h);
	}

	/**
	 * 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d(TAG, "measure: " + widthMeasureSpec + " x " + heightMeasureSpec);
	}
}
