package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuActionListAdapter;
import app.resta.com.restaurantapp.db.dao.MenuButtonActionDao;
import app.resta.com.restaurantapp.db.dao.MenuButtonDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.ColorCodeEnum;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonPropEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonShapeEnum;
import app.resta.com.restaurantapp.model.MenuCardLayoutEnum;
import app.resta.com.restaurantapp.util.ListViewUtils;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.validator.MenuCardButtonValidator;

public class MenuButtonEditActivity extends BaseActivity {
    MenuCardButton menuCardButton = null;
    MenuCardButtonValidator validator;
    MenuTypeDao menuTypeDao;
    ArrayAdapter<String> menuDataArrayAdapter = null;
    ArrayAdapter<String> locationArrayAdapter = null;
    ArrayAdapter<String> layoutArrayAdapter = null;
    ArrayAdapter<String> shapeArrayAdapter = null;
    ArrayAdapter<String> colorAdapter = null;

    MenuButtonDao buttonDao;
    MenuButtonActionDao menuButtonActionDao;
    MenuActionListAdapter menuActionListAdapter;


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
        menuButtonActionDao = new MenuButtonActionDao();
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
        } else {
            List<MenuCardAction> actions = menuButtonActionDao.getActions(menuCardButton.getId());
            menuCardButton.setActions(actions);
        }
    }

    private void setFields() {
        setName();
        setMenuStyle();
        setMenuData();
        setMenuLayout();
        setActions();
        setShapeSpinner();
        setButtonTextColorSpinner();
        setButtonColorSpinner();
        setLocation();
        setVisibility();
        setStatus();
        setIsBlinkText();
    }

    private void setActions() {
        updateList();
        List<MenuCardAction> actions = menuCardButton.getActions();
        if (actions != null && actions.size() > 0) {
            MenuCardAction lastElement = actions.get(actions.size() - 1);
            highestPositionOfAction = lastElement.getPosition();
        }
    }

    private void setButtonTextColorSpinner() {
        Spinner buttonTextColorSpinner = (Spinner) findViewById(R.id.menuButtonTextColorSpinner);
        String colorId = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_TEXT_COLOR);
        if (colorId == null || ColorCodeEnum.of(colorId) == null) {
            colorId = ColorCodeEnum.Black.getValue();
        }
        setColorSpinner(buttonTextColorSpinner, colorId);
    }

    private void setButtonColorSpinner() {
        Spinner buttonColorSpinner = (Spinner) findViewById(R.id.menuButtonColorSpinner);
        String colorId = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_COLOR);
        if (colorId != null || ColorCodeEnum.of(colorId) == null) {
            colorId = ColorCodeEnum.White.getValue();
        }
        setColorSpinner(buttonColorSpinner, colorId);
    }

    private void getFields() {
        getName();
        getActions();
        getModifiedStatus();
        getButtonShape();
        getButtonColor();
        getButtonTextColor();
        getButtonLocation();
        getModifiedIsBlink();
    }

    private void getActions() {
        menuCardButton.setActions(menuActionListAdapter.getData());
    }
  /*
    private void getMenuData() {
        Spinner menuTypeSpinner = (Spinner) findViewById(R.id.menuTypesSpinner);
        if (menuTypeSpinner.getSelectedItem() != null && menuTypeSpinner.getSelectedItem().toString() != null && !menuTypeSpinner.getSelectedItem().toString().equals("")) {
            String selectedMenuType = menuTypeSpinner.getSelectedItem().toString();
            long menuTypeId = menuTypeDao.getMenuGroupsByName().get(selectedMenuType);
            menuCardButton.getProps().put(MenuCardButtonPropEnum.MENU_DATA, menuTypeId + "");
        } else {
            menuCardButton.getProps().put(MenuCardButtonPropEnum.MENU_DATA, null);
        }
    }
*/

    private void getButtonShape() {
        Spinner shapeSpinner = (Spinner) findViewById(R.id.menuButtonShapeSpinner);
        String selectedShape = shapeSpinner.getSelectedItem().toString();
        MenuCardButtonShapeEnum shapeEnum = MenuCardButtonShapeEnum.valueOf(selectedShape);
        menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_SHAPE, shapeEnum.getValue() + "");
    }


    private void getButtonTextColor() {
        Spinner textColorSpinner = (Spinner) findViewById(R.id.menuButtonTextColorSpinner);
        String selectedColor = textColorSpinner.getSelectedItem().toString();
        ColorCodeEnum colorEnum = ColorCodeEnum.valueOf(selectedColor);
        menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_TEXT_COLOR, colorEnum.getValue() + "");
    }

    private void getButtonColor() {
        Spinner buttonColorSpinner = (Spinner) findViewById(R.id.menuButtonColorSpinner);
        String selectedColor = buttonColorSpinner.getSelectedItem().toString();
        ColorCodeEnum colorEnum = ColorCodeEnum.valueOf(selectedColor);
        menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_COLOR, colorEnum.getValue() + "");
    }


    private void getButtonLocation() {
        Spinner locationSpinner = (Spinner) findViewById(R.id.menuButtonLocationSpinner);
        MenuCardButtonEnum locationEnum = null;
        if (locationSpinner.getSelectedItem() != null && locationSpinner.getSelectedItem().toString() != null && !locationSpinner.getSelectedItem().toString().equals("")) {
            String selectedLocation = locationSpinner.getSelectedItem().toString();

            locationEnum = MenuCardButtonEnum.valueOf(selectedLocation);
        }
        menuCardButton.setLocation(locationEnum);
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


    private void getModifiedIsBlink() {
        ToggleButton blink = (ToggleButton) findViewById(R.id.menuButtonTextBlinkToggle);
        String isBlink = blink.getText().toString();
        if (isBlink.equalsIgnoreCase("on")) {
            menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_TEXT_BLINK, "on");
        } else {
            menuCardButton.getProps().put(MenuCardButtonPropEnum.BUTTON_TEXT_BLINK, "off");
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
            //  buttonName = menuCardButton.getLocation().name();
        }
        name.setText(buttonName);
    }

    private void setMenuStyle() {

    }


    private void setMenuLayout() {
        Spinner menuCardLayoutSpinner = (Spinner) findViewById(R.id.menuCardLayoutSpinner);
        String[] layouts = Arrays.toString(MenuCardLayoutEnum.values()).replaceAll("^.|.$", "").split(", ");
        List<String> layOuts = Arrays.asList(layouts);
        layoutArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, layOuts);
        layoutArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuCardLayoutSpinner.setAdapter(layoutArrayAdapter);
        if (layoutArrayAdapter.getCount() > 0) {
            menuCardLayoutSpinner.setSelection(0);
        }
    }


    private void setMenuData() {
        Spinner menuTypeSpinner = (Spinner) findViewById(R.id.menuTypesSpinner);
        List<String> menuTypes = new ArrayList<>(menuTypeDao.getMenuGroupsByName().keySet());

        menuDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, menuTypes);
        menuDataArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuTypeSpinner.setAdapter(menuDataArrayAdapter);
        if (menuDataArrayAdapter.getCount() > 0) {
            menuTypeSpinner.setSelection(0);
        }
    }

    private void setLocation() {
        Spinner menuLocationSpinner = (Spinner) findViewById(R.id.menuButtonLocationSpinner);
        Set<MenuCardButtonEnum> buttonsInDB = buttonDao.getMenuCardButtons(menuCardButton.getCardId()).keySet();
        List<MenuCardButtonEnum> remainingButtons = new ArrayList<>();
        MenuCardButtonEnum selectedLocation = menuCardButton.getLocation();
        for (MenuCardButtonEnum location : MenuCardButtonEnum.values()) {
            if (!buttonsInDB.contains(location) || selectedLocation == location) {
                remainingButtons.add(location);
            }
        }

        String[] locations = Arrays.toString(remainingButtons.toArray()).replaceAll("^.|.$", "").split(", ");

        locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(locations));
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuLocationSpinner.setAdapter(locationArrayAdapter);
        if (locationArrayAdapter.getCount() > 0) {
            int spinnerPosition = 0;
            if (selectedLocation != null) {
                String menuDataSelectedStr = selectedLocation.name();
                if (menuDataSelectedStr != null) {
                    spinnerPosition = locationArrayAdapter.getPosition(menuDataSelectedStr);
                    if (MenuCardButtonEnum.isMainButton(selectedLocation)) {
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

    private void setColorSpinner(Spinner spinner, String selectedColorhexCode) {
        String[] colors = Arrays.toString(ColorCodeEnum.values()).replaceAll("^.|.$", "").split(", ");
        colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(colorAdapter);
        if (colorAdapter.getCount() > 0) {
            int spinnerPosition = 0;
            if (selectedColorhexCode != null && selectedColorhexCode.length() > 0) {
                ColorCodeEnum color = ColorCodeEnum.of(selectedColorhexCode);
                String colorSelectedStr = color.name();
                if (colorSelectedStr != null) {
                    spinnerPosition = colorAdapter.getPosition(colorSelectedStr);
                }
            }
            spinner.setSelection(spinnerPosition);
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


    private void setIsBlinkText() {
        ToggleButton isBlinkToggle = (ToggleButton) findViewById(R.id.menuButtonTextBlinkToggle);
        String isBlink = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_TEXT_BLINK);
        if (isBlink != null && isBlink.equalsIgnoreCase("on")) {
            isBlinkToggle.setText("Y");
            isBlinkToggle.setChecked(true);
        } else {
            isBlinkToggle.setText("N");
            isBlinkToggle.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("activity_menucardEdit_cardId", menuCardButton.getCardId());
        authenticationController.goToMenuEditPage(params);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void save(View view) {
        validator = new MenuCardButtonValidator(this, menuCardButton);
        getFields();
        if (validator.validate()) {
            buttonDao.insertOrUpdateButton(menuCardButton);
            menuButtonActionDao.insertOrUpdateActions(menuCardButton);
            goToModifyPage();
        }
    }

    private void goToModifyPage() {
        Map<String, Object> params = new HashMap<>();
        params.put("menuCardEdit_menuCardId", menuCardButton.getCardId());
        authenticationController.goToMenuCardSettingsPage(params);
    }

    int highestPositionOfAction = 0;

    public void addAction(View view) {
        Spinner menuTypeSpinner = (Spinner) findViewById(R.id.menuTypesSpinner);
        Spinner layoutSpinner = (Spinner) findViewById(R.id.menuCardLayoutSpinner);

        if (menuTypeSpinner.getSelectedItem() != null && menuTypeSpinner.getSelectedItem().toString() != null && !menuTypeSpinner.getSelectedItem().toString().equals("")
                && layoutSpinner.getSelectedItem() != null && layoutSpinner.getSelectedItem().toString() != null && !layoutSpinner.getSelectedItem().toString().equals("")) {
            String selectedMenuType = menuTypeSpinner.getSelectedItem().toString();
            long menuTypeId = menuTypeDao.getMenuGroupsByName().get(selectedMenuType);

            String selectedLayout = layoutSpinner.getSelectedItem().toString();
            int layoutId = MenuCardLayoutEnum.valueOf(selectedLayout).getValue();

            MenuCardAction action = new MenuCardAction();
            action.setButtonId(menuCardButton.getId());
            action.setMenuTypeId(menuTypeId);
            action.setLayoutId(layoutId);
            action.setPosition(++highestPositionOfAction);
            action.setMenuTypeName(selectedMenuType);
            action.setLayoutName(selectedLayout);
            if (menuCardButton.getActions().contains(action)) {
                Toast.makeText(MyApplication.getAppContext(), "This combination is already present.", Toast.LENGTH_SHORT).show();
            } else {
                menuCardButton.addAction(action);
                updateList();
            }
        } else {
            Toast.makeText(MyApplication.getAppContext(), "Please select both the dropdowns before adding.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeAction(View view) {
        MenuCardAction menuCardAction = (MenuCardAction) view.getTag();
        if (menuCardAction != null) {
            menuCardButton.removeAction(menuCardAction);
            updateList();
        }
    }

    public void updateList() {
        ListView listView = (ListView) findViewById(R.id.menuActionAddListView);
        menuActionListAdapter = new MenuActionListAdapter(new ArrayList<>(menuCardButton.getActions()), this);
        listView.setAdapter(menuActionListAdapter);
        ListViewUtils.setListViewHeightBasedOnChildren(listView);
    }

    public void setActions(List<MenuCardAction> actions) {
        menuCardButton.setActions(actions);
    }
}
