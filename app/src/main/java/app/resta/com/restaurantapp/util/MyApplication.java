package app.resta.com.restaurantapp.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Sriram on 13/01/2017.
 */
public class MyApplication extends Application {
    private static Context context;
    public static String TAG = "App";

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
