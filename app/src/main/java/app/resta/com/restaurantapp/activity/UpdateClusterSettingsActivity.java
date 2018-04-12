package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import app.resta.com.restaurantapp.R;

public class UpdateClusterSettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_updates_cluster);
        setToolbar();
        loadIntentParams();
        setFields();
    }

    private void setFields() {
        setUpdatesToPullButton();
        setUpdatesToPushButton();
        setNumberOfDevicesConnected();
        setDeviceDetails();
        setUpdateTable();
    }

    private void setDeviceDetails() {

    }

    private void setUpdateTable() {

    }

    private void setUpdatesToPullButton() {

    }

    private void setUpdatesToPushButton() {

    }

    private void setNumberOfDevicesConnected() {

    }

    public void viewOtherDevices(View view) {
        authenticationController.goToOtherDevicesDetailsPage();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }
}
