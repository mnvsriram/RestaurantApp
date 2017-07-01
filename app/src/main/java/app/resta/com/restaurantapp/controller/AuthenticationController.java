package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.AdminLauncherActivity;
import app.resta.com.restaurantapp.activity.HorizontalMenuActivity;
import app.resta.com.restaurantapp.activity.IngredientsActivity;
import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.activity.OrderActivity;
import app.resta.com.restaurantapp.activity.OrdersViewActivity;
import app.resta.com.restaurantapp.activity.SettingsActivity;
import app.resta.com.restaurantapp.activity.TagsActivity;
import app.resta.com.restaurantapp.activity.TopLevelActivity;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

/**
 * Created by Sriram on 18/06/2017.
 */
public class AuthenticationController {
    private Activity activity;
    private LoginController loginController;

    public AuthenticationController(Activity activity) {
        this.activity = activity;
        loginController = LoginController.getInstance();
    }


    public void loginForAdmin(final MenuItem item) {
        LayoutInflater li = LayoutInflater.from(activity);
        final View promptsView = li.inflate(R.layout.admin_login_dialog, null);


        if (loginController.isAdminLoggedIn() || loginController.isReviewAdminLoggedIn()) {
            loginController.logout();
            item.setIcon(R.drawable.login);
            goToHomePage();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText userNamePass = (EditText) promptsView.findViewById(R.id.adminUserNamePass);
                                String adminUserName = userNamePass.getText().toString();

                                if (loginController.login(adminUserName)) {
                                    if (loginController.isAdminLoggedIn()) {
                                        item.setIcon(R.drawable.admin);
                                        //goToMenuPage();
                                        goToAdminLaunchPage();
                                    } else if (loginController.isReviewAdminLoggedIn()) {
                                        //set review user icon
                                        item.setIcon(R.drawable.admin);
                                        goToReviewMenuPage();
                                    }
                                } else {
                                    loginController.logout();
                                    dialog.cancel();
                                    Toast.makeText(MyApplication.getAppContext(), "You are not authorix", Toast.LENGTH_LONG);
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /*
        public void goToMenuPage() {
            Intent intent = null;
            String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
            if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
                intent = new Intent(MyApplication.getAppContext(), NarrowMenuActivity.class);
                intent.putExtra("modifiedItemId", -1l);

            } else {
                intent = new Intent(MyApplication.getAppContext(), HorizontalMenuActivity.class);
            }
            activity.startActivity(intent);
        }
    */

    public void goToMenuPage() {
        Intent intent = null;
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        } else {
            String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
            if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
                intent = new Intent(MyApplication.getAppContext(), NarrowMenuActivity.class);
            } else {
                intent = new Intent(MyApplication.getAppContext(), HorizontalMenuActivity.class);
            }
        }
        activity.startActivity(intent);
    }

    private void goToReviewMenuPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        intent.putExtra("modifiedItemId", -1l);
        activity.startActivity(intent);
    }


    public void goToHomePage() {
        if (LoginController.getInstance().isAdminLoggedIn()) {
            goToAdminLaunchPage();
        } else {
            goToTopLevelPage();
        }
    }


    public void goBackFromMenuPage() {
        if (LoginController.getInstance().isAdminLoggedIn()) {
            goToSettingsPage();
        } else {
            goToTopLevelPage();
        }
    }


    private void goToTopLevelPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), TopLevelActivity.class);
        activity.startActivity(intent);
    }

    public void goToSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
        activity.startActivity(intent);
    }

    public void goToAdminLaunchPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), AdminLauncherActivity.class);
        activity.startActivity(intent);
    }

    public void goToTagsSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), TagsActivity.class);
        activity.startActivity(intent);
    }

    public void goToOrdersPage() {
        Intent intent = new Intent(MyApplication.getAppContext(), OrdersViewActivity.class);
        activity.startActivity(intent);
    }

    public void goToIngredientsSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), IngredientsActivity.class);
        activity.startActivity(intent);
    }
}
