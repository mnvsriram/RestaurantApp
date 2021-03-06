package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class MenuCardButton implements Serializable {
    public static final String FIRESTORE_NAME_KEY = "name";
    public static final String FIRESTORE_CARD_ID_KEY = "cardId";
    public static final String FIRESTORE_BUTTON_TYPE_KEY = "type";
    public static final String FIRESTORE_FONT_TYPE_KEY = "FontType";
    public static final String FIRESTORE_CONTENT_FONT_TYPE_KEY = "ContentFontType";

    public static final String FIRESTORE_CONTENT_FONT_COLOR_KEY = "ContentFontColor";
    public static final String FIRESTORE_CONTENT_BG_COLOR_KEY = "ContentBGColor";

    public static final String FIRESTORE_ACTIVE_KEY = "active";
    public static final String FIRESTORE_BUTTON_SHAPE_KEY = "buttonShape";
    public static final String FIRESTORE_BUTTON_TEXT_COLOR_KEY = "buttonTextColor";
    public static final String FIRESTORE_BUTTON_COLOR_KEY = "buttonColor";
    public static final String FIRESTORE_BUTTON_TEXT_BLINK_KEY = "buttonTextBlink";


    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private String id;
    private String name;
    private String cardId;
    private String buttonShape;
    private String buttonTextColor;
    private String buttonColor;
    private String contentColor;
    private String contentBackgroundColor;
    private boolean buttonTextBlink;
    private MenuCardButtonEnum location;
    private AppFontEnum font;
    private AppFontEnum contentFont;
    private boolean enabled = true;
    List<MenuCardAction> actions = new ArrayList<>();

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public String getContentBackgroundColor() {
        return contentBackgroundColor;
    }

    public void setContentBackgroundColor(String contentBackgroundColor) {
        this.contentBackgroundColor = contentBackgroundColor;
    }

    public AppFontEnum getContentFont() {
        return contentFont;
    }

    public void setContentFont(AppFontEnum contentFont) {
        this.contentFont = contentFont;
    }

    public String getButtonShape() {
        return buttonShape;
    }

    public void setButtonShape(String buttonShape) {
        this.buttonShape = buttonShape;
    }

    public String getButtonTextColor() {
        return buttonTextColor;
    }

    public AppFontEnum getFont() {
        return font;
    }

    public void setFont(AppFontEnum font) {
        this.font = font;
    }

    public void setButtonTextColor(String buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public String getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }

    public boolean isButtonTextBlink() {
        return buttonTextBlink;
    }

    public void setButtonTextBlink(boolean buttonTextBlink) {
        this.buttonTextBlink = buttonTextBlink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<MenuCardAction> getActions() {
        return actions;
    }

    public void setActions(List<MenuCardAction> actions) {
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public MenuCardButtonEnum getLocation() {
        return location;
    }

    public void setLocation(MenuCardButtonEnum location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean blink() {
        return buttonTextBlink;
    }

    public void addAction(MenuCardAction menuCardAction) {
        actions.add(menuCardAction);
    }


    public void removeAction(MenuCardAction menuCardAction) {
        if (actions != null) {
            actions.remove(menuCardAction);
        }
    }


    public static MenuCardButton prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    public static MenuCardButton prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static MenuCardButton get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        MenuCardButton menuCardButton = new MenuCardButton();
        try {
            if (keyValueMap != null) {
                menuCardButton.setId(documentSnapshot.getId());
                menuCardButton.setName(FireStoreUtil.getString(keyValueMap, FIRESTORE_NAME_KEY));
                menuCardButton.setCardId(FireStoreUtil.getString(keyValueMap, FIRESTORE_CARD_ID_KEY));
                menuCardButton.setButtonShape(FireStoreUtil.getString(keyValueMap, FIRESTORE_BUTTON_SHAPE_KEY));
                menuCardButton.setButtonTextColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_BUTTON_TEXT_COLOR_KEY));
                menuCardButton.setButtonColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_BUTTON_COLOR_KEY));
                menuCardButton.setButtonTextBlink(FireStoreUtil.getBoolean(keyValueMap, FIRESTORE_BUTTON_TEXT_BLINK_KEY));
                menuCardButton.setEnabled(FireStoreUtil.getBoolean(keyValueMap, FIRESTORE_ACTIVE_KEY));

                String location = FireStoreUtil.getString(keyValueMap, FIRESTORE_BUTTON_TYPE_KEY);
                if (location != null) {
                    menuCardButton.setLocation(MenuCardButtonEnum.valueOf(location));
                }

                String fontType = FireStoreUtil.getString(keyValueMap, FIRESTORE_FONT_TYPE_KEY);
                if (fontType != null) {
                    menuCardButton.setFont(AppFontEnum.valueOf(fontType));
                }

                String contentFontType = FireStoreUtil.getString(keyValueMap, FIRESTORE_CONTENT_FONT_TYPE_KEY);
                if (contentFontType != null) {
                    menuCardButton.setContentFont(AppFontEnum.valueOf(contentFontType));
                }

                menuCardButton.setContentColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_CONTENT_FONT_COLOR_KEY));
                menuCardButton.setContentBackgroundColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_CONTENT_BG_COLOR_KEY));

            }

        } catch (Exception e) {
            Log.e("MenuCard", "Error when creating the menu card button");
        }
        return menuCardButton;
    }

}
