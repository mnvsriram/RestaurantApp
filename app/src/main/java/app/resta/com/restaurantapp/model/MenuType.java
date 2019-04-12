package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class MenuType implements Serializable {
    public static final String FIRESTORE_NAME_KEY = "name";
    public static final String FIRESTORE_DESC_KEY = "description";
    public static final String FIRESTORE_PRICE_KEY = "price";
    public static final String FIRESTORE_SHOW_PRICE_FOR_CHILDREN = "showPriceForChildren";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private String id;
    private String name;
    private String price;
    private String description;
    private boolean showPriceOfChildren = true;

    public boolean isShowPriceOfChildren() {
        return showPriceOfChildren;
    }

    public void setShowPriceOfChildren(boolean showPriceOfChildren) {
        this.showPriceOfChildren = showPriceOfChildren;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public static MenuType prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    public static MenuType prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static MenuType get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        MenuType menuType = new MenuType();
        menuType.setId(documentSnapshot.getId());
        if (keyValueMap != null) {
            menuType.setName(FireStoreUtil.getString(keyValueMap, FIRESTORE_NAME_KEY));
            menuType.setDescription(FireStoreUtil.getString(keyValueMap, FIRESTORE_DESC_KEY));
            menuType.setPrice(FireStoreUtil.getString(keyValueMap, FIRESTORE_PRICE_KEY));
            menuType.setShowPriceOfChildren(FireStoreUtil.getBoolean(keyValueMap, FIRESTORE_SHOW_PRICE_FOR_CHILDREN));
        }

        return menuType;
    }
}
