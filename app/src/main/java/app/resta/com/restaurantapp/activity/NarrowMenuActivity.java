package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.LazyAdapterToDelete;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
import app.resta.com.restaurantapp.fragment.MenuListFragment;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class NarrowMenuActivity extends BaseActivity implements MenuListFragment.OnMenuItemSelectedListener {
    ListView list;
    LazyAdapterToDelete adapter;

    private TextView filePath;
    private Button Browse;
    private File selectedFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrow_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

    @Override
    public void onRestaurantItemClicked(int groupPosition, int childPosition) {
        MenuDetailFragment frag = new MenuDetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        frag.setChildPosition(childPosition);
        frag.setGroupPosition(groupPosition);

        ft.replace(R.id.fragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
