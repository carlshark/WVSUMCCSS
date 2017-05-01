package com.blogspot.carlshark.wvsumccss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Data access object for the application.
 * @author Carl Demetria
 */

public class SurveyDAO {

    private SQLiteDatabase sdb;
    private SurveyDBHelper sdbhelper;

    public SurveyDAO(Context context){
        sdbhelper = new SurveyDBHelper(context);
    }

    public void open() throws SQLException {
        sdb = sdbhelper.getWritableDatabase();
    }

    public void close(){
        sdbhelper.close();
    }

    /**
     * Gets the possible areas that can be surveyed.
     * @return ArrayList of areas
     */

    public ArrayList<String> chooseArea(){
        ArrayList<String> areaChoices = new ArrayList<String>();

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.STFA_COL_1 + " FROM " +
                sdbhelper.TABLE_STFA, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                String area = cursor.getString(cursor.getColumnIndex(sdbhelper.STFA_COL_1));
                areaChoices.add(area);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return areaChoices;
    }

    /**
     * Get the survey type for the area specified.
     * @param area as String
     * @return int representing survey type that can be used in SurveyDAO.questionNumberList
     */

    public int typeForArea(String area){
        int type = 0;

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.STFA_COL_2 + " FROM " +
                sdbhelper.TABLE_STFA + " WHERE " + sdbhelper.STFA_COL_1 + " = '" + area + "'", null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                type = cursor.getInt(cursor.getColumnIndex(sdbhelper.STFA_COL_2));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return type;
    }

    /**
     * Gets an ArrayList of question numbers for the specified survey type.
     * @param surveyType as int
     * @return list of question numbers, in sequence, that can be used by SurveyQuestionManager
     */

    public ArrayList<Integer> questionNumberList(int surveyType){
        ArrayList<Integer> questionNumberList = new ArrayList<Integer>();

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.QFST_COL_2 + " FROM " +
                sdbhelper.TABLE_QFST + " WHERE " + sdbhelper.QFST_COL_1 + " = '" + surveyType +
                "'", null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                int questionNumber = cursor.getInt(cursor.getColumnIndex(sdbhelper.QFST_COL_2));
                questionNumberList.add(questionNumber);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return questionNumberList;
    }

    /**
     * Gets an ArrayList of details for a specific question number. Question type and question text
     * are stored as Strings.
     * @param questionNumber as int
     * @return list of Strings as mentioned above
     */

    public ArrayList<String> questionDetail(int questionNumber){
        ArrayList<String> questionDetail = new ArrayList<String>();

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.QD_COL_2 + ", " + sdbhelper.QD_COL_3 +
                " FROM " + sdbhelper.TABLE_QD + " WHERE " + sdbhelper.QD_COL_1 + " = " +
                questionNumber, null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String questionType = cursor.getString(cursor.getColumnIndex(sdbhelper.QD_COL_2));
                String questionText = cursor.getString(cursor.getColumnIndex(sdbhelper.QD_COL_3));
                questionDetail.add(questionType);
                questionDetail.add(questionText);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return questionDetail;
    }

    /**
     * Save data to completed_form_data table, indicating that a respondent has started
     * accomplishing a survey form.
     * @param selectedArea as String
     * @param respondentName as String
     * @return ID for the survey that will be used by question DialogFragments to save data using
     * SurveyDAO.saveAnswerToQuestion
     */

    public int createNewForm(String selectedArea, String respondentName){
        int surveyID = 0;

        ContentValues values = new ContentValues();
        values.put(sdbhelper.CFD_COL_2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put(sdbhelper.CFD_COL_3, selectedArea);
        values.put(sdbhelper.CFD_COL_4, respondentName);
        long insertID = sdb.insert(sdbhelper.TABLE_CFD, null, values);

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.CFD_COL_1 + " FROM " +
                sdbhelper.TABLE_CFD + " ORDER BY " + sdbhelper.CFD_COL_1 + " DESC LIMIT 1", null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                surveyID = cursor.getInt(cursor.getColumnIndex(sdbhelper.CFD_COL_1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return surveyID;

    }

    /**
     * Save data to completed_form_answers table, using ID generated by SurveyDAO.createNewForm.
     * @param formID
     * @param currentQuestionNumber obtained from SurveyDAO.questionNumberList before being removed
     * @param answer as int for Likert-type or Yes/No questions
     * @param commentAnswer as String for questions asking for comments
     */

    public void saveAnswerToQuestion(int formID, int currentQuestionNumber, int answer,
                                     String commentAnswer){
        ContentValues values = new ContentValues();
        values.put(sdbhelper.CFA_COL_1, formID);
        values.put(sdbhelper.CFA_COL_2, currentQuestionNumber);
        values.put(sdbhelper.CFA_COL_3, answer);
        values.put(sdbhelper.CFA_COL_4, commentAnswer);
        long insertID = sdb.insert(sdbhelper.TABLE_CFA, null, values);
    }

    /**
     * Modify row in completed_form_data once survey is successfully completed.
     * @param formID
     */

    public void flagAsCompleted(int formID){

        ContentValues values = new ContentValues();
        values.put(sdbhelper.CFD_COL_5, 1);
        sdb.update(sdbhelper.TABLE_CFD, values, sdbhelper.CFD_COL_1 + " = " +
                formID, null);
    }

    /**
     * Get an ArrayList of questionNumbers depending on areaSelected and timeRangeSelected.
     * @param areaSelected
     * @param timeRangeSelected
     * @return questionNumbers that are arranged in order of their appearance in database.
     */

    public ArrayList<Integer> formsForDateRange(String areaSelected, String timeRangeSelected){
        ArrayList<Integer> formsForDateRange = new ArrayList<Integer>();

        String buildQuery = "SELECT " + sdbhelper.CFD_COL_1 + " FROM " + sdbhelper.TABLE_CFD +
                " WHERE " + sdbhelper.CFD_COL_3 + " = '" + areaSelected + "'";
        switch(timeRangeSelected){
            case "Last 24 hours":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= datetime('now', '+8 hours', " +
                        "'-24 hours')";
                break;
            case "Last 7 days":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= datetime('now', '+8 hours', " +
                        "'-7 days')";
                break;
            case "Current month":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= date('now', '+8 hours', " +
                        "'start of month')";
                break;
            case "Previous month":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= date('now', '+8 hours', " +
                        "'-1 month', 'start of month') AND " + sdbhelper.CFD_COL_2 +
                        " < date('now', '+8 hours', 'start of month')";
        }
        Cursor cursor = sdb.rawQuery(buildQuery, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                int formID = cursor.getInt(cursor.getColumnIndex(sdbhelper.CFD_COL_1));
                formsForDateRange.add(formID);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return formsForDateRange;
    }

    /**
     * Get count of completed forms based on areaSelected and timeRangeSelected.
     * @param areaSelected
     * @param timeRangeSelected
     * @return count of completed forms.
     */

    public int countCompletedForms(String areaSelected, String timeRangeSelected){
        int countCompletedForms = 0;
        String buildQuery = "SELECT " + sdbhelper.CFD_COL_1 + " FROM " + sdbhelper.TABLE_CFD +
                " WHERE " + sdbhelper.CFD_COL_3 + " = '" + areaSelected + "' AND " +
                sdbhelper.CFD_COL_5 + " = 1";
        switch(timeRangeSelected){
            //Philippine Standard Time is UTC+8
            case "Last 24 hours":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= datetime('now', '+8 hours', " +
                        "'-24 hours')";
                break;
            case "Last 7 days":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= datetime('now', '+8 hours', " +
                        "'-7 days')";
                break;
            case "Current month":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= date('now', '+8 hours', " +
                        "'start of month')";
                break;
            case "Previous month":
                buildQuery += " AND " + sdbhelper.CFD_COL_2 + " >= date('now', '+8 hours', " +
                        "'-1 month', 'start of month') AND " + sdbhelper.CFD_COL_2 + " < " +
                        "date('now', '+8 hours', 'start of month')";
        }
        Cursor cursor = sdb.rawQuery(buildQuery, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                countCompletedForms++;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return countCompletedForms;
    }

    /**
     * Show a human-readable String representation of the date range selected.
     * @param timeRangeSelected
     * @return String as datetime ("YYYY-MM-DD HH:MM:SS") or date ("YYYY-MM-DD") if current month/
     * previous month
     */

    public String returnDateRange(String timeRangeSelected){
        String dateRange = "";
        String buildQuery = "";
        switch(timeRangeSelected){
            //Philippine Standard Time is UTC+8
            case "Last 24 hours":
                buildQuery = "SELECT datetime('now', '+8 hours', '-24 hours')";
                break;
            case "Last 7 days":
                buildQuery = "SELECT datetime('now', '+8 hours', '-7 days')";
                break;
            case "Current month":
                buildQuery = "SELECT date('now', '+8 hours', 'start of month')";
                break;
            case "Previous month":
                buildQuery = "SELECT date('now', '+8 hours', '-1 month', 'start of month')";
        }
        Cursor cursor = sdb.rawQuery(buildQuery, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                dateRange = cursor.getString(0);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return dateRange;
    }

    /**
     * Return an ArrayList of questions for selection during report generation, based on
     * areaSelected (which determines survey type).
     * @param areaSelected
     * @return question list for report
     */

    public ArrayList<String> getListOfQuestionsForReport(String areaSelected){
        ArrayList<String> questionList = new ArrayList<String>();
        int surveyType = typeForArea(areaSelected);
        ArrayList<Integer> qNumberList = questionNumberList(surveyType);
        for(int qNumber: qNumberList){
            questionList.add(questionDetail(qNumber).get(1));
        }
        return questionList;
    }

    /**
     * Get question ID and type for specific item report as an ArrayList. Remember to convert
     * the first member of the list back to an Integer.
     * @param questionText
     * @return ArrayList of questionNumber and questionType
     */

    public ArrayList<String> getQuestionIDAndType(String questionText){
        ArrayList<String> questionIDAndType = new ArrayList<String>();

        Cursor cursor = sdb.rawQuery("SELECT " + sdbhelper.QD_COL_1 + ", " + sdbhelper.QD_COL_2 +
                " FROM " + sdbhelper.TABLE_QD + " WHERE " + sdbhelper.QD_COL_3 + " = '" +
                questionText + "'", null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                // convert questionNumber to String so that data can be sent as one pair
                // remember to convert questionNumber back to int
                String questionNumber = Integer.toString(cursor.getInt(cursor.getColumnIndex(sdbhelper.QD_COL_1)));
                String questionType = cursor.getString(cursor.getColumnIndex(sdbhelper.QD_COL_2));
                questionIDAndType.add(questionNumber);
                questionIDAndType.add(questionType);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return questionIDAndType;
    }

    /**
     * Count answers containing a specific value, reading from completed_form_answers.
     * @param respondentForms
     * @param questionNumber
     * @param answerValue
     * @return count of answers for a value
     */
    public int getAnswerCountsForValue(ArrayList<Integer> respondentForms, int questionNumber,
                                        int answerValue){
        ArrayList<Integer> copyOfRespondentForms = new ArrayList<Integer>(respondentForms);
        int count = 0;

        String buildQuery = "SELECT * FROM " + sdbhelper.TABLE_CFA + " WHERE " +
                sdbhelper.CFA_COL_1 + " IN (";
        while(!copyOfRespondentForms.isEmpty()){
            buildQuery += Integer.toString(copyOfRespondentForms.get(0));
            if(copyOfRespondentForms.size() > 1) {
                buildQuery += ", ";
            }
            copyOfRespondentForms.remove(0);
        }
        buildQuery += ") AND " + sdbhelper.CFA_COL_2 + " = " + Integer.toString(questionNumber) +
                " AND " + sdbhelper.CFA_COL_3 + " = " + Integer.toString(answerValue);

        Cursor cursor = sdb.rawQuery(buildQuery, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                count++;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return count;

    }

    /**
     * Get all free-form String comments that are not null and not empty (""), and concatenate them
     * in a single String with newlines separating them.
     * @param respondentForms
     * @param questionNumber
     * @return comment String
     */

    public String getComments(ArrayList<Integer> respondentForms, int questionNumber){
        ArrayList<Integer> copyOfRespondentForms = new ArrayList<Integer>(respondentForms);
        String commentsList = "";

        String buildQuery = "SELECT " + sdbhelper.CFA_COL_4 + " FROM " + sdbhelper.TABLE_CFA +
                " WHERE " + sdbhelper.CFA_COL_1 + " IN (";
        while(!copyOfRespondentForms.isEmpty()){
            buildQuery += Integer.toString(copyOfRespondentForms.get(0));
            if(copyOfRespondentForms.size() > 1) {
                buildQuery += ", ";
            }
            copyOfRespondentForms.remove(0);
        }
        buildQuery += ") AND " + sdbhelper.CFA_COL_2 + " = " + Integer.toString(questionNumber) +
                " AND " + sdbhelper.CFA_COL_4 + " IS NOT NULL AND " + sdbhelper.CFA_COL_4 +
                " != \"\"";

        Cursor cursor = sdb.rawQuery(buildQuery, null);
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false){
                commentsList += " * " + cursor.getString(cursor.getColumnIndex(sdbhelper.CFA_COL_4)) + "\n";
                cursor.moveToNext();
            }
        }
        cursor.close();
        return commentsList;
    }

    //

    /**
     * Reference: http://stackoverflow.com/questions/19093458/copy-database-file-to-sdcard-in-android
     * Save a copy of the SQLite database into device storage so that users can download to the PC
     * and view using an SQLite viewer.
     */

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data/com.blogspot.carlshark.wvsumccss/databases/SurveyApp.db";
                String backupDBPath = "SurveyApp.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

}
