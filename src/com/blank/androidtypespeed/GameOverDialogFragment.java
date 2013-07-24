package com.blank.androidtypespeed;

import com.blank.androidtypespeed.Game.GameStatistics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 */
public class GameOverDialogFragment extends DialogFragment {
	private Game.GameStatistics gameStatistics;

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
        
        builder.setMessage(sbMessage.toString())
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });
        return builder.create();
    }
}
