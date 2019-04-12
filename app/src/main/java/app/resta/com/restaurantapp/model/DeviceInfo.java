package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class DeviceInfo implements Serializable {
    public static final String FIRESTORE_DEVICE_RESTAURANTID = "restaurantId";
    public static final String FIRESTORE_DEVICE_USERNAME = "userName";
    public static final String FIRESTORE_DEVICE_ID = "deviceId";
    public static final String FIRESTORE_LAST_SYNC_TIME = "lastSyncedTimeStamp";

    private String deviceId;
    private String restaurantName;
    private String restaurantId;
    private String address;
    private String username;
    private String lastSyncedTimeStamp;
//    private String verificationCode;
//    private String currentDevice;

    public String getLastSyncedTimeStamp() {
        return lastSyncedTimeStamp;
    }

    public void setLastSyncedTimeStamp(String lastSyncedTimeStamp) {
        this.lastSyncedTimeStamp = lastSyncedTimeStamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

//    public String getVerificationCode() {
//        return verificationCode;
//    }
//
//    public void setVerificationCode(String verificationCode) {
//        this.verificationCode = verificationCode;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getCurrentDevice() {
//        return currentDevice;
//    }
//
//    public void setCurrentDevice(String currentDevice) {
//        this.currentDevice = currentDevice;
//    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

//    @Override
//    public String toString() {
//        return "DeviceInfo{" +
//                "restaurantName='" + restaurantName + '\'' +
//                ", restaurantId='" + restaurantId + '\'' +
//                ", address='" + address + '\'' +
//                ", username='" + username + '\'' +
//                ", verificationCode='" + verificationCode + '\'' +
//                ", currentDevice='" + currentDevice + '\'' +
//                '}';
//    }


    public static DeviceInfo prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }


    public static DeviceInfo prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static DeviceInfo get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setRestaurantId(FireStoreUtil.getString(keyValueMap, FIRESTORE_DEVICE_RESTAURANTID));
        deviceInfo.setUsername(FireStoreUtil.getString(keyValueMap, FIRESTORE_DEVICE_USERNAME));
        deviceInfo.setDeviceId(FireStoreUtil.getString(keyValueMap, FIRESTORE_DEVICE_ID));
        deviceInfo.setLastSyncedTimeStamp(FireStoreUtil.getString(keyValueMap, FIRESTORE_LAST_SYNC_TIME));
        return deviceInfo;
    }

}
