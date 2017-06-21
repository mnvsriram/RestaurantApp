package app.resta.com.restaurantapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public class BaseActivity extends AppCompatActivity {
    //public static boolean isAdmin = false;
    final LoginController loginController = LoginController.getInstance();
    AuthenticationController authenticationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil util = new StyleUtil();
        authenticationController = new AuthenticationController(this);
    }

    public void loginForAdmin(final MenuItem item) {
        authenticationController.loginForAdmin(item);
    }

    /*public void loginForAdmin(final MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.admin_login_dialog, null);

        if (loginController.isAdminLoggedIn()) {
            loginController.logout();
            item.setIcon(R.drawable.deletered);
            goToHomePage();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText userNamePass = (EditText) promptsView.findViewById(R.id.adminUserNamePass);
                                String adminUserName = userNamePass.getText().toString();

                                if (loginController.login(adminUserName)) {
                                    //    isAdmin = true;
//                                    MenuItemDao.dataFetched = false;
                                    item.setIcon(R.drawable.edit);
                                    goToMenuPage();
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
        Toast.makeText(this, "clicked", Toast.LENGTH_LONG);
    }

    private void goToMenuPage() {
        Intent intent = null;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(BaseActivity.this, NarrowMenuActivity.class);
            intent.putExtra("modifiedItemId", -1l);

        } else {
            intent = new Intent(BaseActivity.this, HorizontalMenuActivity.class);
        }
        BaseActivity.this.startActivity(intent);
    }

    protected void goToHomePage() {
        Intent intent = null;
        intent = new Intent(BaseActivity.this, TopLevelActivity.class);
        BaseActivity.this.startActivity(intent);
    }

*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem compose = menu.findItem(R.id.miCompose);
        if (loginController.isAdminLoggedIn()) {
            compose.setIcon(R.drawable.edit);
        } else if (loginController.isReviewAdminLoggedIn()) {
            compose.setIcon(R.drawable.edit);
        } else {
            compose.setIcon(R.drawable.deletered);
        }


        return true;
    }

    @Override

    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
