package app.resta.com.restaurantapp.controller;

import java.util.ArrayList;
import java.util.List;

public class RestaurantItemExtraDataController {
    List<Long> ggwItemsAdded;
    List<Long> ggwItemsDeleted;
    List<Long> ingredientsAdded;
    List<Long> ingredientsDeleted;
    List<Long> tagsAdded;
    List<Long> tagsDeleted;

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

    public void addGGWItem(Long ggwId) {
        //Long id = Long.parseLong(ggwId);
        if (ggwItemsDeleted.contains(ggwId)) {
            ggwItemsDeleted.remove(ggwId);
        } else {
            ggwItemsAdded.add(ggwId);
        }
    }

    public void addTagItem(Long tagId) {
        //tag = tag.toLowerCase();
        if (tagsDeleted.contains(tagId)) {
            tagsDeleted.remove(tagId);
        } else {
            tagsAdded.add(tagId);
        }
    }


    public void addIngredientItem(Long ingrdientId) {
        //ingrdient = ingrdient.toLowerCase();
        if (ingredientsDeleted.contains(ingrdientId)) {
            ingredientsDeleted.remove(ingrdientId);
        } else {
            ingredientsAdded.add(ingrdientId);
        }
    }


    public void deleteGGWItem(Long id) {
        if (ggwItemsAdded.contains(id)) {
            ggwItemsAdded.remove(id);
        } else {
            ggwItemsDeleted.add(id);
        }
    }


    public void deleteTagItem(Long tagId) {
        if (tagsAdded.contains(tagId)) {
            tagsAdded.remove(tagId);
        } else {
            tagsDeleted.add(tagId);
        }
    }


    public void deleteIngredientItem(Long ingredientId) {
        if (ingredientsAdded.contains(ingredientId)) {
            ingredientsAdded.remove(ingredientId);
        } else {
            ingredientsDeleted.add(ingredientId);
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

    public List<Long> getIngredientsAdded() {
        return ingredientsAdded;
    }

    public void setIngredientsAdded(List<Long> ingredientsAdded) {
        this.ingredientsAdded = ingredientsAdded;
    }

    public List<Long> getIngredientsDeleted() {
        return ingredientsDeleted;
    }

    public void setIngredientsDeleted(List<Long> ingredientsDeleted) {
        this.ingredientsDeleted = ingredientsDeleted;
    }

    public List<Long> getTagsAdded() {
        return tagsAdded;
    }

    public void setTagsAdded(List<Long> tagsAdded) {
        this.tagsAdded = tagsAdded;
    }

    public List<Long> getTagsDeleted() {
        return tagsDeleted;
    }

    public void setTagsDeleted(List<Long> tagsDeleted) {
        this.tagsDeleted = tagsDeleted;
    }
}
