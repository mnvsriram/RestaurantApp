package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ReviewAdapter;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.fragment.ReviewListFragment;
import app.resta.com.restaurantapp.model.OrderItem;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class SubmitReviewActivity extends BaseActivity {

    ReviewListFragment frag = new ReviewListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submit_menu);

        ListView listView = (ListView) findViewById(R.id.reviewlist);

        RestaurantItem item = new RestaurantItem();
        item.setName("helllo");
        item.setId(12);

        ArrayList<RestaurantItem> items = new ArrayList<>();
        items.add(item);

        ReviewAdapter adapter = new ReviewAdapter(items, getApplicationContext());
        listView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

}
