
package app.resta.com.restaurantapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.tag.TagUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.tag.TagUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.util.TextUtils;

public class ExpandableMenuWithDetailsFragment extends Fragment implements MenuCardViewExpandableMenuListFragment.OnMenuCardExpandableItemSelectedListener {
    private TagUserDaoI tagUserDao = new TagUserFireStoreDao();
    private MenuItemUserDaoI menuItemUserDao = new MenuItemUserFireStoreDao();
    private String menuTypeId;
    private View inflatedView;
    private StyleController styleController;

    public void setStyleController(StyleController styleController) {
        this.styleController = styleController;
    }

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
        frag.setStyleController(styleController);

        ft.add(R.id.expandable_menu_container, frag);
        ft.commit();


        ViewGroup mainLayout = inflatedView.findViewById(R.id.mainlayoutExpandableMenuWithDetails);
        StyleUtil.setStyle(mainLayout, styleController);

        setFields();

        return inflatedView;
    }

    private void setFields() {
        MenuTypeUserDaoI menuTypeUserDao = new MenuTypeUserFireStoreDao();
        menuTypeUserDao.getMenuType_u(menuTypeId, new OnResultListener<MenuType>() {
            @Override
            public void onCallback(MenuType menuType) {
                if (menuType != null) {
                    setMenuTypeName(menuType);
                    setMenuTypeDescription(menuType);
                }

            }
        });
    }

    private void setMenuTypeName(MenuType menuType) {
        TextView menuTypeName = inflatedView.findViewById(R.id.menuCardExpandableViewMenuTypeName);
        menuTypeName.setText(TextUtils.getUnderlinesString(menuType.getName()));
        StyleUtil.setStyleForTextView(menuTypeName, styleController.getMenuNameStyle());
    }

    private void setMenuTypeDescription(MenuType menuType) {
        TextView menuTypeDescription = inflatedView.findViewById(R.id.menuCardExpandableMenuTypeDescription);
        menuTypeDescription.setText(menuType.getDescription());
        StyleUtil.setStyleForTextView(menuTypeDescription, styleController.getMenuDescStyle());
    }

    @Override
    public void onMenuCardExpandableItemSelectedListener(RestaurantItem item) {
        final MenuCardViewMenuDetailFragment frag = new MenuCardViewMenuDetailFragment();
        frag.setSelectedItem(item);
        frag.setStyleController(styleController);
        final FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (item != null) {
            menuItemUserDao.getTagsForItem_u(item.getId() + "", new OnResultListener<List<String>>() {
                @Override
                public void onCallback(List<String> tagIds) {
                    final List<Tag> tagList = new ArrayList<>();
                    for (String tagId : tagIds) {
                        tagUserDao.getTag_u(tagId, new OnResultListener<Tag>() {
                            @Override
                            public void onCallback(Tag tag) {
                                tagList.add(tag);
                            }
                        });
                    }
                    frag.setTagList(tagList);
                    ft.replace(R.id.expandable_menu_detail_container, frag);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            });
        }
    }

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

}
