package app.resta.com.restaurantapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;

public class TopLevelActivity extends BaseActivity {
    private static String TAG = "TopLevelActivity";
    public static final int PERMISSIONS_REQUEST_CAMERA_USE = 220;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level_new);
        setToolbar();
        getPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA_USE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Camera permission granted");
            } else {
                Log.e(TAG, "Camera permission not granted.");
            }
        }
    }

    private void getPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CAMERA_USE);
        }


    }

    public void showFoodMenu(View view) {
        Map<String, Object> params = new HashMap<>();
//        params.put("groupToOpen", 0L);
        params.put("groupMenuId", "1");
        authenticationController.goToMenuPage(params);
    }

    public void showDrinksMenu(View view) {
        Map<String, Object> params = new HashMap<>();
//        params.put("groupToOpen", 0l);
        params.put("groupMenuId", "2");
        authenticationController.goToMenuPage(params);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

}