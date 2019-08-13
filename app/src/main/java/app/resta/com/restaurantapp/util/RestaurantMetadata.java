package app.resta.com.restaurantapp.util;

import android.provider.Settings;

import app.resta.com.restaurantapp.controller.LoginController;

/**
 * Created by Sriram on 31/12/2018.
 */

public class RestaurantMetadata {
    private static String restaurantId;
    private static String username;
    private static String restaurantAddress;
    private static String restaurantName;

    public static String getDeviceId() {
        String thisDeviceId = Settings.Secure.getString(MyApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return thisDeviceId;
    }


    public static String getRestaurantId() {
        if (restaurantId == null) {
            restaurantId = LoginController.getRestaurantId();
        }
        return restaurantId;
    }

    public static String getUsername() {
//        if (username == null) {
        username = LoginController.getUsername();
//        }
        return username;
    }


    public static String getLastSyncTime() {
        return LoginController.getLastSyncTime();
    }


    public static String
    getLatestPubDataTime() {
        return LoginController.getLatestPublishedDataTime();
    }

    public static String getRestaurantAddress() {
//        if (restaurantAddress == null) {
        restaurantAddress = LoginController.getRestaurantAddress();
//        }
        return restaurantAddress;
    }

    public static String getRestaurantName() {
//        if (restaurantName == null) {
        restaurantName = LoginController.getRestaurantName();
//        }
        return restaurantName;
    }
}
