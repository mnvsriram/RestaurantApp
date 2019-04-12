package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class MenuCardAction implements Serializable {
    public static final String FIRESTORE_BUTTON_ID_KEY = "buttonId";
    public static final String FIRESTORE_MENU_TYPE_ID_KEY = "menuTypeId";
    public static final String FIRESTORE_LAYOUT_ID_KEY = "layoutId";
    public static final String FIRESTORE_POSITION_KEY = "position";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private String id;
    private String buttonId;
    private String menuTypeId;
    private String menuTypeName;
    private long layoutId;
    private String layoutName;
    private long position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }


    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public static MenuCardAction prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    public static MenuCardAction prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static MenuCardAction get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        MenuCardAction menuCardAction = new MenuCardAction();
        menuCardAction.setId(documentSnapshot.getId());
        menuCardAction.setButtonId(FireStoreUtil.getString(keyValueMap, FIRESTORE_BUTTON_ID_KEY));
        menuCardAction.setMenuTypeId(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_TYPE_ID_KEY));
        menuCardAction.setLayoutId(FireStoreUtil.getLong(keyValueMap, FIRESTORE_LAYOUT_ID_KEY));
        menuCardAction.setPosition(FireStoreUtil.getLong(keyValueMap, FIRESTORE_POSITION_KEY));
        menuCardAction.setLayoutName(MenuCardLayoutEnum.of(menuCardAction.getLayoutId()).name());
        return menuCardAction;
    }
}
