package app.resta.com.restaurantapp.db.dao.admin.passwords;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Map;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.FireStoreUtil;

public class PasswordAdminFireStoreDao implements PasswordsAdminDaoI {
    private static final String TAG = "passwordDao";
    FirebaseFirestore db;

    public PasswordAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    protected void getAdminPassword(Source source, final OnResultListener<String> listener) {
        getPassword(listener, FireStoreLocation.getAdminPasswordLocation(db));
    }


    protected void getWaiterPassword(Source source, final OnResultListener<String> listener) {
        getPassword(listener, FireStoreLocation.getWaiterPasswordLocation(db));
    }

    private void getPassword(final OnResultListener<String> listener, DocumentReference passwordLocation) {
        passwordLocation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    listener.onCallback(FireStoreUtil.getString(data, "password"));
                    return;
                }
                listener.onCallback(null);
            }
        });
    }

    @Override
    public void getAdminPassword(final OnResultListener<String> listener) {
        getAdminPassword(Source.DEFAULT, listener);
    }

    @Override
    public void getWaiterPassword(final OnResultListener<String> listener) {
        getWaiterPassword(Source.DEFAULT, listener);
    }
}
