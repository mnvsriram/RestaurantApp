package app.resta.com.restaurantapp.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Sriram on 02/01/2019.
 */

public class FirebaseAppInstance {

    public static FirebaseFirestore getFireStoreInstance() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }

    public static StorageReference getStorageReferenceInstance() {
        return FirebaseStorage.getInstance().getReference();
    }
}
