package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.content.Intent;

import app.resta.com.restaurantapp.activity.HorizontalMenuActivity;
import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.StyleUtil;

/**
 * Created by Sriram on 26/05/2017.
 */
public class MenuGroupDeleteDialog extends MenuDeleteDialog {

    public void delete(final Activity activity, final RestaurantItem item, final int groupPosition) {
        menuGroupAdminDao.deleteGroup(item.getId(), item.getMenuTypeId(), new OnResultListener<String>() {
            @Override
            public void onCallback(String status) {
                reset();
                dispatchToMenuPage(activity, item, groupPosition);
            }
        });
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
        intent.putExtra("groupMenuId", item.getMenuTypeId());
        activity.startActivity(intent);
    }

}