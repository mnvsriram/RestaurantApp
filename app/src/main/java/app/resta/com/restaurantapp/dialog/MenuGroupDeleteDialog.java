package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

import app.resta.com.restaurantapp.activity.HorizontalMenuActivity;
import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.StyleUtil;

/**
 * Created by Sriram on 26/05/2017.
 */
public class MenuGroupDeleteDialog extends MenuDeleteDialog {

    public void delete(final Activity activity, final RestaurantItem item, final int groupPosition) {
        RestaurantImage[] images = item.getImages();
        menuItemDao.deleteMenuItem(item);
        deleteImages(activity, item);
        deleteImagesFromPhone(activity, item.getImages());
        dispatchToMenuPage(activity, item, groupPosition);
        reset();
    }


    private void deleteImagesFromPhone(Activity activity, RestaurantImage[] images) {
    }


    public String buildDeleteConfirmationString(RestaurantItem item) {
        String message = "\nAre you sure you want to delete? ";
        return message;
    }

    public void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition) {
        Intent intent = null;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(activity, NarrowMenuActivity.class);
        } else {
            intent = new Intent(activity, HorizontalMenuActivity.class);
        }
        reset();
        if (item.getParent() != null) {
            intent.putExtra("groupToOpen", item.getParent().getId());
        }
        intent.putExtra("modifiedItemId", item.getId());
        intent.putExtra("modifiedItemGroupPosition", groupPosition);
        intent.putExtra("modifiedItemChildPosition", -1);
        activity.startActivity(intent);
    }

}