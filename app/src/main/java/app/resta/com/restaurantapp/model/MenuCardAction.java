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

    public static final String FIRESTORE_MENU_NAME_FONT = "menuName.font";
    public static final String FIRESTORE_MENU_NAME_FONT_SIZE = "menuName.size";
    public static final String FIRESTORE_MENU_NAME_FONT_FACE = "menuName.face";
    public static final String FIRESTORE_MENU_NAME_FONT_COLOR = "menuName.color";


    public static final String FIRESTORE_MENU_DESC_FONT = "menuDesc.font";
    public static final String FIRESTORE_MENU_DESC_FONT_SIZE = "menuDesc.size";
    public static final String FIRESTORE_MENU_DESC_FONT_FACE = "menuDesc.face";
    public static final String FIRESTORE_MENU_DESC_FONT_COLOR = "menuDesc.color";


    public static final String FIRESTORE_GROUP_NAME_FONT = "groupName.font";
    public static final String FIRESTORE_GROUP_NAME_FONT_SIZE = "groupName.size";
    public static final String FIRESTORE_GROUP_NAME_FONT_FACE = "groupName.face";
    public static final String FIRESTORE_GROUP_NAME_FONT_COLOR = "groupName.color";


    public static final String FIRESTORE_ITEM_NAME_FONT = "itemName.font";
    public static final String FIRESTORE_ITEM_NAME_FONT_SIZE = "itemName.size";
    public static final String FIRESTORE_ITEM_NAME_FONT_FACE = "itemName.face";
    public static final String FIRESTORE_ITEM_NAME_FONT_COLOR = "itemName.color";


    public static final String FIRESTORE_ITEM_DESC_FONT = "itemDesc.font";
    public static final String FIRESTORE_ITEM_DESC_FONT_SIZE = "itemDesc.size";
    public static final String FIRESTORE_ITEM_DESC_FONT_FACE = "itemDesc.face";
    public static final String FIRESTORE_ITEM_DESC_FONT_COLOR = "itemDesc.color";


    private String id;
    private String buttonId;
    private String menuTypeId;
    private String menuTypeName;
    private long layoutId;
    private String layoutName;
    private long position;

    private MenuCardActionStyle menuNameStyle;
    private MenuCardActionStyle menuDescStyle;
    private MenuCardActionStyle GroupNameStyle;
    private MenuCardActionStyle itemNameStyle;
    private MenuCardActionStyle itemDescStyle;

    public MenuCardActionStyle getMenuDescStyle() {
        return menuDescStyle;
    }

    public void setMenuDescStyle(MenuCardActionStyle menuDescStyle) {
        this.menuDescStyle = menuDescStyle;
    }

    public MenuCardActionStyle getGroupNameStyle() {
        return GroupNameStyle;
    }

    public void setGroupNameStyle(MenuCardActionStyle groupNameStyle) {
        GroupNameStyle = groupNameStyle;
    }

    public MenuCardActionStyle getItemNameStyle() {
        return itemNameStyle;
    }

    public void setItemNameStyle(MenuCardActionStyle itemNameStyle) {
        this.itemNameStyle = itemNameStyle;
    }

    public MenuCardActionStyle getItemDescStyle() {
        return itemDescStyle;
    }

    public void setItemDescStyle(MenuCardActionStyle itemDescStyle) {
        this.itemDescStyle = itemDescStyle;
    }

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

    public MenuCardActionStyle getMenuNameStyle() {
        return menuNameStyle;
    }

    public void setMenuNameStyle(MenuCardActionStyle menuNameStyle) {
        this.menuNameStyle = menuNameStyle;
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


        MenuCardActionStyle menuNameStyle = new MenuCardActionStyle();
        menuNameStyle.setFont(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_NAME_FONT));
        menuNameStyle.setFontSize(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_NAME_FONT_SIZE));
        menuNameStyle.setFontFace(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_NAME_FONT_FACE));
        menuNameStyle.setFontColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_NAME_FONT_COLOR));

        MenuCardActionStyle menuDescStyle = new MenuCardActionStyle();
        menuDescStyle.setFont(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_DESC_FONT));
        menuDescStyle.setFontSize(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_DESC_FONT_SIZE));
        menuDescStyle.setFontFace(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_DESC_FONT_FACE));
        menuDescStyle.setFontColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_DESC_FONT_COLOR));


        MenuCardActionStyle groupNameStyle = new MenuCardActionStyle();
        groupNameStyle.setFont(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_NAME_FONT));
        groupNameStyle.setFontSize(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_NAME_FONT_SIZE));
        groupNameStyle.setFontFace(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_NAME_FONT_FACE));
        groupNameStyle.setFontColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_NAME_FONT_COLOR));

        MenuCardActionStyle itemNameStyle = new MenuCardActionStyle();
        itemNameStyle.setFont(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_NAME_FONT));
        itemNameStyle.setFontSize(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_NAME_FONT_SIZE));
        itemNameStyle.setFontFace(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_NAME_FONT_FACE));
        itemNameStyle.setFontColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_NAME_FONT_COLOR));


        MenuCardActionStyle itemDescStyle = new MenuCardActionStyle();
        itemDescStyle.setFont(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_DESC_FONT));
        itemDescStyle.setFontSize(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_DESC_FONT_SIZE));
        itemDescStyle.setFontFace(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_DESC_FONT_FACE));
        itemDescStyle.setFontColor(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_DESC_FONT_COLOR));

        menuCardAction.setMenuNameStyle(menuNameStyle);
        menuCardAction.setMenuDescStyle(menuDescStyle);
        menuCardAction.setGroupNameStyle(groupNameStyle);
        menuCardAction.setItemNameStyle(itemNameStyle);
        menuCardAction.setItemDescStyle(itemDescStyle);

        return menuCardAction;
    }
}
