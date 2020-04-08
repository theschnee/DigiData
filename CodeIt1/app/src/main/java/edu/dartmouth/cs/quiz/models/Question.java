package edu.dartmouth.cs.quiz.models;

public class Question {
    private int mQuestionResID;
    private boolean mAnswerIsTrue;
    private boolean mCheated;

    public Question(int questionResID, boolean answerIsTrue) {
        mQuestionResID = questionResID;
        mAnswerIsTrue = answerIsTrue;
    }

    // experimented with storing cheated booleans in question structure, ultimately didn't go down this path
    // kept in case it could be useful to return to in the future
    public Question(int questionResID, boolean answerIsTrue, boolean mUserCheated) {
        mQuestionResID = questionResID;
        mAnswerIsTrue = answerIsTrue;
        mCheated = mUserCheated;
    }

    public int getQuestionResID() {
        return mQuestionResID;
    }

    // setter for the boolean that states whether user has cheated on the question
    public void setmCheated(boolean mCheated) {
        this.mCheated = mCheated;
    }

    public void setQuestionResID(int questionResID) {
        mQuestionResID = questionResID;
    }

    public boolean isAnswerIsTrue() {
        return mAnswerIsTrue;
    }

    // getter for the boolean that states whether user has cheated on the question
    public boolean hasCheated() {
        return mCheated;
    }

    public void setAnswerIsTrue(boolean answerIsTrue) {
        mAnswerIsTrue = answerIsTrue;
    }

}
