package edu.dartmouth.cs.dartnets.testthis;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 *
 * Individual tests are defined as any method beginning with 'test'.
 *
 * ActivityInstrumentationTestCase2 allows these tests to run alongside a running
 * copy of the application under inspection. Calling getActivity() will return a
 * handle to this activity (launching it if needed).
 *
 * 
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mActivityRule = new ActivityTestRule(ProfileActivity.class);

    /**
     * Test to make sure the image is persisted after screen rotation.
     *
     * Launches the main activity, sets a test bitmap, rotates the screen.
     * Checks to make sure that the bitmap value matches what we set it to.
     */
    @Test
    public void testImagePersistedAfterRotate() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();
        // Define a test bitmap
        final Bitmap TEST_BITMAP = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.blue_pushpin);

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TEST_BITMAP.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final byte[] TEST_BITMAP_VALUE = bos.toByteArray();

        final ImageView mImageView = activity.findViewById(R.id.imageProfile);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Attempts to manipulate the UI must be performed on a UI thread.
                // Calling this outside runOnUiThread() will cause an exception.
                //
                // You could also use @UiThreadTest, but activity lifecycle methods
                // cannot be called if this annotation is used.
                //set the test bitmap to the image view
                mImageView.setImageBitmap(TEST_BITMAP);
            }
        });

        // Suspends the current thread for 1 second. This is no necessary.
        // But you can see the change on your phone.
        Thread.sleep(2000);

        // Information about a particular kind of Intent that is being monitored.
        // It is required to open your phone screen, otherwise the test will be hanging.
        Instrumentation.ActivityMonitor monitor =
                new Instrumentation.ActivityMonitor(ProfileActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(monitor);
        // Rotate the screen
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();
        // Updates current activity
        ProfileActivity activity_updated = (ProfileActivity) getInstrumentation().waitForMonitor(monitor);

        // Suspends the current thread for 1 second. This is no necessary.
        // But you can see the change on your phone.
        Thread.sleep(2000);

        final ImageView mImageView2 = activity_updated.findViewById(R.id.imageProfile);
        // Get the current bitmap from image view
        Bitmap currentBitMap = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();

        // Convert bitmap to byte array
        bos = new ByteArrayOutputStream();
        currentBitMap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] currentBitmapValue = bos.toByteArray();

        // Check if these two bitmaps have the same byte values.
        // If the program executes correctly, they should be the same
        assertArrayEquals(TEST_BITMAP_VALUE, currentBitmapValue);
    }

    /** DONE
     * Test to make sure that value of name is persisted across activity restarts.
     *
     * Launches the main activity (done) , sets a name value (done) , clicks the save button (done), closes the activity (done),
     * then relaunches that activity (done). Checks to make sure that the name value match what we
     * set it to.
     */
    @Test
    public void testNameValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize string name
        final String name = "Callum";

        // get name textbox and save button
        final Button save = activity.findViewById(R.id.btnSave);
        final EditText editText = activity.findViewById(R.id.editName);

        // UI thread to set name and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editName field
                editText.requestFocus();

                //set the field's content to our string
                editText.setText(name);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the String held within the name field and verify that it is the same as the string saved
        final EditText nameCheck = activity.findViewById(R.id.editName);
        String savedName = nameCheck.getText().toString();
        assertEquals(savedName, name);
    }

    /** DONE
     * Test to make sure that value of email is persisted across activity restarts.
     *
     * Launches the main activity, sets a email value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the email value match what we
     * set it to.
     */
    @Test
    public void testEmailValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize string email
        final String email = "chs.21@dartmouth.edu";

        // get email textbox and save button
        final Button save = activity.findViewById(R.id.btnSave);
        final EditText editText = activity.findViewById(R.id.editEmail);

        // UI thread to set email and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editEmail field
                editText.requestFocus();

                //set the field's content to our string
                editText.setText(email);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the String held within the email field and verify that it is the same as the string saved
        final EditText emailCheck = activity.findViewById(R.id.editEmail);
        String savedMail = emailCheck.getText().toString();
        assertEquals(savedMail, email);
    }

    /** DONE
     * Test to make sure that value of phone is persisted across activity restarts.
     *
     * Launches the main activity, sets a phone value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the phone value match what we
     * set it to.
     */
    @Test
    public void testPhoneValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize string phone
        final String phone = "123 456 7890";

        // get phone textbox and save button
        final Button save = activity.findViewById(R.id.btnSave);
        final EditText editText = activity.findViewById(R.id.editPhone);

        // UI thread to set phone and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editPhone field
                editText.requestFocus();

                //set the field's content to our string
                editText.setText(phone);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the String held within the phone field and verify that it is the same as the string saved
        final EditText emailCheck = activity.findViewById(R.id.editPhone);
        String savedPhone = emailCheck.getText().toString();
        assertEquals(savedPhone, phone);
    }

    /**
     * Test to make sure that value of gender is persisted across activity restarts.
     *
     * Launches the main activity, sets a gender value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the gender value match what we
     * set it to.
     */
    @Test
    public void testGenderValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize String corresponding to gender
        final String gender = "Male";

        // get radiogroup, gender and save buttons
        final Button save = activity.findViewById(R.id.btnSave);
        final RadioGroup radioGroup = activity.findViewById(R.id.radioGender);

        // UI thread to set name and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editName field
                radioGroup.requestFocus();

                //set the field's content to our string depending on boolean value
                if(gender.equals("Male"))
                    radioGroup.check(R.id.radioGenderM);

                if(gender.equals("Female"))
                    radioGroup.check(R.id.radioGenderF);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the gender value held within the radioGroup and check that it is the same as the original input
        final RadioGroup genderCheck = activity.findViewById(R.id.radioGender);

        if(genderCheck.getCheckedRadioButtonId() == -1) {

        } else {
            final int buttonId = genderCheck.getCheckedRadioButtonId();
            final RadioButton genderButtonCheck = activity.findViewById(buttonId);
            assertEquals(genderButtonCheck.getText().toString(), gender);
        }

    }

    /** DONE
     * Test to make sure that value of class is persisted across activity restarts.
     *
     * Launches the main activity, sets a class value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the class value match what we
     * set it to.
     */
    @Test
    public void testClassValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize string class
        final String classYear = "2021";

        // get class textbox and save button
        final Button save = activity.findViewById(R.id.btnSave);
        final EditText editText = activity.findViewById(R.id.editClass);

        // UI thread to set class and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editClass field
                editText.requestFocus();

                //set the field's content to our string
                editText.setText(classYear);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the String held within the class field and verify that it is the same as the string saved
        final EditText emailCheck = activity.findViewById(R.id.editClass);
        String savedClass = emailCheck.getText().toString();
        assertEquals(savedClass , classYear);
    }

    /** DONE
     * Test to make sure that value of major is persisted across activity restarts.
     *
     * Launches the main activity, sets a major value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the major value match what we
     * set it to.
     */
    @Test
    public void testMajorValuePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize string major
        final String major = "Computer Science";

        // get major textbox and save button
        final Button save = activity.findViewById(R.id.btnSave);
        final EditText editText = activity.findViewById(R.id.editMajor);

        // UI thread to set major and press save button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // opens the text input for the editMajor field
                editText.requestFocus();

                //set the field's content to our string
                editText.setText(major);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // get the String held within the major field and verify that it is the same as the string saved
        final EditText emailCheck = activity.findViewById(R.id.editMajor);
        String savedMajor = emailCheck.getText().toString();
        assertEquals(savedMajor, major);
    }

    /**
     * Test to make sure that image is persisted across activity restarts.
     *
     * Launches the main activity, sets an image, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the image matches what we
     * set it to.
     */
    @Test
    public void testImagePersistedBetweenLaunches() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();

        // initialize bitmap and save button
        final Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.blue_pushpin);
        final Button save = activity.findViewById(R.id.btnSave);

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final byte[] bitmap_value = bos.toByteArray();

        final ImageView imageView = activity.findViewById(R.id.imageProfile);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //set the test bitmap to the image view
                imageView.setImageBitmap(bitmap);

                //click the save button
                save.performClick();
            }
        });

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        //close the activity
        activity.finish();

        // launch a new activity
        mActivityRule.launchActivity(null);

        // get bitmap from new program
        final ImageView imageCheck = activity.findViewById(R.id.imageProfile);
        Bitmap check_Bitmap = ((BitmapDrawable) imageCheck.getDrawable()).getBitmap();

        // Suspends the current thread for 1 second.
        Thread.sleep(2000);

        // convert the bitmap into a byte array for comparison
        bos = new ByteArrayOutputStream();
        check_Bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] check_bitmap_value = bos.toByteArray();

        // compare the two byte arrays to check they are the same
        assertArrayEquals(check_bitmap_value, bitmap_value);
    }


}
