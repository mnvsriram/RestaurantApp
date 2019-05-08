package app.resta.com.restaurantapp.controller;

import android.widget.Toast;

import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.db.dao.user.passwords.PasswordUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.passwords.PasswordsUserDaoI;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.util.MyApplication;


/**
 * Created by Sriram on 13/06/2017.
 */
public class LoginController {
    static SessionManager sessionManager = null;
    static LoginController loginController = null;
    private PasswordsUserDaoI passwordsAdminDao;

    public static LoginController getInstance() {
        if (loginController == null) {
            loginController = new LoginController();
        }
        return loginController;
    }

    private LoginController() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        passwordsAdminDao = new PasswordUserFireStoreDao();
    }

    public void clearLogin() {
        sessionManager.removeAdminLoginSession();
        sessionManager.removeReviewerLoginSession();
    }

    public boolean isAdminLoggedIn() {
        return sessionManager.getBooleanPreference(SessionManager.SESSION_KEY_IS_ADMIN_LOGIN);
    }


    public boolean isReviewAdminLoggedIn() {
        return sessionManager.getBooleanPreference(SessionManager.SESSION_KEY_IS_REVIEW_ADMIN_LOGIN);
    }

    public static boolean isLicenceValid() {
        return sessionManager.getBooleanPreference("IS_VALID_LICENCE");
    }

    public static void markAsValidLicence() {
        sessionManager.markLicenceValidity("true");
    }

    public static void markAsInvalidLicence() {
        sessionManager.markLicenceValidity("false");
    }

    public static void setRestaurantId(String restaurantId) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_RESTAURANT_ID, restaurantId);
    }

    public static String getRestaurantId() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_RESTAURANT_ID);
    }

    public static void setRestaurantName(String restaurantName) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_RESTAURANT_NAME, restaurantName);
    }

    public static String getRestaurantName() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_RESTAURANT_NAME);
    }

    public static void setRestaurantAddress(String restaurantAddress) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_RESTAURANT_ADDRESS, restaurantAddress);
    }

    public static String getRestaurantAddress() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_RESTAURANT_ADDRESS);
    }

    public static void setUsername(String username) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_USERNAME, username);
    }

    public static String getUsername() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_USERNAME);
    }


    public static void setLastSyncTime(String lastSyncTime) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_LAST_SYNC_TIME, lastSyncTime);
    }

    public static String getLastSyncTime() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_LAST_SYNC_TIME);
    }


    public static void setLatestPublishedDataTime(String latestPublishedDataTime) {
        sessionManager.insertStringToSession(SessionManager.SESSION_KEY_LATEST_PUB_DATA_TIME, latestPublishedDataTime);
    }

    public static String getLatestPublishedDataTime() {
        return sessionManager.getStringPreference(SessionManager.SESSION_KEY_LATEST_PUB_DATA_TIME);
    }

    public boolean waiterLogin(final String userName, final AuthenticationController authenticationController) {
        boolean loginSuccess = false;
        passwordsAdminDao.getWaiterPassword(new OnResultListener<String>() {
            @Override
            public void onCallback(String password) {
                if (userName.equalsIgnoreCase(password)) {
                    sessionManager.createReviewLoginSession(userName);
                    authenticationController.goToReviewerLaunchPage();
                } else {
                    Toast.makeText(MyApplication.getAppContext(), "Not a valid user", Toast.LENGTH_LONG).show();
                }
            }
        });
        return loginSuccess;
    }


    public boolean adminLogin(final String userName, final AuthenticationController authenticationController) {
        boolean loginSuccess = false;
        passwordsAdminDao.getAdminPassword_u(new OnResultListener<String>() {
            @Override
            public void onCallback(String password) {
                if (userName.equalsIgnoreCase(password)) {
                    sessionManager.createLoginSession(userName);
                    authenticationController.goToAdminLaunchPage();
                } else {
                    Toast.makeText(MyApplication.getAppContext(), "Not a valid admin user", Toast.LENGTH_LONG).show();
                }
            }
        });
        return loginSuccess;
    }

    public void logout() {
        clearLogin();
        MenuExpandableListAdapter.setMenuCounter = 1;
    }
}
