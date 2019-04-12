package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class Ingredient implements Serializable {
    private String id;
    private String name;
    private String image;


    public static final String FIRESTORE_NAME_KEY = "name";
    public static final String FIRESTORE_IMAGE_URL = "ingredientImageUrl";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Ingredient prepareIngredient(QueryDocumentSnapshot documentSnapshot) {
        return getIngredient(documentSnapshot);
    }

    public static Ingredient prepareIngredient(DocumentSnapshot documentSnapshot) {
        return getIngredient(documentSnapshot);
    }

    @NonNull
    private static Ingredient getIngredient(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        Ingredient ingredientFromDB = null;
        if (keyValueMap != null) {
            ingredientFromDB = new Ingredient();
            ingredientFromDB.setId(documentSnapshot.getId());
            String name = FireStoreUtil.getString(keyValueMap, FIRESTORE_NAME_KEY);
            String image = FireStoreUtil.getImageUrl(keyValueMap, FIRESTORE_IMAGE_URL);

            ingredientFromDB.setName(name);
            ingredientFromDB.setImage(image);
        }
        return ingredientFromDB;
    }


}
