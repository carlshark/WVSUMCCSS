package com.blogspot.carlshark.wvsumccss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * First DialogFragment shown after choosing an area to be surveyed in SurveyStartFragment.
 * @author Carl Demetria
 */

public class SurveyStartDialog extends DialogFragment implements SurveyQuestionManager{

    private ArrayList<Integer> mSurveyQNumbers;
    private SurveyDAO mDatasource;
    private ArrayList<String> mQuestionDetail;
    private String mSelectedArea;
    private String mRespondentName;
    private int mRowID = 0;

    public SurveyStartDialog(){
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_surveyintro,
                new LinearLayout(getActivity()), false);

        final EditText surveyRespondent = (EditText)dialogView.findViewById(R.id.surveyRespondent);
        TextView surveyIntroText = (TextView)dialogView.findViewById(R.id.surveyIntroText);

        Bundle surveyQNumbers = getArguments();

        mSurveyQNumbers = surveyQNumbers.getIntegerArrayList("Survey_QNumbers");
        mSelectedArea = surveyQNumbers.getString("Selected_Area");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView).setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mRespondentName = surveyRespondent.getText().toString();
                mQuestionDetail = mDatasource.questionDetail(mSurveyQNumbers.get(0));
                mRowID = mDatasource.createNewForm(mSelectedArea, mRespondentName);
                process(mQuestionDetail, mSurveyQNumbers);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // exit dialog
            }
        });
        return builder.create();
    }

    @Override
    public void process(ArrayList<String> questionDetail, ArrayList<Integer> mSurveyQNumbers) {
        String questionType = questionDetail.get(0);
        Bundle remainingQuestions = new Bundle();
        switch(questionType){
            case "likert":
                SurveyLikertQuestionDialog newLikertQuestion = new SurveyLikertQuestionDialog();
                remainingQuestions.putStringArrayList("QuestionDetail", questionDetail);
                remainingQuestions.putIntegerArrayList("Remaining_Questions", mSurveyQNumbers);
                remainingQuestions.putInt("Form_ID", mRowID);
                newLikertQuestion.setArguments(remainingQuestions);
                newLikertQuestion.setCancelable(false);
                newLikertQuestion.show(getFragmentManager(), "124");
                break;
            case "yes_no":
                SurveyYesNoQuestionDialog newYesNoQuestion = new SurveyYesNoQuestionDialog();
                remainingQuestions.putStringArrayList("QuestionDetail", questionDetail);
                remainingQuestions.putIntegerArrayList("Remaining_Questions", mSurveyQNumbers);
                remainingQuestions.putInt("Form_ID", mRowID);
                newYesNoQuestion.setArguments(remainingQuestions);
                newYesNoQuestion.setCancelable(false);
                newYesNoQuestion.show(getFragmentManager(), "125");
                break;
            case "comments":
                SurveyCommentsQuestionDialog newCommentsQuestion = new SurveyCommentsQuestionDialog();
                remainingQuestions.putStringArrayList("QuestionDetail", questionDetail);
                remainingQuestions.putIntegerArrayList("Remaining_Questions", mSurveyQNumbers);
                remainingQuestions.putInt("Form_ID", mRowID);
                newCommentsQuestion.setArguments(remainingQuestions);
                newCommentsQuestion.setCancelable(false);
                newCommentsQuestion.show(getFragmentManager(), "126");
        }
    }
}
