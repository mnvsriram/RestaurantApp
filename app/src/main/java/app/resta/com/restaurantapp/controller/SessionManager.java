package app.resta.com.restaurantapp.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 13/06/2017.
 */
public class SessionManager {
    Map<String, String> preferences;
    private static final String PREF_NAME = "adminPref";

    public SessionManager() {
        preferences = new HashMap<>();
    }


    public void createLoginSession(String username) {
        preferences.put("IS_ADMIN_LOGIN", "true");
        preferences.put("ADMINUSER", username);
    }


    public void createReviewLoginSession(String username) {
        preferences.put("IS_REVIEW_ADMIN_LOGIN", "true");
        preferences.put("ADMINUREVIEWSER", username);
    }


    public boolean getBooleanPreference(String pref) {
        boolean result = false;
        String preference = preferences.get(pref);
        if (preference != null && preference.equalsIgnoreCase("true")) {
            result = true;
        }
        return result;
    }

    public void clearSession() {
        preferences = new HashMap<>();
    }
}
