package com.blank.androidtypespeed;

import com.blank.androidtypespeed.game.Game.GameStatistics;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Displays the results of the game.
 */
public class GameOverActivity extends Activity {
	public final static String GAME_ID = "GAME_ID";
	private WebView webView;
	private GameStatistics gameStatistics;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);
		int defaultValue = -1;
		int gameId = getIntent().getIntExtra(GAME_ID, defaultValue);
		
		// fetch from DB.
		
		webView = (WebView) findViewById(R.id.game_over_webview);
		
		String htmlText = getText();
		webView.loadData(htmlText, "text/html", null);
	}
	
	/**
	 * 
	 */
	private String getText() {
		StringBuilder sbMessage = new StringBuilder();
		sbMessage.append(getResources().getString(R.string.game_over));
		sbMessage.append("<br />");
		sbMessage.append("valid cps: " + String.format("%.2f", gameStatistics.getValidCPS()));
		sbMessage.append("<br />");
		sbMessage.append("total cps: " + String.format("%.2f", gameStatistics.getTotalCPS()));
		return sbMessage.toString();
	}

}
