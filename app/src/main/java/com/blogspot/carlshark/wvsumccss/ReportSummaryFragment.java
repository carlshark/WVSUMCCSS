package com.blogspot.carlshark.wvsumccss;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Fragment showing report summary.
 * @author Carl Demetria
 */

public class ReportSummaryFragment extends Fragment {
    private String mAreaSelected = "";
    private String mTimeRange = "";
    private ArrayList<Integer> mRespondentForms = new ArrayList<Integer>();
    private int mCompletedForms = 0;
    private SurveyDAO mDatasource;
    private String mSelectedQuestion = "";
    private ArrayList<String> mQuestionIDAndType = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate
                (R.layout.fragment_reportsummary, container, false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Bundle reportDataBundle = getArguments();
        mAreaSelected = reportDataBundle.getString("Area");
        mTimeRange = reportDataBundle.getString("Time_Range");
        //mRespondentForms = reportDataBundle.getIntegerArrayList("Respondent_Forms");
        mRespondentForms = mDatasource.formsForDateRange(mAreaSelected, mTimeRange);
        mCompletedForms = reportDataBundle.getInt("Completed_Forms");

        TextView rptAreaTextView = (TextView)view.findViewById(R.id.rptAreaTextView);
        TextView rptDateRangeTextView = (TextView)view.findViewById(R.id.rptDateRangeTextView);
        TextView rptRespondents = (TextView)view.findViewById(R.id.rptRespondents);
        TextView rptCompletedForms = (TextView)view.findViewById(R.id.rptCompletedForms);

        final Button rptQuestionDetailBtn = (Button)view.findViewById(R.id.rptQuestionDetailBtn);
        final Button rptReturnReport = (Button)view.findViewById(R.id.rptReturnReport);
        final Spinner rptQuestionSpinner = (Spinner)view.findViewById(R.id.rptQuestionSpinner);
        ArrayList<String> questionList = mDatasource.getListOfQuestionsForReport(mAreaSelected);

        if(questionList != null) {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, questionList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rptQuestionSpinner.setAdapter(spinnerArrayAdapter);
        }

        rptAreaTextView.setText(mAreaSelected);
        rptDateRangeTextView.setText(mTimeRange + " (" +
                mDatasource.returnDateRange(mTimeRange) + ")");
        rptRespondents.setText(Integer.toString(mRespondentForms.size()));
        rptCompletedForms.setText(Integer.toString(mCompletedForms));

        rptQuestionDetailBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mSelectedQuestion = rptQuestionSpinner.getSelectedItem().toString();
                /*Toast.makeText(getActivity(), "Selected question : " + mSelectedQuestion,
                        Toast.LENGTH_SHORT).show();*/
                mQuestionIDAndType = mDatasource.getQuestionIDAndType(mSelectedQuestion);
                /*Toast.makeText(getActivity(), "Selected question details: " +
                        mQuestionIDAndType.toString(), Toast.LENGTH_SHORT).show();*/
                showReportForQuestion(mSelectedQuestion, mQuestionIDAndType, mTimeRange,
                        mRespondentForms, mCompletedForms);
            }
        });

        rptReturnReport.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Bundle reportBundle = new Bundle();
                reportBundle.putStringArrayList("Area_Choices", mDatasource.chooseArea());
                ReportStartFragment reportFragment = new ReportStartFragment();
                reportFragment.setArguments(reportBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, reportFragment,
                                "REPORT_START").commit();
            }
        });



        return view;
    }

    public void showReportForQuestion(String selectedQuestion,
                                      ArrayList<String> selectedQuestionDetails,
                                      String timeRange,
                                      ArrayList<Integer> respondentForms,
                                      int completedForms){
        Bundle reportQuestionData = new Bundle();
        reportQuestionData.putString("Question", selectedQuestion);
        reportQuestionData.putString("Time_Range", timeRange);
        reportQuestionData.putIntegerArrayList("Respondent_Forms", respondentForms);
        reportQuestionData.putInt("Completed_Forms", completedForms);
        reportQuestionData.putStringArrayList("Question_Details", selectedQuestionDetails);


        switch(selectedQuestionDetails.get(1)){
            case "likert":
                ReportLikertQuestionDialog reportLikert = new ReportLikertQuestionDialog();
                reportLikert.setArguments(reportQuestionData);
                reportLikert.setCancelable(false);
                reportLikert.show(getFragmentManager(), "128");
                break;
            case "yes_no":
                ReportYesNoQuestionDialog reportYesNo = new ReportYesNoQuestionDialog();
                reportYesNo.setArguments(reportQuestionData);
                reportYesNo.setCancelable(false);
                reportYesNo.show(getFragmentManager(), "129");
                break;
            case "comments":
                /*Toast.makeText(getActivity(), mDatasource.getComments(respondentForms,
                        Integer.valueOf(selectedQuestionDetails.get(0))), Toast.LENGTH_SHORT).show();*/
                ReportCommentsQuestionDialog reportComments = new ReportCommentsQuestionDialog();
                reportComments.setArguments(reportQuestionData);
                reportComments.setCancelable(false);
                reportComments.show(getFragmentManager(), "130");
        }
    }
}
