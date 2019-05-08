package app.resta.com.restaurantapp.db.dao.admin.button;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardButtonAdminFireStoreDao implements MenuCardButtonAdminDaoI {
    private static final String TAG = "MenuButtonAdminDao";
    FirebaseFirestore db;

    public MenuCardButtonAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }

    @Override
    public void getActions(String menuCardId, String buttonId, Source source, final OnResultListener<List<MenuCardAction>> listener) {
        Log.i(TAG, "Getting list of actions for the button" + buttonId + " in the card " + menuCardId);
        final List<MenuCardAction> actions = new ArrayList<>();
        FireStoreLocation.getButtonActionsRootLocationForId(db, menuCardId, buttonId)
                .orderBy(MenuCardAction.FIRESTORE_POSITION_KEY)
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                actions.add(MenuCardAction.prepare(document));
                            }
                            Log.i(TAG, "Got " + actions.size() + " actions for the selected button.");
                        } else {
                            Log.e(TAG, "Error getting actions for the selected button.", task.getException());
                        }
                        listener.onCallback(actions);
                    }
                });
    }


    @Override
    public void deleteAndInsertAllActionsForButton(final String menuCardId,
                                                   final String buttonId, final List<MenuCardAction> actionsToBeCreated,
                                                   final OnResultListener<List<MenuCardAction>> listener) {
        getActions(menuCardId, buttonId, Source.DEFAULT, new OnResultListener<List<MenuCardAction>>() {
            @Override
            public void onCallback(final List<MenuCardAction> actions) {
                if (actions != null && actions.size() > 0) {
                    final AtomicInteger index = new AtomicInteger(0);
                    for (MenuCardAction action : actions) {
                        deleteAction(menuCardId, buttonId, action, new OnResultListener<MenuCardAction>() {
                            @Override
                            public void onCallback(MenuCardAction action) {
                                index.getAndIncrement();
                                if (index.get() == actions.size()) {
                                    insertActions(actionsToBeCreated, menuCardId, buttonId, listener);
                                }
                            }
                        });
                    }
                } else {
                    insertActions(actionsToBeCreated, menuCardId, buttonId, listener);
                }
            }
        });
    }


    private void deleteAction(String menuCardId, String buttonId, final MenuCardAction action, final OnResultListener<MenuCardAction> listener) {
        Log.i(TAG, "Trying to delete an action: " + action);
        if (action.getId() != null && action.getId().length() > 0) {
            FireStoreLocation.getButtonActionsRootLocationForId(db, menuCardId, buttonId).document(action.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Action successfully deleted!");
                    } else {
                        String errorMessage = "Error while deleting action";
                        Log.e(TAG, errorMessage);
                    }
                    listener.onCallback(action);
                }
            });
        } else {
            Log.i(TAG, "This action do not present in the database and so cannot delete it.");
            listener.onCallback(action);
        }
    }
//
//    public void getButton(final String id, final String cardId, final OnResultListener<MenuCardButton> listener) {
//        FireStoreLocation.getButtonsInMenuCardRootLocation(db, cardId).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                MenuCardButton menuCardButton = null;
//                if (task.isSuccessful()) {
//                    menuCardButton = MenuCardButton.prepare(task.getResult());
//                } else {
//                    Log.e(TAG, "Error getting item with the button id.", task.getException());
//                }
//                listener.onCallback(menuCardButton);
//            }
//        });
//
//    }

    public void deleteButton(String id, String cardId, final OnResultListener<String> listener) {
        Log.i(TAG, "Trying to delete a button: " + id);
        if (id != null && id.length() > 0) {
            FireStoreLocation.getButtonsInMenuCardRootLocation(db, cardId).document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Button successfully deleted!");
                    } else {
                        String errorMessage = "Error while deleting button";
                        Log.e(TAG, errorMessage);
                    }
                    listener.onCallback("success");
                }
            });
        } else {
            Log.i(TAG, "This button do not present in the database and so cannot delete it.");
            listener.onCallback("noId");
        }
    }

    private void insertActions(final List<MenuCardAction> actions, final String menuCardId, final String buttonId, final OnResultListener<List<MenuCardAction>> listener) {
        final List<MenuCardAction> createdActions = new ArrayList<>();
        if (actions != null && actions.size() > 0) {
            final AtomicInteger index = new AtomicInteger(0);
            for (MenuCardAction action : actions) {
                index.incrementAndGet();
                action.setPosition(index.get());
                insertAction(action, menuCardId, buttonId, new OnResultListener<MenuCardAction>() {
                    @Override
                    public void onCallback(MenuCardAction action) {
                        createdActions.add(action);
                        if (index.get() == createdActions.size()) {
                            listener.onCallback(createdActions);
                        }
                    }
                });
            }
        } else {
            listener.onCallback(createdActions);
        }
    }

    private void insertAction(final MenuCardAction action, final String menuCardId, final String buttonId, final OnResultListener<MenuCardAction> listener) {
        Log.i(TAG, "Trying to insert an action: " + action);
        Map<String, Object> actionValueMap = new HashMap<>();
        actionValueMap.put(MenuCardAction.FIRESTORE_BUTTON_ID_KEY, action.getButtonId());
        actionValueMap.put(MenuCardAction.FIRESTORE_MENU_TYPE_ID_KEY, action.getMenuTypeId());
        actionValueMap.put(MenuCardAction.FIRESTORE_LAYOUT_ID_KEY, action.getLayoutId());
        actionValueMap.put(MenuCardAction.FIRESTORE_POSITION_KEY, action.getPosition());

        actionValueMap.put(MenuCardAction.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        actionValueMap.put(MenuCardAction.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        actionValueMap.put(MenuCardAction.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        actionValueMap.put(MenuCardAction.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newActionReference = FireStoreLocation.getButtonActionsRootLocationForId(db, menuCardId, buttonId).document();
        action.setId(newActionReference.getId());

        newActionReference.set(actionValueMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Action successfully created!");
                        } else {
                            String errorMessage = "Error while creation action";
                            Log.e(TAG, errorMessage);
                        }
                        listener.onCallback(action);
                    }
                });
    }

    @Override
    public void insertOrUpdateButton(final MenuCardButton menuCardButton, String oldLocation, final OnResultListener<MenuCardButton> listener) {
        if (menuCardButton.getId() == null || menuCardButton.getId().length() == 0) {
            insertButton(menuCardButton, listener);
        } else {
            deleteButton(oldLocation, menuCardButton.getCardId(), new OnResultListener<String>() {
                @Override
                public void onCallback(String status) {
                    updateButton(menuCardButton, listener);
                }
            });

        }
    }

    private void insertButton(final MenuCardButton menuCardButton, final OnResultListener<MenuCardButton> listener) {
        Log.i(TAG, "Trying to insert an button: " + menuCardButton);
        Map<String, Object> buttonValueMap = new HashMap<>();
        buttonValueMap.put(MenuCardButton.FIRESTORE_NAME_KEY, menuCardButton.getName());
        buttonValueMap.put(MenuCardButton.FIRESTORE_CARD_ID_KEY, menuCardButton.getCardId());
        buttonValueMap.put(MenuCardButton.FIRESTORE_ACTIVE_KEY, menuCardButton.isEnabled());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TYPE_KEY, menuCardButton.getLocation().name());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_SHAPE_KEY, menuCardButton.getButtonShape());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TEXT_COLOR_KEY, menuCardButton.getButtonTextColor());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_COLOR_KEY, menuCardButton.getButtonColor());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TEXT_BLINK_KEY, menuCardButton.isButtonTextBlink());

        buttonValueMap.put(MenuCardButton.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        buttonValueMap.put(MenuCardButton.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        buttonValueMap.put(MenuCardButton.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        buttonValueMap.put(MenuCardButton.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newButtonReference = FireStoreLocation.getButtonsInMenuCardRootLocation(db, menuCardButton.getCardId()).document(menuCardButton.getLocation().name());
        menuCardButton.setId(newButtonReference.getId());

        newButtonReference.set(buttonValueMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Menu Button successfully created!");
                        listener.onCallback(menuCardButton);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting Menu Button";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }

    private void updateButton(final MenuCardButton menuCardButton, final OnResultListener<MenuCardButton> listener) {
        Log.i(TAG, "Trying to update an MenuCardButton: " + menuCardButton);

        DocumentReference existingButtonReference = FireStoreLocation.getButtonsInMenuCardRootLocation(db, menuCardButton.getCardId()).document(menuCardButton.getLocation().name());

        Map<String, Object> buttonValueMap = new HashMap<>();
        buttonValueMap.put(MenuCardButton.FIRESTORE_NAME_KEY, menuCardButton.getName());
        buttonValueMap.put(MenuCardButton.FIRESTORE_CARD_ID_KEY, menuCardButton.getCardId());
        buttonValueMap.put(MenuCardButton.FIRESTORE_ACTIVE_KEY, menuCardButton.isEnabled());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TYPE_KEY, menuCardButton.getLocation().name());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_SHAPE_KEY, menuCardButton.getButtonShape());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TEXT_COLOR_KEY, menuCardButton.getButtonTextColor());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_COLOR_KEY, menuCardButton.getButtonColor());
        buttonValueMap.put(MenuCardButton.FIRESTORE_BUTTON_TEXT_BLINK_KEY, menuCardButton.isButtonTextBlink());


        buttonValueMap.put(MenuCardButton.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        buttonValueMap.put(MenuCardButton.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        existingButtonReference.set(buttonValueMap, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Button successfully updated!");
                        } else {
                            String errorMessage = "Error while updating Button";
                            Log.e(TAG, errorMessage);
                        }
                        listener.onCallback(menuCardButton);
                    }
                });
    }
}
