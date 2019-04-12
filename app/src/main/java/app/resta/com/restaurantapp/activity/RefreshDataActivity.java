package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.db.loader.DataLoader;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.DateUtil;

public class RefreshDataActivity extends BaseActivity {
    AuthenticationController authenticationController;
    DeviceInfo deviceInfo = new DeviceInfo();
    DeviceAdminDaoI deviceAdminDao = new DeviceAdminFireStoreDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        setContentView(R.layout.activity_refresh_data);
        loadIntentParams();
        setFields();
    }

    private void initialize() {
        authenticationController = new AuthenticationController(this);
    }


    private void loadIntentParams() {
        Intent intent = getIntent();
        if (intent.hasExtra("thisDevice_details")) {
            deviceInfo = (DeviceInfo) intent.getSerializableExtra("thisDevice_details");
        }
    }

    private void setFields() {
        setSyncDataTime();
    }

    public void syncData(View view) {
        final TextView syncStatus = findViewById(R.id.syncStatus);
        syncStatus.setText("Please wait while the data is being synced");

        final Button goBackButton = findViewById(R.id.refreshData_goBack);
        goBackButton.setText("Please wait ..");
        goBackButton.setEnabled(false);

        final Button syncButton = findViewById(R.id.syncDataFromServer);
        syncButton.setEnabled(false);
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData(syncStatus, new OnResultListener<String>() {
            @Override
            public void onCallback(String status) {
                deviceInfo.setLastSyncedTimeStamp(DateUtil.getCurrentTimeStamp());
                deviceAdminDao.updateDevice(deviceInfo, new OnResultListener<String>() {
                    @Override
                    public void onCallback(String status) {
                        syncStatus.setText("Data sync completed.(Images will be downloaded in the background.)");
                        syncButton.setEnabled(true);

                        goBackButton.setEnabled(true);
                        goBackButton.setText("Go Back");
                    }
                });
            }
        });
    }

    private void setSyncDataTime() {
        TextView syncDataTime = findViewById(R.id.syncDataTime);
        syncDataTime.setText(deviceInfo.getLastSyncedTimeStamp());
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuClusterNetworkSettingsPage(null);
    }

}