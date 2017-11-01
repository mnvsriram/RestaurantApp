package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemParentDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.validator.RestaurantItemParentValidator;

public class GroupEditActivity extends BaseActivity {
    RestaurantItem item = null;
    int groupPosition = 0;
    private MenuItemParentDao menuItemParentDao;
    private MenuTypeDao menuTypeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        initialize();
        loadIntentValues();
        setFieldValues(item);
        setToolbar();
    }

    private void initialize() {
        menuTypeDao = new MenuTypeDao();
        menuItemParentDao = new MenuItemParentDao();
    }

    private void loadIntentValues() {
        Intent intent = getIntent();
        if (intent.hasExtra("groupEditActivity_parent_obj")) {
            item = (RestaurantItem) intent.getSerializableExtra("groupEditActivity_parent_obj");
        }
        groupPosition = intent.getIntExtra("groupEditActivity_group_position", 0);
    }

    private void displayMenuType(RestaurantItem item) {
        TextView groupMenuTypeName = (TextView) findViewById(R.id.groupMenuTypeName);
        MenuType menuType = menuTypeDao.getMenuGroupsById().get(item.getMenuTypeId());
        if (menuType != null) {
            groupMenuTypeName.setText(menuType.getName());
        }
    }

    @Override
    public void onBackPressed() {
        dispatchToMenuPage();
    }

    private void setFieldValues(RestaurantItem item) {
        setItemName(item);
        setStatus(item);
        setDescription(item);
        displayMenuType(item);
    }


    private void setItemName(RestaurantItem item) {
        EditText userInput = (EditText) findViewById(R.id.editGroupName);
        userInput.setText(item.getName());
    }

    private void setDescription(RestaurantItem item) {
        EditText description = (EditText) findViewById(R.id.menuGroupDescription);
        description.setText(item.getDescription());
    }


    private void setStatus(RestaurantItem item) {
        ToggleButton status = (ToggleButton) findViewById(R.id.editItemGroupToggleActive);
        if (item.getActive() == null || item.getActive().equalsIgnoreCase("Y")) {
            status.setText("Y");
            status.setChecked(true);
        }
    }

    private boolean validateInput() {
        RestaurantItemParentValidator validator = new RestaurantItemParentValidator(this, item);
        return validator.validate();
    }

    public void save(View view) {
        getModifiedItemName(item);
        getModifiedDescription(item);
        getModifiedStatus(item);
        if (validateInput()) {
            menuItemParentDao.insertOrUpdateMenuItemParent(item);
            dispatchToMenuPage();
        }
    }

    private void dispatchToMenuPage() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", item.getId());
        params.put("groupMenuId", item.getMenuTypeId());
        params.put("modifiedItemGroupPosition", groupPosition);
        authenticationController.goToMenuPage(params);
    }

    private void getModifiedItemName(RestaurantItem item) {
        EditText userInput = (EditText) findViewById(R.id.editGroupName);
        String modifiedName = userInput.getText().toString();
        item.setName(modifiedName);
    }

    private void getModifiedDescription(RestaurantItem item) {
        EditText userInput = (EditText) findViewById(R.id.menuGroupDescription);
        String description = userInput.getText().toString();
        item.setDescription(description);
    }

    private void getModifiedStatus(RestaurantItem item) {
        ToggleButton status = (ToggleButton) findViewById(R.id.editItemGroupToggleActive);
        String activeStatus = status.getText().toString();
        if (activeStatus.equalsIgnoreCase("on")) {
            activeStatus = "Y";
        } else {
            activeStatus = "N";
        }
        item.setActive(activeStatus);
    }

    public void goBack(View view) {
        dispatchToMenuPage();
    }
}
