package app.resta.com.restaurantapp.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 13/06/2017.
 */
public class SessionManager {
    public static final String SESSION_KEY_RESTAURANT_ID = "RESTAURANT_ID";
    public static final String SESSION_KEY_LOGGED_IN_EMAIL = "LOGGED_IN_EMAIL";
    public static final String SESSION_KEY_SUCCESSFULLY_LOGGED_IN_EMAIL = "SUCCESSFULLY_LOGGED_IN_EMAIL";
    public static final String SESSION_KEY_RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String SESSION_KEY_RESTAURANT_ADDRESS = "RESTAURANT_ADDRESS";
    public static final String SESSION_KEY_USERNAME = "USERNAME";
    public static final String SESSION_KEY_LAST_SYNC_TIME = "LASTSYNCTIME";
    public static final String SESSION_KEY_LATEST_PUB_DATA_TIME = "LATESTPUBDATATIME";
    public static final String SESSION_KEY_IS_ADMIN_LOGIN = "IS_ADMIN_LOGIN";
    public static final String SESSION_KEY_IS_REVIEW_ADMIN_LOGIN = "IS_REVIEW_ADMIN_LOGIN";

    SharedPreferences prefs;

    public SessionManager() {
        prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    }

    public void insertStringToSession(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public void createLoginSession(String username) {
        insertStringToSession(SESSION_KEY_IS_ADMIN_LOGIN, "true");
    }


    public void createReviewLoginSession(String username) {
        insertStringToSession(SESSION_KEY_IS_REVIEW_ADMIN_LOGIN, "true");
    }

    public void removeReviewerLoginSession() {
        insertStringToSession(SESSION_KEY_IS_REVIEW_ADMIN_LOGIN, "false");
    }

    public void removeAdminLoginSession() {
        insertStringToSession(SESSION_KEY_IS_ADMIN_LOGIN, "false");
    }

    public void markLicenceValidity(String value) {
        insertStringToSession("IS_VALID_LICENCE", value);
    }

    public String getStringPreference(String pref) {
        return prefs.getString(pref, null);
    }


    public boolean getBooleanPreference(String pref) {
        boolean result = false;
        String preference = prefs.getString(pref, null);
        if (preference != null && preference.equalsIgnoreCase("true")) {
            result = true;
        }
        return result;
    }
}
