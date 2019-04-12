package app.resta.com.restaurantapp.db.dao.user.passwords;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.passwords.PasswordAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;

public class PasswordUserFireStoreDao extends PasswordAdminFireStoreDao implements PasswordsUserDaoI {
    private static final String TAG = "passwordDao";
    FirebaseFirestore db;

    public PasswordUserFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    @Override
    public void getAdminPassword_u(final OnResultListener<String> listener) {
        getAdminPassword(Source.DEFAULT, listener);
    }

    @Override
    public void getWaiterPassword(final OnResultListener<String> listener) {
        getWaiterPassword(Source.DEFAULT, listener);
    }
}
