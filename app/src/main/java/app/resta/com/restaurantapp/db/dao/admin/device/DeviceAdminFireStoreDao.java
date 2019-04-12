package app.resta.com.restaurantapp.db.dao.admin.device;

import android.provider.Settings;
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
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

public class DeviceAdminFireStoreDao implements DeviceAdminDaoI {
    private static final String TAG = "deviceDao";
    FirebaseFirestore db;

    public DeviceAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }

    protected void getThisDeviceDetails(Source source, final OnResultListener<DeviceInfo> listener) {
        String thisDeviceId = RestaurantMetadata.getDeviceId();
        FireStoreLocation.getDevicesRootLocation(db).document(thisDeviceId).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    listener.onCallback(DeviceInfo.prepare(result));
                } else {
                    listener.onCallback(null);
                }
            }
        });
    }


    @Override
    public void getThisDeviceDetails(final OnResultListener<DeviceInfo> listener) {
//        getThisDeviceDetails(Source.CACHE, new OnResultListener<DeviceInfo>() {
//            @Override
//            public void onCallback(DeviceInfo deviceInfo) {
//                if (deviceInfo==null || deviceInfo.getDeviceId() == null) {
//                    getThisDeviceDetails(Source.DEFAULT, listener);
//                }else{
//                    listener.onCallback(deviceInfo);
//                }
//            }
//        });
//
//
        getThisDeviceDetails(Source.DEFAULT, listener);
    }
//
//    @Override
//    public void isValidDevice(final OnResultListener<String> listener) {
//        isValidDevice(Source.CACHE, new OnResultListener<String>() {
//            @Override
//            public void onCallback(String restaurantId) {
//                if (restaurantId == null) {
//                    isValidDevice(Source.DEFAULT, listener);
//                }
//            }
//        });
//    }
//
//    private void isValidDevice(Source source, final OnResultListener<String> listener) {
//        String thisDeviceId = Settings.Secure.getString(MyApplication.getAppContext().getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        FireStoreLocation.getDevicesRootLocation(db).document(thisDeviceId).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot result = task.getResult();
//                    if (result.exists()) {
//                        Map<String, Object> data = task.getResult().getData();
//                        String restaurantId = FireStoreUtil.getString(data, "restaurantId");
//                        listener.onCallback(restaurantId);
//                        return;
//                    }
//                }
//                listener.onCallback(null);
//            }
//        });
//    }
//

    public void updateDevice(final DeviceInfo deviceInfo, final OnResultListener<String> listener) {
        String thisDeviceId = Settings.Secure.getString(MyApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(DeviceInfo.FIRESTORE_DEVICE_USERNAME, deviceInfo.getUsername());
        valueMap.put(DeviceInfo.FIRESTORE_LAST_SYNC_TIME, deviceInfo.getLastSyncedTimeStamp());

        FireStoreLocation.getDevicesRootLocation(db).document(thisDeviceId).set(valueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    LoginController.setUsername(deviceInfo.getUsername());
                    listener.onCallback("updated");
                } else {
                    listener.onCallback("error");
                }
            }
        });
    }

}
