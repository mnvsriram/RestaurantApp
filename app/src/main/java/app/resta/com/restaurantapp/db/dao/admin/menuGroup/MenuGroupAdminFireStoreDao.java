package app.resta.com.restaurantapp.db.dao.admin.menuGroup;

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
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuGroupAdminFireStoreDao implements MenuGroupAdminDaoI {
    private static final String TAG = "MenuGroupAdminDao";
    private FirebaseFirestore db;
    private MenuItemAdminDaoI menuItemAdminDaoI;
    private MenuItemUserDaoI menuItemUserDao;

    public MenuGroupAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
        menuItemAdminDaoI = new MenuItemAdminFireStoreDao();
        menuItemUserDao = new MenuItemUserFireStoreDao();
    }


    private void insertGroup(final RestaurantItem group, final OnResultListener<RestaurantItem> listener) {
        Log.i(TAG, "Trying to insert an group: " + group);
        Map<String, Object> groupValueMap = new HashMap<>();
        groupValueMap.put(RestaurantItem.FIRESTORE_NAME_KEY, group.getName());
        groupValueMap.put(RestaurantItem.FIRESTORE_DESC_KEY, group.getDescription());
        groupValueMap.put(RestaurantItem.FIRESTORE_ACTIVE_KEY, group.getActive());

        groupValueMap.put(RestaurantItem.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        groupValueMap.put(RestaurantItem.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        groupValueMap.put(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        groupValueMap.put(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newGroupReference = FireStoreLocation.getMenuGroupsRootLocation(db).document();
        group.setId(newGroupReference.getId());

        newGroupReference.set(groupValueMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Group successfully created!");
                        MenuTypeAndGroupMapping mapping = new MenuTypeAndGroupMapping();
                        mapping.setGroupId(group.getId());
                        mapping.setMenuTypeId(group.getMenuTypeId());
                        mapping.setGroupPositionInMenuType(-1);

                        addGroupToMenuType(mapping, new OnResultListener<MenuTypeAndGroupMapping>() {
                            @Override
                            public void onCallback(MenuTypeAndGroupMapping menuTypeAndGroupMapping) {
                                listener.onCallback(group);
                            }
                        });

                    }

                });
    }


    private void updateGroup(final RestaurantItem group, final OnResultListener<RestaurantItem> listener) {
        Log.i(TAG, "Trying to update a group: " + group);
        DocumentReference existingGroupReference = FireStoreLocation.getMenuGroupsRootLocation(db).document(group.getId());
        existingGroupReference.update(RestaurantItem.FIRESTORE_NAME_KEY, group.getName());
        existingGroupReference.update(RestaurantItem.FIRESTORE_DESC_KEY, group.getDescription());
        existingGroupReference.update(RestaurantItem.FIRESTORE_ACTIVE_KEY, group.getActive());
        existingGroupReference.update(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        existingGroupReference.update(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Group successfully updated!");
                        listener.onCallback(group);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while updating group";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }


    @Override
    public void insertOrUpdateGroup(final RestaurantItem group, final OnResultListener<RestaurantItem> listener) {
        if (group.getId() == null || group.getId().length() == 0) {
            insertGroup(group, listener);
        } else {
            updateGroup(group, listener);
        }
    }

    @Override
    public void deleteGroup(final String groupId, final String menuTypeId, final OnResultListener<String> listener) {
        FireStoreLocation.getMenuGroupsLocationForId(db, groupId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FireStoreLocation.getMenuGroupsLocationForId(db, groupId).document(document.getId()).delete();
                                Log.i(TAG, "Deleted item mapping for the group");
                            }
                        } else {
                            Log.e(TAG, "Error while deleting items from all groups.", task.getException());
                        }

                        removeGroupFromMenuType(menuTypeId, groupId, new OnResultListener<String>() {
                            @Override
                            public void onCallback(String status) {
                                FireStoreLocation.getMenuGroupsRootLocation(db).document(groupId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Menu Group successfully deleted!");
                                        } else {
                                            Log.w(TAG, "Error deleting Menu Group", task.getException());
                                        }
                                        listener.onCallback("Success");
                                    }
                                });

                            }
                        });
                    }
                });
    }

    public void deleteItemFromAllGroups(final String itemId, final OnResultListener<String> listener) {
        FireStoreLocation.getMenuGroupsRootLocation(db).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final AtomicInteger index = new AtomicInteger(0);
                            if (task.getResult().size() > 0) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    index.incrementAndGet();
                                    String groupId = document.getId();
                                    deleteItemFromGroup(itemId, groupId, new OnResultListener<String>() {
                                        @Override
                                        public void onCallback(String status) {
                                            //success
                                        }
                                    });
                                    if (index.intValue() == task.getResult().size()) {
                                        listener.onCallback("Success");
                                    }
                                }
                            } else {
                                listener.onCallback("noGroupsWithThisItemPresent");
                            }
                            Log.i(TAG, "Deleted item from all groups");
                        } else {
                            Log.e(TAG, "Error while deleting items from all groups.", task.getException());
                            listener.onCallback("failure");
                        }
                    }
                });
    }

    public void deleteItemFromGroup(String itemId, String groupId, final OnResultListener<String> listener) {
        FireStoreLocation.getMenuGroupsLocationForId(db, groupId).document(itemId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Menu Item is successfully removed from the group");
                } else {
                    String errorMessage = "Error while removing the item from the group.";
                    Log.e(TAG, errorMessage);
                }
                listener.onCallback("Success");
            }
        });
    }

    @Override
    public void addItemToGroup(GroupAndItemMapping mapping) {
        Log.i(TAG, "Trying to insert a child " + mapping.getItemId() + " to group " + mapping.getGroupId());
        Map<String, Object> itemValueMap = new HashMap<>();
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_ITEM_ID, mapping.getItemId());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_GROUP_ID, mapping.getGroupId());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_ITEM_POSITION, mapping.getItemPosition());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_ITEM_PRICE_KEY, mapping.getItemPrice());

        itemValueMap.put(GroupAndItemMapping.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        itemValueMap.put(GroupAndItemMapping.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newItemReference = FireStoreLocation.getMenuGroupsLocationForId(db, mapping.getGroupId()).document();

        newItemReference.set(itemValueMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Parent Child Mapping Successfully created!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting parent child mapping.";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });

    }

    @Override
    public void getGroups(final List<MenuTypeAndGroupMapping> mappings, Source source, final OnResultListener<List<RestaurantItem>> listener) {
        final List<RestaurantItem> groups = new ArrayList<>();
        if (mappings != null && mappings.size() > 0) {
            final AtomicInteger index = new AtomicInteger(0);
            for (final MenuTypeAndGroupMapping mapping : mappings) {
                getGroup(mapping.getGroupId(), source, new OnResultListener<RestaurantItem>() {
                    @Override
                    public void onCallback(RestaurantItem group) {
                        index.incrementAndGet();
                        if (group != null) {
                            group.setPosition(mapping.getGroupPositionInMenuType());
                            groups.add(group);
                        }
                        if (index.get() == mappings.size()) {
                            listener.onCallback(groups);
                        }
                    }
                });
            }
        } else {
            listener.onCallback(groups);
        }

    }

    public void getGroupsAlongWithItems(final List<MenuTypeAndGroupMapping> mappings, final Source source, final OnResultListener<List<RestaurantItem>> listener) {
        final List<RestaurantItem> groups = new ArrayList<>();
        if (mappings != null && mappings.size() > 0) {
            final AtomicInteger index = new AtomicInteger(0);
            for (final MenuTypeAndGroupMapping mapping : mappings) {
                getGroup(mapping.getGroupId(), source, new OnResultListener<RestaurantItem>() {
                    @Override
                    public void onCallback(final RestaurantItem group) {
                        index.incrementAndGet();
                        if (group != null) {
                            group.setPosition(mapping.getGroupPositionInMenuType());
                            groups.add(group);
                            getItemsInGroup(group.getId(), source, new OnResultListener<List<RestaurantItem>>() {
                                @Override
                                public void onCallback(List<RestaurantItem> groupsInItem) {
                                    group.setChildItems(groupsInItem);
                                    if (index.get() == mappings.size()) {
                                        listener.onCallback(groups);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        } else {
            listener.onCallback(groups);
        }

    }

    private void getGroup(String groupId, Source source, final OnResultListener<RestaurantItem> listener) {
        if (groupId == null) {
            listener.onCallback(null);
        } else {
            FireStoreLocation.getMenuGroupsRootLocation(db).document(groupId).get(source)
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

    }

    public void getGroup(String groupId, final OnResultListener<RestaurantItem> listener) {
        getGroup(groupId, Source.DEFAULT, listener);
    }

    protected void getItemsInGroup(final String groupId, final Source source, final OnResultListener<List<RestaurantItem>> listener) {
        Log.i(TAG, "Trying to get items in group : " + groupId);
        FireStoreLocation.getMenuGroupsLocationForId(db, groupId).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<GroupAndItemMapping> mappings = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mappings.add(GroupAndItemMapping.prepare(document));
                            }
                            Log.i(TAG, "Got " + mappings.size() + " items in this group.");
                        } else {
                            Log.e(TAG, "Error getting groups in meny type.", task.getException());
                        }

                        if (Source.CACHE == source) {
                            menuItemUserDao.getItems_u(mappings, listener);
                        } else {
                            menuItemAdminDaoI.getItems(mappings, listener);
                        }
                    }
                });

    }

    @Override
    public void getItemsInGroup(final String groupId, final OnResultListener<List<RestaurantItem>> listener) {
        getItemsInGroup(groupId, Source.DEFAULT, listener);
    }

    @Override
    public void updateItemPositions(List<GroupAndItemMapping> mappings) {
        for (GroupAndItemMapping mapping : mappings) {
            Log.i(TAG, "Trying to update the position of the item: " + mapping.getItemId() + " in the group " + mapping.getGroupId());
            DocumentReference existingMappingReference = FireStoreLocation.getMenuGroupsLocationForId(db, mapping.getGroupId()).document(mapping.getItemId());
            existingMappingReference.update(GroupAndItemMapping.FIRESTORE_ITEM_POSITION, mapping.getItemPosition())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Menu Item and Group Mapping successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage = "Error while updating Menu Item and Group Mapping ";
                    Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage, e);
                }
            });

        }
    }


    public void removeGroupFromMenuType(String menuTypeId, String groupId, final OnResultListener<String> listener) {
        FireStoreLocation.getMenuTypesLocationForID(db, menuTypeId).document(groupId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Menu group is successfully removed from the Menu Type");
                } else {
                    String errorMessage = "Error while removing the group from the Menu Type.";
                    Log.e(TAG, errorMessage);
                }
                listener.onCallback("Success");
            }
        });

    }

    @Override
    public void addGroupToMenuType(final MenuTypeAndGroupMapping mapping, final OnResultListener<MenuTypeAndGroupMapping> listener) {
        Log.i(TAG, "Trying to insert a group " + mapping.getGroupId() + " to Menu Type " + mapping.getMenuTypeId());
        Map<String, Object> groupToMenuTypeMapping = new HashMap<>();
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_MENU_TYPE_ID, mapping.getMenuTypeId());
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_GROUP_ID, mapping.getGroupId());
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_GROUP_POSITION, mapping.getGroupPositionInMenuType());

        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        groupToMenuTypeMapping.put(MenuTypeAndGroupMapping.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newGroupReference = FireStoreLocation.getMenuTypesLocationForID(db, mapping.getMenuTypeId()).document(mapping.getGroupId());

        newGroupReference.set(groupToMenuTypeMapping)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Parent Child Mapping Successfully created for Group and Menu type!");
                        listener.onCallback(mapping);
                    }
                });

    }

}
