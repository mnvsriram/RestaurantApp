package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuButtonDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonPropEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonShapeEnum;

public class MenuButtonEditActivity extends BaseActivity {
    MenuCardButton menuCardButton = null;
    MenuTypeDao menuTypeDao;
    ArrayAdapter<String> menuDataArrayAdapter = null;
    ArrayAdapter<String> locationArrayAdapter = null;
    ArrayAdapter<String> shapeArrayAdapter = null;

    MenuButtonDao buttonDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_button_edit);
        initialize();
        setToolbar();
        loadIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setFields();
    }

    private void initialize() {
        menuTypeDao = new MenuTypeDao();
        buttonDao = new MenuButtonDao();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();

        long menuCardId = intent.getLongExtra("menuCardEditActivity_cardId", 1l);
        MenuCardButtonEnum menuCardButtonEnum = null;
        if (intent.hasExtra("menuCardEditActivity_buttonClicked")) {
            menuCardButtonEnum = (MenuCardButtonEnum) intent.getSerializableExtra("menuCardEditActivity_buttonClicked");
            menuCardButton = buttonDao.getButtonInCard(menuCardId, menuCardButtonEnum);
        }
        if (menuCardButton == null) {
            menuCardButton = new MenuCardButton();
            menuCardButton.setCardId(menuCardId);
            menuCardButton.setLocation(menuCardButtonEnum);
        }
    }

    private void setFields() {
        setName();
        setMenuStyle();
        setMenuData();
        setLocation();
        setShapeSpinner();
        setVisibility();
        setStatus();
    }

    private void getFields() {
        getName();
        getMenuData();
        getModifiedStatus();
        getButtonShape();
    }

    private void getMenuData() {
        Spinner menuTypeSpinner = (Spinner) findViewById(R.id.menuTypesSpinner);
        String selectedMenuType = menuTypeSpinner.getSelectedItem().toString();
        long menuTypeId = menuTypeDao.getMenuGroupsByName().get(selectedMenuType);
        menuCardButton.getProps().put(MenuCardButtonPropEnum.MENU_DATA, menuTypeId + "");
    }


    private void getButtonShape() {
        Spinner shapeSpinner = (Spinner) findViewById(R.id.menuButtonShapeSpinner);
        String selectedShape = shapeSpinner.getSelectedItem().toString();
        MenuCardButtonShapeEnum shapeEnum = MenuCardButtonShapeEnum.valueOf(selectedShape);
        menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_SHAPE, shapeEnum.getValue() + "");
    }

    private void getModifiedStatus() {
        ToggleButton status = (ToggleButton) findViewById(R.id.menuButtonActiveToggle);
        String activeStatus = status.getText().toString();
        if (activeStatus.equalsIgnoreCase("on")) {
            menuCardButton.setEnabled(true);
        } else {
            menuCardButton.setEnabled(false);
        }
    }


    private void getName() {
        TextView name = (TextView) findViewById(R.id.menuButtonName);
        menuCardButton.setName(name.getText().toString());
    }

    private void setName() {
        TextView name = (TextView) findViewById(R.id.menuButtonName);
        String buttonName = menuCardButton.getName();

        if (buttonName == null && menuCardButton.getLocation() != null) {
            buttonName = menuCardButton.getLocation().name();
        }
        name.setText(buttonName);
    }

    private void setMenuStyle() {

    }

    private void setMenuData() {
        Spinner menuTypeSpinner = (Spinner) findViewById(R.id.menuTypesSpinner);
        List<String> menuTypes = new ArrayList<>(menuTypeDao.getMenuGroupsByName().keySet());

        menuDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, menuTypes);
        menuDataArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuTypeSpinner.setAdapter(menuDataArrayAdapter);
/*
        menuTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                long selectedMenuTypeId = menuTypeDao.getMenuGroupsByName().get(menuDataArrayAdapter.getItem(position));
                menuCardButton.getProps().put(MenuCardButtonPropEnum.MENU_DATA, selectedMenuTypeId + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
*/

        if (menuDataArrayAdapter.getCount() > 0) {
            String menuDataSelected = menuCardButton.getProps().get(MenuCardButtonPropEnum.MENU_DATA);
            int spinnerPosition = 0;
            if (menuDataSelected != null) {
                String menuTypeNameSelected = menuTypeDao.getMenuGroupsById().get(Long.parseLong(menuDataSelected)).getName();
                spinnerPosition = menuDataArrayAdapter.getPosition(menuTypeNameSelected);
            }
            menuTypeSpinner.setSelection(spinnerPosition);
        }
    }

    private void setLocation() {
        Spinner menuLocationSpinner = (Spinner) findViewById(R.id.menuButtonLocationSpinner);
        String[] locations = Arrays.toString(MenuCardButtonEnum.values()).replaceAll("^.|.$", "").split(", ");

        locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(locations));
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuLocationSpinner.setAdapter(locationArrayAdapter);
/*
        menuLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MenuCardButtonEnum selectedLocation = MenuCardButtonEnum.valueOf(locationArrayAdapter.getItem(position));
                menuCardButton.setLocation(selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
*/
        if (locationArrayAdapter.getCount() > 0) {


            MenuCardButtonEnum location = menuCardButton.getLocation();

            int spinnerPosition = 0;

            if (location != null) {
                String menuDataSelectedStr = location.name();
                if (menuDataSelectedStr != null) {
                    //int menuDataSelected = Integer.parseInt(menuDataSelectedStr);
                    spinnerPosition = locationArrayAdapter.getPosition(menuDataSelectedStr);

                    if (MenuCardButtonEnum.isMainButton(location)) {
                        menuLocationSpinner.setEnabled(false);
                    }
                }
            }
            menuLocationSpinner.setSelection(spinnerPosition);
        }
    }


    private void setShapeSpinner() {
        Spinner buttonShapeSpinner = (Spinner) findViewById(R.id.menuButtonShapeSpinner);
        String[] shapes = Arrays.toString(MenuCardButtonShapeEnum.values()).replaceAll("^.|.$", "").split(", ");

        shapeArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(shapes));
        shapeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buttonShapeSpinner.setAdapter(shapeArrayAdapter);
/*
        buttonShapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MenuCardButtonShapeEnum selectedShape = MenuCardButtonShapeEnum.valueOf(shapeArrayAdapter.getItem(position));
                menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_SHAPE, selectedShape.getValue() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
*/
        if (shapeArrayAdapter.getCount() > 0) {
            String shapeId = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_SHAPE);

            int spinnerPosition = 0;

            if (shapeId != null) {
                MenuCardButtonShapeEnum shape = MenuCardButtonShapeEnum.of(Integer.parseInt(shapeId));
                String shapeSelectedStr = shape.name();
                if (shapeSelectedStr != null) {
                    spinnerPosition = shapeArrayAdapter.getPosition(shapeSelectedStr);
                }
            }
            buttonShapeSpinner.setSelection(spinnerPosition);
        }
    }

    private void setVisibility() {

    }

    private void setStatus() {
        ToggleButton status = (ToggleButton) findViewById(R.id.menuButtonActiveToggle);
        if (menuCardButton.isEnabled()) {
            status.setText("Y");
            status.setChecked(true);
        } else {
            status.setText("N");
            status.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("activity_menucardEdit_cardId", menuCardButton.getCardId());
        authenticationController.goToMenuEditPage(params);
    }

    public void save(View view) {
        getFields();
        buttonDao.insertOrUpdateButton(menuCardButton);
        onBackPressed();
    }
}
