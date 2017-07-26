package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuItemGroupDao;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.validator.RestaurantItemValidator;

public class GroupEditActivity extends BaseActivity {
    RestaurantItem item = null;
    int groupPosition = 0;
    private MenuItemGroupDao groupDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        groupDao = new MenuItemGroupDao();
        Intent intent = getIntent();
        if (intent.hasExtra("group_obj")) {
            item = (RestaurantItem) intent.getSerializableExtra("group_obj");
        }
        groupPosition = intent.getIntExtra("group_position", 0);
        setFieldValues(item);

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

    private void setGroupMenuSpinner(RestaurantItem item) {
        Spinner parentSpinner = (Spinner) findViewById(R.id.groupMenuSpinner);
        List<String> parents = new ArrayList<String>();
        parents.add("Select Menu Type");
        parents.addAll(groupDao.getMenuGroupsByName().keySet());
        parents.add("Create New..");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MyApplication.getAppContext(), android.R.layout.simple_spinner_item, parents);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentSpinner.setAdapter(dataAdapter);
        long groupMenuId = item.getMenuGroupId();

        if (groupDao.getMenuGroupsById().get(groupMenuId) != null) {
            parentSpinner.setSelection(dataAdapter.getPosition(groupDao.getMenuGroupsById().get(groupMenuId)));
        }
        setSpinnerListener();
    }


    void setSpinnerListener() {
        Spinner parentSpinner = (Spinner) findViewById(R.id.groupMenuSpinner);

        parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                                               int position, long id) {

                                                        TextView selectedTextView = (TextView) selectedItemView;
                                                        String text = selectedTextView.getText().toString();
                                                        if (text.equals("Create New..")) {
                                                            System.out.println("");
                                                        }
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parentView) {
                                                    }

                                                }
        );
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuPage();
    }

    private void setFieldValues(RestaurantItem item) {
        setItemName(item);
        setStatus(item);
        setGroupMenuSpinner(item);
    }


    private void setItemName(RestaurantItem item) {
        EditText userInput = (EditText) findViewById(R.id.editGroupName);
        userInput.setText(item.getName());
    }

    private void setStatus(RestaurantItem item) {
        ToggleButton status = (ToggleButton) findViewById(R.id.editItemGroupToggleActive);

        if (item.getActive() == null || item.getActive().equalsIgnoreCase("Y")) {
            status.setText("Y");
            status.setChecked(true);
        }
    }

    private boolean validateInput() {
        RestaurantItemValidator validator = new RestaurantItemValidator(this, item);
        return validator.validateGroup();
    }

    public void save(View view) {
        getModifiedItemName(item);
        getModifiedStatus(item);
        getModifiedGroupMenu(item);
        if (validateInput()) {
            MenuItemDao.insertOrUpdateMenuItem(item);
            //once the above updateMenuItem method is changed to insertOrUpdateMenuItem method, then remove the refresh data below as the data gets refreshed in the insertOrUpdate method.
            dispatchToMenuPage();
        }
    }

    private void dispatchToMenuPage() {
        Intent intent = null;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(this, NarrowMenuActivity.class);
        } else {
            intent = new Intent(this, HorizontalMenuActivity.class);
        }
        //intent.putExtra("test", "hello");
        intent.putExtra("groupToOpen", item.getParentId());
        intent.putExtra("modifiedItemGroupPosition", groupPosition);
        startActivity(intent);
    }

    private void getModifiedItemName(RestaurantItem item) {
        EditText userInput = (EditText) findViewById(R.id.editGroupName);
        String modifiedName = userInput.getText().toString();
        item.setName(modifiedName);
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


    private void getModifiedGroupMenu(RestaurantItem item) {
        Spinner groupmenu = (Spinner) findViewById(R.id.groupMenuSpinner);
        String selectedGroupMenu = (String) groupmenu.getSelectedItem();
        if (groupDao.getMenuGroupsByName().get(selectedGroupMenu) != null) {
            item.setMenuGroupId(groupDao.getMenuGroupsByName().get(selectedGroupMenu));
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }

}
