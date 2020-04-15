package edu.cs.dartmouth.myruns1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cs.dartmouth.myruns1.Preferences.UserData;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    private ImageButton mProfilePic;
    private String mImagePath = "NA";
    private EditText mUserName;
    private EditText mPassword;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mMajor;
    private EditText mClass;
    private RadioButton mFemale, mMale;
    private UserData userdata;
    private Uri mFinalURI;
    private String uriString;
    private Uri mURI;
    private Intent intent;
    private Button mChange;
    private boolean permissiongranted;
    private Bitmap bitmap;
    private File photoFile;
    public static final String Registered_USER = "We have a registered User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeBar();
        setContentView(R.layout.activity_profile);

        userdata = new UserData(this);
        mClass = findViewById(R.id.Class);
        mMajor = findViewById(R.id.Major);
        mPhone = findViewById(R.id.Phone);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mUserName = findViewById(R.id.Name);
        mProfilePic = findViewById(R.id.imageButton);
        mFemale = findViewById(R.id.GenderF);
        mMale = findViewById(R.id.GenderM);
        mChange = findViewById(R.id.ChangePic);


        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

        if(savedInstanceState != null){
            mFinalURI = savedInstanceState.getParcelable("URI_OF_PROFILE_PIC");
            if(mFinalURI !=null) {

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFinalURI);
                    mProfilePic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }



    }

    /* all methods for creating, modifying, and using the menu to sign up and go back,
     as necessary */
    private void makeBar() {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle("Sign Up");
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_register:
                saveUserData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* all methods for checking and saving user data */
    private void saveUserData() {
        String Class = mClass.getText().toString();
        String Major = mMajor.getText().toString();
        String Phone = mPhone.getText().toString();
        String Email = mEmail.getText().toString();
        String Password = mPassword.getText().toString();
        String Name = mUserName.getText().toString();

        View focus = null;
        boolean valid = true;
        boolean Gender = false;

        if(TextUtils.isEmpty(Class)) {
            mClass.setError("This field must be filled out!");
            focus = mClass;
            valid = false;
        }

        if(GenderChosen(Gender)) {
            valid = true;
        } else {
            valid = false;
            mMale.setError("Please check one");
            mFemale.setError("Please check one");
        }

        if(TextUtils.isEmpty(Major)) {
            mMajor.setError("This field must be filled out!");
            focus = mMajor;
            valid = false;
        }

        if(TextUtils.isEmpty(Phone)) {
            mMajor.setError("This field must be filled out!");
            focus = mMajor;
            valid = false;
        }

        if(TextUtils.isEmpty(Email)) {
            mEmail.setError("This field must be filled out!");
            focus = mEmail;
            valid = false;
        } else {
            if(ValidEmail(Email)) {
                valid = true;
            } else {
                valid = false;
                mEmail.setError("This E-mail is not valid, please try again");
                focus = mEmail;
            }
        }

        if(TextUtils.isEmpty(Password)){
            mMajor.setError("This field must be filled out!");
            focus = mMajor;
            valid = false;
        } else {
            if(ValidPassword(Password)){
                valid = true;
            } else {
                valid = false;
                mPassword.setError("This Password is not valid, it's too short or does not contain numbers, please try again!");
                focus = mPassword;
            }
        }

        if(TextUtils.isEmpty(Name)){
            mMajor.setError("This field must be filled out!");
            focus = mMajor;
            valid = false;
        }

        if(valid) {

         //   userdata.ClearAll();
            userdata.setUserClass(Class);
            userdata.setUserName(Name);
            userdata.setUserGender(Gender);
            userdata.setUserPassword(Password);
            userdata.setUserEmail(Email);
            userdata.setUserMajor(Major);

            if(!mImagePath.equalsIgnoreCase("NA")){
                userdata.setUserPhoto(mImagePath);
            }
            Toast.makeText(this, "Successful Registration",Toast.LENGTH_LONG).show();
            Intent registered = new Intent(ProfileActivity.this, SignInActivity.class);
            registered.putExtra(Registered_USER,true);
            startActivity(registered);

        } else {
            Toast.makeText(this, "Fill out approriately before being able saving",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean ValidEmail(String Email) {
        if (!Email.contains("@") || !Email.contains(".")) {
            return(false);
        } else {
            return(true);
        }
    }

    private boolean ValidPassword(String Password) {
        if (Password.length() < 6 || !Password.matches(".*\\d.*")) {
            return(false);
        } else {
            return(true);
        }
    }

    private boolean GenderChosen(boolean Gender){
        if(mFemale.isChecked()){
            // valid = true;
            Gender = true;
        } else if(mMale.isChecked()) {
            //  valid = true
            Gender = true;
        } else {
            // valid = false;
            Gender = false;

        }
        return(Gender);
    }

    /* all functions relevant to the operation of the camera dialog */
    public void Dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Choose where to take image from");

        alertDialogBuilder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
                if(!permissiongranted) {

                } else {
                    GetPhoto();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int argl) {
                checkPermission();
                if(!permissiongranted) {

                } else {
                    TakePhoto();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /* the following functions are for checking, approving, and denying permissions for accessing memory and taking photos */
    private void checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        } else {
            permissiongranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            permissiongranted = true;
            GetPhoto();

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    AlertDialog.Builder Request = new AlertDialog.Builder(this);
                    Request.setMessage("This permission is important for the proper function of the app.").setTitle("Important Permission Requested");
                    Request.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                        }
                    });

                    Request.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    Request.show();
                } else {
                    finish();
                }
            }
        }
    }


    /* the following functions are for the things done in this activity on photo button presses or returns */
    public void GetPhoto() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Sign Up")
                .setAllowFlipping(false)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }

    public void TakePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = createImageFile();

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch(ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void crop(Uri source) {
        if(photoFile != null)
            CropImage.activity(source)
                    .setActivityTitle("Sign Up")
                    .setAllowFlipping(false)
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_FROM_CAMERA && resultCode == AppCompatActivity.RESULT_OK) {
            mURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            crop(mURI);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            CropImage.ActivityResult res = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mFinalURI = res.getUri();
                mImagePath = mFinalURI.getPath();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFinalURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mProfilePic.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = res.getError();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mFinalURI!=null) {
            outState.putParcelable("URI_OF_PROFILE_PIC", mFinalURI);
        }
    }



}
