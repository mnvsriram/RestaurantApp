package app.resta.com.restaurantapp.db.dao.user.device;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface DeviceUserDaoI {
    void getThisDeviceDetails_u(final OnResultListener<DeviceInfo> listener);
}
