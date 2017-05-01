package com.blogspot.carlshark.wvsumccss;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment for report selection.
 * @author Carl Demetria
 */

public class ReportStartFragment extends Fragment {

    private SurveyDAO mDatasource;
    private ArrayList<Integer> mFormsForRange;
    private int mCompletedFormsForRange = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout file then get all necessary references
        View view = inflater.inflate
                (R.layout.fragment_reportstart, container, false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Button rptButton = (Button)view.findViewById(R.id.rptButton);
        final Spinner rptSpinner = (Spinner)view.findViewById(R.id.rptSpinner);
        final Spinner rptSpinner2 = (Spinner)view.findViewById(R.id.rptSpinner2);

        Bundle reportBundle = getArguments();
        ArrayList<String> spinnerAreaChoices = reportBundle.getStringArrayList("Area_Choices");

        if(spinnerAreaChoices != null){
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, spinnerAreaChoices);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rptSpinner.setAdapter(spinnerArrayAdapter);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(preferences.contains("Preferred Area")){
                rptSpinner.setSelection(spinnerArrayAdapter.getPosition(preferences.getString("Preferred Area", "")));
            }
        }


        rptButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mFormsForRange = mDatasource.formsForDateRange(rptSpinner.getSelectedItem().toString(),
                        rptSpinner2.getSelectedItem().toString());
                mCompletedFormsForRange = mDatasource.countCompletedForms(rptSpinner.getSelectedItem().toString(),
                        rptSpinner2.getSelectedItem().toString());
                ReportSummaryFragment reportSummary = new ReportSummaryFragment();
                Bundle reportDataBundle = new Bundle();
                reportDataBundle.putString("Area", rptSpinner.getSelectedItem().toString());
                reportDataBundle.putString("Time_Range", rptSpinner2.getSelectedItem().toString());
                reportDataBundle.putIntegerArrayList("Respondent_Forms", mFormsForRange);
                reportDataBundle.putInt("Completed_Forms", mCompletedFormsForRange);
                reportSummary.setArguments(reportDataBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, reportSummary,
                                "REPORT_SUMMARY").commit();
            }
        });

        return view;
    }
}
