package app.resta.com.restaurantapp.db.dao.user.ingredient;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface IngredientUserDaoI {
    void getIngredient_u(String ingredientId, final OnResultListener<Ingredient> listener);
}
