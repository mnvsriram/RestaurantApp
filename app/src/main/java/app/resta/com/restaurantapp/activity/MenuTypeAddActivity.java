package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuTypeGroupListAdapter;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.validator.MenuTypeValidator;

public class MenuTypeAddActivity extends BaseActivity {

    private MenuType menuType;

    private MenuTypeAdminDaoI menuTypeAdminDao;

    private MenuTypeValidator menuTypeValidator;
    private String menuTypeId;
    List<RestaurantItem> groupsInThisMenuType;
    private MenuTypeGroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_type_add);
        getIntentParams();
        initialize();
        setToolbar();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void getIntentParams() {
        if (getIntent().hasExtra("activityMenuTypeAdd_menuTypeId")) {
            menuTypeId = getIntent().getStringExtra("activityMenuTypeAdd_menuTypeId");
        }
    }

    private void initialize() {
        menuTypeAdminDao = new MenuTypeAdminFireStoreDao();
        if (menuTypeId != null) {
            menuTypeAdminDao.getMenuType(menuTypeId + "", new OnResultListener<MenuType>() {
                @Override
                public void onCallback(MenuType mt) {
                    menuType = mt;
                    setFields();
                }
            });
        } else {
            menuType = new MenuType();
            setFields();
        }

    }

    private void setFields() {
        setName();
        setPrice();
        setDescription();
        setShowPriceForChildren();
        setList();
    }


    public void addRemoveItemsToGroup(View view) {
        if (menuType.getId() == null) {
            Toast.makeText(this, "Please save before adding Items.", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> params = new HashMap<>();
//            params.put("groupToOpen", 0l);
            params.put("groupMenuId", menuType.getId());

            authenticationController.goToMenuPage(params);
        }
    }

    private void setName() {
        TextView menuTypeName = (TextView) findViewById(R.id.menuTypeAddName);
        menuTypeName.setText(menuType.getName());
    }

    private void setDescription() {
        TextView description = (TextView) findViewById(R.id.menuTypeDescription);
        description.setText(menuType.getDescription());
    }

    private void setPrice() {
        TextView menuTypeAddPrice = (TextView) findViewById(R.id.menuTypeAddPrice);
        menuTypeAddPrice.setText(menuType.getPrice());
    }


    private void setShowPriceForChildren() {
        ToggleButton status = (ToggleButton) findViewById(R.id.menuTypeAddShowPriceOfChildren);
        if (menuType.isShowPriceOfChildren()) {
            status.setText("Y");
            status.setChecked(true);
        } else {
            status.setText("N");
            status.setChecked(false);
        }
    }

    private void setGroupListHeader(String text) {
        TextView menuTypeGroupListHeaderTextView = (TextView) findViewById(R.id.menuTypeGroupListHeader);
        menuTypeGroupListHeaderTextView.setText(text);
    }

    private void setList() {
        final ListView listView = (ListView) findViewById(R.id.menuTypeAddListView);
        if (menuType.getId() == null) {
            listView.setVisibility(View.GONE);
            setGroupListHeader("There are no groups in this Menu Type");
        } else {

            menuTypeAdminDao.getGroupsInMenuType(menuType.getId(), new OnResultListener<List<RestaurantItem>>() {
                @Override
                public void onCallback(List<RestaurantItem> groups) {
                    groupsInThisMenuType = groups;
                    groupListAdapter = new MenuTypeGroupListAdapter(groupsInThisMenuType, MenuTypeAddActivity.this);
                    listView.setAdapter(groupListAdapter);
                    setGroupListHeader("Groups in " + menuType.getName());
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuTypeSettingsPage(null);
    }

    private void getName() {
        TextView menuTypeName = (TextView) findViewById(R.id.menuTypeAddName);
        menuType.setName(menuTypeName.getText().toString());
    }

    private void getDescription() {
        TextView description = (TextView) findViewById(R.id.menuTypeDescription);
        menuType.setDescription(description.getText().toString());
    }

    private void getPrice() {
        TextView menuTypeAddPrice = (TextView) findViewById(R.id.menuTypeAddPrice);
        menuType.setPrice(menuTypeAddPrice.getText().toString());
    }


    private void getShowChildrenPrice() {
        ToggleButton showChildrenPrice = (ToggleButton) findViewById(R.id.menuTypeAddShowPriceOfChildren);
        String activeStatus = showChildrenPrice.getText().toString();
        if (activeStatus.equalsIgnoreCase("on")) {
            menuType.setShowPriceOfChildren(true);
        } else {
            menuType.setShowPriceOfChildren(false);
        }
    }

    public void save(View view) {
        getName();
        getDescription();
        getPrice();
        getShowChildrenPrice();

        menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
            @Override
            public void onCallback(List<MenuType> menuTypes) {
                validateAndSubmit(menuTypes);
            }
        });
    }

    public void validateAndSubmit(List<MenuType> menuTypes) {
        menuTypeValidator = new MenuTypeValidator(MenuTypeAddActivity.this, menuType);
        if (menuTypeValidator.validate(menuTypes)) {
            menuTypeAdminDao.insertOrUpdateMenuType(menuType, new OnResultListener<MenuType>() {
                @Override
                public void onCallback(MenuType mt) {
                    if (groupListAdapter != null && groupListAdapter.dataChanged) {

                        List<RestaurantItem> groups = groupListAdapter.getData();
                        List<MenuTypeAndGroupMapping> mappings = new ArrayList<>();
                        int index = 1;
                        for (RestaurantItem group : groups) {
                            MenuTypeAndGroupMapping mapping = new MenuTypeAndGroupMapping();
                            mapping.setGroupId(group.getId());
                            mapping.setMenuTypeId(mt.getId());
                            mapping.setGroupPositionInMenuType(index++);
                            mappings.add(mapping);
                        }
                        menuTypeAdminDao.updatePositions(mappings);
                    }
                    onBackPressed();
                }
            });
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }
}
