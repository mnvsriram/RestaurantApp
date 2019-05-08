package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;

import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ImageUtil;


/**
 * Created by Sriram on 26/05/2017.
 */
public abstract class MenuDeleteDialog {
    protected static ImageButton deleteButton;
    MenuItemAdminDaoI menuItemAdminDao = new MenuItemAdminFireStoreDao();
    MenuGroupAdminDaoI menuGroupAdminDao = new MenuGroupAdminFireStoreDao();
    protected AuthenticationController authenticationController;

    static void reset() {
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

    static void deleteImage(String url) {
        ImageUtil.deleteImage(url, new OnResultListener<Object>() {
            @Override
            public void onCallback(Object object) {

            }
        });

    }

    static void deleteImages(RestaurantItem item) {
        deleteImage(item.getItemImage1().getStorageUrl());
        deleteImage(item.getItemImage2().getStorageUrl());
        deleteImage(item.getItemImage3().getStorageUrl());
    }


    public abstract void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition);

    private void cancel(DialogInterface dialog) {
        reset();
        dialog.cancel();
    }

    public abstract String buildDeleteConfirmationString(RestaurantItem item);

    public abstract void delete(Activity activity, RestaurantItem item, int groupPosition, final MenuType menuType);

    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Activity activity, final RestaurantItem item, final int groupPosition, final MenuType menuType) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage(buildDeleteConfirmationString(item));
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete(activity, item, groupPosition, menuType);
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

    public void show(final int buttonId, final View view, final Activity activity, final RestaurantItem item, final int groupPosition, final MenuType menuType) {
        deleteButton = view.findViewById(buttonId);
        authenticationController = new AuthenticationController(activity);

        if (showHideControls()) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            activity);
                    buildAlertWindow(alertDialogBuilder, activity, item, groupPosition, menuType);
                }
            });
        }
    }
}