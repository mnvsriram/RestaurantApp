package app.resta.com.restaurantapp.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sriram on 15/06/2017.
 */
public class RestaurantItemExtraDataController {
    List<Long> ggwItemsAdded;
    List<Long> ggwItemsDeleted;
    List<String> ingredientsAdded;
    List<String> ingredientsDeleted;
    List<String> tagsAdded;
    List<String> tagsDeleted;

    public RestaurantItemExtraDataController() {
        ggwItemsAdded = new ArrayList<>();
        ggwItemsDeleted = new ArrayList<>();
        ingredientsAdded = new ArrayList<>();
        ingredientsDeleted = new ArrayList<>();
        tagsAdded = new ArrayList<>();
        tagsDeleted = new ArrayList<>();
    }

    public void addGGWItem(Long ggwId) {
        //Long id = Long.parseLong(ggwId);
        if (ggwItemsDeleted.contains(ggwId)) {
            ggwItemsDeleted.remove(ggwId);
        } else {
            ggwItemsAdded.add(ggwId);
        }
    }

    public void addTagItem(String tag) {
        tag = tag.toLowerCase();
        if (tagsDeleted.contains(tag)) {
            tagsDeleted.remove(tag);
        } else {
            tagsAdded.add(tag);
        }
    }


    public void addIngredientItem(String ingrdient) {
        ingrdient = ingrdient.toLowerCase();
        if (ingredientsDeleted.contains(ingrdient)) {
            ingredientsDeleted.remove(ingrdient);
        } else {
            ingredientsAdded.add(ingrdient);
        }
    }


    public void deleteGGWItem(Long id) {

        if (ggwItemsAdded.contains(id)) {
            ggwItemsAdded.remove(id);
        } else {
            ggwItemsDeleted.add(id);
        }
    }


    public void deleteTagItem(String tag) {
        if (tagsAdded.contains(tag)) {
            tagsAdded.remove(tag);
        } else {
            tagsDeleted.add(tag);
        }
    }


    public void deleteIngredientItem(String ingredient) {
        if (ingredientsAdded.contains(ingredient)) {
            ingredientsAdded.remove(ingredient);
        } else {
            ingredientsDeleted.add(ingredient);
        }
    }

    public List<Long> getGgwItemsAdded() {
        return ggwItemsAdded;
    }

    public void setGgwItemsAdded(List<Long> ggwItemsAdded) {
        this.ggwItemsAdded = ggwItemsAdded;
    }

    public List<Long> getGgwItemsDeleted() {
        return ggwItemsDeleted;
    }

    public void setGgwItemsDeleted(List<Long> ggwItemsDeleted) {
        this.ggwItemsDeleted = ggwItemsDeleted;
    }

    public List<String> getIngredientsAdded() {
        return ingredientsAdded;
    }

    public void setIngredientsAdded(List<String> ingredientsAdded) {
        this.ingredientsAdded = ingredientsAdded;
    }

    public List<String> getIngredientsDeleted() {
        return ingredientsDeleted;
    }

    public void setIngredientsDeleted(List<String> ingredientsDeleted) {
        this.ingredientsDeleted = ingredientsDeleted;
    }

    public List<String> getTagsAdded() {
        return tagsAdded;
    }

    public void setTagsAdded(List<String> tagsAdded) {
        this.tagsAdded = tagsAdded;
    }

    public List<String> getTagsDeleted() {
        return tagsDeleted;
    }

    public void setTagsDeleted(List<String> tagsDeleted) {
        this.tagsDeleted = tagsDeleted;
    }
}
