package app.resta.com.restaurantapp.db.dao.admin.device;

import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface DeviceAdminDaoI {
    void getThisDeviceDetails(final OnResultListener<DeviceInfo> listener);

    void isValidEmail(String email,Source source, final OnResultListener<String> listener);

    void updateDevice(DeviceInfo deviceInfo, final OnResultListener<String> listener);

    void addCommentForExitLockMode(final String comment, final OnResultListener<String> listener);
}
