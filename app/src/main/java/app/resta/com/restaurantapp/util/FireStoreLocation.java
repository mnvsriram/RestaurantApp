package app.resta.com.restaurantapp.util;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static app.resta.com.restaurantapp.util.RestaurantMetadata.getLoggedInEmail;
import static app.resta.com.restaurantapp.util.RestaurantMetadata.getRestaurantId;
import static app.resta.com.restaurantapp.util.RestaurantMetadata.getUsername;

/**
 * Created by Sriram on 31/12/2018.
 */

public class FireStoreLocation {

    public static CollectionReference getIngredientsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("ingredients");
    }

    public static CollectionReference getTagsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("tags");
    }

    public static CollectionReference getMenuItemsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("menuItems");
    }


    public static CollectionReference getMenuTypesRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("menuTypes");
    }


    public static CollectionReference getMenuTypesLocationForID(FirebaseFirestore db, String menuTypeId) {
        return FireStoreLocation.getMenuTypesRootLocation(db).document(menuTypeId).collection("groups");
    }


    public static CollectionReference getMenuGroupsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("menuGroups");
    }


    public static CollectionReference getMenuGroupsLocationForId(FirebaseFirestore db, String groupId) {
        return FireStoreLocation.getMenuGroupsRootLocation(db).document(groupId).collection("children");
    }


    public static CollectionReference getMenuCardsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("menuCards");
    }


    public static CollectionReference getButtonsInMenuCardRootLocation(FirebaseFirestore db, String menuCardId) {
        return getMenuCardsRootLocation(db).document(menuCardId).collection("buttons");
    }


    public static CollectionReference getButtonActionsRootLocationForId(FirebaseFirestore db, String menuCardId, String buttonId) {
        return getButtonsInMenuCardRootLocation(db, menuCardId).document(buttonId).collection("actions");
    }

    public static CollectionReference getOrderedItemsRootLocationForOrderId(FirebaseFirestore db, String orderId) {
        return getOrdersRootLocation(db).document(orderId).collection("items");
    }


    public static DocumentReference getPublishedDataInformation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("publisher").document("data");
    }

    public static CollectionReference getOrdersRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("orders");
    }

    public static CollectionReference getScoresRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("scores");
    }


    public static CollectionReference getReviewsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("reviews");
    }

    public static CollectionReference getRatingSummaryRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("ratingSummary");
    }


    public static CollectionReference getRatingSummaryLocationForADate(FirebaseFirestore db, String date) {
        return db.collection("restaurants").document(getRestaurantId()).collection("ratingSummary").document(date).collection("items");
    }

    public static DocumentReference getAdminPasswordLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("passwords").document("admin");
    }

    public static DocumentReference getWaiterPasswordLocation(FirebaseFirestore db) {
        return db.collection("restaurants").document(getRestaurantId()).collection("passwords").document("waiter");
    }

    public static CollectionReference getRestaurantsRootLocation(FirebaseFirestore db) {
        return db.collection("restaurants");
    }

    public static CollectionReference getDevicesRootLocation(FirebaseFirestore db) {
        return db.collection("users").document(getLoggedInEmail()).collection("devices");
    }

    public static CollectionReference getRegisteredEmailsRootLocation(FirebaseFirestore db) {
        return db.collection("users");
    }

    public static String getUserLoggedIn() {
        return getUsername();
    }

}
