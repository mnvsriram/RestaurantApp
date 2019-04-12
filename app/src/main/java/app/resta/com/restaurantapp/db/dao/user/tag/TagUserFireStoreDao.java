package app.resta.com.restaurantapp.db.dao.user.tag;

import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Tag;

/**
 * Created by Sriram on 02/01/2019.
 */

public class TagUserFireStoreDao extends TagAdminFireStoreDao implements TagUserDaoI {

    private static final String TAG = "tagUserDao";

    public void getTag_u(final String tagId, final OnResultListener<Tag> listener) {
        getTag(tagId, Source.CACHE, listener);
    }
}
