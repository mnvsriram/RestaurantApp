package app.resta.com.restaurantapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem compose = menu.findItem(R.id.miCompose);
        if (loginController.isAdminLoggedIn()) {
            compose.setIcon(R.drawable.admin);
        } else if (loginController.isReviewAdminLoggedIn()) {
            compose.setIcon(R.drawable.admin);
        } else {
            compose.setIcon(R.drawable.login);
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
