package com.blogspot.carlshark.wvsumccss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * DialogFragment to show a Likert-type question and store the answer.
 * @author Carl Demetria
 */

public class SurveyLikertQuestionDialog extends DialogFragment implements SurveyQuestionManager{

    private ArrayList<Integer> mSurveyQNumbers;
    private ArrayList<String> mQuestionDetail;
    private SurveyDAO mDatasource;
    private int currentQuestionNumber = 0;
    private int mRowID = 0;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private int mCheckedItem = -1;
    private int mAnswer = 0;

    public SurveyLikertQuestionDialog(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Bundle remainingQuestions = getArguments();
        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        mQuestionDetail = remainingQuestions.getStringArrayList("QuestionDetail");
        mSurveyQNumbers = remainingQuestions.getIntegerArrayList("Remaining_Questions");
        mRowID = remainingQuestions.getInt("Form_ID");
        currentQuestionNumber = mSurveyQNumbers.get(0);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_surveyquestion_likert,
                new LinearLayout(getActivity()), false);
        TextView likertText = (TextView)dialogView.findViewById(R.id.likertTextView);
        likertText.setText(mQuestionDetail.get(1));
        mRadioGroup = (RadioGroup)dialogView.findViewById(R.id.likertRadioGroup);
        mSurveyQNumbers.remove(0); //remove current question number for process
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView).setPositiveButton(R.string.next,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mCheckedItem = mRadioGroup.getCheckedRadioButtonId();
                // default mAnswer value is 0 if respondent fails to choose answer
                if(mCheckedItem != -1){
                    mRadioButton = (RadioButton)dialogView.findViewById(mCheckedItem);
                   mAnswer = convertRBTextToInt(mRadioButton.getText().toString());
                }

                // save answer to database
                mDatasource.saveAnswerToQuestion(mRowID, currentQuestionNumber, mAnswer, "");

                if(!mSurveyQNumbers.isEmpty()){
                    mQuestionDetail = mDatasource.questionDetail(mSurveyQNumbers.get(0));
                    process(mQuestionDetail, mSurveyQNumbers);
                }
                else {
                    SurveyEndDialog newEndSurvey = new SurveyEndDialog();
                    Bundle endData = new Bundle();
                    endData.putInt("Form_ID", mRowID);
                    newEndSurvey.setArguments(endData);
                    newEndSurvey.setCancelable(false);
                    newEndSurvey.show(getFragmentManager(), "127");
                }
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

    /**
     * Assigns a numerical value to chosen mRadioButton, so that it can be stored by
     * SurveyDAO.saveAnswerToQuestion
     * @param radioButtonText of the selected mRadioButton
     * @return int to be saved
     */

    public int convertRBTextToInt(String radioButtonText){
        int returnVal = 0;
        switch(radioButtonText){
            case "Not Applicable":
                returnVal = 0;
                break;
            case "Poor":
                returnVal = 1;
                break;
            case "Fair":
                returnVal = 2;
                break;
            case "Good":
                returnVal = 3;
                break;
            case "Very Good":
                returnVal = 4;
                break;
            case "Excellent":
                returnVal = 5;
        }
        return returnVal;
    }

}
