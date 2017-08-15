package de.griot_app.griot.dataclasses;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for local topic catalog
 */

public class TopicCatalog {

    private SparseArray<QuestionGroup> questionGroups;

    //default-constructor
    public TopicCatalog() {
        questionGroups = new SparseArray<>();
    }

    //get-methods:
    public SparseArray<QuestionGroup> getQuestionGroups() { return questionGroups; }

    //set-methods:
    public void setQuestionGroups(SparseArray<QuestionGroup> questionGroups) { this.questionGroups = questionGroups; }
}
