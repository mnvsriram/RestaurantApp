package app.resta.com.restaurantapp.db.dao.admin.menuItem;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.FireStoreUtil;
import app.resta.com.restaurantapp.util.ItemPositionComparator;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.PerformanceUtils;

public class MenuItemAdminFireStoreDao implements MenuItemAdminDaoI {
    private static final String TAG = "MenuItemAdminDao";
    FirebaseFirestore db;

    public MenuItemAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    private void addIngredientToItem(final String itemId, final String ingredientId) {
        Log.i(TAG, "Adding ingredient " + ingredientId + " to menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("ingredients", FieldValue.arrayUnion(ingredientId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ingredient " + ingredientId + " added to item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when adding Ingredient " + ingredientId + " to item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    @Override
    public void addIngredientsToItem(final String itemId, final List<String> ingredientIds) {
        for (String ingredientId : ingredientIds) {
            addIngredientToItem(itemId, ingredientId);
        }
    }

    @Override
    public void addTagsToItem(final String itemId, final List<String> tagIds) {
        for (String tagId : tagIds) {
            addTagToItem(itemId, tagId);
        }
    }

    private void addTagToItem(final String itemId, final String tagId) {
        Log.i(TAG, "Adding ingredient " + tagId + " to menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("tags", FieldValue.arrayUnion(tagId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Tag " + tagId + " added to item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when adding Tag " + tagId + " to item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    @Override
    public void addGGWsToItem(String itemId, List<String> ggwItemIds) {
        for (String ggwItemId : ggwItemIds) {
            addGGWToItem(itemId, ggwItemId);
        }
    }

    private void addGGWToItem(final String itemId, final String ggwItemId) {
        Log.i(TAG, "Adding GGW " + ggwItemId + " to menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("ggwItems", FieldValue.arrayUnion(ggwItemId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "GGW " + ggwItemId + " added to item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when adding GGW " + ggwItemId + " to item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    @Override
    public void removeIngredientsFromItem(String itemId, List<String> ingredientIds) {
        Log.i(TAG, "Removing " + ingredientIds.size() + " ingredients from item Id" + itemId);
        for (String ingredientId : ingredientIds) {
            removeIngredientFromItem(itemId, ingredientId);
        }
    }

    @Override
    public void removeTagsFromItem(String itemId, List<String> tagIds) {
        Log.i(TAG, "Removing " + tagIds.size() + " tags from item Id" + itemId);
        for (String tagId : tagIds) {
            removeTagFromItem(itemId, tagId);
        }
    }


    private void removeTagFromItem(final String itemId, final String tagId) {
        Log.i(TAG, "Removing tag " + tagId + " from menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("tags", FieldValue.arrayRemove(tagId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Tag " + tagId + " removed from item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when removing Tag " + tagId + " from item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    @Override
    public void removeGGWsFromItem(String itemId, List<String> ggwItemIds) {
        Log.i(TAG, "Removing " + ggwItemIds.size() + " GGW from item Id" + itemId);
        for (String ggwItemId : ggwItemIds) {
            removeGGWFromItem(itemId, ggwItemId);
        }
    }


    private void removeGGWFromItem(final String itemId, final String ggwItemId) {
        Log.i(TAG, "Removing GGW " + ggwItemId + " from menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("ggwItems", FieldValue.arrayRemove(ggwItemId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "GGW " + ggwItemId + " removed from item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when removing GGW " + ggwItemId + " from item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    private void removeIngredientFromItem(final String itemId, final String ingredientId) {
        Log.i(TAG, "Removing ingredient " + ingredientId + " from menu item " + itemId);
        DocumentReference menuItemRef = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);

        menuItemRef.update("ingredients", FieldValue.arrayRemove(ingredientId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ingredient " + ingredientId + " removed from item " + itemId + " successfully !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error when removing Ingredient " + ingredientId + " from item " + itemId + " . Error:" + e.getMessage());
                    }
                });
    }

    protected void getTagsForItem(String itemId, Source source, final OnResultListener<List<String>> listener) {
        Log.i(TAG, "Trying to fetch tags for the item" + itemId);
        FireStoreLocation.getMenuItemsRootLocation(db).document(itemId).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> tags = (List<String>) documentSnapshot.get("tags");
                if (tags == null) {
                    tags = new ArrayList<>();
                }
                listener.onCallback(tags);
            }
        });
    }

    @Override
    public void getTagsForItem(String itemId, final OnResultListener<List<String>> listener) {
        getTagsForItem(itemId, Source.DEFAULT, listener);
    }

    @Override
    public void getGGWsForItem(String itemId, final OnResultListener<List<String>> listener) {
        Log.i(TAG, "Trying to fetch GGW for the item" + itemId);
        FireStoreLocation.getMenuItemsRootLocation(db).document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> ggwItems = (List<String>) documentSnapshot.get("ggwItems");
                if (ggwItems == null) {
                    ggwItems = new ArrayList<>();
                }
                listener.onCallback(ggwItems);
            }
        });
    }

    @Override
    public void getIngredientsForItem(String itemId, final OnResultListener<List<String>> listener) {
        Log.i(TAG, "Trying to fetch ingredients for the item" + itemId);
        FireStoreLocation.getMenuItemsRootLocation(db).document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> ingredients = (List<String>) documentSnapshot.get("ingredients");
                if (ingredients == null) {
                    ingredients = new ArrayList<>();
                }
                listener.onCallback(ingredients);
            }
        });
    }

    @Override
    public void getAllItemsWithIngredient(final String ingredientId, final OnResultListener<List<RestaurantItem>> listener) {
        FireStoreLocation.getMenuItemsRootLocation(db).whereArrayContains("ingredients", ingredientId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<RestaurantItem> menuItemsWithMatchingIngredient = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                menuItemsWithMatchingIngredient.add(RestaurantItem.prepareRestaurantItem(document));
                            }
                            Log.i(TAG, "Got " + menuItemsWithMatchingIngredient.size() + " items matching the ingredient " + ingredientId + ".");
                            listener.onCallback(menuItemsWithMatchingIngredient);
                        } else {
                            Log.e(TAG, "Error getting items with matching ingredients.", task.getException());
                        }
                    }
                });

    }

    public void deleteItem(final String itemId, final OnResultListener<String> listener) {
        FireStoreLocation.getMenuItemsRootLocation(db).document(itemId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Menu Item is successfully deleted.");
//                        deleteGGWReferencesFromMenuItems(itemId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting the menu item .", e.getCause());
                    }
                });
        listener.onCallback("deleteItemFinished");
    }


    public void deleteGGWReferencesFromMenuItems(final String itemId, final OnResultListener<String> listener) {
        Log.i(TAG, "Deleting the references of the itemId " + itemId + " from all the menu items.");
        getAllItemsWithGGW(itemId, new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                for (RestaurantItem restaurantItem : items) {
                    Log.d(TAG, "Deleting  ggw " + itemId + " from item " + restaurantItem.getName());
                    removeGGWFromItem(restaurantItem.getId(), itemId);
                }
                listener.onCallback("success");
            }
        });
    }

    protected void getItem(final String itemId, Source source, final OnResultListener<RestaurantItem> listener) {
        FireStoreLocation.getMenuItemsRootLocation(db).document(itemId).get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        RestaurantItem restaurantItem = null;
                        if (task.isSuccessful()) {
                            restaurantItem = RestaurantItem.prepareRestaurantItem(task.getResult());
                        } else {
                            Log.e(TAG, "Error getting item with the provided id.", task.getException());
                        }
                        listener.onCallback(restaurantItem);
                    }
                });

    }

    @Override
    public void getItem(final String itemId, final OnResultListener<RestaurantItem> listener) {
        getItem(itemId, Source.DEFAULT, listener);
    }

    @Override
    public void getAllItemsWithTag(final String tagId, final OnResultListener<List<RestaurantItem>> listener) {
        FireStoreLocation.getMenuItemsRootLocation(db).whereArrayContains("tags", tagId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<RestaurantItem> menuItemsWithMatchingTag = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                menuItemsWithMatchingTag.add(RestaurantItem.prepareRestaurantItem(document));
                            }
                            Log.i(TAG, "Got " + menuItemsWithMatchingTag.size() + " items matching the tag " + tagId + ".");
                            listener.onCallback(menuItemsWithMatchingTag);
                        } else {
                            Log.e(TAG, "Error getting items with matching tag.", task.getException());
                        }
                    }
                });
    }


    @Override
    public void getAllItemsWithGGW(final String ggwId, final OnResultListener<List<RestaurantItem>> listener) {
        FireStoreLocation.getMenuItemsRootLocation(db).whereArrayContains("ggwItems", ggwId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<RestaurantItem> menuItemsWithMatchingGGW = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                menuItemsWithMatchingGGW.add(RestaurantItem.prepareRestaurantItem(document));
                            }
                            Log.i(TAG, "Got " + menuItemsWithMatchingGGW.size() + " items matching the ggw " + ggwId + ".");
                            listener.onCallback(menuItemsWithMatchingGGW);
                        } else {
                            Log.e(TAG, "Error getting items with matching ggw.", task.getException());
                        }
                    }
                });
    }

    protected void getAllItems(Source source, final OnResultListener<List<RestaurantItem>> listener) {
        Log.i(TAG, "Fetching all items ");
        FireStoreLocation.getMenuItemsRootLocation(db).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<RestaurantItem> items = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(RestaurantItem.prepareRestaurantItem(document));
                            }
                            Log.i(TAG, "Got " + items.size() + " items .");
                        } else {
                            Log.e(TAG, "Error getting all items.", task.getException());
                        }
                        listener.onCallback(items);
                    }
                });
    }

    @Override
    public void getAllItems(final OnResultListener<List<RestaurantItem>> listener) {
        getAllItems(Source.DEFAULT, listener);
    }


    @Override
    public void insertOrUpdateMenuItem(final RestaurantItem item, final OnResultListener<RestaurantItem> listener) {
        if (item.getId() == null || item.getId().length() == 0) {
            insertItem(item, listener);
        } else {
            updateItem(item, listener);
        }
    }


    private void insertItem(final RestaurantItem item, final OnResultListener<RestaurantItem> listener) {
        Log.i(TAG, "Trying to insert an item: " + item);
        Map<String, Object> itemValueMap = new HashMap<>();
        itemValueMap.put(RestaurantItem.FIRESTORE_NAME_KEY, item.getName());
        itemValueMap.put(RestaurantItem.FIRESTORE_DESC_KEY, item.getDescription());
        itemValueMap.put(RestaurantItem.FIRESTORE_PRICE_KEY, item.getPrice());
        itemValueMap.put(RestaurantItem.FIRESTORE_ACTIVE_KEY, item.getActive());

        itemValueMap.put(RestaurantItem.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        itemValueMap.put(RestaurantItem.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        itemValueMap.put(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        itemValueMap.put(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newItemReference = FireStoreLocation.getMenuItemsRootLocation(db).document();
        item.setId(newItemReference.getId());

        itemValueMap.put(RestaurantItem.FIRESTORE_IMAGE1_URL, FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_1.jpg");
        itemValueMap.put(RestaurantItem.FIRESTORE_IMAGE2_URL, FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_2.jpg");
        itemValueMap.put(RestaurantItem.FIRESTORE_IMAGE3_URL, FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_3.jpg");

        newItemReference.set(itemValueMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item successfully created!");
                        listener.onCallback(item);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting item";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }

    private void updateItem(final RestaurantItem item, final OnResultListener<RestaurantItem> listener) {
        Log.i(TAG, "Trying to update an item: " + item);
        DocumentReference existingItemReference = FireStoreLocation.getMenuItemsRootLocation(db).document(item.getId());
        existingItemReference.update(RestaurantItem.FIRESTORE_NAME_KEY, item.getName());
        existingItemReference.update(RestaurantItem.FIRESTORE_DESC_KEY, item.getDescription());
        existingItemReference.update(RestaurantItem.FIRESTORE_PRICE_KEY, item.getPrice());
        existingItemReference.update(RestaurantItem.FIRESTORE_ACTIVE_KEY, item.getActive());
        existingItemReference.update(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        existingItemReference.update(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item successfully updated!");
                        listener.onCallback(item);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while updating item";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }
//
//    public void updateImageUrl(final RestaurantItem item, String imageNum, String imageDesc, String imageStorageUrl) {
//        Log.i(TAG, "Trying to update an item: " + item);
//        DocumentReference existingItemReference = FireStoreLocation.getMenuItemsRootLocation(db).document(item.getId());
//        Map<String, Object> data = new HashMap<>();
//        data.put("itemImageUrl_" + imageNum, imageStorageUrl);
//        data.put("itemImageDesc_" + imageNum, imageDesc);
//
//        item.setImage(imageNum, imageStorageUrl, imageDesc);
//        existingItemReference.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Image for Item successfully updated!");
//                } else {
//                    Log.d(TAG, "Error while updating image for Item!");
//                }
//            }
//        });
//    }


    @Override
    public void getItems(final List<GroupAndItemMapping> mappings, final OnResultListener<List<RestaurantItem>> listener) {
        getItems(mappings, Source.DEFAULT, listener);
    }

    protected void getItems(final List<GroupAndItemMapping> mappings, Source source, final OnResultListener<List<RestaurantItem>> listener) {
        final List<RestaurantItem> items = new ArrayList<>();
        if (mappings != null && mappings.size() > 0) {
            final AtomicInteger index = new AtomicInteger(0);
            for (final GroupAndItemMapping mapping : mappings) {
                getItem(mapping.getItemId(), source, new OnResultListener<RestaurantItem>() {
                    @Override
                    public void onCallback(RestaurantItem item) {
                        index.incrementAndGet();
                        if (item != null) {
                            item.setPosition(mapping.getItemPosition());
                            RestaurantItem parent = new RestaurantItem();
                            parent.setId(mapping.getGroupId());
                            item.setParent(parent);
                            items.add(item);
                        }
                        if (index.get() == mappings.size()) {
                            Collections.sort(items, new ItemPositionComparator());
                            listener.onCallback(items);
                        }
                    }
                });

            }
        } else {
            listener.onCallback(items);
        }
    }


    private String getFieldForRating(ReviewEnum reviewEnum) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.BAD) {
                return RestaurantItem.FIRESTORE_NO_OF_1_RATING;
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                return RestaurantItem.FIRESTORE_NO_OF_2_RATING;
            } else if (reviewEnum == ReviewEnum.GOOD) {
                return RestaurantItem.FIRESTORE_NO_OF_3_RATING;
            }
        }
        return null;
    }

    private String getFieldForTodaysRating(ReviewEnum reviewEnum) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.BAD) {
                return RestaurantItem.FIRESTORE_NO_OF_1_RATING_FOR_TODAY;
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                return RestaurantItem.FIRESTORE_NO_OF_2_RATING_FOR_TODAY;
            } else if (reviewEnum == ReviewEnum.GOOD) {
                return RestaurantItem.FIRESTORE_NO_OF_3_RATING_FOR_TODAY;
            }
        }
        return null;
    }


    public void updateItemsWithRating(List<ReviewForDish> reviewForDishes) {
        for (ReviewForDish reviewForDish : reviewForDishes) {
            updateItemWithRating(reviewForDish.getItem().getId(), reviewForDish.getReview(), new OnResultListener<String>() {
                @Override
                public void onCallback(String status) {
                }
            });
        }
    }

    public void updateItemWithRating(final String itemId, final ReviewEnum reviewEnum, final OnResultListener<String> listener) {
        Log.i(TAG, "Trying to update an item: " + itemId);
        final String fieldForRating = getFieldForRating(reviewEnum);
        final String fieldForTodaysRating = getFieldForTodaysRating(reviewEnum);
        if (fieldForRating != null) {
            final DocumentReference existingItemReference = FireStoreLocation.getMenuItemsRootLocation(db).document(itemId);
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(existingItemReference);
                    Map<String, Object> data = snapshot.getData();
                    long newNoOfRating = FireStoreUtil.getLong(data, fieldForRating, 0l) + 1;
                    long newTodaysRating = FireStoreUtil.getLong(data, fieldForTodaysRating, 0l) + 1;


                    ReviewCount todaysReviewCount = new ReviewCount(FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_3_RATING_FOR_TODAY,0l), FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_2_RATING_FOR_TODAY,0l), FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_1_RATING_FOR_TODAY,0l));
                    ReviewCount overallReviewCount = new ReviewCount(FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_3_RATING,0l), FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_2_RATING,0l), FireStoreUtil.getLong(data,RestaurantItem.FIRESTORE_NO_OF_1_RATING,0l));

                    todaysReviewCount.increment(reviewEnum);
                    overallReviewCount.increment(reviewEnum);

                    Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(todaysReviewCount);
                    Double updatedOverallScore = PerformanceUtils.getDayPerformanceScore(overallReviewCount);

                    Map<String, Object> itemValueMap = new HashMap<>();
                    itemValueMap.put(fieldForRating, newNoOfRating);
                    itemValueMap.put(fieldForTodaysRating, newTodaysRating);
                    itemValueMap.put(RestaurantItem.FIRESTORE_REVIEW_SCORE_TODAY, updatedTodaysScore);
                    itemValueMap.put(RestaurantItem.FIRESTORE_REVIEW_SCORE, updatedOverallScore);

                    transaction.update(existingItemReference, itemValueMap);
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listener.onCallback("Updated");
                }
            });
        }
    }

}
