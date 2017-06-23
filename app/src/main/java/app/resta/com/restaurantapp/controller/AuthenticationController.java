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
import app.resta.com.restaurantapp.activity.HorizontalMenuActivity;
import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.activity.OrderActivity;
import app.resta.com.restaurantapp.activity.TopLevelActivity;
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
            item.setIcon(R.drawable.deletered);
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
                                        item.setIcon(R.drawable.edit);
                                        goToMenuPage();
                                    } else if (loginController.isReviewAdminLoggedIn()) {
                                        //set review user icon
                                        item.setIcon(R.drawable.edit);
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

    private void goToMenuPage() {
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


    private void goToReviewMenuPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        intent.putExtra("modifiedItemId", -1l);
        activity.startActivity(intent);
    }


    public void goToHomePage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), TopLevelActivity.class);
        activity.startActivity(intent);
    }


}
