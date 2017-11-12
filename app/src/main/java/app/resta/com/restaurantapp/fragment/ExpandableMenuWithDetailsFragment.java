
package app.resta.com.restaurantapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;

public class ExpandableMenuWithDetailsFragment extends Fragment implements MenuCardViewExpandableMenuListFragment.OnMenuCardExpandableItemSelectedListener {
    private IngredientDao ingredientDao = new IngredientDao();
    private TagsDao tagsDao = new TagsDao();
    private long menuTypeId;
    private View inflatedView;
    private RestaurantItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_expandable_menu_with_details, container, false);


        MenuCardViewExpandableMenuListFragment frag = new MenuCardViewExpandableMenuListFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        frag.setGroupMenuId(menuTypeId);
        frag.setContainer(this);

        ft.add(R.id.expandable_menu_container, frag);
        ft.commit();


        return inflatedView;
    }

    @Override
    public void onMenuCardExpandableItemSelectedListener(RestaurantItem item) {
        MenuDetailFragment frag = new MenuDetailFragment();
        frag.setSelectedItem(item);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (item != null) {
            List<Tag> tagList = tagsDao.getTagsData().get(item.getId());
            frag.setTagList(tagList);
            ft.replace(R.id.expandable_menu_detail_container, frag);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    public long getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(long menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public RestaurantItem getItem() {
        return item;
    }

    public void setItem(RestaurantItem item) {
        this.item = item;
    }
}
