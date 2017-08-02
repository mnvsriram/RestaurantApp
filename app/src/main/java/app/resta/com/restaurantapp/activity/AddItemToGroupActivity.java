package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListView;
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
import app.resta.com.restaurantapp.adapter.ItemListInGroupAdapter;
import app.resta.com.restaurantapp.cache.RestaurantCache;
import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ItemNameComparator;
import app.resta.com.restaurantapp.util.MyApplication;

public class AddItemToGroupActivity extends BaseActivity {

    private GridView allItemsGrid;
    private SearchView searchView;
    ButtonArrayAdapter adapter;
    private MenuItemParentDao menuItemParentDao;
    private RestaurantItem parentItem;

    private ListView listView;
    List<RestaurantItem> allItemsInGrid;
    List<RestaurantItem> dataModels;
    private ItemListInGroupAdapter itemListInGroupAdapter;

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
                adapter.setData(convertListToArray(allItemsInGrid));
                adapter.getFilter().filter(query);
                return false;
            }
        });

    }

    View.OnClickListener buttonOnClickAddToGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RestaurantItem item = (RestaurantItem) v.getTag();
            if (item != null) {
                dataModels = itemListInGroupAdapter.getData();
                if (dataModels.contains(item)) {
                    Toast.makeText(MyApplication.getAppContext(), item.getName() + " is already added to " + parentItem.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    addToList(item);

                    allItemsInGrid.remove(item);


                    Collections.sort(allItemsInGrid, new ItemNameComparator());
                    //((ViewGroup)v.getParent()).removeView(v);
                    //v.setVisibility(View.GONE);
                    adapter.setData(convertListToArray(allItemsInGrid));
                    adapter.notifyDataSetChanged();
                }

                //boolean isExists = menuItemParentDao.isChildExistForParent(item.getId(), parentItem.getId());

            }
        }
    };

    public RestaurantItem[] convertListToArray(List<RestaurantItem> itemList) {
        Collections.sort(itemList, new ItemNameComparator());
        Object[] itemObjectArr = itemList.toArray();
        return Arrays.copyOf(itemObjectArr, itemObjectArr.length, RestaurantItem[].class);
    }

    public void addToGrid(RestaurantItem item) {
        allItemsInGrid.add(item);
        adapter.setData(convertListToArray(allItemsInGrid));
        adapter.notifyDataSetChanged();
    }

    /*public void refreshAllItemList() {
        dataModels = itemListInGroupAdapter.getData();
        adapter.setData(getAllRestaurantItems());
        adapter.notifyDataSetChanged();
    }*/

    private void addToList(RestaurantItem item) {
        dataModels = itemListInGroupAdapter.getData();
        dataModels.add(item);
        itemListInGroupAdapter.setData(dataModels);
        ((ItemListInGroupAdapter) listView.getAdapter()).notifyDataSetChanged();
    }


    private List<RestaurantItem> getAllRestaurantItems() {
        ArrayList<RestaurantItem> items = new ArrayList<RestaurantItem>(RestaurantCache.allChildItemsByName.values());
        ArrayList<RestaurantItem> remainingItems = new ArrayList<>();
        for (RestaurantItem item : items) {
            if (!dataModels.contains(item)) {
                remainingItems.add(item);
            }
        }
        return remainingItems;
    }

    private void setAdapter() {
        dataModels = menuItemParentDao.loadChildrenForParentId(parentItem.getId());
        setGridViewAdapter();
        setListAdapter();
    }

    private void setGridViewAdapter() {
        allItemsInGrid = getAllRestaurantItems();
        adapter = new ButtonArrayAdapter(this, android.R.layout.simple_list_item_1, convertListToArray(allItemsInGrid), buttonOnClickAddToGroupListener);
        allItemsGrid.setAdapter(adapter);
    }

    private void setListAdapter() {
        listView = (ListView) findViewById(R.id.itemsInTheGroupList);
        itemListInGroupAdapter = new ItemListInGroupAdapter(dataModels, this);
        listView.setAdapter(itemListInGroupAdapter);

    }

    private void initialize() {
        allItemsGrid = (GridView) findViewById(R.id.itemsGridView);
        searchView = (SearchView) findViewById(R.id.searchItemsInGrid);
        long parentId = getIntent().getLongExtra("addItemActivity_group_id", 0l);
        parentItem = RestaurantCache.allParentItemsById.get(parentId);
        menuItemParentDao = new MenuItemParentDao();
        setAdapter();
        setFields();
    }

    private void setFields() {
        TextView itemsInGroupTitle = (TextView) findViewById(R.id.itemsInGroupTitleForList);
        itemsInGroupTitle.setText("Items in " + parentItem.getName() + ":");

        TextView addItemToGroupHeader = (TextView) findViewById(R.id.addItemToGroupHeader);
        addItemToGroupHeader.setText("Add an Item to " + parentItem.getName() + "(" + parentItem.getMenuTypeName() + "):");
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
        params.put("groupMenuId", parentItem.getMenuTypeId());

        authenticationController.goToMenuPage(params);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void save(View view) {
        parentItem.setChildItems(itemListInGroupAdapter.getData());
        menuItemParentDao.updateChildren(parentItem);
        onBackPressed();
    }

}
