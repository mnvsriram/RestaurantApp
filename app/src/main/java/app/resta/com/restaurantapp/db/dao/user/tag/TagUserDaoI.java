package app.resta.com.restaurantapp.db.dao.user.tag;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Tag;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface TagUserDaoI {
    void getTag_u(String tagId, final OnResultListener<Tag> listener);
}
