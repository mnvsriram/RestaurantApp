package app.resta.com.restaurantapp.db.dao.user.ingredient;

import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;

public class IngredientUserFireStoreDao extends IngredientAdminFireStoreDao implements IngredientUserDaoI {
    private static final String TAG = "MenuItemAdminDao";

    @Override
    public void getIngredient_u(String ingredientId, final OnResultListener<Ingredient> listener) {
        getIngredient(ingredientId, Source.CACHE, listener);
    }
}
