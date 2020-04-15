package edu.cs.dartmouth.myruns1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private boolean mLoggedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggedIn = getIntent().getBooleanExtra(SignInActivity.CLICKING_SIGINING_IN, false);


        if((savedInstanceState != null))
            setContentView(R.layout.activity_main);
        else if(mLoggedIn == true) {
            setContentView(R.layout.activity_main);
        } else {
            Intent SignIn = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(SignIn);
        }
    }
}
