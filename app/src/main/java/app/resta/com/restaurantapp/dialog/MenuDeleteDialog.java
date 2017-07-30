package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

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
public class MenuDeleteDialog {

    private static ImageButton deleteButton;

    public static void reset() {
        deleteButton = null;
    }

    private MenuItemDao menuItemDao = new MenuItemDao();

    private static boolean showHideControls() {
        boolean isShow = false;
        LoginController loginController = LoginController.getInstance();
        if (loginController.isAdminLoggedIn()) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setFocusable(false);
            deleteButton.setFocusableInTouchMode(false);
            isShow = true;
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        return isShow;
    }

    private static void deleteImages(Activity activity, RestaurantItem item) {
        ImageSaver imageSaver = new ImageSaver(activity);
        if (item.getImages() != null) {
            for (RestaurantImage image : item.getImages()) {
                if (image != null && image.getName() != null && !image.getName().equalsIgnoreCase("noImage")) {
                    imageSaver.deleteImage(image.getName());
                }
            }
        }
    }

    private void delete(final Activity activity, final RestaurantItem item, final int groupPosition) {
        RestaurantImage[] images = item.getImages();
        menuItemDao.deleteMenuItem(item);
        deleteImages(activity, item);
        deleteImagesFromPhone(activity, item.getImages());
        dispatchToMenuPage(activity, item, groupPosition);
        reset();
    }


    private void deleteImagesFromPhone(Activity activity, RestaurantImage[] images) {
        ImageSaver saver = new ImageSaver(activity);
        for (RestaurantImage restaurantImage : images) {
            if (restaurantImage != null && restaurantImage.getName() != null) {
                saver.deleteImage(restaurantImage.getName());
            }
        }
    }

    private static void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition) {
        Intent intent = null;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(activity, NarrowMenuActivity.class);
        } else {
            intent = new Intent(activity, HorizontalMenuActivity.class);
        }
        reset();
        intent.putExtra("groupToOpen", item.getParent().getId());
        intent.putExtra("modifiedItemId", item.getId());
        intent.putExtra("modifiedItemGroupPosition", groupPosition);
        intent.putExtra("modifiedItemChildPosition", -1);
        activity.startActivity(intent);
    }

    private void cancel(DialogInterface dialog) {
        reset();
        dialog.cancel();
    }

    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Activity activity, final RestaurantItem item, final int groupPosition) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage("Do you really want to delete the item - " + item.getName() + "?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete(activity, item, groupPosition);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancel(dialog);
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public  void show(final int buttonId, final View view, final Activity activity, final RestaurantItem item, final int groupPosition) {
        deleteButton = (ImageButton) view.findViewById(buttonId);

        if (showHideControls()) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            activity);
                    buildAlertWindow(alertDialogBuilder, activity, item, groupPosition);
                }
            });
        }
    }
}