package app.resta.com.restaurantapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.reflect.Field;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.ImageSaver;

public class AdminLauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_launch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
            copyAllImages();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }


    private void copyAllImages() {
        ImageSaver saver = new ImageSaver(this);
        final R.drawable drawableResources = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final Field[] fields = c.getDeclaredFields();
        for (int i = 0, max = fields.length; i < max; i++) {
            final int resourceId;
            try {
                resourceId = fields[i].getInt(drawableResources);
            } catch (Exception e) {
                continue;
            }

            String resourceName = getResources().getResourceName(resourceId);
            //to copy any image to local system when installing the app, the image names should contain one definite string...as of now keeping it as review...what ever images you want to copy to the phone, make a text to present in the name of all the images so that we can mentoin in the below condition
            if (resourceName != null && resourceName.contains("review")) {
                if (resourceName.indexOf("/") > -1) {
                    resourceName = resourceName.substring(resourceName.indexOf("/") + 1, resourceName.length());
                    saver.saveImageFromDrawableToLocal(resourceId, resourceName, false);
                }
            }
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