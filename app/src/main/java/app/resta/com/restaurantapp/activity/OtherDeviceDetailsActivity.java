package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;

import app.resta.com.restaurantapp.R;

public class OtherDeviceDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_device_details);
        setToolbar();
        loadIntentParams();
        setFields();
    }

    private void setFields() {
        setDeviceTable();
    }

    private void setDeviceTable() {

    }

    private void loadIntentParams() {
        Intent intent = getIntent();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuClusterNetworkSettingsPage(null);
    }
}
