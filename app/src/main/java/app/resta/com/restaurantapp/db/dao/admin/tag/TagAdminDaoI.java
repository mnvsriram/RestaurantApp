package app.resta.com.restaurantapp.db.dao.admin.tag;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Tag;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface TagAdminDaoI {

    void insertTag(final Tag tag, final OnResultListener<Tag> listener);

    void getTags(final OnResultListener<List<Tag>> listener);

    void deleteTag(String tagId);

    void getTag(String tagId, final OnResultListener<Tag> listener);

    void updateImageUrl(final Tag tag, String imageStorageUrl, final OnResultListener<String> listener);
}
