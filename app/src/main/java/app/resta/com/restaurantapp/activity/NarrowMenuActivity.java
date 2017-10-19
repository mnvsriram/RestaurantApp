package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
import app.resta.com.restaurantapp.fragment.MenuListFragment;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;

public class NarrowMenuActivity extends BaseActivity implements MenuListFragment.OnMenuItemSelectedListener, MenuListFragment.OnMenuTypeChanged {
    private IngredientDao ingredientDao = new IngredientDao();
    private TagsDao tagsDao = new TagsDao();
    private static Map<Long, List<Tag>> tagsData = new HashMap<>();
    private GGWDao ggwDao;
    private long menuTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrow_menu);
        ggwDao = new GGWDao();
        setToolbar();
        menuTypeId = getIntent().getLongExtra("groupMenuId", 0l);
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("activityMenuTypeAdd_menuTypeId", menuTypeId);
        authenticationController.goBackFromMenuPage(params, menuTypeId);
    }


    @Override
    public void onMenuTypeChanged(long groupMenuId) {
        menuTypeId = groupMenuId;
    }

    @Override
    public void onRestaurantItemClicked(int groupPosition, int childPosition) {
        MenuDetailFragment frag = new MenuDetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        frag.setChildPosition(childPosition);
        frag.setGroupPosition(groupPosition);

        if (childPosition >= 0) {
            RestaurantItem item = MenuExpandableListAdapter.getChildMenuItem(groupPosition, childPosition);
            if (item != null) {
                List<Ingredient> ingredientList = ingredientDao.getIngredientsData().get(item.getId());
                List<Tag> tagList = tagsDao.getTagsData().get(item.getId());
                frag.setIngredientList(ingredientList);
                frag.setTagList(tagList);
                ft.replace(R.id.fragment_container, frag);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void showGoesGreatWithItems(View view) {
        Long itemId = (Long) view.getTag();
        RestaurantItem restaurantItem = MenuItemDao.getAllItemsById().get(itemId);
        if (restaurantItem.getGgwItems() == null) {
            restaurantItem.setGgwItems(ggwDao.getGGWMappings(itemId));
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.edit);
        builderSingle.setTitle(restaurantItem.getName() + " goes great along with:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);

        List<RestaurantItem> ggwItems = restaurantItem.getGgwItems();
        if (ggwItems != null) {
            for (RestaurantItem ggwItem : ggwItems) {
                arrayAdapter.add(ggwItem.getName());
            }
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }
}
