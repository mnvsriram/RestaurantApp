package app.resta.com.restaurantapp.util;

import java.util.Map;

/**
 * Created by Sriram on 13/01/2019.
 */

public class FireStoreUtil {
    public static String getString(Map<String, Object> keyValueMap, String fireStoreKey) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((String) keyValueMap.get(fireStoreKey)) : null;
    }

    public static String getString(Map<String, Object> keyValueMap, String fireStoreKey, String defaultValue) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((String) keyValueMap.get(fireStoreKey)) : defaultValue;
    }


    public static Long getLong(Map<String, Object> keyValueMap, String fireStoreKey) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Long) keyValueMap.get(fireStoreKey)) : null;
    }


    public static Long getLong(Map<String, Object> keyValueMap, String fireStoreKey, Long defaultValue) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Long) keyValueMap.get(fireStoreKey)) : defaultValue;
    }

    public static Integer getInt(Map<String, Object> keyValueMap, String fireStoreKey) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Long) keyValueMap.get(fireStoreKey)).intValue() : null;
    }


    public static Integer getInt(Map<String, Object> keyValueMap, String fireStoreKey, Integer defaultValue) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Long) keyValueMap.get(fireStoreKey)).intValue() : defaultValue;
    }

    public static Double getDouble(Map<String, Object> keyValueMap, String fireStoreKey) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Double) keyValueMap.get(fireStoreKey)) : null;
    }

//
//    public static Date getDate(Map<String, Object> keyValueMap, String fireStoreKey) {
//        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Date) keyValueMap.get(fireStoreKey)) : null;
//    }

    public static boolean getBoolean(Map<String, Object> keyValueMap, String fireStoreKey) {
        return (keyValueMap != null && fireStoreKey != null && keyValueMap.get(fireStoreKey) != null) ? ((Boolean) keyValueMap.get(fireStoreKey)) : false;
    }


    public static String getImageUrl(Map<String, Object> keyValueMap, String fireStoreKey) {
        String imageUrl = getString(keyValueMap, fireStoreKey);
        return (imageUrl != null && imageUrl.length() > 0) ? imageUrl : FireBaseStorageLocation.getNoImageLocation();
    }

}
