package app.resta.com.restaurantapp.db.dao.user.device;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;

public class DeviceUserFireStoreDao extends DeviceAdminFireStoreDao implements DeviceUserDaoI {
    private static final String TAG = "deviceDao";
    FirebaseFirestore db;

    public DeviceUserFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }

    @Override
    public void getThisDeviceDetails_u(OnResultListener<DeviceInfo> listener) {
        getThisDeviceDetails(Source.CACHE, listener);
    }
}
