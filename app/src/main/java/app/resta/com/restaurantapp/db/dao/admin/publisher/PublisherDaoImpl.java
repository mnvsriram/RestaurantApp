package app.resta.com.restaurantapp.db.dao.admin.publisher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.db.loader.DataLoader;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

/**
 * Created by Sriram on 08/05/2019.
 */

public class PublisherDaoImpl implements PublisherDaoI {
    private FirebaseFirestore db;
    private final String TAG = "publisherDao";
    private DeviceAdminDaoI deviceAdminDao;

    public PublisherDaoImpl() {
        deviceAdminDao = new DeviceAdminFireStoreDao();
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    @Override
    public void publishData(DeviceInfo deviceInfo) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(DeviceInfo.FIRESTORE_DEVICE_ID, deviceInfo.getDeviceId());
        valueMap.put(DeviceInfo.FIRESTORE_DEVICE_USERNAME, deviceInfo.getUsername());
        valueMap.put(DeviceInfo.FIRESTORE_LAST_SYNC_TIME, FieldValue.serverTimestamp());

        FireStoreLocation.getPublishedDataInformation(db).set(valueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                }
            }
        });
    }

    @Override
    public void setListenerToPublishedData() {
        final DocumentReference docRef = FireStoreLocation.getPublishedDataInformation(db);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if (snapshot != null && snapshot.exists()) {
                    if (source != null && source.equalsIgnoreCase("Server")) {
                        Log.d(TAG, "Current data: " + snapshot.getData());

                        Map<String, Object> data = snapshot.getData();
                        Timestamp t = (Timestamp) data.get(DeviceInfo.FIRESTORE_LAST_SYNC_TIME);
                        String pubTime = "";
                        if (t != null) {
                            pubTime = t.getSeconds() + "";
                        }
                        if (!pubTime.equalsIgnoreCase(RestaurantMetadata.getLatestPubDataTime())) {
                            LoginController.setLatestPublishedDataTime(pubTime);

                            DataLoader dataLoader = new DataLoader();

                            dataLoader.loadData(null, new OnResultListener<String>() {
                                @Override
                                public void onCallback(String status) {
                                    String currentTimeStamp = DateUtil.getCurrentTimeStamp();
                                    DeviceInfo deviceInfo = new DeviceInfo();
                                    deviceInfo.setUsername(RestaurantMetadata.getUsername());
                                    deviceInfo.setLastSyncedTimeStamp(currentTimeStamp);

                                    LoginController.setLastSyncTime(currentTimeStamp);
                                    deviceAdminDao.updateDevice(deviceInfo, new OnResultListener<String>() {
                                        @Override
                                        public void onCallback(String status) {
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.d(TAG, "Ignoring as this device already have the latest data.");
                        }
                    } else {
                        Log.d(TAG, "Ignoring as this event is from local device but not the server.");
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
