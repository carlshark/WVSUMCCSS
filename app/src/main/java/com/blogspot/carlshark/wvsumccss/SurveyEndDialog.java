package com.blogspot.carlshark.wvsumccss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Last DialogFragment shown after all questions for a specific survey type have been shown.
 * It also changes the row identifying the accomplished survey by flagging it as completed,
 * once OK is clicked.
 * @author Carl Demetria
 */

public class SurveyEndDialog extends DialogFragment {
    private int mRowID = 0;
    private SurveyDAO mDatasource;

    public SurveyEndDialog(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Bundle endData = getArguments();
        mRowID = endData.getInt("Form_ID");
        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_surveyend,
                new LinearLayout(getActivity()), false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatasource.flagAsCompleted(mRowID);
            }
        });
        return builder.create();
    }

}
