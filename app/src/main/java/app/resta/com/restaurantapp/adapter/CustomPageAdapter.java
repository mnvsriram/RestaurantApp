package app.resta.com.restaurantapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.GlideApp;
import app.resta.com.restaurantapp.util.MyApplication;


public class CustomPageAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> imageUrls;
    private ImageView imageView;
    private StorageReference storageRef;
    private boolean skipCacheForImages;

    public CustomPageAdapter(Context context, List<String> imageUrls, boolean skipCache) {
        mContext = context;
        this.imageUrls = imageUrls;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();
        skipCacheForImages = skipCache;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        String imageUrl = imageUrls.get(position);
        final StorageReference child = storageRef.child(imageUrl);
        child.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uriForExistingImage) {
                        if (skipCacheForImages) {
                            displayImageBySkippingCache(container, itemView, child);
                        } else {
                            displayImage(container, itemView, child);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        int errorCode = ((StorageException) exception).getErrorCode();
                        if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            if (skipCacheForImages) {
                                displayImageBySkippingCache(container, itemView, storageRef.child(FireBaseStorageLocation.getNoImageLocation()));
                            } else {
                                displayImage(container, itemView, storageRef.child(FireBaseStorageLocation.getNoImageLocation()));
                            }
                        }
                    }
                });
        return itemView;
    }

    private void displayImageBySkippingCache(final ViewGroup container, final View itemView, StorageReference child) {
        GlideApp.with(MyApplication.getAppContext())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .load(child)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView = itemView.findViewById(R.id.imageView);
                        imageView.setImageBitmap(resource);
                        container.addView(itemView);
                    }
                });
    }


    private void displayImage(final ViewGroup container, final View itemView, StorageReference child) {
        GlideApp.with(MyApplication.getAppContext())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(child)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView = itemView.findViewById(R.id.imageView);
                        imageView.setImageBitmap(resource);
                        container.addView(itemView);
                    }
                });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
