package app.resta.com.restaurantapp.db.dao.user.passwords;

import app.resta.com.restaurantapp.db.listener.OnResultListener;

/**
 * Created by Sriram on 04/04/2019.
 */

public interface PasswordsUserDaoI {
    void getAdminPassword_u(final OnResultListener<String> listener);
    void getWaiterPassword(final OnResultListener<String> listener);

}
