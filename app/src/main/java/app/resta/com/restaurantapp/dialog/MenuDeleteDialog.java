package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;

import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ImageSaver;

/**
 * Created by Sriram on 26/05/2017.
 */
public abstract class MenuDeleteDialog {
    protected static ImageButton deleteButton;
    protected MenuItemDao menuItemDao = new MenuItemDao();
    protected MenuItemParentDao menuItemParentDao = new MenuItemParentDao();

    protected AuthenticationController authenticationController;

    public static void reset() {
        deleteButton = null;
    }


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

    protected static void deleteImages(Activity activity, RestaurantItem item) {
        ImageSaver imageSaver = new ImageSaver(activity);
        if (item.getImages() != null) {
            for (RestaurantImage image : item.getImages()) {
                if (image != null && image.getName() != null && !image.getName().equalsIgnoreCase("noImage")) {
                    imageSaver.deleteImage(image.getName());
                }
            }
        }
    }


    public abstract void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition);

    private void cancel(DialogInterface dialog) {
        reset();
        dialog.cancel();
    }

    public abstract String buildDeleteConfirmationString(RestaurantItem item);

    public abstract void delete(Activity activity, RestaurantItem item, int groupPosition);

    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Activity activity, final RestaurantItem item, final int groupPosition) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage(buildDeleteConfirmationString(item));
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

    public void show(final int buttonId, final View view, final Activity activity, final RestaurantItem item, final int groupPosition) {
        deleteButton = (ImageButton) view.findViewById(buttonId);
        authenticationController = new AuthenticationController(activity);

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