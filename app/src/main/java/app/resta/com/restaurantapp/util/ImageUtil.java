package app.resta.com.restaurantapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;

/**
 * Created by Sriram on 02/01/2019.
 */

public class ImageUtil {
    private static final String TAG = "ImageUtil";
    private static final StorageReference storageRef = FirebaseAppInstance.getStorageReferenceInstance();

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, ContentResolver contentResolver) {
        String path = "";
        if (contentResolver != null) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public static void loadImageFromStorage(Context context, String path, final String imageName, ImageView view) {
//        if (LoginController.getInstance().isAdminLoggedIn()) {
//            StorageReference imageReference = storageRef.child(path);
//            loadImageFromStorage(context, imageReference, imageName, view);
//        }
        StorageReference imageReference = storageRef.child(path);
        loadImageFromStorage(context, imageReference, imageName, view);
    }

    public static void loadImageFromStorageBySkippingCache(Context context, String path, final String imageName, ImageView view) {
//        if (LoginController.getInstance().isAdminLoggedIn()) {
//            StorageReference imageReference = storageRef.child(path);
//            loadImageFromStorage(context, imageReference, imageName, view);
//        }
        StorageReference imageReference = storageRef.child(path);
        loadImageFromStorageBySkippingCache(context, imageReference, imageName, view);
    }

    public static void loadImageFromStorage(Context context, StorageReference imageLocationRefInFirebaseStorage, final String imageName, ImageView view) {
        GlideApp.with(context)
                .load(imageLocationRefInFirebaseStorage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  Log.i(TAG, "No image found for " + imageName + " in storage");
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  return false;
                              }
                          }
                )
                .into(view);
    }

    public static void loadImageFromStorageBySkippingCache(Context context, StorageReference imageLocationRefInFirebaseStorage, final String imageName, ImageView view) {
        GlideApp.with(context)
                .load(imageLocationRefInFirebaseStorage)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  Log.i(TAG, "No image found for " + imageName + " in storage");
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  return false;
                              }
                          }
                )
                .into(view);
    }

    public static void deleteImage(StorageReference photoRef, final String imageName, final OnResultListener<Object> listener) {
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted " + imageName);
                listener.onCallback(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete " + imageName);
            }
        });
    }

    public static void deleteImage(final String url, final OnResultListener<Object> listener) {
        StorageReference imageRef = storageRef.child(url);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted " + url);
                listener.onCallback(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete " + url
                );
            }
        });
    }

    public static void createImage(final StorageReference storageReference, Uri localImageUri, final String imageName, final OnResultListener<String> listener) {
        storageReference.putFile(localImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                listener.onCallback(storageReference.getPath());
//                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Uri downloadUrl = uri;
//                        Log.i("", "Image " + imageName + "uploaded successfully. Url: " + downloadUrl.toString());
//                        listener.onCallback(uri);
//                    }
//                });

            }
        });
    }
}