package app.resta.com.restaurantapp.controller;

/**
 * Created by Sriram on 13/06/2017.
 */
public class LoginController {
    static SessionManager sessionManager = null;

    static LoginController loginController = null;


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
            loginSuccess = true;
        } else if (isValidReviewAdminUser(userName)) {
            sessionManager.createReviewLoginSession(userName);
            loginSuccess = true;
        }
        return loginSuccess;
    }

    public void logout() {
        sessionManager.clearSession();
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
