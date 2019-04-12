package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class MenuCard implements Serializable {
    private String id;
    private String name;
    private boolean isDefault;
    private String greetingText;
    private String logoBigImageUrl;
    private String logoSmallImageUrl;
    private String backgroundColor;

    public static final String FIRESTORE_NAME_KEY = "name";
    public static final String FIRESTORE_DEFAULT_KEY = "default";
    public static final String FIRESTORE_GREETING_TEXT_KEY = "greetingText";
    public static final String FIRESTORE_LOGO_IMABE_BIG_URL = "logoBigImageUrl";
    public static final String FIRESTORE_LOGO_IMABE_SMALL_URL = "logoSmallImageUrl";
    public static final String FIRESTORE_BG_COLOR = "menuCardBgColor";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";

    private Map<MenuCardButtonEnum, MenuCardButton> buttons = new HashMap<>();

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }

    public String getLogoBigImageUrl() {
        return logoBigImageUrl;
    }

    public void setLogoBigImageUrl(String logoBigImageUrl) {
        this.logoBigImageUrl = logoBigImageUrl;
    }

    public String getLogoSmallImageUrl() {
        return logoSmallImageUrl;
    }

    public void setLogoSmallImageUrl(String logoSmallImageUrl) {
        this.logoSmallImageUrl = logoSmallImageUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<MenuCardButtonEnum, MenuCardButton> getButtons() {
        return buttons;
    }

    public void setButtons(Map<MenuCardButtonEnum, MenuCardButton> buttons) {
        this.buttons = buttons;
    }

    public void setButtons(List<MenuCardButton> buttons) {
        Map<MenuCardButtonEnum, MenuCardButton> allButtonsMap = new HashMap<>();
        if (buttons != null) {
            for (MenuCardButton button : buttons) {
                allButtonsMap.put(button.getLocation(), button);
            }
        }
        this.buttons = allButtonsMap;
    }

    public Map<MenuCardButtonEnum, MenuCardButton> getMainButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> mainButtons = new LinkedHashMap<>();
        mainButtons.put(MenuCardButtonEnum.MAIN_1, getDefaultIfNull(MenuCardButtonEnum.MAIN_1));
        mainButtons.put(MenuCardButtonEnum.MAIN_2, getDefaultIfNull(MenuCardButtonEnum.MAIN_2));
        mainButtons.put(MenuCardButtonEnum.MAIN_3, getDefaultIfNull(MenuCardButtonEnum.MAIN_3));
        mainButtons.put(MenuCardButtonEnum.MAIN_4, getDefaultIfNull(MenuCardButtonEnum.MAIN_4));
        return mainButtons;
    }


    public Map<MenuCardButtonEnum, MenuCardButton> getOtherButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> otherButtons = new LinkedHashMap<>();

        otherButtons.put(MenuCardButtonEnum.TOP_LEFT, buttons.get(MenuCardButtonEnum.TOP_LEFT));
        otherButtons.put(MenuCardButtonEnum.TOP_CENTER, buttons.get(MenuCardButtonEnum.TOP_CENTER));
        otherButtons.put(MenuCardButtonEnum.TOP_RIGHT, buttons.get(MenuCardButtonEnum.TOP_RIGHT));

        otherButtons.put(MenuCardButtonEnum.MIDDLE_LEFT, buttons.get(MenuCardButtonEnum.MIDDLE_LEFT));
        otherButtons.put(MenuCardButtonEnum.MIDDLE_CENTER, buttons.get(MenuCardButtonEnum.MIDDLE_CENTER));
        otherButtons.put(MenuCardButtonEnum.MIDDLE_RIGHT, buttons.get(MenuCardButtonEnum.MIDDLE_RIGHT));

        otherButtons.put(MenuCardButtonEnum.BOTTOM_LEFT, buttons.get(MenuCardButtonEnum.BOTTOM_LEFT));
        otherButtons.put(MenuCardButtonEnum.BOTTOM_CENTER, buttons.get(MenuCardButtonEnum.BOTTOM_CENTER));
        otherButtons.put(MenuCardButtonEnum.BOTTOM_RIGHT, buttons.get(MenuCardButtonEnum.BOTTOM_RIGHT));

        return otherButtons;
    }

    private MenuCardButton getDefaultIfNull(MenuCardButtonEnum buttonEnum) {
        MenuCardButton button = buttons.get(buttonEnum);
        if (button == null) {
            button = new MenuCardButton();
            button.setLocation(buttonEnum);
        }
        return button;
    }


    public static MenuCard prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }


    public static MenuCard prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static MenuCard get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        MenuCard menuCard = null;
        if (keyValueMap != null) {
            menuCard = new MenuCard();
            menuCard.setId(documentSnapshot.getId());
            String name = FireStoreUtil.getString(keyValueMap, FIRESTORE_NAME_KEY);
            String greetingTxt = FireStoreUtil.getString(keyValueMap, FIRESTORE_GREETING_TEXT_KEY);

            String logoBigImageUrl = FireStoreUtil.getImageUrl(keyValueMap, FIRESTORE_LOGO_IMABE_BIG_URL);
            String logoSmallImageUrl = FireStoreUtil.getImageUrl(keyValueMap, FIRESTORE_LOGO_IMABE_SMALL_URL);
            String bgColor = FireStoreUtil.getString(keyValueMap, FIRESTORE_BG_COLOR);

            menuCard.setName(name);
            menuCard.setGreetingText(greetingTxt);
            menuCard.setLogoBigImageUrl(logoBigImageUrl);
            menuCard.setLogoSmallImageUrl(logoSmallImageUrl);
            menuCard.setBackgroundColor(bgColor);
        }
        return menuCard;
    }


}
