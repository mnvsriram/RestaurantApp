package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.restaurant.RestaurantAdminDao;
import app.resta.com.restaurantapp.db.dao.admin.restaurant.RestaurantAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.validator.DeviceDetailsValidator;

public class UpdateDeviceActivity extends BaseActivity {
    private DeviceAdminDaoI deviceAdminDao;
    private RestaurantAdminDao restaurantAdminDao;
    AuthenticationController authenticationController;
    DeviceDetailsValidator deviceDetailsValidator;
    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        setContentView(R.layout.activity_update_device);
        loadIntentParams();
        setFields();
    }

    private void initialize() {
        deviceInfo = new DeviceInfo();
//        deviceAdminDao.getThisDeviceDetails(new OnResultListener<DeviceInfo>() {
//            @Override
//            public void onCallback(DeviceInfo thisDeviceInfo) {
//                deviceInfo = thisDeviceInfo;
//                setFields();
//            }
//        });
        restaurantAdminDao = new RestaurantAdminFirestoreDao();
        deviceAdminDao = new DeviceAdminFireStoreDao();
        authenticationController = new AuthenticationController(this);
        deviceDetailsValidator = new DeviceDetailsValidator(this);
    }


    private void loadIntentParams() {
        Intent intent = getIntent();
        if (intent.hasExtra("thisDevice_details")) {
            deviceInfo = (DeviceInfo) intent.getSerializableExtra("thisDevice_details");
        }
    }

    private void setFields() {
        setUsername();
        setRestaurantAddress();
        setRestaurantId();
        setRestaurantName();
        setDeviceId();
    }

    private void setRestaurantId() {
        TextView restaurantIdText = findViewById(R.id.restaurantIdText);
        restaurantIdText.setText(deviceInfo.getRestaurantId());
    }

    private void setUsername() {
        EditText usernameText = findViewById(R.id.usernameText);
        usernameText.setText(deviceInfo.getUsername());
    }

    private void setRestaurantName() {
        EditText restaurantName = findViewById(R.id.restaurantText);
        restaurantName.setText(deviceInfo.getRestaurantName());
    }

    private void setRestaurantAddress() {
        EditText addressLineOneText = findViewById(R.id.addressLineOneText);
        addressLineOneText.setText(deviceInfo.getAddress());
    }

    private void setDeviceId() {
        TextView deviceIdText = findViewById(R.id.deviceIdText);
        deviceIdText.setText(deviceInfo.getDeviceId());
    }


    @Override
    public void onBackPressed() {
        authenticationController.goToMenuClusterNetworkSettingsPage(null);
    }

    public void goBackFromRegisterButton(View view) {
        onBackPressed();
    }

    public void update(View view) {
        getUsername();
        getRestaurantAddress();
        getRestaurantName();
        if (deviceDetailsValidator.validate(deviceInfo)) {
            restaurantAdminDao.updateRestaurant(deviceInfo, new OnResultListener<String>() {
                @Override
                public void onCallback(String tags) {
                    deviceAdminDao.updateDevice(deviceInfo, new OnResultListener<String>() {
                        @Override
                        public void onCallback(String tags) {
                            authenticationController.goToMenuClusterNetworkSettingsPage(null);
                        }
                    });
                }
            });
        }
    }

    private void getUsername() {
        EditText username = findViewById(R.id.usernameText);
        deviceInfo.setUsername(username.getText().toString());
    }

    private void getRestaurantAddress() {
        EditText address = findViewById(R.id.addressLineOneText);
        deviceInfo.setAddress(address.getText().toString());
    }

    private void getRestaurantName() {
        EditText restaurantText = findViewById(R.id.restaurantText);
        deviceInfo.setRestaurantName(restaurantText.getText().toString());
    }

}