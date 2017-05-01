package com.blogspot.carlshark.wvsumccss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Report DialogFragment for yes/no type question.
 * @author Carl Demetria
 */

public class ReportYesNoQuestionDialog extends DialogFragment {
    private String mReportQuestion = "";
    private String mReportTimeRange = "";
    private ArrayList<Integer> mRespondentForms = new ArrayList<Integer>();
    private SurveyDAO mDatasource;
    private int mCompletedForms = 0;
    private ArrayList<String> mQuestionIDAndType = new ArrayList<String>();
    private int mCountYes = 0;
    private int mCountNo = 0;
    private int mCountTotal = 0;


    public ReportYesNoQuestionDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_yesnoquestion_result,
                new LinearLayout(getActivity()), false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Bundle reportQuestionData = getArguments();
        mReportQuestion = reportQuestionData.getString("Question");
        mReportTimeRange = reportQuestionData.getString("Time_Range");
        mRespondentForms = reportQuestionData.getIntegerArrayList("Respondent_Forms");
        mCompletedForms = reportQuestionData.getInt("Completed_Forms");
        mQuestionIDAndType = reportQuestionData.getStringArrayList("Question_Details");

        final TextView rptDialogYNQuestion = (TextView)dialogView.findViewById(R.id.rptDialogYNQuestion);
        final TextView rptDialogYNDateRange = (TextView)dialogView.findViewById(R.id.rptDialogYNDateRange);
        final TextView rptDialogYNRespondents = (TextView)dialogView.findViewById(R.id.rptDialogYNRespondents);
        final TextView rptDialogYNCompletedForms = (TextView)dialogView.findViewById(R.id.rptDialogYNCompletedForms);
        final TextView tvCountYes = (TextView)dialogView.findViewById(R.id.tvCountYes);
        final TextView tvCountNo = (TextView)dialogView.findViewById(R.id.tvCountNo);
        final TextView tvCountYNTotal = (TextView)dialogView.findViewById(R.id.tvCountYNTotal);
        final TextView tvPctYes = (TextView)dialogView.findViewById(R.id.tvPctYes);
        final TextView tvPctNo = (TextView)dialogView.findViewById(R.id.tvPctNo);
        final TextView tvPctYNTotal = (TextView)dialogView.findViewById(R.id.tvPctYNTotal);

        mCountYes = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 1);
        mCountNo = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 0);

        mCountTotal = mCountYes + mCountNo;

        if(mCountTotal < mRespondentForms.size()){
            int difference = mRespondentForms.size() - mCountTotal;
            mCountNo += difference;
            mCountTotal += difference;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //all setting of texts here - might need to create methods to compute
        rptDialogYNQuestion.setText(mReportQuestion);
        rptDialogYNDateRange.setText(mReportTimeRange + " (" +
                mDatasource.returnDateRange(mReportTimeRange) + ")");
        rptDialogYNRespondents.setText(Integer.toString(mRespondentForms.size()));
        rptDialogYNCompletedForms.setText(Integer.toString(mCompletedForms));
        tvCountYes.setText(Integer.toString(mCountYes));
        tvCountNo.setText(Integer.toString(mCountNo));
        tvCountYNTotal.setText(Integer.toString(mCountTotal));
        tvPctYes.setText(Float.toString((mCountYes * 100.0f)/mCountTotal) + "%");
        tvPctNo.setText(Float.toString((mCountNo * 100.0f)/mCountTotal) + "%");
        tvPctYNTotal.setText(Float.toString((mCountTotal * 100.0f)/mCountTotal) + "%");

        builder.setView(dialogView).setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // exit dialog
            }
        });

        return builder.create();
    }
}