package edu.cs.dartmouth.myruns2.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserData {

    private SharedPreferences userData;

    /* initializes userData storage structure
     */
    public UserData(Context context) {
        userData = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* all of the setters in order that they appear in the registration window
        except for setUserPhoto, which is at the bottom as it is the most complex of the
        data to set and employ.

        default setUserData left in as null testing option
     */
    public void setUserData(SharedPreferences userData) {
        this.userData = userData;
    }

    public void setUserPhoto(String imagePath) {
        userData.edit().putString("imagePath", imagePath).apply();
    }

    public void setUserName(String userName) {
        userData.edit().putString("username", userName).apply();
    }

    // makes gender correlated with boolean value as a tracker
    public void setUserGender(boolean userGender) {
        userData.edit().putBoolean("gender", userGender).apply();
    }

    public void setUserPassword(String userPassword) {
        userData.edit().putString("password", userPassword).apply();
    }

    public void setUserEmail(String userEmail) {
        userData.edit().putString("email", userEmail).apply();
    }

    public void setUserPhone(String userPhone) {
        userData.edit().putString("phone", userPhone).apply();
    }

    public void setUserMajor(String userMajor) {
        userData.edit().putString("major", userMajor).apply();
    }

    public void setUserClass(String userClass) {
        userData.edit().putString("class", userClass).apply();
    }

    /* all of the getters in order that they appear in the registration window
     */
    public String getUserPhoto() {
        return userData.getString("imagePath", "NA");
    }

    public String getUserName() {
        return userData.getString("username", "NA");
    }

    public boolean getUserGender() {
        return userData.getBoolean("gender", false);
    }

    public String getUserPassword() {
        return userData.getString("password", "NA");
    }

    public String getUserEmail() {
        return userData.getString("email", "NA");
    }

    public String getUserPhone() {
        return userData.getString("phone", "NA");
    }

    public String getUserMajor() {
        return userData.getString("major", "NA");
    }

    public String getUserClass() {
        return userData.getString("class", "NA");
    }

    /* clears all data within the userData structure
     */
    public void clearAll() {
        userData.edit().remove("imagePath");
        userData.edit().remove("username");
        userData.edit().remove("gender");
        userData.edit().remove("password");
        userData.edit().remove("email");
        userData.edit().remove("major");
        userData.edit().remove("class");
    }

}
