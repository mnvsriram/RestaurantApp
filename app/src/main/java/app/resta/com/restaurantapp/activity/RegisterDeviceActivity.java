package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.passwords.PasswordAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.passwords.PasswordsAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.restaurant.RestaurantAdminDao;
import app.resta.com.restaurantapp.db.dao.admin.restaurant.RestaurantAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

public class RegisterDeviceActivity extends BaseActivity {
    private DeviceAdminDaoI deviceAdminDao;
    private RestaurantAdminDao restaurantAdminDao;
    AuthenticationController authenticationController;
    private PasswordsAdminDaoI passwordsAdminDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        setContentView(R.layout.activity_invalid_device);
        routeToTopLevelPage();

    }

    public void reTryLicenceValidation(View view) {
        LoginController.markAsInvalidLicence();
        routeToTopLevelPage();
    }

    private void routeToTopLevelPage() {
        LoginController.getInstance().clearLogin();
        if (LoginController.isLicenceValid() && LoginController.getRestaurantId() != null) {
            authenticationController.goToHomePage();
        } else {
            deviceAdminDao.getThisDeviceDetails(new OnResultListener<DeviceInfo>() {
                @Override
                public void onCallback(final DeviceInfo device) {
                    if (device == null || device.getDeviceId() == null) {
                        TextView invalidMessageText = findViewById(R.id.statusMessageText);
                        invalidMessageText.setText("This device with id " + RestaurantMetadata.getDeviceId() + " is not registerd. Please talk to the Support for registering the device " + RestaurantMetadata.getDeviceId() + ". Once the device is registered, please close the app and open again.");
                    } else if (device.getRestaurantId() == null) {
                        TextView invalidMessageText = findViewById(R.id.statusMessageText);
                        invalidMessageText.setText("There device (" + RestaurantMetadata.getDeviceId() + ") is not subscribed to any restaurant. Please talk to the support to get this device subscribed to a Restaurant");
                    } else {
                        LoginController.setRestaurantId(device.getRestaurantId());
                        String username = device.getUsername();
                        if (username == null) {
                            username = device.getDeviceId();
                        }
                        LoginController.setUsername(username);
                        LoginController.setLastSyncTime(device.getLastSyncedTimeStamp());
                        passwordsAdminDao.getAdminPassword(new OnResultListener<String>() {
                            @Override
                            public void onCallback(String password) {
                                if (password == null) {
                                    TextView invalidMessageText = findViewById(R.id.statusMessageText);
                                    invalidMessageText.setText("The admin passwords are not set for the registerd restaurant with id " + device.getRestaurantId() + ". Please talk to the support. ");
                                } else {
                                    passwordsAdminDao.getWaiterPassword(new OnResultListener<String>() {
                                        @Override
                                        public void onCallback(String password) {
                                            if (password == null) {
                                                TextView invalidMessageText = findViewById(R.id.statusMessageText);
                                                invalidMessageText.setText("The passwords (reviewer) are not set for the registerd restaurant with id " + device.getRestaurantId() + ". Please talk to the support. ");
                                            } else {
                                                restaurantAdminDao.loadRestaurantInfo(device.getRestaurantId(), new OnResultListener<String>() {
                                                    @Override
                                                    public void onCallback(String status) {
                                                        if (status == null) {
                                                            TextView invalidMessageText = findViewById(R.id.statusMessageText);
                                                            invalidMessageText.setText("This device with id " + RestaurantMetadata.getDeviceId() + " is subscribed to a restaurant with id " + device.getRestaurantId() + " which is not valid. Please talk to the support team.");
                                                        } else {
                                                            TextView statusText = findViewById(R.id.statusMessageText);
                                                            LoginController.markAsValidLicence();
                                                            authenticationController.goToHomePage();
//                                                            DataLoader dataLoader = new DataLoader();
//                                                            dataLoader.loadData(statusText, new OnResultListener<String>() {
//                                                                @Override
//                                                                public void onCallback(String status) {
//                                                                    authenticationController.goToHomePage();
//                                                                }
//                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    private void initialize() {
        deviceAdminDao = new DeviceAdminFireStoreDao();
        restaurantAdminDao = new RestaurantAdminFirestoreDao();
        authenticationController = new AuthenticationController(this);
        passwordsAdminDao = new PasswordAdminFireStoreDao();
//        deviceDetailsValidator = new DeviceDetailsValidator(this);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MyApplication.getAppContext(), "Please register before going ahead.", Toast.LENGTH_LONG).show();
    }

//    public void save(View view) {
//        deviceAdminDao.isValidDevice(new OnResultListener<String>() {
//            @Override
//            public void onCallback(String restaurantId) {
//                if (restaurantId != null) {
//                    deviceInfo = new DeviceInfo();
//                    getUsername();
//                    if (deviceDetailsValidator.validate(deviceInfo, registration)) {
//                        deviceAdminDao.updateDevice(deviceInfo, new OnResultListener<String>() {
//                            @Override
//                            public void onCallback(String tags) {
//                                authenticationController.goToHomePage();
//                            }
//                        });
//
//                    }
//                }
//            }
//        });
//    }
//
//    public void update(View view) {
//        getUsername();
//        if (deviceDetailsValidator.validate(deviceInfo, registration)) {
//            deviceAdminDao.updateDevice(deviceInfo, new OnResultListener<String>() {
//                @Override
//                public void onCallback(String tags) {
//                    authenticationController.goToMenuClusterNetworkSettingsPage(null);
//                }
//            });
//
//        }
//    }
//
//    private void getUsername() {
//        EditText username = (EditText) findViewById(R.id.usernameText);
//        deviceInfo.setUsername(username.getText().toString());
//    }
}