package app.resta.com.restaurantapp.controller;

import android.view.MenuItem;

import app.resta.com.restaurantapp.cache.RestaurantCache;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.RatingSummaryDao;

/**
 * Created by Sriram on 13/06/2017.
 */
public class LoginController {
    static SessionManager sessionManager = null;
    static LoginController loginController = null;
    private RatingSummaryDao ratingSummaryDao;
    private MenuItemDao menuItemDao;

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
        ratingSummaryDao = new RatingSummaryDao();
        menuItemDao = new MenuItemDao();
    }

    public boolean isAdminLoggedIn() {
        return sessionManager.getBooleanPreference("IS_ADMIN_LOGIN");
    }


    public boolean isReviewAdminLoggedIn() {
        return sessionManager.getBooleanPreference("IS_REVIEW_ADMIN_LOGIN");
    }

    public boolean login(String userName) {
        boolean loginSuccess = false;
        if (isValidAdminUser(userName)) {
            sessionManager.createLoginSession(userName);
            RestaurantCache.refreshCache();
            loginSuccess = true;
        } else if (isValidReviewAdminUser(userName)) {
            sessionManager.createReviewLoginSession(userName);
            loginSuccess = true;
        }
        return loginSuccess;
    }

    public void logout() {
        sessionManager.clearSession();
        RestaurantCache.refreshCache();
        ratingSummaryDao.clearReviewCache();
    }

    public boolean isValidAdminUser(String userName) {
        boolean isValidUser = false;
//go to DB and check if its a valid username.
        if (userName.equalsIgnoreCase("a")) {
            isValidUser = true;
        }
        return isValidUser;
    }


    public boolean isValidReviewAdminUser(String userName) {
        boolean isValidUser = false;
//go to DB and check if its a valid username.
        if (userName.equalsIgnoreCase("r")) {
            isValidUser = true;
        }
        return isValidUser;
    }
}
