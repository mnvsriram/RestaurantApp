package app.resta.com.restaurantapp.db.dao.admin.passwords;

import app.resta.com.restaurantapp.db.listener.OnResultListener;

/**
 * Created by Sriram on 04/04/2019.
 */

public interface PasswordsAdminDaoI {
    void getAdminPassword(final OnResultListener<String> listener);
    void getWaiterPassword(final OnResultListener<String> listener);

}
