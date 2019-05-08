package app.resta.com.restaurantapp.db.dao.admin.publisher;

import app.resta.com.restaurantapp.model.DeviceInfo;

/**
 * Created by Sriram on 08/05/2019.
 */

public interface PublisherDaoI {
    void setListenerToPublishedData();

    void publishData(DeviceInfo deviceInfo);
}
