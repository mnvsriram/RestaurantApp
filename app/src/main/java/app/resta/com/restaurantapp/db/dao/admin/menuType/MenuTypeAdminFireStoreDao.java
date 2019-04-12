package app.resta.com.restaurantapp.db.dao.admin.menuType;

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

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuTypeAdminFireStoreDao implements MenuTypeAdminDaoI {
    private static final String TAG = "MenuGroupAdminDao";
    FirebaseFirestore db;
    private MenuGroupAdminDaoI menuGroupAdminDao;

    public MenuTypeAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
        menuGroupAdminDao = new MenuGroupAdminFireStoreDao();
    }

    protected void getGroupsInMenuType(final String menuTypeId, final Source source, final OnResultListener<List<RestaurantItem>> listener) {
        Log.i(TAG, "Trying to get groups in menu type : " + menuTypeId);
        FireStoreLocation.getMenuTypesLocationForID(db, menuTypeId).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MenuTypeAndGroupMapping> mappings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mappings.add(MenuTypeAndGroupMapping.prepare(document));
                            }
                            Log.i(TAG, "Got " + mappings.size() + " groups in this menu type .");
                            menuGroupAdminDao.getGroups(mappings, source, listener);
                        } else {
                            Log.e(TAG, "Error getting groups in meny type.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getGroupsInMenuType(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener) {
        getGroupsInMenuType(menuTypeId, Source.DEFAULT, listener);
    }


    protected void getGroupsWithItemsInMenuType(final String menuTypeId, final Source source, final OnResultListener<List<RestaurantItem>> listener) {
        Log.i(TAG, "Trying to get groups in menu type : " + menuTypeId);
        FireStoreLocation.getMenuTypesLocationForID(db, menuTypeId).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MenuTypeAndGroupMapping> mappings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mappings.add(MenuTypeAndGroupMapping.prepare(document));
                            }
                            Log.i(TAG, "Got " + mappings.size() + " groups in this menu type .");
                            menuGroupAdminDao.getGroupsAlongWithItems(mappings, source, listener);
                        } else {
                            Log.e(TAG, "Error getting groups in meny type.", task.getException());
                        }
                    }
                });

    }

    @Override
    public void getGroupsWithItemsInMenuType(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener) {
        getGroupsWithItemsInMenuType(menuTypeId, Source.DEFAULT, listener);
    }


    private void insertMenuType(final MenuType menuType, final OnResultListener<MenuType> listener) {
        Log.i(TAG, "Trying to insert a menu Type: " + menuType);
        Map<String, Object> menuTypeValueMap = new HashMap<>();

        menuTypeValueMap.put(MenuType.FIRESTORE_NAME_KEY, menuType.getName());
        menuTypeValueMap.put(MenuType.FIRESTORE_DESC_KEY, menuType.getDescription());
        menuTypeValueMap.put(MenuType.FIRESTORE_PRICE_KEY, menuType.getPrice());
        menuTypeValueMap.put(MenuType.FIRESTORE_SHOW_PRICE_FOR_CHILDREN, menuType.isShowPriceOfChildren());

        menuTypeValueMap.put(RestaurantItem.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        menuTypeValueMap.put(RestaurantItem.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        menuTypeValueMap.put(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        menuTypeValueMap.put(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newMenuTypeRef = FireStoreLocation.getMenuTypesRootLocation(db).document();
        menuType.setId(newMenuTypeRef.getId());

        newMenuTypeRef.set(menuTypeValueMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Menu Type successfully created!");
                        } else {
                            Log.d(TAG, "Failed to create MenuType", task.getException());
                        }
                        listener.onCallback(menuType);
                    }
                });
    }


    private void updateMenuType(final MenuType menuType, final OnResultListener<MenuType> listener) {
        Log.i(TAG, "Trying to update a Menu Type: " + menuType);
        DocumentReference existingMenuTypeReference = FireStoreLocation.getMenuTypesRootLocation(db).document(menuType.getId());

        Map<String, Object> menuTypeValueMap = new HashMap<>();

        menuTypeValueMap.put(MenuType.FIRESTORE_NAME_KEY, menuType.getName());
        menuTypeValueMap.put(MenuType.FIRESTORE_DESC_KEY, menuType.getDescription());
        menuTypeValueMap.put(MenuType.FIRESTORE_PRICE_KEY, menuType.getPrice());
        menuTypeValueMap.put(MenuType.FIRESTORE_SHOW_PRICE_FOR_CHILDREN, menuType.isShowPriceOfChildren());

        menuTypeValueMap.put(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        menuTypeValueMap.put(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        existingMenuTypeReference.set(menuTypeValueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Menu Type successfully updated!");
                } else {
                    Log.d(TAG, "Failed to update MenuType", task.getException());
                }
                listener.onCallback(menuType);
            }
        });

    }


    @Override
    public void insertOrUpdateMenuType(final MenuType menuType, final OnResultListener<MenuType> listener) {
        if (menuType.getId() == null || menuType.getId().length() == 0) {
            insertMenuType(menuType, listener);
        } else {
            updateMenuType(menuType, listener);
        }
    }

    protected void getMenuType(final String menuTypeId, Source source, final OnResultListener<MenuType> listener) {
        FireStoreLocation.getMenuTypesRootLocation(db).document(menuTypeId).get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        MenuType menuType = null;
                        if (task.isSuccessful()) {
                            menuType = MenuType.prepare(task.getResult());
                        } else {
                            Log.e(TAG, "Error getting menu type with the provided id.", task.getException());
                        }
                        listener.onCallback(menuType);
                    }
                });

    }

    @Override
    public void getMenuType(final String menuTypeId, final OnResultListener<MenuType> listener) {
        getMenuType(menuTypeId, Source.DEFAULT, listener);
    }

    public void updatePositions(List<MenuTypeAndGroupMapping> mappings) {
        for (MenuTypeAndGroupMapping mapping : mappings) {
            Log.i(TAG, "Trying to update the position of the group: " + mapping.getGroupId() + " in the menuType " + mapping.getMenuTypeId());
            DocumentReference existingMappingReference = FireStoreLocation.getMenuTypesLocationForID(db, mapping.getMenuTypeId()).document(mapping.getGroupId());
            existingMappingReference.update(MenuTypeAndGroupMapping.FIRESTORE_GROUP_POSITION, mapping.getGroupPositionInMenuType())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Menu Type and Group Mapping successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage = "Error while updating Menu Type and Group Mapping ";
                    Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage, e);
                }
            });

        }
    }

    @Override
    public void getAllMenuTypes(final OnResultListener<List<MenuType>> listener) {
        Log.i(TAG, "Fetching all menu types");
        FireStoreLocation.getMenuTypesRootLocation(db).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<MenuType> items = new ArrayList<>();
                        if (task.isSuccessful()) {
                            items = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(MenuType.prepare(document));
                            }
                            Log.i(TAG, "Got " + items.size() + " Menu Types .");
                        } else {
                            Log.e(TAG, "Error getting menu types.", task.getException());
                        }
                        listener.onCallback(items);
                    }
                });
    }
}
