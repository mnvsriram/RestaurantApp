package app.resta.com.restaurantapp.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
import app.resta.com.restaurantapp.fragment.ReviewListFragment;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class ReviewMenuActivity extends BaseActivity implements ReviewListFragment.OnReviewMenuItemSelectedListener {

    ReviewListFragment frag = new ReviewListFragment();

    private Map<String, List<RestaurantItem>> dataCollection;
    private List<String> headerItems;

    public void addItemToReview(RestaurantItem item) {
        List<RestaurantItem> existingItems = dataCollection.get(item.getParentItem().getName());
        if (existingItems == null) {
            existingItems = new ArrayList<>();
        }
        existingItems.add(item);
        dataCollection.put(item.getParentItem().getName(), existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
        //notifyDataSetChanged();
    }

    @Override
    public void onRestaurantItemClicked(RestaurantItem item) {

        ReviewListFragment frag = new ReviewListFragment();
        addItemToReview(item);
        frag.setDataCollection(dataCollection);
        frag.setHeaderItems(headerItems);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.review_fragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dataCollection = new LinkedHashMap<>();
        headerItems = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
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
