package edu.cs.dartmouth.myruns.Utils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.cs.dartmouth.myruns.Preferences.UserData;
import edu.cs.dartmouth.myruns.R;

public class SignInActivity extends AppCompatActivity {

    public static final String CLICKING_SIGINING_IN = "We want to go into the main";
    private Button mRegister;
    private Button mSignIn;
    private boolean mRegistered_User = false;
    private UserData userdata;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // not sure if this is wanted/needed
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Sign In");
        bar.setDisplayShowTitleEnabled(true);

        userdata = new UserData(this);
        mEmail = findViewById(R.id.Input_Email);
        mPassword = findViewById(R.id.Input_Password);

        mSignIn = findViewById(R.id.sign_in_button);
        mRegister = findViewById(R.id.register_button);
        System.out.println(mRegistered_User);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(SignInActivity.this, ProfileActivity.class);
                startActivity(register);

            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(verifyLogin()) {
                     Intent main = new Intent(SignInActivity.this, MainActivity.class);
                     main.putExtra(CLICKING_SIGINING_IN, true);
                     startActivity(main);
                 } else {
                     Toast.makeText(SignInActivity.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                 }
            }
        });

        mRegistered_User = getIntent().getBooleanExtra(ProfileActivity.Registered_USER, false);
        if(mRegistered_User){
            LoadProfile();
            System.out.println(mRegistered_User);

        }

    }

    private void LoadProfile() {
        String Email = userdata.getUserEmail();
        String Password = userdata.getUserPassword();

        mEmail.setText(Email);
        mPassword.setText(Password);
    }

    private boolean verifyLogin() {
        String userInput = mEmail.getText().toString();
        String passInput = mPassword.getText().toString();

        if(userInput.equals(userdata.getUserEmail()) && passInput.equals(userdata.getUserPassword())) {
            return true;
        } else {
            return false;
        }
    }

}
