package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

/**
 * Created by Sriram on 13/05/2017.
 */
public class MenuTypeAndGroupMapping implements Serializable, Cloneable {
    public static final String FIRESTORE_GROUP_ID = "groupId";
    public static final String FIRESTORE_MENU_TYPE_ID = "menuTypeId";
    public static final String FIRESTORE_GROUP_POSITION = "groupPositionInMenuType";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private String groupId;
    private String menuTypeId;//parent
    private long groupPositionInMenuType;
    private String itemPrice;

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getGroupPositionInMenuType() {
        return groupPositionInMenuType;
    }

    public void setGroupPositionInMenuType(long groupPositionInMenuType) {
        this.groupPositionInMenuType = groupPositionInMenuType;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }


    public static MenuTypeAndGroupMapping prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }


    public static MenuTypeAndGroupMapping prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static MenuTypeAndGroupMapping get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        MenuTypeAndGroupMapping menuTypeAndGroupMapping = new MenuTypeAndGroupMapping();
        menuTypeAndGroupMapping.setMenuTypeId(FireStoreUtil.getString(keyValueMap, FIRESTORE_MENU_TYPE_ID));
        menuTypeAndGroupMapping.setGroupId(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_ID));
        menuTypeAndGroupMapping.setGroupPositionInMenuType(FireStoreUtil.getLong(keyValueMap, FIRESTORE_GROUP_POSITION));
        return menuTypeAndGroupMapping;
    }
}
