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
public class GroupAndItemMapping implements Serializable, Cloneable {
    public static final String FIRESTORE_ITEM_ID = "itemId";
    public static final String FIRESTORE_GROUP_ID = "groupId";
    public static final String FIRESTORE_ITEM_POSITION = "itemPositionInGroup";
    public static final String FIRESTORE_ITEM_PRICE_KEY = "itemPriceInGroup";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private String itemId;
    private String groupId;
    private long itemPosition;
    private String itemPrice;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(long itemPosition) {
        this.itemPosition = itemPosition;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }


    public static GroupAndItemMapping prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }


    public static GroupAndItemMapping prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static GroupAndItemMapping get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        GroupAndItemMapping groupAndItemMapping = new GroupAndItemMapping();
        groupAndItemMapping.setItemId(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_ID));
        groupAndItemMapping.setGroupId(FireStoreUtil.getString(keyValueMap, FIRESTORE_GROUP_ID));
        groupAndItemMapping.setItemPosition(FireStoreUtil.getLong(keyValueMap, FIRESTORE_ITEM_POSITION));
        groupAndItemMapping.setItemPrice(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_PRICE_KEY));
        return groupAndItemMapping;
    }
}
