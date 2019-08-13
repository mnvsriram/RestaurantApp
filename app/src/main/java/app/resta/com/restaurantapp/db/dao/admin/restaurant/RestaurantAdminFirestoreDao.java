package app.resta.com.restaurantapp.db.dao.admin.restaurant;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.FireStoreUtil;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

/**
 * Created by Sriram on 04/04/2019.
 */

public class RestaurantAdminFirestoreDao implements RestaurantAdminDao {
    private static final String TAG = "restDao";
    FirebaseFirestore db;

    public RestaurantAdminFirestoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    @Override
    public void loadRestaurantInfo(final String restaurantId, final OnResultListener<String> listener) {
        getRestaurantInfo(restaurantId, Source.DEFAULT, listener);

    }


    public void updateRestaurant(final DeviceInfo deviceInfo, final OnResultListener<String> listener) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("name", deviceInfo.getRestaurantName());
        valueMap.put("address", deviceInfo.getAddress());
        FireStoreLocation.getRestaurantsRootLocation(db).document(RestaurantMetadata.getRestaurantId()).set(valueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onCallback("updated");
                    LoginController.setRestaurantName(deviceInfo.getRestaurantName());
                    LoginController.setRestaurantAddress(deviceInfo.getAddress());
                } else {
                    listener.onCallback("error");
                }
            }
        });
    }

    private void getRestaurantInfo(final String restaurantId, Source source, final OnResultListener<String> listener) {
        FireStoreLocation.getRestaurantsRootLocation(db).document(restaurantId).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    LoginController.setRestaurantName(FireStoreUtil.getString(data, "name"));
                    LoginController.setRestaurantAddress(FireStoreUtil.getString(data, "address"));
                    listener.onCallback(restaurantId);
                    return;
                }
                listener.onCallback(null);
            }
        });
    }
}
