package app.resta.com.restaurantapp.controller;

import java.util.ArrayList;
import java.util.List;

public class RestaurantItemExtraDataController {
    List<String> ggwItemsAdded;
    List<String> ggwItemsDeleted;
    List<String> ingredientsAdded;
    List<String> ingredientsDeleted;
    List<String> tagsAdded;
    List<String> tagsDeleted;

    List<Long> itemsAdded;
    List<Long> deleted;

    public RestaurantItemExtraDataController() {
        ggwItemsAdded = new ArrayList<>();
        ggwItemsDeleted = new ArrayList<>();
        ingredientsAdded = new ArrayList<>();
        ingredientsDeleted = new ArrayList<>();
        tagsAdded = new ArrayList<>();
        tagsDeleted = new ArrayList<>();
    }

    public void addGGWItem(String ggwId) {
        //Long id = Long.parseLong(ggwId);
        if (ggwItemsDeleted.contains(ggwId)) {
            ggwItemsDeleted.remove(ggwId);
        } else {
            ggwItemsAdded.add(ggwId);
        }
    }

    public void addTagItem(String tagId) {
        if (tagsDeleted.contains(tagId)) {
            tagsDeleted.remove(tagId);
        } else {
            tagsAdded.add(tagId);
        }
    }


    public void addIngredientItem(String ingrdientAppId) {
        //ingrdient = ingrdient.toLowerCase();
        if (ingredientsDeleted.contains(ingrdientAppId)) {
            ingredientsDeleted.remove(ingrdientAppId);
        } else {
            ingredientsAdded.add(ingrdientAppId);
        }
    }


    public void deleteGGWItem(String id) {
        if (ggwItemsAdded.contains(id)) {
            ggwItemsAdded.remove(id);
        } else {
            ggwItemsDeleted.add(id);
        }
    }


    public void deleteTagItem(String tagId) {
        if (tagsAdded.contains(tagId)) {
            tagsAdded.remove(tagId);
        } else {
            tagsDeleted.add(tagId);
        }
    }


    public void deleteIngredientItem(String ingredientAppId) {
        if (ingredientsAdded.contains(ingredientAppId)) {
            ingredientsAdded.remove(ingredientAppId);
        } else {
            ingredientsDeleted.add(ingredientAppId);
        }
    }

    public List<String> getGgwItemsAdded() {
        return ggwItemsAdded;
    }

    public void setGgwItemsAdded(List<String> ggwItemsAdded) {
        this.ggwItemsAdded = ggwItemsAdded;
    }

    public List<String> getGgwItemsDeleted() {
        return ggwItemsDeleted;
    }

    public void setGgwItemsDeleted(List<String> ggwItemsDeleted) {
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
