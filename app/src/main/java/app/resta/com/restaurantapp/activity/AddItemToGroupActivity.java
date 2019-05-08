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
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ItemNameComparator;
import app.resta.com.restaurantapp.util.MyApplication;

public class AddItemToGroupActivity extends BaseActivity {

    private GridView allItemsGrid;
    private SearchView searchView;
    ButtonArrayAdapter adapter;
    private RestaurantItem parentItem;

    private ListView listView;
    List<RestaurantItem> allItemsInGrid;
    List<RestaurantItem> dataModels;
    private ItemListInGroupAdapter itemListInGroupAdapter;

    private MenuItemAdminDaoI menuItemAdminDao;
    private MenuGroupAdminDaoI menuGroupAdminDao;

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
                    adapter.setData(convertListToArray(allItemsInGrid));
                    adapter.notifyDataSetChanged();
                }
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

    private void addToList(RestaurantItem item) {
        dataModels = itemListInGroupAdapter.getData();
        dataModels.add(item);
        itemListInGroupAdapter.setData(dataModels);
        ((ItemListInGroupAdapter) listView.getAdapter()).notifyDataSetChanged();
    }


    private void setGridViewAdapter() {
        menuItemAdminDao.getAllItems(new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> itemsFromDB) {
                if (itemsFromDB != null && itemsFromDB.size() > 0) {
                    ArrayList<RestaurantItem> remainingItems = new ArrayList<>();
                    for (RestaurantItem item : itemsFromDB) {
                        if (!dataModels.contains(item)) {
                            remainingItems.add(item);
                        }
                    }

                    allItemsInGrid = remainingItems;
                    adapter = new ButtonArrayAdapter(AddItemToGroupActivity.this, android.R.layout.simple_list_item_1, convertListToArray(allItemsInGrid), buttonOnClickAddToGroupListener);
                    allItemsGrid.setAdapter(adapter);
                }
            }
        });
    }

    private void setAdapter() {
        menuGroupAdminDao.getItemsInGroup(parentItem.getId(), new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> itemsInGroup) {
                dataModels = itemsInGroup;
                setGridViewAdapter();
                setListAdapter();
            }
        });

    }

    private void setListAdapter() {
        listView = findViewById(R.id.itemsInTheGroupList);
        itemListInGroupAdapter = new ItemListInGroupAdapter(dataModels, this);
        listView.setAdapter(itemListInGroupAdapter);

    }

    private void initialize() {
        allItemsGrid = findViewById(R.id.itemsGridView);
        searchView = findViewById(R.id.searchItemsInGrid);
        final String parentId = getIntent().getStringExtra("addItemActivity_group_id");
        final String menuTypeId = getIntent().getStringExtra("addItemActivity_menuType_id");
        menuItemAdminDao = new MenuItemAdminFireStoreDao();
        menuGroupAdminDao = new MenuGroupAdminFireStoreDao();
        menuGroupAdminDao.getGroup(parentId, new OnResultListener<RestaurantItem>() {
            @Override
            public void onCallback(RestaurantItem group) {
                parentItem = group;
                parentItem.setMenuTypeId(menuTypeId);
                setAdapter();
                setFields();
            }
        });
    }

    private void setFields() {
        TextView itemsInGroupTitle = findViewById(R.id.itemsInGroupTitleForList);
        itemsInGroupTitle.setText("Items in " + parentItem.getName() + ":");

        TextView addItemToGroupHeader = findViewById(R.id.addItemToGroupHeader);
        addItemToGroupHeader.setText("Add an Item to " + parentItem.getName() + "(" + parentItem.getMenuTypeName() + "):");
    }

    public void goToAddNewItemPage(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("itemEditActivity_parentItem", parentItem);
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
        List<RestaurantItem> childItems = parentItem.getChildItems();
        List<GroupAndItemMapping> mappings = new ArrayList<>();
        if (childItems != null && childItems.size() > 0) {
            int index = 1;
            for (RestaurantItem child : childItems) {
                mappings.add(getParentChildMapping(child.getId(), parentItem.getId(), index++));
            }
        }
        menuGroupAdminDao.updateItemsInGroup(parentItem.getId(), mappings, new OnResultListener<String>() {
            @Override
            public void onCallback(String status) {
                onBackPressed();
            }
        });

    }


    private GroupAndItemMapping getParentChildMapping(String itemId, String groupId, int index) {
        GroupAndItemMapping mapping = new GroupAndItemMapping();
        mapping.setItemId(itemId);
        mapping.setGroupId(groupId);
        mapping.setItemPosition(index);
        return mapping;
    }
}
