package edu.dartmouth.cs.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import edu.dartmouth.cs.quiz.models.Question;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "lifecycle";
    private static final String KEY_CURRENT_INDEX = "index";
    public static final String EXTRA_ANSWER = "answer";
    public static final String CHEATED_ARRAY = "cheated";
    private static final int REQUEST_CHEATED = 01;
    private Button mTrueButton;
    private Button mFalseButton, mNextButton, mPreviousButton, mCheatButton;

    private TextView mQuestionText;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_soccer, true),
            new Question(R.string.question_ivy, false),
            new Question(R.string.question_hope, true),
            new Question(R.string.question_joke, true),
    };

    private boolean[] mCheatedBank = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;
    private boolean mUserCheated = false;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_INDEX, mCurrentIndex);
        outState.putBooleanArray(CHEATED_ARRAY, mCheatedBank);

        // used for earlier version of solution, kept in case array needs to be reverted
        // outState.putBoolean(CHEATED, mQuestionBank[mCurrentIndex].hasCheated());

        Log.d(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
            mCheatedBank = savedInstanceState.getBooleanArray(CHEATED_ARRAY);
        }

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mPreviousButton = findViewById(R.id.previous_button);
        mCheatButton = findViewById(R.id.cheat_button);

        mQuestionText = findViewById(R.id.question_text);
        mQuestionText.setText(mQuestionBank[mCurrentIndex].getQuestionResID());

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAnswer(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mQuestionText.setText(mQuestionBank[mCurrentIndex].getQuestionResID());
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentIndex ==0)
                    mCurrentIndex = mQuestionBank.length - 1;
                else
                    mCurrentIndex = mCurrentIndex - 1;
                mQuestionText.setText(mQuestionBank[mCurrentIndex].getQuestionResID());

            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                intent.putExtra(EXTRA_ANSWER, mQuestionBank[mCurrentIndex].isAnswerIsTrue());
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CHEATED);
            }
        });

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHEATED && resultCode == RESULT_OK && data != null)
            mUserCheated = data.getBooleanExtra(CheatActivity.EXTRA_USER_CHEATED, false);
            mCheatedBank[mCurrentIndex] = mUserCheated;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    private void checkAnswer(boolean answer) {

        /*
        don't know if you want false to show 'cheating is wrong' text even if they cheated but still clicked wrong one
        this is here in case you want that change to be made

            if (mQuestionBank[mCurrentIndex].isAnswerIsTrue() != answer) {
                Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else
        */

        if (mCheatedBank[mCurrentIndex] == true) {
            Toast.makeText(QuizActivity.this, "Cheating is wrong!", Toast.LENGTH_SHORT).show();
        } else {
            if (mQuestionBank[mCurrentIndex].isAnswerIsTrue() == answer)
                Toast.makeText(QuizActivity.this, "Correct", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
        mUserCheated = false;
    }
}
