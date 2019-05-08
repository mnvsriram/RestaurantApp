package app.resta.com.restaurantapp.db.dao.admin.order;

import android.support.annotation.NonNull;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.Order;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.FireStoreLocation;

/**
 * Created by Sriram on 06/03/2019.
 */

public class OrderAdminFirestoreDao implements OrderAdminDaoI {
    private final String TAG = "OrderDao";
    private FirebaseFirestore db;

    public OrderAdminFirestoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    @Override
    public void placeOrder(final List<OrderedItem> items, String tableNo, String notes, final OnResultListener<Order> listener) {
        Log.i(TAG, "Trying to insert a order.");
        final Order order = new Order();
        order.setInstructions(notes);
        order.setItems(items);
        order.setTableNumber(tableNo);
        order.setTotalPrice(order.calculateTotalPrice());
        Map<String, Object> orderValueMap = new HashMap<>();
        orderValueMap.put(Ingredient.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        orderValueMap.put(Ingredient.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        orderValueMap.put(Order.FIRESTORE_TABLE_NO_KEY, order.getTableNumber());
        orderValueMap.put(Order.FIRESTORE_INSTRUCTIONS_KEY, order.getInstructions());
        orderValueMap.put(Order.FIRESTORE_PRICE_KEY, order.getTotalPrice());
        orderValueMap.put(Order.FIRESTORE_ACTIVE_KEY, "Y");
        DocumentReference newOrderReference = FireStoreLocation.getOrdersRootLocation(db).document();
        order.setOrderId(newOrderReference.getId());

        newOrderReference.set(orderValueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Order successfully created!");
                    addItemsToOrder(items, order.getOrderId());
                } else {
                    Log.d(TAG, "Error while creating order!");
                }
                listener.onCallback(order);
            }
        });
    }


    public void markOrderAsComplete(String orderId, final OnResultListener<String> listener) {
        Log.i(TAG, "Trying to update a order.");
        Map<String, Object> orderValueMap = new HashMap<>();

        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        orderValueMap.put(Order.FIRESTORE_ACTIVE_KEY, "N");
        FireStoreLocation.getOrdersRootLocation(db).document(orderId).update(orderValueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Order successfully updated!");
                } else {
                    Log.d(TAG, "Error while updating order!");
                }
                listener.onCallback("success");
            }
        });
    }


    public void updateCommentForOrder(String orderId, String comment, String tableNo) {
        Log.i(TAG, "Trying to update a order.");
        Map<String, Object> orderValueMap = new HashMap<>();
        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        orderValueMap.put(Ingredient.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        orderValueMap.put(Order.FIRESTORE_TABLE_NO_KEY, tableNo);
        orderValueMap.put(Order.FIRESTORE_INSTRUCTIONS_KEY, comment);

        FireStoreLocation.getOrdersRootLocation(db).document(orderId).update(orderValueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Order successfully updated!");
                } else {
                    Log.d(TAG, "Error while updating order!");
                }
            }
        });
    }

    public void modifyOrder(final List<OrderedItem> items, final String orderId, final String comment, final String tableNo, final OnResultListener<Order> listener) {
        deleteAllItemsInOrder(orderId, new OnResultListener<String>() {
            @Override
            public void onCallback(String status) {
                addItemsToOrder(items, orderId);
                updateCommentForOrder(orderId, comment, tableNo);
                listener.onCallback(new Order());
            }
        });

    }

    void deleteAllItemsInOrder(final String orderId, final OnResultListener<String> listener) {
        FireStoreLocation.getOrderedItemsRootLocationForOrderId(db, orderId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteOrderedItem(document.getId(), orderId);
                    }
                    listener.onCallback("success");
                } else {
                    Log.e(TAG, "Error getting ingredients.", task.getException());
                }
            }
        });

    }

    public void deleteOrderedItem(final String orderedItemId, final String orderId) {
        FireStoreLocation.getOrderedItemsRootLocationForOrderId(db, orderId).document(orderedItemId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ordered Item successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Ordered Item", e);
                    }
                });
    }

    private void addItemsToOrder(final List<OrderedItem> items, String orderId) {
        for (OrderedItem orderedItem : items) {
            addItemToOrder(orderedItem, orderId);
        }
    }


    public void addReviewsAndRatingsToOrder(List<ReviewForDish> reviewForDishes) {
        for (ReviewForDish reviewForDish : reviewForDishes) {
            if (reviewForDish.getReview() != null || reviewForDish.getReviewText() != null) {
                addReviewToItem(reviewForDish);
            }
        }
    }


    private void addReviewToItem(final ReviewForDish reviewForDish) {
        Log.i(TAG, "Trying to insert an review for an item in the order.");

        Map<String, Object> reviewMap = new HashMap<>();
        if (reviewForDish.getReview() != null) {
            reviewMap.put(OrderedItem.FIRESTORE_RATING_KEY, reviewForDish.getReview().getValue());
        } else {
            reviewMap.put(OrderedItem.FIRESTORE_RATING_KEY, ReviewEnum.NOREVIEW.getValue());
        }
        reviewMap.put(OrderedItem.FIRESTORE_REVIEW_KEY, reviewForDish.getReviewText());
        DocumentReference newReview = FireStoreLocation.getOrderedItemsRootLocationForOrderId(db, reviewForDish.getOrderId()).document(reviewForDish.getItem().getId());
        newReview.set(reviewMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "review successfully added to order!");
                } else {
                    Log.d(TAG, "Error while adding item to order!");
                }
            }
        });
    }

    private void addItemToOrder(final OrderedItem item, String orderId) {
        Log.i(TAG, "Trying to insert an item in the order.");

        Map<String, Object> orderItemValueMap = new HashMap<>();
        orderItemValueMap.put(OrderedItem.FIRESTORE_INSTRUCTIONS_KEY, item.getInstructions());
        orderItemValueMap.put(OrderedItem.FIRESTORE_ITEM_ID_KEY, item.getItemId());
        orderItemValueMap.put(OrderedItem.FIRESTORE_ITEM_NAME_KEY, item.getItemName());
        orderItemValueMap.put(OrderedItem.FIRESTORE_PRICE_KEY, item.getPrice());
        if (item.getRating() != null) {
            orderItemValueMap.put(OrderedItem.FIRESTORE_RATING_KEY, item.getRating());
        } else {
            orderItemValueMap.put(OrderedItem.FIRESTORE_RATING_KEY, ReviewEnum.NOREVIEW.getValue());
        }


        orderItemValueMap.put(OrderedItem.FIRESTORE_QUANTITY_KEY, new Integer(item.getQuantity()));
        orderItemValueMap.put(OrderedItem.FIRESTORE_REVIEW_KEY, item.getReview());
        orderItemValueMap.put(OrderedItem.FIRESTORE_MENU_TYPE_ID, item.getMenuTypeId());

        DocumentReference newOrderedItemReference = FireStoreLocation.getOrderedItemsRootLocationForOrderId(db, orderId).document(item.getItemId());
        item.setOrderId(orderId);

        newOrderedItemReference.set(orderItemValueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "item successfully added to order!");
                } else {
                    Log.d(TAG, "Error while adding item to order!");
                }
            }
        });
    }

    public void getOrdersWithItems(Date fromDate, Date toDate, final OnResultListener<List<Order>> listener) {
        getOrders(fromDate, toDate, new OnResultListener<List<Order>>() {
            @Override
            public void onCallback(final List<Order> orders) {
                final AtomicInteger index = new AtomicInteger(0);
                if (orders != null && orders.size() > 0) {
                    for (final Order order : orders) {
                        getOrderedItems(order.getOrderId(), order.getActive(), new OnResultListener<List<OrderedItem>>() {
                            @Override
                            public void onCallback(List<OrderedItem> orderedItems) {
                                order.setItems(orderedItems);
                                index.getAndIncrement();
                                if (index.get() == orders.size()) {
                                    listener.onCallback(orders);
                                }
                            }
                        });
                    }
                } else {
                    listener.onCallback(orders);
                }
            }
        });
    }


    public void getReviewsForOrders(final Set<String> orderIds, final OnResultListener<Map<String, List<ReviewForDish>>> listener) {
        final Map<String, List<ReviewForDish>> ratingsForOrders = new HashMap<>();
        final AtomicInteger index = new AtomicInteger(0);
        if (orderIds != null && orderIds.size() > 0) {
            for (final String orderId : orderIds) {
                getOrderedItems(orderId, "", new OnResultListener<List<OrderedItem>>() {
                    @Override
                    public void onCallback(List<OrderedItem> orderedItems) {
                        List<ReviewForDish> reviewForDishes = new ArrayList<>();
                        for (OrderedItem orderedItem : orderedItems) {
                            ReviewForDish reviewForDish = new ReviewForDish();
                            RestaurantItem item = new RestaurantItem();
                            item.setId(orderedItem.getItemId());
                            item.setName(orderedItem.getItemName());

                            reviewForDish.setItem(item);
                            reviewForDish.setOrderId(orderId);
                            reviewForDish.setReview(ReviewEnum.of(orderedItem.getRating()));
                            reviewForDish.setReviewText(orderedItem.getReview());
                            reviewForDishes.add(reviewForDish);
                        }
                        ratingsForOrders.put(orderId, reviewForDishes);
                        index.getAndIncrement();
                        if (index.get() == orderIds.size()) {
                            listener.onCallback(ratingsForOrders);
                        }

                    }
                });

            }
        } else {
            listener.onCallback(ratingsForOrders);
        }
    }

    public void getOrderedItems(final String orderId, final String orderStatus, final OnResultListener<List<OrderedItem>> listener) {
        final List<OrderedItem> orderedItems = new ArrayList<>();
        FireStoreLocation.getOrderedItemsRootLocationForOrderId(db, orderId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderedItem e = OrderedItem.prepareOrderedItem(document);
                                e.setOrderStatus(orderStatus);
                                e.setOrderId(orderId);
                                orderedItems.add(e);
                            }
                        } else {
                            Log.e(TAG, "Error getting order items.", task.getException());
                        }
                        listener.onCallback(orderedItems);
                    }
                });
    }


    public void getOrders(Date fromDate, Date toDate, final OnResultListener<List<Order>> listener) {
        final List<Order> orders = new ArrayList<>();
        FireStoreLocation.getOrdersRootLocation(db)
                .whereGreaterThanOrEqualTo(Order.FIRESTORE_CREATED_AT_KEY, fromDate)
                .whereLessThanOrEqualTo(Order.FIRESTORE_CREATED_AT_KEY, toDate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orders.add(Order.prepareOrder(document));
                            }
                        } else {
                            Log.e(TAG, "Error getting ingredients.", task.getException());
                        }
                        listener.onCallback(orders);
                    }
                });
    }
}
