package app.resta.com.restaurantapp.db.dao.admin.menuItem;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuItemAdminDaoI {
    //Ingredients
    void addIngredientsToItem(String itemId, List<String> ingredientIds);

    void removeIngredientsFromItem(String itemId, List<String> ingredientIds);

    void getIngredientsForItem(String itemId, final OnResultListener<List<String>> listener);

    void getAllItemsWithIngredient(final String ingredientId, final OnResultListener<List<RestaurantItem>> listener);

    //Tags
    void addTagsToItem(String itemId, List<String> tagIds);

    void removeTagsFromItem(String itemId, List<String> tagIds);

    void getTagsForItem(String itemId, final OnResultListener<List<String>> listener);

    void getAllItemsWithTag(final String tagId, final OnResultListener<List<RestaurantItem>> listener);

    //GGW Items
    void addGGWsToItem(String itemId, List<String> ggwItemIds);

    void removeGGWsFromItem(String itemId, List<String> ggwItemIds);

    void getGGWsForItem(String itemId, final OnResultListener<List<String>> listener);

    void getAllItemsWithGGW(final String ggwId, final OnResultListener<List<RestaurantItem>> listener);

    //Menu Item
    void getItem(final String itemId, final OnResultListener<RestaurantItem> listener);

    void deleteItem(final String itemId, final OnResultListener<String> listener);

    void getAllItems(final OnResultListener<List<RestaurantItem>> listener);

    void insertOrUpdateMenuItem(final RestaurantItem item, final OnResultListener<RestaurantItem> listener);

//    void updateImageUrl(final RestaurantItem item, String imageNum, String imageDesc, String imageStorageUrl);

    void getItems(List<GroupAndItemMapping> mappings, final OnResultListener<List<RestaurantItem>> listener);

//review

    void updateItemsWithRating(List<ReviewForDish> reviewForDishes);

    void updateItemWithRating(String itemId, ReviewEnum reviewEnum, final OnResultListener<String> listener);

    void deleteGGWReferencesFromMenuItems(final String itemId, final OnResultListener<String> listener);
}
