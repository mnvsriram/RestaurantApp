package app.resta.com.restaurantapp.util;

import android.content.res.AssetManager;
import android.view.View;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Nirmal Dhara on 12-07-2015.
 */
public class RestaurantUtil {

    public static void setImage(View view, int id, int width, int height) {
        view.setBackgroundResource(id);
        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        int widthDp = (int) (width * scale + 0.5f);
        int heightDp = (int) (height * scale + 0.5f);
        view.getLayoutParams().width = widthDp;
        view.getLayoutParams().height = heightDp;
    }
}