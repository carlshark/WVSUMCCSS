package com.blogspot.carlshark.wvsumccss;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Fragment showing About section.
 * @author Carl Demetria
 */

public class AboutFragment extends Fragment {

    private SurveyDAO mDatasource;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout file then get all necessary references
        View view = inflater.inflate
                (R.layout.fragment_about, container, false);

        mDatasource = new SurveyDAO(getActivity());
        mDatasource.open();

        Button prefSaveButton = (Button)view.findViewById(R.id.prefSaveButton);
        final Spinner prefSaveSpinner = (Spinner)view.findViewById(R.id.prefSaveSpinner);
        Button importDBToSDBtn = (Button)view.findViewById(R.id.importDBToSDBtn);

        Bundle reportBundle = getArguments();
        final ArrayList<String> spinnerAreaChoices = reportBundle.getStringArrayList("Area_Choices");

        if(spinnerAreaChoices != null){
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, spinnerAreaChoices);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            prefSaveSpinner.setAdapter(spinnerArrayAdapter);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(preferences.contains("Preferred Area")){
                prefSaveSpinner.setSelection(spinnerArrayAdapter.getPosition(preferences.getString("Preferred Area", "")));
            }
        }

        prefSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Preferred Area", prefSaveSpinner.getSelectedItem().toString());
                editor.apply();
            }
        });

        importDBToSDBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDatasource.exportDB();
                Toast.makeText(getActivity(), "Check if a copy of SurveyApp.db has been saved " +
                        "in the root folder of your device storage. If not, grant Storage " +
                        "Permissions to this application in Settings.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
