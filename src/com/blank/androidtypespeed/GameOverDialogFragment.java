package com.blank.androidtypespeed;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;

import com.blank.androidtypespeed.game.Game;
import com.blank.androidtypespeed.game.Game.GameStatistics;

/**
 * 
 */
public class GameOverDialogFragment extends DialogFragment {
	private final static String TAG = "GameOverDialogFragment";
	private Game.GameStatistics gameStatistics;
	private long delayInactivateButtonsMS = 2000;

	/**
	 * 
	 */
	// Should be the constructor, but it cannot since it is a Fragment.
	public void initialize(GameStatistics gameStatistics) {
		this.gameStatistics = gameStatistics;
	}

	/**
	 * 
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		StringBuilder sbMessage = new StringBuilder();
		sbMessage.append(getResources().getString(R.string.game_over));
		sbMessage.append("\n");
		sbMessage.append("valid cps: " + String.format("%.2f", gameStatistics.getValidCPS()));
		sbMessage.append("\n");
		sbMessage.append("total cps: " + String.format("%.2f", gameStatistics.getTotalCPS()));

		builder.setMessage(sbMessage.toString()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog dialog = builder.create();
		return dialog;
	}

	/**
	 * 
	 */
	@Override
	public void onStart() {
		super.onStart();

		AlertDialog dialog = (AlertDialog) getDialog();
		Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		if (positiveButton != null) {
			positiveButton.setEnabled(false);
		} else {
			Log.e(TAG, "did not find the positive button in the alert dialog. 1.");
		}
		
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(new Runnable() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog dialog = (AlertDialog) getDialog();
						Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
						if (positiveButton != null) {
							positiveButton.setEnabled(true);
							Log.d(TAG, "did find the positive button in the alert dialog. 2.");
						} else {
							Log.e(TAG, "did not find the positive button in the alert dialog. 2.");
						}
					}
				});
			}
		}, delayInactivateButtonsMS, TimeUnit.MILLISECONDS);
	}

}
