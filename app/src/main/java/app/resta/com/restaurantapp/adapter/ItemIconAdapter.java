package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.dialog.MenuItemDetailDialog;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 15/11/2017.
 */
public class ItemIconAdapter extends ArrayAdapter<RestaurantItem> {

    private List<RestaurantItem> dataSet;
    Activity activity;

    // View lookup cache
    private static class ViewHolder {
        ImageView itemImage;
        TextView itemName;
    }

    public ItemIconAdapter(List<RestaurantItem> data, Activity activity) {
        super(activity, R.layout.menu_list_item, data);
        this.dataSet = data;
        this.activity = activity;
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
        setImage(dataModel.getFireStoreImages().get(0).getStorageUrl(), viewHolder.itemImage);

        viewHolder.itemName.setOnClickListener(showItemDetails);
        viewHolder.itemName.setTag(dataModel);
        viewHolder.itemImage.setOnClickListener(showItemDetails);
        viewHolder.itemImage.setTag(R.string.tag_data_model_in_icon_adapter, dataModel);
        return convertView;
    }


    View.OnClickListener showItemDetails = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Object tag = v.getTag(R.string.tag_data_model_in_icon_adapter);
            RestaurantItem item = new RestaurantItem();
            if (tag == null) {
                tag = v.getTag();
            }
            if (tag != null && tag instanceof RestaurantItem) {
                item = (RestaurantItem) tag;
            }

            MenuItemDetailDialog cdd = new MenuItemDetailDialog(activity, item);
            cdd.show();
        }
    };

    private void setImage(String imagePath, ImageView imageView) {
        ImageUtil.loadImageFromStorage(MyApplication.getAppContext(), imagePath, "Image", imageView);
    }
}
