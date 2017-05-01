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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Report DialogFragment for Likert-type question.
 * @author Carl Demetria
 */

public class ReportLikertQuestionDialog extends DialogFragment{
    private String mReportQuestion = "";
    private String mReportTimeRange = "";
    private ArrayList<Integer> mRespondentForms = new ArrayList<Integer>();
    private SurveyDAO mDatasource;
    private int mCompletedForms = 0;
    private ArrayList<String> mQuestionIDAndType = new ArrayList<String>();
    private int mCountExcellent = 0;
    private int mCountVeryGood = 0;
    private int mCountGood = 0;
    private int mCountFair = 0;
    private int mCountPoor = 0;
    private int mCountNA = 0;
    private int mCountTotal = 0;


    public ReportLikertQuestionDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_likertquestion_result,
                new LinearLayout(getActivity()), false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Bundle reportQuestionData = getArguments();
        mReportQuestion = reportQuestionData.getString("Question");
        mReportTimeRange = reportQuestionData.getString("Time_Range");
        mRespondentForms = reportQuestionData.getIntegerArrayList("Respondent_Forms");
        mCompletedForms = reportQuestionData.getInt("Completed_Forms");
        mQuestionIDAndType = reportQuestionData.getStringArrayList("Question_Details");

        final TextView rptDialogQuestion = (TextView)dialogView.findViewById(R.id.rptDialogQuestion);
        final TextView rptDialogDateRange = (TextView)dialogView.findViewById(R.id.rptDialogDateRange);
        final TextView rptDialogRespondents = (TextView)dialogView.findViewById(R.id.rptDialogRespondents);
        final TextView rptDialogCompletedForms = (TextView)dialogView.findViewById(R.id.rptDialogCompletedForms);
        final TextView tvCountExcellent = (TextView)dialogView.findViewById(R.id.tvCountExcellent);
        final TextView tvCountVeryGood = (TextView)dialogView.findViewById(R.id.tvCountVeryGood);
        final TextView tvCountGood = (TextView)dialogView.findViewById(R.id.tvCountGood);
        final TextView tvCountFair = (TextView)dialogView.findViewById(R.id.tvCountFair);
        final TextView tvCountPoor = (TextView)dialogView.findViewById(R.id.tvCountPoor);
        final TextView tvCountNA = (TextView)dialogView.findViewById(R.id.tvCountNA);
        final TextView tvCountTotal = (TextView)dialogView.findViewById(R.id.tvCountTotal);
        final TextView tvPctExcellent = (TextView)dialogView.findViewById(R.id.tvPctExcellent);
        final TextView tvPctVeryGood = (TextView)dialogView.findViewById(R.id.tvPctVeryGood);
        final TextView tvPctGood = (TextView)dialogView.findViewById(R.id.tvPctGood);
        final TextView tvPctFair = (TextView)dialogView.findViewById(R.id.tvPctFair);
        final TextView tvPctPoor = (TextView)dialogView.findViewById(R.id.tvPctPoor);
        final TextView tvPctNA = (TextView)dialogView.findViewById(R.id.tvPctNA);
        final TextView tvPctTotal = (TextView)dialogView.findViewById(R.id.tvPctTotal);

        mCountExcellent = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 5);
        mCountVeryGood = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 4);
        mCountGood = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 3);
        mCountFair = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 2);
        mCountPoor = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 1);
        mCountNA = mDatasource.getAnswerCountsForValue(mRespondentForms,
                Integer.valueOf(mQuestionIDAndType.get(0)), 0);

        mCountTotal = mCountExcellent + mCountVeryGood + mCountGood + mCountFair +
                mCountPoor + mCountNA;

        if(mCountTotal < mRespondentForms.size()){
            int difference = mRespondentForms.size() - mCountTotal;
            mCountNA += difference;
            mCountTotal += difference;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        rptDialogQuestion.setText(mReportQuestion);
        rptDialogDateRange.setText(mReportTimeRange + " (" +
                mDatasource.returnDateRange(mReportTimeRange) + ")");
        rptDialogRespondents.setText(Integer.toString(mRespondentForms.size()));
        rptDialogCompletedForms.setText(Integer.toString(mCompletedForms));
        tvCountExcellent.setText(Integer.toString(mCountExcellent));
        tvCountVeryGood.setText(Integer.toString(mCountVeryGood));
        tvCountGood.setText(Integer.toString(mCountGood));
        tvCountFair.setText(Integer.toString(mCountFair));
        tvCountPoor.setText(Integer.toString(mCountPoor));
        tvCountNA.setText(Integer.toString(mCountNA));
        tvCountTotal.setText(Integer.toString(mCountTotal));
        tvPctExcellent.setText(Float.toString((mCountExcellent * 100.0f)/mCountTotal) + "%");
        tvPctVeryGood.setText(Float.toString((mCountVeryGood * 100.0f)/mCountTotal) + "%");
        tvPctGood.setText(Float.toString((mCountGood * 100.0f)/mCountTotal) + "%");
        tvPctFair.setText(Float.toString((mCountFair * 100.0f)/mCountTotal) + "%");
        tvPctPoor.setText(Float.toString((mCountPoor * 100.0f)/mCountTotal) + "%");
        tvPctNA.setText(Float.toString((mCountNA * 100.0f)/mCountTotal) + "%");
        tvPctTotal.setText(Float.toString((mCountTotal * 100.0f)/mCountTotal) + "%");


        builder.setView(dialogView).setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // exit dialog
            }
        });

        return builder.create();
    }
}
