package app.resta.com.restaurantapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import java.lang.reflect.Field;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.ImageSaver;

public class AdminLauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_launch);
        setToolbar();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*
        when ever the app is being upgraded or any new images are added which have to be copied to
        the local machine..then add the below commented code to ta place where it will be executed
        once.so that the flag will set to "firstTime" and the function to copy the files to
        the local machine will be called.*/
        /*
        SharedPreferences.Editor editorr = prefs.edit();
        editorr.putBoolean("firstTime", false);
        editorr.commit();
*/
        if (!prefs.getBoolean("firstTime", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

    public void showSettingsPage(View view) {
        authenticationController.goToSettingsPage();
    }

    public void showOrdersPage(View view) {
        authenticationController.goToOrderSummaryPage();
    }

    public void showReviewsMainPage(View view) {
        authenticationController.goToReviewsPage(null);
    }

}