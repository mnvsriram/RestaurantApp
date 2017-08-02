package app.resta.com.restaurantapp.dialog;

import android.app.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ImageSaver;

/**
 * Created by Sriram on 26/05/2017.
 */
public class MenuItemDeleteDialog extends MenuDeleteDialog {

    private MenuItemParentDao menuItemParentDao = new MenuItemParentDao();

    public void delete(final Activity activity, final RestaurantItem item, final int groupPosition) {
        if (item.getParent() != null && item.getParent().getId() > 0) {
            menuItemParentDao.deleteMappingsForItem(item.getId(), item.getParent().getId());
        } else {
            deleteItem(activity, item, groupPosition);
        }
        dispatchToMenuPage(activity, item, groupPosition);
        reset();
    }

    private void deleteItem(final Activity activity, final RestaurantItem item, final int groupPosition) {
        menuItemParentDao.deleteAllMappingsForItem(item.getId());
        menuItemDao.deleteMenuItem(item);
        deleteImages(activity, item);
        deleteImagesFromPhone(activity, item.getImages());
    }


    private void deleteImagesFromPhone(Activity activity, RestaurantImage[] images) {
        if (images != null) {
            ImageSaver saver = new ImageSaver(activity);
            for (RestaurantImage restaurantImage : images) {
                if (restaurantImage != null && restaurantImage.getName() != null) {
                    saver.deleteImage(restaurantImage.getName());
                }
            }
        }
    }


    public String buildDeleteConfirmationString(RestaurantItem item) {
        String groups = "";
        List<RestaurantItem> parents = menuItemDao.getParents(item);
        String message = "";

        if (item.getParent() != null && item.getParent().getId() > 0) {
            message = "This action will remove '" + item.getName() + "' from '" + item.getParent().getName() + "' group.";
        } else {
            for (RestaurantItem parent : parents) {
                groups += parent.getName() + "- " + parent.getMenuTypeName() + "\n";
            }
            if (groups.length() > 0) {
                message = "'" + item.getName() + "' is part of the below groups. Deleting this item will remove it from all of the below groups: \n\n ";
                message += groups;
            }

        }

        message += "\nAre you sure you want to delete? ";
        return message;
    }


    public void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition) {
        reset();
        Map<String, Object> params = new HashMap<>();
        params.put("modifiedItemId", item.getId());
        params.put("modifiedItemGroupPosition", groupPosition);
        params.put("modifiedItemChildPosition", -1);


        if (item.getParent() == null || item.getParent().getId() <= 0) {
            params.put("groupToOpen", 0l);
            params.put("groupMenuId", -1l);
        } else {
            params.put("groupMenuId", item.getParent().getMenuTypeId());
            params.put("groupToOpen", item.getParent().getId());
        }


        authenticationController.goToMenuPage(params);
    }


}