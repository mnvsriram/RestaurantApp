package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.StorageReference;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.GlideApp;

/**
 * Created by Sriram on 29/07/2017.
 */

public class TagArrayAdapter extends ArrayAdapter<Tag> {

    //RestaurantItem[] dataTemp;
    Tag[] data;
    Activity activity;
    private View.OnClickListener buttoOnClickListener;
    StorageReference storageRef;

    public TagArrayAdapter(Activity activity, int textViewResourceId, Tag[] tagArray, View.OnClickListener buttonOnClickListener) {
        super(activity, textViewResourceId, tagArray);
        this.activity = activity;
        //  this.dataTemp = itemArray;
        this.data = tagArray;
        this.buttoOnClickListener = buttonOnClickListener;
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();
    }

    public void setData(Tag[] data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (activity).getLayoutInflater();
            row = inflater.inflate(R.layout.row_tag_and_ingredient, parent, false);


            holder.name = (TextView) row.findViewById(R.id.nameOfItem);
            holder.delete = (ImageButton) row.findViewById(R.id.deleteItem);
            holder.image = (ImageView) row.findViewById(R.id.imageForItem);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Tag tag = data[position];
        holder.name.setText(tag.getName());
        holder.delete.setTag(tag);
        showTagsImageInGrid(tag, holder.image);
        if (buttoOnClickListener != null) {
            holder.delete.setOnClickListener(buttoOnClickListener);
        }
        return row;
    }

    private void showTagsImageInGrid(final Tag tag, ImageView imageView) {
        final ImageView imageButton = imageView;
        StorageReference image = storageRef.child(tag.getImage());
        GlideApp.with(activity)
                .load(image)
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  Log.i("Tag", "No image found for " + tag.getName() + " in storage");
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  return false;
                              }
                          }
                )
                .into(imageButton);
    }


    public static class ViewHolder {
        ImageView image;
        ImageButton delete;
        TextView name;
    }

    @Override
    public int getCount() {
        return data.length;
    }
}