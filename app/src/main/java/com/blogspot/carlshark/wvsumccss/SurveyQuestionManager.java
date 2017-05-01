package com.blogspot.carlshark.wvsumccss;

import java.util.ArrayList;

/**
 * Interface implemented to process the question detail and remaining questions by DialogFragments
 * that use them. It must be implemented in a similar fashion by SurveyStartDialog,
 * SurveyLikertQuestionDialog, SurveyYesNoQuestionDialog, and SurveyCommentsQuestionDialog. This is
 * to ensure compatibility if ever future survey forms will have a different sequence of questions
 * @author Carl Demetria
 */

public interface SurveyQuestionManager {
    public void process(ArrayList<String> questionDetail, ArrayList<Integer> remainingNumbers);
}
