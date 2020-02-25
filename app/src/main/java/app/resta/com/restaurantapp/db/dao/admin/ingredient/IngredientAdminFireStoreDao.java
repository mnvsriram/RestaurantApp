package app.resta.com.restaurantapp.db.dao.admin.ingredient;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

public class IngredientAdminFireStoreDao implements IngredientAdminDaoI {
    private static final String TAG = "IngredientAdminDao";
    FirebaseFirestore db;
    public IngredientAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }

    public void insertIngredients(final List<Ingredient> ingredients, final OnResultListener<List<Ingredient>> listener) {
        final AtomicInteger index = new AtomicInteger(0);
        final List<Ingredient> newlyCreatedIngredients = new ArrayList<>();
        if (ingredients != null && ingredients.size() > 0) {
            for (final Ingredient ingredient : ingredients) {
                insertIngredient(ingredient, new OnResultListener<Ingredient>() {
                    @Override
                    public void onCallback(Ingredient newIngredient) {
                        newlyCreatedIngredients.add(newIngredient);
                        index.getAndIncrement();
                        if (index.get() == ingredients.size()) {
                            listener.onCallback(newlyCreatedIngredients);
                        }
                    }
                });

            }
        } else {
            listener.onCallback(ingredients);
        }
    }

    public void insertIngredient(final Ingredient ingredient, final OnResultListener<Ingredient> listener) {
        Log.i(TAG, "Trying to insert a ingredient. ingredient: " + ingredient);
        Map<String, Object> ingredientMap = new HashMap<>();
        ingredientMap.put(Ingredient.FIRESTORE_NAME_KEY, ingredient.getName());
        ingredientMap.put(Ingredient.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        ingredientMap.put(Ingredient.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        ingredientMap.put(Ingredient.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        ingredientMap.put(Ingredient.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());


        DocumentReference newIngredientReference = FireStoreLocation.getIngredientsRootLocation(db).document();
        ingredient.setId(newIngredientReference.getId());

        newIngredientReference.set(ingredientMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ingredient successfully created!");
                        listener.onCallback(ingredient);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting ingredient data";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }

    public void getIngredients(final OnResultListener<List<Ingredient>> listener) {
        Log.i(TAG, "Getting list of ingredients");
        final List<Ingredient> ingredients = new ArrayList<>();
        FireStoreLocation.getIngredientsRootLocation(db)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ingredients.add(Ingredient.prepareIngredient(document));
                            }
                            Log.i(TAG, "Got " + ingredients.size() + " from server.");
                        } else {
                            Log.e(TAG, "Error getting ingredients.", task.getException());
                        }
                        listener.onCallback(ingredients);

                    }
                });
    }


    public void deleteReferencesFromMenuItems(final String ingredientId) {
        Log.i(TAG, "Deleting the references of the ingredient " + ingredientId + " from all the menu items.");
        final MenuItemAdminDaoI menuItemAdminDaoI = new MenuItemAdminFireStoreDao();
        menuItemAdminDaoI.getAllItemsWithIngredient(ingredientId, new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                for (RestaurantItem restaurantItem : items) {
                    List<String> ids = new ArrayList<>();
                    ids.add(ingredientId);
                    Log.d(TAG, "Deleted ingredient " + ingredientId + " from item " + restaurantItem.getName());
                    menuItemAdminDaoI.removeIngredientsFromItem(restaurantItem.getId(), ids);
                }
            }
        });

    }

    public void deleteIngredient(final String ingredientId) {
        Log.i(TAG, "Deleting the ingredient with ID: " + ingredientId);
        FireStoreLocation.getIngredientsRootLocation(db).document(ingredientId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ingredient successfully deleted!");
                        deleteReferencesFromMenuItems(ingredientId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Ingredient", e);
                    }
                });
    }


    @Override
    public void getIngredient(String ingredientId, final OnResultListener<Ingredient> listener) {
        getIngredient(ingredientId, Source.DEFAULT, listener);
    }

    protected void getIngredient(String ingredientId, Source source, final OnResultListener<Ingredient> listener) {
        DocumentReference documentRef = FireStoreLocation.getIngredientsRootLocation(db).document(ingredientId);
        documentRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    listener.onCallback(Ingredient.prepareIngredient(document));
                } else {
                    Log.d(TAG, "Failed to fetch Ingredients", task.getException());
                }
            }
        });
    }

    public void updateImageUrl(final Ingredient ingredient, String imageStorageUrl, final OnResultListener<String> listener) {
        Log.i(TAG, "Trying to update the image on ingredient: " + ingredient.getId());
        DocumentReference existingCardReference = FireStoreLocation.getIngredientsRootLocation(db).document(ingredient.getId());
        Map<String, Object> data = new HashMap<>();
        data.put(Ingredient.FIRESTORE_IMAGE_URL, imageStorageUrl);
        existingCardReference.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Image for Ingredient successfully updated!");
                } else {
                    Log.d(TAG, "Error while updating image for Ingredient!");
                }
                listener.onCallback("success");
            }
        });

    }

}
