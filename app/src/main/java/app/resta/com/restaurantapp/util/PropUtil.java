package app.resta.com.restaurantapp.util;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Nirmal Dhara on 12-07-2015.
 */
public class PropUtil {
    static Properties colorProperties = new Properties();
    static Properties imageProperties = new Properties();
    static Properties configProperties = new Properties();
    static Properties layoutProperties = new Properties();

    static {
        AssetManager assetManager = MyApplication.getAppContext().getAssets();
        try {
            InputStream colorInputStream = assetManager.open("color.properties");
            colorProperties.load(colorInputStream);

            InputStream imageInputStream = assetManager.open("image.properties");
            imageProperties.load(imageInputStream);

            InputStream layoutInputStream = assetManager.open("layout.properties");
            layoutProperties.load(layoutInputStream);

            InputStream configInputStream = assetManager.open("config.properties");
            configProperties.load(configInputStream);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getColorProperties() {
        return getProperties("color");
    }

    public static Map<String, String> getImageProperties() {
        return getProperties("image");
    }

    public static Map<String, String> getLayoutProperties() {
        return getProperties("layout");
    }

    public static Map<String, String> getConfigProperties() {
        return getProperties("config");
    }

    private static Map<String, String> getProperties(String type) {
        Enumeration e = null;
        Map<String, String> propsMap = new HashMap<String, String>();
        Properties props = null;
        if (type != null) {
            switch (type) {
                case "color":
                    props = colorProperties;
                    break;
                case "layout":
                    props = layoutProperties;
                    break;
                case "image":
                    props = imageProperties;
                    break;
                case "config":
                    props = configProperties;
                    break;
            }
        }
        if (props != null) {
            e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                propsMap.put(key, props.getProperty(key));
            }
        }
        return propsMap;
    }

}