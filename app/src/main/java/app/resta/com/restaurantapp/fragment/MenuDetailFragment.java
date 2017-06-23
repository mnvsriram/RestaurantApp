package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuDetailFragment extends Fragment {

    private int childPosition;
    private int groupPosition;

    public MenuDetailFragment() {
        // Required empty public constructor
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            RestaurantItem item = null;



            if (childPosition >= 0) {
                item = MenuExpandableListAdapter.getChildMenuItem(groupPosition, childPosition);
            }
            if (item != null) {
                TextView title = (TextView) view.findViewById(R.id.nameHeader);
                title.setText(item.getName());

                TextView itemDescription = (TextView) view.findViewById(R.id.itemDescripton);
                itemDescription.setText(item.getDescription());


                ImageView ggwImage= (ImageView) view.findViewById(R.id.goesGreatWithImage);
                ggwImage.setTag(item.getId());

                ImageView image = (ImageView) view.findViewById(R.id.list_image);
                //int resId = getResources().getIdentifier(item.getImage(), "drawable", getActivity().getPackageName());
                //image.setImageResource(resId);


                String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";

                String filePath = path + item.getImage() + ".jpeg";

                File file = new File(filePath);
                if (file.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(filePath);
                    image.setImageBitmap(bmp);
                } else {
                    image.setImageResource(R.drawable.noimage);
                }

            }
        }
    }

}
