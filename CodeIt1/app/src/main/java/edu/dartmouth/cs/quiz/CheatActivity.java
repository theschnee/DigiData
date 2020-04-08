package edu.dartmouth.cs.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    public static final String EXTRA_USER_CHEATED = "cheated" ;
    private Button mShowAnswerButton;
    private TextView mShowAnswerText;
    // private boolean mAnswer;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USER_CHEATED, mShowAnswerText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(EXTRA_USER_CHEATED) != null) {
                answerShown();
            }
        }

        setContentView(R.layout.activity_cheat);

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerText = findViewById(R.id.show_answer_text);
        //mAnswer = getIntent().getBooleanExtra(QuizActivity.EXTRA_ANSWER, false);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowAnswerText.setText(Boolean.toString(getIntent()
                        .getBooleanExtra(QuizActivity.EXTRA_ANSWER, false)));
                answerShown();
            }
        });

    }

    private void answerShown() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_CHEATED, true);
        setResult(RESULT_OK, intent);
    }
}
