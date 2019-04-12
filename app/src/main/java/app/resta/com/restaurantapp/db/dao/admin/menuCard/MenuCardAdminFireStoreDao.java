package app.resta.com.restaurantapp.db.dao.admin.menuCard;

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
import app.resta.com.restaurantapp.db.dao.admin.button.MenuCardButtonAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.button.MenuCardButtonAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardAdminFireStoreDao implements MenuCardAdminDaoI {
    private static final String TAG = "MenuCardAdminDao";
    FirebaseFirestore db;
    private MenuCardButtonAdminDaoI menuCardButtonAdminDao;

    public MenuCardAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
        menuCardButtonAdminDao = new MenuCardButtonAdminFireStoreDao();
    }

    private void insertCard(final MenuCard card, final OnResultListener<MenuCard> listener) {
        Log.i(TAG, "Trying to insert a card: " + card);
        Map<String, Object> cardValueMap = new HashMap<>();
        cardValueMap.put(MenuCard.FIRESTORE_NAME_KEY, card.getName());
        cardValueMap.put(MenuCard.FIRESTORE_GREETING_TEXT_KEY, card.getGreetingText());
        cardValueMap.put(MenuCard.FIRESTORE_DEFAULT_KEY, card.isDefault());
        cardValueMap.put(MenuCard.FIRESTORE_LOGO_IMABE_BIG_URL, card.getLogoBigImageUrl());
        cardValueMap.put(MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL, card.getLogoSmallImageUrl());
        cardValueMap.put(MenuCard.FIRESTORE_BG_COLOR, card.getBackgroundColor());

        cardValueMap.put(RestaurantItem.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        cardValueMap.put(RestaurantItem.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        cardValueMap.put(RestaurantItem.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        cardValueMap.put(RestaurantItem.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        DocumentReference newCardReference = FireStoreLocation.getMenuCardsRootLocation(db).document();
        card.setId(newCardReference.getId());

        newCardReference.set(cardValueMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Card successfully created!");
                        listener.onCallback(card);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting card";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }


    private void updateCard(final MenuCard card, final OnResultListener<MenuCard> listener) {
        Log.i(TAG, "Trying to update a card: " + card);
        DocumentReference existingCardReference = FireStoreLocation.getMenuCardsRootLocation(db).document(card.getId());
        Map<String, Object> cardValueMap = new HashMap<>();

        cardValueMap.put(MenuCard.FIRESTORE_NAME_KEY, card.getName());
        cardValueMap.put(MenuCard.FIRESTORE_GREETING_TEXT_KEY, card.getGreetingText());
        cardValueMap.put(MenuCard.FIRESTORE_DEFAULT_KEY, card.isDefault());
        cardValueMap.put(MenuCard.FIRESTORE_LOGO_IMABE_BIG_URL, card.getLogoBigImageUrl());
        cardValueMap.put(MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL, card.getLogoSmallImageUrl());
        cardValueMap.put(MenuCard.FIRESTORE_BG_COLOR, card.getBackgroundColor());


        cardValueMap.put(MenuCard.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        cardValueMap.put(MenuCard.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        existingCardReference.set(cardValueMap, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MenuCard menuCardFromDB = null;
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Card successfully updated!");
                        } else {
                            String errorMessage = "Error while updating card";
                            Log.e(TAG, errorMessage);
                        }
                        listener.onCallback(card);
                    }
                });
    }


    @Override
    public void insertOrUpdateCard(final MenuCard card, final OnResultListener<MenuCard> listener) {
        if (card.getId() == null || card.getId().length() == 0) {
            insertCard(card, listener);
        } else {
            updateCard(card, listener);
        }
    }

    protected void getCardWithButtonsAndActions(final String cardId, final Source source, final OnResultListener<MenuCard> listener) {
        getCard(cardId, source, new OnResultListener<MenuCard>() {
            @Override
            public void onCallback(final MenuCard card) {
                if (card != null) {
                    getButtonsInCard(cardId, source, new OnResultListener<List<MenuCardButton>>() {
                        @Override
                        public void onCallback(List<MenuCardButton> buttonsInCard) {
                            if (buttonsInCard != null && buttonsInCard.size() > 0) {
                                card.setButtons(buttonsInCard);
                                for (final MenuCardButton button : buttonsInCard) {
                                    menuCardButtonAdminDao.getActions(cardId, button.getId(), source, new OnResultListener<List<MenuCardAction>>() {
                                        @Override
                                        public void onCallback(List<MenuCardAction> actions) {
                                            button.setActions(actions);
                                            listener.onCallback(card);
                                        }
                                    });
                                }
                            } else {
                                listener.onCallback(card);
                            }
                        }
                    });
                }
            }
        });
    }

    public void getCardWithButtonsAndActions(final String cardId, final OnResultListener<MenuCard> listener) {

    }

    protected void getCard(String cardId, Source source, final OnResultListener<MenuCard> listener) {
        Log.i(TAG, "Fetching card with id " + cardId);
        FireStoreLocation.getMenuCardsRootLocation(db).document(cardId).get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        MenuCard menuCardFromDB = null;
                        if (task.isSuccessful()) {
                            menuCardFromDB = MenuCard.prepare(task.getResult());
                        } else {
                            Log.e(TAG, "Error getting Menu card with the provided id.", task.getException());
                        }
                        listener.onCallback(menuCardFromDB);
                    }
                });

    }

    @Override
    public void getCard(String cardId, final OnResultListener<MenuCard> listener) {
        getCard(cardId, Source.DEFAULT, listener);
    }

    @Override
    public void getDefaultCard(final OnResultListener<MenuCard> listener) {
        Log.i(TAG, "Fetching Default card ");
        FireStoreLocation.getMenuCardsRootLocation(db)
                .whereEqualTo("default", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        MenuCard menuCard = null;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                menuCard = MenuCard.prepare(document);
                            }
                        } else {
                            Log.e(TAG, "Error getting default menu card ", task.getException());
                        }
                        listener.onCallback(menuCard);

                    }
                });
    }

    public void getDefaultCardWithButtonsAndActions(final Source source, final OnResultListener<MenuCard> listener) {
        Log.i(TAG, "Fetching Default card ");
        FireStoreLocation.getMenuCardsRootLocation(db)
                .whereEqualTo("default", true)
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        MenuCard menuCard = null;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                menuCard = MenuCard.prepare(document);
                                getCardWithButtonsAndActions(menuCard.getId(), source, listener);
                            }
                        } else {
                            Log.e(TAG, "Error getting default menu card ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getDefaultCardWithButtonsAndActions(final OnResultListener<MenuCard> listener) {
        getDefaultCardWithButtonsAndActions(Source.DEFAULT, listener);
    }

    @Override
    public void getButtonInCard(final String menuCardId, final MenuCardButtonEnum buttonType, final OnResultListener<MenuCardButton> listener) {
        if (menuCardId != null && buttonType != null) {
            Log.i(TAG, "Trying to get button in card  : " + menuCardId);
            FireStoreLocation.getButtonsInMenuCardRootLocation(db, menuCardId).document(buttonType.name()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            MenuCardButton button = null;
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                button = MenuCardButton.prepare(documentSnapshot);
                                button.setCardId(menuCardId);
                            } else {
                                Log.e(TAG, "Error getting groups in meny type.", task.getException());
                            }
                            listener.onCallback(button);
                        }
                    });
        }else{
            listener.onCallback(null);
        }
    }

    protected void getButtonsInCard(final String menuCardId, Source source, final OnResultListener<List<MenuCardButton>> listener) {
        Log.i(TAG, "Trying to get buttons in card  : " + menuCardId);
        FireStoreLocation.getButtonsInMenuCardRootLocation(db, menuCardId).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<MenuCardButton> buttons = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                buttons.add(MenuCardButton.prepare(document));
                            }
                        } else {
                            Log.e(TAG, "Error getting buttons in groups .", task.getException());
                        }
                        listener.onCallback(buttons);
                    }
                });
    }

    @Override
    public void getButtonsInCard(final String menuCardId, final OnResultListener<List<MenuCardButton>> listener) {
        getButtonsInCard(menuCardId, Source.DEFAULT, listener);
    }


    public void updateImageUrl(final MenuCard menuCard, String imageNameKey, String imageStorageUrl) {
        Log.i(TAG, "Trying to update the image on card: " + menuCard.getId());
        DocumentReference existingCardReference = FireStoreLocation.getMenuCardsRootLocation(db).document(menuCard.getId());
        Map<String, Object> data = new HashMap<>();
        data.put(imageNameKey, imageStorageUrl);
        existingCardReference.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Image for card successfully updated!");
                } else {
                    Log.d(TAG, "Error while updating image for Card!");
                }
            }
        });
    }


}
