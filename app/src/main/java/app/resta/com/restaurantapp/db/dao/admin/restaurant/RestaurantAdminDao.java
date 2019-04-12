package app.resta.com.restaurantapp.db.dao.admin.restaurant;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;

/**
 * Created by Sriram on 04/04/2019.
 */

public interface RestaurantAdminDao {
    void loadRestaurantInfo(final String restaurantId, final OnResultListener<String> listener);

    void updateRestaurant(final DeviceInfo deviceInfo, final OnResultListener<String> listener);
}
