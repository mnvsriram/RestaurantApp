package app.resta.com.restaurantapp.db.dao.admin.ingredient;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface IngredientAdminDaoI {

    void insertIngredient(final Ingredient ingredient, final OnResultListener<Ingredient> listener);

    void insertIngredients(final List<Ingredient> ingredients, final OnResultListener<List<Ingredient>> listener);

    void getIngredients(final OnResultListener<List<Ingredient>> listener);

    void deleteIngredient(String ingredientAppId);

    void getIngredient(String ingredientId, final OnResultListener<Ingredient> listener);

    void updateImageUrl(final Ingredient ingredient, String imageStorageUrl, final OnResultListener<String> listener);
}
