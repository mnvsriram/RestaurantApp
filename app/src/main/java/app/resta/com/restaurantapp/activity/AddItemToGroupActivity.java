package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ButtonArrayAdapter;
import app.resta.com.restaurantapp.cache.RestaurantCache;
import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ItemNameComparator;
import app.resta.com.restaurantapp.util.MyApplication;

public class AddItemToGroupActivity extends BaseActivity {

    private GridView allItemsGrid;
    private SearchView searchView;
    private GridView itemsInGrid;
    ButtonArrayAdapter adapter;
    ButtonArrayAdapter adapterForGroupItems;
    private MenuItemParentDao menuItemParentDao;
    private RestaurantItem parentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_group);
        setToolbar();
        initialize();
        setListeners();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setListeners() {
        setSearchViewTextListener();
    }

    private void setSearchViewTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

    }

    private RestaurantItem[] getChildrenForThisGroup() {
        List<RestaurantItem> children = menuItemParentDao.loadChildrenForParentId(parentItem.getId());
        Object[] itemObjectArr = children.toArray();
        return Arrays.copyOf(itemObjectArr, itemObjectArr.length, RestaurantItem[].class);
    }

    View.OnClickListener buttonOnClickAddToGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RestaurantItem item = (RestaurantItem) v.getTag();
            if (item != null) {
                boolean isExists = menuItemParentDao.isChildExistForParent(item.getId(), parentItem.getId());
                if (isExists) {
                    Toast.makeText(MyApplication.getAppContext(), item.getName() + " is already added to " + parentItem.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    menuItemParentDao.insertParentChildMapping(item.getId(), parentItem.getId());
                    adapterForGroupItems.setData(getChildrenForThisGroup());
                    adapterForGroupItems.notifyDataSetChanged();
                }
            }
        }
    };

    View.OnClickListener buttonOnClickRemoveFromGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RestaurantItem item = (RestaurantItem) v.getTag();
            if (item != null) {
                menuItemParentDao.deleteMappingsForItem(item.getId(), parentItem.getId());
                adapterForGroupItems.setData(getChildrenForThisGroup());
                adapterForGroupItems.notifyDataSetChanged();
            }

        }
    };

    private RestaurantItem[] getRestaurantItems() {
        ArrayList<RestaurantItem> items = new ArrayList<RestaurantItem>(RestaurantCache.allChildItemsByName.values());
        Collections.sort(items, new ItemNameComparator());

        Object[] itemObjectArr = items.toArray();
        return Arrays.copyOf(itemObjectArr, itemObjectArr.length, RestaurantItem[].class);
    }

    private void setAdapter() {
        adapter = new ButtonArrayAdapter(this, android.R.layout.simple_list_item_1, getRestaurantItems(), buttonOnClickAddToGroupListener);
        allItemsGrid.setAdapter(adapter);

        adapterForGroupItems = new ButtonArrayAdapter(this, android.R.layout.simple_list_item_1, getChildrenForThisGroup(), buttonOnClickRemoveFromGroupListener);
        itemsInGrid.setAdapter(adapterForGroupItems);

    }

    private void initialize() {
        allItemsGrid = (GridView) findViewById(R.id.itemsGridView);
        searchView = (SearchView) findViewById(R.id.searchItemsInGrid);
        itemsInGrid = (GridView) findViewById(R.id.itemsInTheGroupGrid);
        long parentId = getIntent().getLongExtra("addItemActivity_group_id", 0l);
        parentItem = RestaurantCache.allParentItemsById.get(parentId);
        menuItemParentDao = new MenuItemParentDao();
        setAdapter();
        setFields();
    }

    private void setFields() {
        TextView itemsInGroupTitle = (TextView) findViewById(R.id.itemsInGroupTitle);
        itemsInGroupTitle.setText("Items in " + parentItem.getName() + ":");

        TextView itemAddToGroupHeaderLabel = (TextView) findViewById(R.id.itemAddToGroupHeaderLabel);
        itemAddToGroupHeaderLabel.setText("Add an Item to " + parentItem.getName() + "(" + parentItem.getMenuTypeName() + "):");

    }

    public void goToAddNewItemPage(View view) {
        //int groupPosition = (Integer) view.getTag(R.string.tag_item_group_position);
        //int childPosition = (Integer) view.getTag(R.string.tag_item_child_position);

        Map<String, Object> params = new HashMap<>();
        params.put("itemEditActivity_parentItem", parentItem);
        //params.put("item_group_position", groupPosition);
        //params.put("item_child_position", childPosition);

        authenticationController.goToItemEditPage(params);
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", parentItem.getId());
        authenticationController.goToMenuPage(params);
    }


}
