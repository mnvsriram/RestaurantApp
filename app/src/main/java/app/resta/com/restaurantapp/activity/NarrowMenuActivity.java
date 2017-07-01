package app.resta.com.restaurantapp.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
import app.resta.com.restaurantapp.fragment.MenuListFragment;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;

public class NarrowMenuActivity extends BaseActivity implements MenuListFragment.OnMenuItemSelectedListener {
    private IngredientDao ingredientDao = new IngredientDao();
    private TagsDao tagsDao = new TagsDao();
    private static Map<Long, List<Ingredient>> ingredientsData = new HashMap<>();
    private static Map<Long, List<Tag>> tagsData = new HashMap<>();
    public static boolean fetchData = true;

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        authenticationController.goBackFromMenuPage();
    }

    @Override
    public void onRestaurantItemClicked(int groupPosition, int childPosition) {
        MenuDetailFragment frag = new MenuDetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        frag.setChildPosition(childPosition);
        frag.setGroupPosition(groupPosition);

        if (fetchData) {
            fetchData = false;
            ingredientsData = new HashMap<>();
            tagsData = new HashMap<>();
            ingredientsData.putAll(ingredientDao.getAllIngredientsMappings());
            tagsData.putAll(tagsDao.getTags());
        }

        List<Ingredient> ingredients = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();

        if (childPosition >= 0) {
            RestaurantItem item = MenuExpandableListAdapter.getChildMenuItem(groupPosition, childPosition);
            if (ingredientsData != null && ingredientsData.get(item.getId()) != null) {
                ingredients.addAll(ingredientsData.get(item.getId()));
            }
            if (tagsData != null && tagsData.get(item.getId()) != null) {
                tags.addAll(tagsData.get(item.getId()));
            }
        }
        frag.setIngredientList(ingredients);
        frag.setTagList(tags);

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
