package com.blogspot.carlshark.wvsumccss;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * MainActivity showing default screen on app startup.
 * @author Carl Demetria
 */

public class MainActivity extends AppCompatActivity {

    private ListView mNavDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private SurveyDAO mDatasource;
    private ArrayList<String> mAreaChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatasource = new SurveyDAO(this);
        mDatasource.open();

        mAreaChoices = mDatasource.chooseArea();

        mNavDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mActivityTitle = getTitle().toString();
        // Initialize an array with our titles from strings.xml
        String[] navMenuTitles = getResources().
                getStringArray(R.array.nav_drawer_items);
        // Initialize our ArrayAdapter
        mAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, navMenuTitles);
        // Set the adapter to the ListView
        mNavDrawerList.setAdapter(mAdapter);
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mNavDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter,
                View view, int whichItem, long id) {
                switchFragment(whichItem);
            }
        });
        switchFragment(0);
    }

    /**
     * Selects the appropriate Fragment and shows it in fragmentHolder, then closes mDrawerToggle
     * @param position depends on option selected on mDrawerToggle
     */

    private void switchFragment(int position) {
        Fragment fragment = null;
        String fragmentID ="";
        switch (position) {
            case 0:
                fragmentID = "SURVEY_START";
                Bundle argsSurvey = new Bundle();
                argsSurvey.putStringArrayList("Area_Choices", mAreaChoices);
                fragment = new SurveyStartFragment();
                fragment.setArguments(argsSurvey);
                break;
            case 1:
                fragmentID = "REPORT_START";
                Bundle argsReport = new Bundle();
                argsReport.putStringArrayList("Area_Choices", mAreaChoices);
                fragment = new ReportStartFragment();
                fragment.setArguments(argsReport);
                break;
            case 2:
                fragmentID = "ABOUT";
                Bundle argsAbout = new Bundle();
                argsAbout.putStringArrayList("Area_Choices", mAreaChoices);
                fragment = new AboutFragment();
                fragment.setArguments(argsAbout);
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        fragmentID).commit();
        // Close the drawer
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    /**
     * Setups mDrawerToggle
     */

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle
                (this, mDrawerLayout, R.string.drawer_open,
                        R.string.drawer_close) {
            // Called when drawer is opened
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Make selection");
                // triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();

            }
            // Called when drawer is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                // triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            //drawer is open so close it
            mDrawerLayout.closeDrawer(mNavDrawerList);
        }else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        // fix orientation to portrait mode
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
