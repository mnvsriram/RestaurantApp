package app.resta.com.restaurantapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 15/11/2017.
 */
public class ItemIconAdapter extends ArrayAdapter<RestaurantItem> {

    private List<RestaurantItem> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView itemImage;
        TextView itemName;
    }

    public ItemIconAdapter(List<RestaurantItem> data, Context context) {
        super(context, R.layout.menu_list_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RestaurantItem dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_image_icon_with_details, parent, false);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.itemIconImage);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.itemIconName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemName.setText(dataModel.getName());
        setImage(dataModel.getImage(0), viewHolder.itemImage);
        return convertView;
    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        RestaurantItem dataModel = getItem(position);
//        ImageView imageView;
//        if (convertView == null) {
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//        } else {
//            imageView = (ImageView) convertView;
//        }
//
//
//        String image = dataModel.getImage(0);
//
//        if (image == null) {
//            imageView.setImageResource(R.drawable.noimage);
//        } else {
//
//            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
//            String filePath = path + image + ".jpeg";
//            File file = new File(filePath);
//            if (file.exists()) {
//                Bitmap bmp = BitmapFactory.decodeFile(filePath);
//                imageView.setImageBitmap(bmp);
//            }
//        }
//
//
//        return imageView;
//    }

    private void setImage(String imageName, ImageView imageView){
        if (imageName == null) {
            imageView.setImageResource(R.drawable.noimage);
        } else {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + imageName + ".jpeg";
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bmp);
            }
        }
    }

}
