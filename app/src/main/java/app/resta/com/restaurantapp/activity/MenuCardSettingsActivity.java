package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import app.resta.com.restaurantapp.R;

public class MenuCardSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu_cards);
        setToolbar();

        View view = findViewById(R.id.home_page_fragment_view);
        disable((ViewGroup) view);
    }

    private static void disable(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    public void modifyMenuCard(View view) {
        authenticationController.goToMenuEditPage(null);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }
}
