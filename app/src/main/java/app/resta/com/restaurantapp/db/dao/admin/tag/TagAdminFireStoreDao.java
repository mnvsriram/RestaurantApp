package app.resta.com.restaurantapp.db.dao.admin.tag;

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
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 02/01/2019.
 */

public class TagAdminFireStoreDao implements TagAdminDaoI {

    private static final String TAG = "tagAdminDao";
    FirebaseFirestore db;

    public TagAdminFireStoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    public void insertTag(final Tag tag, final OnResultListener<Tag> listener) {
        Log.i(TAG, "Trying to insert a tag. tag: " + tag);
        Map<String, Object> tagMap = new HashMap<>();
        tagMap.put(Tag.FIRESTORE_NAME_KEY, tag.getName());
        tagMap.put(Tag.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        tagMap.put(Tag.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        tagMap.put(Tag.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        tagMap.put(Tag.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());


        DocumentReference newTagReference = FireStoreLocation.getTagsRootLocation(db).document();
        tag.setId(newTagReference.getId());

        newTagReference.set(tagMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Tag successfully created!");
                        listener.onCallback(tag);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Error while inserting tag data";
                Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }

    public void getTags(final OnResultListener<List<Tag>> listener) {
        Log.i(TAG, "Getting list of tags");
        final List<Tag> tags = new ArrayList<>();
        FireStoreLocation.getTagsRootLocation(db)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tags.add(Tag.prepare(document));
                            }
                            Log.i(TAG, "Got " + tags.size() + " from server.");
                            listener.onCallback(tags);
                        } else {
                            listener.onCallback(tags);
                            Log.e(TAG, "Error getting tags.", task.getException());
                        }
                    }
                });
    }

    protected void getTag(String tagId, Source source, final OnResultListener<Tag> listener) {
        DocumentReference documentRef = FireStoreLocation.getTagsRootLocation(db).document(tagId);
        documentRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Tag preparedTag = null;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    preparedTag = Tag.prepare(document);
                } else {
                    Log.d(TAG, "Failed to fetch Tags", task.getException());
                }
                listener.onCallback(preparedTag);
            }
        });

    }

    @Override
    public void getTag(String tagId, final OnResultListener<Tag> listener) {
        getTag(tagId, Source.DEFAULT, listener);
    }


    public void deleteTag(final String tagId) {
        Log.i(TAG, "Deleting the Tag with ID: " + tagId);
        FireStoreLocation.getTagsRootLocation(db).document(tagId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Tag successfully deleted!");
                        deleteReferencesFromMenuItems(tagId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting tag", e);
                    }
                });
    }

    public void deleteReferencesFromMenuItems(final String tagId) {
        Log.i(TAG, "Deleting the references of the tag " + tagId + " from all the menu items.");
        final MenuItemAdminDaoI menuItemAdminDaoI = new MenuItemAdminFireStoreDao();
        menuItemAdminDaoI.getAllItemsWithTag(tagId, new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                for (RestaurantItem restaurantItem : items) {
                    List<String> ids = new ArrayList<>();
                    ids.add(tagId);
                    Log.d(TAG, "Deleted tag " + tagId + " from item " + restaurantItem.getName());
                    menuItemAdminDaoI.removeTagsFromItem(restaurantItem.getId(), ids);
                }
            }
        });

    }

    public void updateImageUrl(final Tag tag, String imageStorageUrl, final OnResultListener<String> listener) {
        Log.i(TAG, "Trying to update the image on tag: " + tag.getId());
        DocumentReference existingCardReference = FireStoreLocation.getTagsRootLocation(db).document(tag.getId());
        Map<String, Object> data = new HashMap<>();
        data.put(Tag.FIRESTORE_IMAGE_URL, imageStorageUrl);
        existingCardReference.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Image for tag successfully updated!");
                } else {
                    Log.d(TAG, "Error while updating image for tag!");
                }
                listener.onCallback("success");
            }
        });

    }

}
