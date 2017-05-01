package com.blogspot.carlshark.wvsumccss;

import android.app.Fragment;
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
 * Fragment for starting a survey.
 * @author Carl Demetria
 */

public class SurveyStartFragment extends Fragment {

    private SurveyDAO mDatasource;
    private int mSurveyType = 0;
    private ArrayList<Integer> mSurveyQNumbers;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout file then get all necessary references
        View view = inflater.inflate
                (R.layout.fragment_surveystart, container, false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Button surveyButton = (Button)view.findViewById(R.id.surveyButton);
        final Spinner surveyAreaSpinner = (Spinner)view.findViewById(R.id.surveySpinner);

        Bundle surveyBundle = getArguments();

        ArrayList<String> spinnerAreaChoices = surveyBundle.getStringArrayList("Area_Choices");

        if(spinnerAreaChoices != null){
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, spinnerAreaChoices);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            surveyAreaSpinner.setAdapter(spinnerArrayAdapter);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(preferences.contains("Preferred Area")){
                surveyAreaSpinner.setSelection(spinnerArrayAdapter.getPosition(preferences.getString("Preferred Area", "")));
            }
        }

        surveyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(), "Area selected: " + surveyAreaSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                mSurveyType = mDatasource.typeForArea(surveyAreaSpinner.getSelectedItem().toString());
                mSurveyQNumbers = mDatasource.questionNumberList(mSurveyType);
                Bundle surveyQNumbers = new Bundle();
                surveyQNumbers.putString("Selected_Area", surveyAreaSpinner.getSelectedItem().toString());
                surveyQNumbers.putIntegerArrayList("Survey_QNumbers", mSurveyQNumbers);
                SurveyStartDialog surveyStart = new SurveyStartDialog();
                surveyStart.setArguments(surveyQNumbers);
                surveyStart.setCancelable(false);
                surveyStart.show(getFragmentManager(), "123");
            }

        });

        return view;
    }
}
