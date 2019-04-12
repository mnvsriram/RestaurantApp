package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.Ingredient;

/**
 * Created by Sriram on 29/07/2017.
 */
public class IngredientNameComparator implements Comparator<Ingredient> {
    @Override
    public int compare(Ingredient o1, Ingredient o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
