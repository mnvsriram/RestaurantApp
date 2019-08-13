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

import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuActionListAdapter;
import app.resta.com.restaurantapp.db.dao.admin.button.MenuCardButtonAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.button.MenuCardButtonAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.AppFontEnum;
import app.resta.com.restaurantapp.model.ColorCodeEnum;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonShapeEnum;
import app.resta.com.restaurantapp.model.MenuCardLayoutEnum;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.util.ListViewUtils;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.validator.MenuCardButtonValidator;

public class MenuButtonEditActivity extends BaseActivity {
    MenuCardButton menuCardButton = null;
    MenuCardButtonValidator validator;
    final Map<String, MenuType> menuTypeNameMap = new HashMap<>();
    final Map<String, MenuType> menuTypeIDMap = new HashMap<>();

    ArrayAdapter<String> menuDataArrayAdapter = null;
    ArrayAdapter<String> locationArrayAdapter = null;
    ArrayAdapter<String> layoutArrayAdapter = null;
    ArrayAdapter<String> shapeArrayAdapter = null;
    ArrayAdapter<String> buttonFontArrayAdapter = null;
    ArrayAdapter<String> colorAdapter = null;

    MenuTypeAdminDaoI menuTypeAdminDao;
    MenuCardAdminDaoI menuCardAdminDao;
    MenuCardButtonAdminDaoI menuCardButtonAdminDao;
    MenuActionListAdapter menuActionListAdapter;
    String locationOfButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_button_edit);
        initialize();
        setToolbar();
        loadIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initialize() {
        menuTypeAdminDao = new MenuTypeAdminFireStoreDao();
        menuCardAdminDao = new MenuCardAdminFireStoreDao();
        menuCardButtonAdminDao = new MenuCardButtonAdminFireStoreDao();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();

        final String menuCardId = intent.getStringExtra("menuCardEditActivity_cardId");
        MenuCardButtonEnum menuCardButtonEnum = null;
        if (intent.hasExtra("menuCardEditActivity_buttonClicked")) {
            menuCardButtonEnum = (MenuCardButtonEnum) intent.getSerializableExtra("menuCardEditActivity_buttonClicked");
            locationOfButton = menuCardButtonEnum.name();
        }

        menuCardButton = new MenuCardButton();
        menuCardButton.setCardId(menuCardId);
        menuCardButton.setLocation(menuCardButtonEnum);
        final MenuCardButtonEnum finalMenuCardButtonEnum = menuCardButtonEnum;
        menuCardAdminDao.getButtonInCard(menuCardId + "", menuCardButtonEnum, new OnResultListener<MenuCardButton>() {
            @Override
            public void onCallback(MenuCardButton menuCardButtonFromDb) {
                menuCardButton = menuCardButtonFromDb;
                if (menuCardButton != null) {
                    menuCardButton.setLocation(finalMenuCardButtonEnum);
                    menuCardButtonAdminDao.getActions(menuCardId + "", menuCardButton.getId() + "", Source.DEFAULT, new OnResultListener<List<MenuCardAction>>() {
                        @Override
                        public void onCallback(List<MenuCardAction> actions) {
                            menuCardButton.setActions(actions);
                            setFields();
                        }
                    });
                } else {
                    menuCardButton = new MenuCardButton();
                    menuCardButton.setCardId(menuCardId);
                    setFields();
                }
            }
        });


    }

    private void setFields() {
        setName();
        setMenuStyle();
        setMenuData();
        setMenuLayout();
        setShapeSpinner();
        setButtonFontSpinner();
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
        Spinner buttonTextColorSpinner = findViewById(R.id.menuButtonTextColorSpinner);
        String colorId = menuCardButton.getButtonTextColor();
        if (colorId == null || ColorCodeEnum.of(colorId) == null) {
            colorId = ColorCodeEnum.Black.getValue();
        }
        setColorSpinner(buttonTextColorSpinner, colorId);
    }

    private void setButtonColorSpinner() {
        Spinner buttonColorSpinner = findViewById(R.id.menuButtonColorSpinner);
        String colorId = menuCardButton.getButtonColor();
        if (colorId == null || ColorCodeEnum.of(colorId) == null) {
            colorId = ColorCodeEnum.White.getValue();
        }
        setColorSpinner(buttonColorSpinner, colorId);
    }

    private void getFields() {
        getName();
        getActions();
        getModifiedStatus();
        getButtonShape();
        getButtonFont();
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
        Spinner shapeSpinner = findViewById(R.id.menuButtonShapeSpinner);
        String selectedShape = shapeSpinner.getSelectedItem().toString();
        MenuCardButtonShapeEnum shapeEnum = MenuCardButtonShapeEnum.valueOf(selectedShape);
        menuCardButton.setButtonShape(shapeEnum.getValue() + "");
    }


    private void getButtonFont() {
        Spinner fontSpinner = findViewById(R.id.menuButtonFontSpinner);
        String selectedFont = fontSpinner.getSelectedItem().toString();
        AppFontEnum fontEnum = AppFontEnum.valueOf(selectedFont);
        menuCardButton.setFont(fontEnum);
    }


    private void getButtonTextColor() {
        Spinner textColorSpinner = findViewById(R.id.menuButtonTextColorSpinner);
        String selectedColor = textColorSpinner.getSelectedItem().toString();
        ColorCodeEnum colorEnum = ColorCodeEnum.valueOf(selectedColor);
        menuCardButton.setButtonTextColor(colorEnum.getValue());
    }

    private void getButtonColor() {
        Spinner buttonColorSpinner = findViewById(R.id.menuButtonColorSpinner);
        String selectedColor = buttonColorSpinner.getSelectedItem().toString();
        ColorCodeEnum colorEnum = ColorCodeEnum.valueOf(selectedColor);
        menuCardButton.setButtonColor(colorEnum.getValue());
    }


    private void getButtonLocation() {
        Spinner locationSpinner = findViewById(R.id.menuButtonLocationSpinner);
        MenuCardButtonEnum locationEnum = null;
        if (locationSpinner.getSelectedItem() != null && locationSpinner.getSelectedItem().toString() != null && !locationSpinner.getSelectedItem().toString().equals("")) {
            String selectedLocation = locationSpinner.getSelectedItem().toString();

            locationEnum = MenuCardButtonEnum.valueOf(selectedLocation);
        }
        menuCardButton.setLocation(locationEnum);
    }


    private void getModifiedStatus() {
        ToggleButton status = findViewById(R.id.menuButtonActiveToggle);
        String activeStatus = status.getText().toString();
        if (activeStatus.equalsIgnoreCase("on")) {
            menuCardButton.setEnabled(true);
        } else {
            menuCardButton.setEnabled(false);
        }
    }


    private void getModifiedIsBlink() {
        ToggleButton blink = findViewById(R.id.menuButtonTextBlinkToggle);
        String isBlink = blink.getText().toString();
        if (isBlink.equalsIgnoreCase("on")) {
            menuCardButton.setButtonTextBlink(true);
        } else {
            menuCardButton.setButtonTextBlink(false);
        }
    }

    private void getName() {
        TextView name = findViewById(R.id.menuButtonName);
        menuCardButton.setName(name.getText().toString());
    }

    private void setName() {
        TextView name = findViewById(R.id.menuButtonName);
        String buttonName = menuCardButton.getName();

        if (buttonName == null && menuCardButton.getLocation() != null) {
            //  buttonName = menuCardButton.getLocation().name();
        }
        name.setText(buttonName);
    }

    private void setMenuStyle() {

    }


    private void setMenuLayout() {
        Spinner menuCardLayoutSpinner = findViewById(R.id.menuCardLayoutSpinner);
        String[] layouts = Arrays.toString(MenuCardLayoutEnum.values()).replaceAll("^.|.$", "").split(", ");
        List<String> layOuts = Arrays.asList(layouts);
        layoutArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, layOuts);
        layoutArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuCardLayoutSpinner.setAdapter(layoutArrayAdapter);
        if (layoutArrayAdapter.getCount() > 0) {
            menuCardLayoutSpinner.setSelection(0);
        }
    }

    private void setMenuData() {
        final Spinner menuTypeSpinner = findViewById(R.id.menuTypesSpinner);
        menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
            @Override
            public void onCallback(List<MenuType> menuTypes) {
                for (MenuType menuType : menuTypes) {
                    menuTypeNameMap.put(menuType.getName(), menuType);
                    menuTypeIDMap.put(menuType.getId(), menuType);
                }
                menuDataArrayAdapter = new ArrayAdapter<>(MenuButtonEditActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(menuTypeNameMap.keySet()));
                menuDataArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                menuTypeSpinner.setAdapter(menuDataArrayAdapter);
                if (menuDataArrayAdapter.getCount() > 0) {
                    menuTypeSpinner.setSelection(0);
                }
                setActions();
            }
        });
    }

    private void setLocation() {
        final Spinner menuLocationSpinner = findViewById(R.id.menuButtonLocationSpinner);

        final Set<MenuCardButtonEnum> buttonsInDB = new HashSet<>();
        menuCardAdminDao.getButtonsInCard(menuCardButton.getCardId(), new OnResultListener<List<MenuCardButton>>() {
            @Override
            public void onCallback(List<MenuCardButton> buttons) {
                for (MenuCardButton button : buttons) {
                    buttonsInDB.add(MenuCardButtonEnum.valueOf(button.getId() + ""));
                }
                List<MenuCardButtonEnum> remainingButtons = new ArrayList<>();
                MenuCardButtonEnum selectedLocation = menuCardButton.getLocation();
                for (MenuCardButtonEnum location : MenuCardButtonEnum.values()) {
                    if (!buttonsInDB.contains(location) || selectedLocation == location) {
                        remainingButtons.add(location);
                    }
                }

                String[] locations = Arrays.toString(remainingButtons.toArray()).replaceAll("^.|.$", "").split(", ");

                locationArrayAdapter = new ArrayAdapter<>(MenuButtonEditActivity.this, android.R.layout.simple_spinner_item, Arrays.asList(locations));
                locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                menuLocationSpinner.setAdapter(locationArrayAdapter);
                if (locationArrayAdapter.getCount() > 0) {
                    int spinnerPosition = 0;
                    if (selectedLocation != null) {
                        String menuDataSelectedStr = selectedLocation.name();
                        locationOfButton = menuDataSelectedStr;
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
        });
    }


    private void setShapeSpinner() {
        Spinner buttonShapeSpinner = findViewById(R.id.menuButtonShapeSpinner);
        String[] shapes = Arrays.toString(MenuCardButtonShapeEnum.values()).replaceAll("^.|.$", "").split(", ");

        shapeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(shapes));
        shapeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buttonShapeSpinner.setAdapter(shapeArrayAdapter);
        if (shapeArrayAdapter.getCount() > 0) {
            String shapeId = menuCardButton.getButtonShape();

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

    private void setButtonFontSpinner() {
        Spinner buttonFontSpinner = findViewById(R.id.menuButtonFontSpinner);
        String[] fonts = Arrays.toString(AppFontEnum.values()).replaceAll("^.|.$", "").split(", ");

        buttonFontArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        buttonFontArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buttonFontSpinner.setAdapter(buttonFontArrayAdapter);
        if (buttonFontArrayAdapter.getCount() > 0) {
            int spinnerPosition = 0;
            AppFontEnum appFontEnum = menuCardButton.getFont();
            if (appFontEnum != null) {
                String appFontSelectedStr = appFontEnum.name();
                if (appFontSelectedStr != null) {
                    spinnerPosition = buttonFontArrayAdapter.getPosition(appFontSelectedStr);
                }
            }
            buttonFontSpinner.setSelection(spinnerPosition);
        }
    }


    private void setColorSpinner(Spinner spinner, String selectedColorhexCode) {
        String[] colors = Arrays.toString(ColorCodeEnum.values()).replaceAll("^.|.$", "").split(", ");
        colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(colors));
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
        ToggleButton status = findViewById(R.id.menuButtonActiveToggle);
        if (menuCardButton.isEnabled()) {
            status.setText("Y");
            status.setChecked(true);
        } else {
            status.setText("N");
            status.setChecked(false);
        }
    }


    private void setIsBlinkText() {
        ToggleButton isBlinkToggle = findViewById(R.id.menuButtonTextBlinkToggle);
        boolean isBlink = menuCardButton.isButtonTextBlink();
        if (isBlink) {
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
            menuCardButtonAdminDao.insertOrUpdateButton(menuCardButton, locationOfButton, new OnResultListener<MenuCardButton>() {
                @Override
                public void onCallback(MenuCardButton button) {
                    menuCardButtonAdminDao.deleteAndInsertAllActionsForButton(button.getCardId(), button.getId() + "", button.getActions(), new OnResultListener<List<MenuCardAction>>() {
                        @Override
                        public void onCallback(List<MenuCardAction> actions) {
                            goToModifyPage();
                        }
                    });
                }
            });
        }
    }

    private void goToModifyPage() {
        Map<String, Object> params = new HashMap<>();
        params.put("menuCardEdit_menuCardId", menuCardButton.getCardId());
        authenticationController.goToMenuCardSettingsPage(params);
    }

    long highestPositionOfAction = 0;

    public void addAction(View view) {
        Spinner menuTypeSpinner = findViewById(R.id.menuTypesSpinner);
        Spinner layoutSpinner = findViewById(R.id.menuCardLayoutSpinner);

        if (menuTypeSpinner.getSelectedItem() != null && menuTypeSpinner.getSelectedItem().toString() != null && !menuTypeSpinner.getSelectedItem().toString().equals("")
                && layoutSpinner.getSelectedItem() != null && layoutSpinner.getSelectedItem().toString() != null && !layoutSpinner.getSelectedItem().toString().equals("")) {
            String selectedMenuType = menuTypeSpinner.getSelectedItem().toString();
            String menuTypeId = menuTypeNameMap.get(selectedMenuType).getId();
            String selectedLayout = layoutSpinner.getSelectedItem().toString();
            long layoutId = MenuCardLayoutEnum.valueOf(selectedLayout).getValue();

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
        ListView listView = findViewById(R.id.menuActionAddListView);
        menuActionListAdapter = new MenuActionListAdapter(new ArrayList<>(menuCardButton.getActions()), menuTypeIDMap, this);
        listView.setAdapter(menuActionListAdapter);
        ListViewUtils.setListViewHeightBasedOnChildren(listView);
    }

    public void setActions(List<MenuCardAction> actions) {
        menuCardButton.setActions(actions);
    }
}
