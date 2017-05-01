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
 * Report DialogFragment for free-form comment type question.
 * @author Carl Demetria
 */

public class ReportCommentsQuestionDialog extends DialogFragment {
    private String mReportQuestion = "";
    private String mReportTimeRange = "";
    private ArrayList<Integer> mRespondentForms = new ArrayList<Integer>();
    private SurveyDAO mDatasource;
    private int mCompletedForms = 0;
    private ArrayList<String> mQuestionIDAndType = new ArrayList<String>();
    private String mComments = "";


    public ReportCommentsQuestionDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_commentsquestion_result,
                new LinearLayout(getActivity()), false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Bundle reportQuestionData = getArguments();
        mReportQuestion = reportQuestionData.getString("Question");
        mReportTimeRange = reportQuestionData.getString("Time_Range");
        mRespondentForms = reportQuestionData.getIntegerArrayList("Respondent_Forms");
        mCompletedForms = reportQuestionData.getInt("Completed_Forms");
        mQuestionIDAndType = reportQuestionData.getStringArrayList("Question_Details");

        final TextView rptDialogCQuestion = (TextView)dialogView.findViewById(R.id.rptDialogCQuestion);
        final TextView rptDialogCDateRange = (TextView)dialogView.findViewById(R.id.rptDialogCDateRange);
        final TextView rptDialogCRespondents = (TextView)dialogView.findViewById(R.id.rptDialogCRespondents);
        final TextView rptDialogCCompletedForms = (TextView)dialogView.findViewById(R.id.rptDialogCCompletedForms);
        final TextView rptCommentList = (TextView)dialogView.findViewById(R.id.rptCommentList);


        final TextView tvCountNo = (TextView)dialogView.findViewById(R.id.tvCountNo);
        final TextView tvCountYNTotal = (TextView)dialogView.findViewById(R.id.tvCountYNTotal);
        final TextView tvPctYes = (TextView)dialogView.findViewById(R.id.tvPctYes);
        final TextView tvPctNo = (TextView)dialogView.findViewById(R.id.tvPctNo);
        final TextView tvPctYNTotal = (TextView)dialogView.findViewById(R.id.tvPctYNTotal);

        mComments = mDatasource.getComments(mRespondentForms,Integer.valueOf(mQuestionIDAndType.get(0)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        rptDialogCQuestion.setText(mReportQuestion);
        rptDialogCDateRange.setText(mReportTimeRange + " (" +
                mDatasource.returnDateRange(mReportTimeRange) + ")");
        rptDialogCRespondents.setText(Integer.toString(mRespondentForms.size()));
        rptDialogCCompletedForms.setText(Integer.toString(mCompletedForms));
        rptCommentList.setText(mComments);

        builder.setView(dialogView).setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // exit dialog
            }
        });

        return builder.create();
    }
}
