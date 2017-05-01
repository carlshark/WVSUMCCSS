package com.blogspot.carlshark.wvsumccss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This will create the database required by the application.
 * @author CarlDemetria
 */

public class SurveyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SurveyApp.db";

    public static final String TABLE_CFD = "completed_form_data";
    public static final String CFD_COL_1 = "survey_id";
    public static final String CFD_COL_2 = "survey_timestamp";
    public static final String CFD_COL_3 = "survey_area"; // dependent on STFA_COL_1
    public static final String CFD_COL_4 = "respondent_name";
    public static final String CFD_COL_5 = "form_complete_flag"; // 1 if complete, 0 if not

    public static final String TABLE_CFA = "completed_form_answers";
    public static final String CFA_COL_1 = "survey_id"; // dependent on CFD_COL_1
    public static final String CFA_COL_2 = "question_number"; // dependent on QD_COL_1
    public static final String CFA_COL_3 = "answer_to_question";
    public static final String CFA_COL_4 = "text_answer_to_question";

    public static final String TABLE_STFA = "survey_type_for_area";
    public static final String STFA_COL_1 = "survey_area";
    public static final String STFA_COL_2 = "survey_type"; // same integer for areas with same survey

    public static final String TABLE_QFST = "questions_for_survey_type";
    public static final String QFST_COL_1 = "survey_type";
    public static final String QFST_COL_2 = "question_number"; // dependent on QD_COL_1

    public static final String TABLE_QD = "question_details";
    public static final String QD_COL_1 = "question_number";
    public static final String QD_COL_2 = "question_type"; // values: 'likert', 'yes_no', 'comments'
    public static final String QD_COL_3 = "question_text";

    private static final String FIRST_TABLE_STFA = "CREATE TABLE " + TABLE_STFA + "(" + STFA_COL_1
            + " TEXT PRIMARY KEY, " + STFA_COL_2 + " INTEGER NOT NULL);";
    private static final String SECOND_TABLE_QD = "CREATE TABLE " + TABLE_QD + "(" + QD_COL_1
            + " INTEGER PRIMARY KEY, " + QD_COL_2 + " TEXT NOT NULL, " + QD_COL_3
            + " TEXT NOT NULL);";
    private static final String THIRD_TABLE_QFST = "CREATE TABLE " + TABLE_QFST + "(" + QFST_COL_1
            + " INTEGER NOT NULL, " + QFST_COL_2 + " INTEGER NOT NULL, FOREIGN KEY(" + QFST_COL_2
            + ") REFERENCES " + TABLE_QD + "(" + QD_COL_1 + "));";
    private static final String FOURTH_TABLE_CFD = "CREATE TABLE " + TABLE_CFD + "(" + CFD_COL_1
            + " INTEGER PRIMARY KEY, " + CFD_COL_2 + " TEXT NOT NULL, " + CFD_COL_3
            + " TEXT NOT NULL, " + CFD_COL_4 + " TEXT, " + CFD_COL_5 + " INTEGER DEFAULT 0,"
            + " FOREIGN KEY(" + CFD_COL_3 + ") REFERENCES "
            + TABLE_STFA + "(" + STFA_COL_1 + "));";
    private static final String FIFTH_TABLE_CFA = "CREATE TABLE " + TABLE_CFA + "(" + CFA_COL_1
            + " INTEGER NOT NULL, " + CFA_COL_2 + " INTEGER NOT NULL, " + CFA_COL_3
            + " INTEGER NOT NULL, " + CFA_COL_4 + " TEXT, FOREIGN KEY (" + CFA_COL_1 + ") REFERENCES "
            + TABLE_CFD + "(" + CFD_COL_1 + "), FOREIGN KEY (" + CFA_COL_2 + ") REFERENCES "
            + TABLE_QD + "(" + QD_COL_1 + "));";

    // double check if there are changes with survey_type_for_area table columns
    private static final String INITIALIZE_STFA = "INSERT INTO '" + TABLE_STFA + "' ('" + STFA_COL_1
            + "', '" + STFA_COL_2 + "') VALUES " +
            "('Emergency Room Complex', 1), " +
            "('Out Patient Department', 2), " +
            "('Management Information Systems Office', 3), " +
            "('Ofc of Asst Hosp Dir for Health Operations', 3);";

    private static final String INITIALIZE_QD = "INSERT INTO '" + TABLE_QD + "' ('" + QD_COL_1
            + "', '" + QD_COL_2 + "', '" + QD_COL_3 + "') VALUES " +
            "(1, 'yes_no', 'Would you recommend this hospital to others?'), " +
            "(2, 'likert', 'Waiting time to be seen by nurse'), " +
            "(3, 'likert', 'Clarity of instructions and procedures as explained by the nursing " +
            "staff'), " +
            "(4, 'likert', 'Courtesy shown by the nursing staff'), " +
            "(5, 'likert', 'Willingness and ability of nursing staff to attend to requests for " +
            "assistance'), " +
            "(6, 'likert', 'Waiting time to be seen and treated by the medical staff'), " +
            "(7, 'likert', 'Courtesy shown by the medical staff'), " +
            "(8, 'likert', 'Ability of the medical staff to explain details about the illness, " +
            "procedures to be done and medications to be taken'), " +
            "(9, 'likert', 'Cleanliness and comfort in the ER'), " +
            "(10, 'likert', 'Follow-up schedule and procedures explained and understood'), " +
            "(11, 'likert', 'Overall level of care received'), " +
            "(12, 'likert', 'Overall rating of your stay in the ER'), " +
            "(13, 'likert', 'Cleanliness and comfort in the OPD'), " +
            "(14, 'likert', 'Overall rating of your stay in the OPD')," +
            "(15, 'comments', 'Comments and Suggestions')," +
            "(16, 'likert', 'Waiting time')," +
            "(17, 'likert', 'Concern was addressed immediately and efficiently')," +
            "(18, 'likert', 'Courtesy shown by the staff')," +
            "(19, 'likert', 'Willingness to answer questions regarding the transaction')," +
            "(20, 'likert', 'Overall rating of the Office');";

    private static final String INITIALIZE_QFST = "INSERT INTO '" + TABLE_QFST + "' ('" + QFST_COL_1
            + "', '" + QFST_COL_2 + "') VALUES " +
            "(1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), " +
            "(1, 12), (1, 1), (1, 15), " +
            "(2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 13), (2, 10), (2, 11), " +
            "(2, 14), (2, 1), (2, 15), " +
            "(3, 16), (3, 17), (3, 18), (3, 19), (3, 20), (3, 1), (3, 15);";


    public SurveyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FIRST_TABLE_STFA);
        db.execSQL(SECOND_TABLE_QD);
        db.execSQL(THIRD_TABLE_QFST);
        db.execSQL(FOURTH_TABLE_CFD);
        db.execSQL(FIFTH_TABLE_CFA);
        db.execSQL(INITIALIZE_STFA);
        db.execSQL(INITIALIZE_QD);
        db.execSQL(INITIALIZE_QFST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STFA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QFST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QD);
        onCreate(db);
    }
}
