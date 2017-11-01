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
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.validator.MenuTypeValidator;

public class MenuTypeAddActivity extends BaseActivity {

    private MenuType menuType;
    private MenuItemDao menuItemDao;
    private MenuTypeDao menuTypeDao;
    private MenuItemParentDao menuItemParentDao;
    private MenuTypeValidator menuTypeValidator;
    private ListView listView;
    private long menuTypeId;
    List<RestaurantItem> dataModels;
    private MenuTypeGroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_type_add);
        getIntentParams();
        initialize();
        setFields();
        setToolbar();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void getIntentParams() {
        if (getIntent().hasExtra("activityMenuTypeAdd_menuTypeId")) {
            menuTypeId = getIntent().getLongExtra("activityMenuTypeAdd_menuTypeId", 0l);
        }
    }

    private void initialize() {
        menuItemDao = new MenuItemDao();
        menuTypeDao = new MenuTypeDao();
        menuItemParentDao = new MenuItemParentDao();
        menuType = new MenuType();
        if (menuTypeId > 0) {
            menuType = menuTypeDao.getMenuGroupsById().get(menuTypeId);
        }
        menuTypeValidator = new MenuTypeValidator(this, menuType);
    }

    private void setFields() {
        setName();
        setPrice();
        setDescription();
        setShowPriceForChildren();
        setList();
    }


    public void addRemoveItemsToGroup(View view) {
        if (menuType.getId() <= 0) {
            Toast.makeText(this, "Please save before adding Items.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("groupToOpen", 0l);
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
        if (menuType.getShowPriceOfChildren() == null || menuType.getShowPriceOfChildren().equalsIgnoreCase("Y")) {
            status.setText("Y");
            status.setChecked(true);
        }
    }

    private void setGroupListHeader(String text) {
        TextView menuTypeGroupListHeaderTextView = (TextView) findViewById(R.id.menuTypeGroupListHeader);
        menuTypeGroupListHeaderTextView.setText(text);
    }

    private void setList() {
        ListView listView = (ListView) findViewById(R.id.menuTypeAddListView);
        if (menuType.getId() <= 0) {
            listView.setVisibility(View.GONE);
            setGroupListHeader("There are no groups in this Menu Type");
        } else {
            dataModels = new ArrayList<>(menuItemDao.fetchMenuItems(menuType.getId()).values());
            listView = (ListView) findViewById(R.id.menuTypeAddListView);
            groupListAdapter = new MenuTypeGroupListAdapter(dataModels, this);
            listView.setAdapter(groupListAdapter);
            setGroupListHeader("Groups in " + menuType.getName());
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
            activeStatus = "Y";
        } else {
            activeStatus = "N";
        }
        menuType.setShowPriceOfChildren(activeStatus);
    }

    public void save(View view) {
        getName();
        getDescription();
        getPrice();
        getShowChildrenPrice();
        if (menuTypeValidator.validate()) {
            menuTypeDao.insertOrUpdateGroup(menuType);
            if (groupListAdapter != null && groupListAdapter.dataChanged) {
                menuItemParentDao.updatePositions(groupListAdapter.getData());
            }
            onBackPressed();
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }
}
