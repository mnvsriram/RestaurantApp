
package app.resta.com.restaurantapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.user.menuGroup.MenuGroupUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuGroup.MenuGroupUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.TextUtils;

public class MenuCardViewGroupListWithItemIconsFragment extends Fragment implements MenuCardViewGroupListFragment.OnMenuCardGroupListWithIconsGroupClickListener {
    private String menuTypeId;
    private View inflatedView;
    private MenuGroupUserDaoI menuGroupUserDao = new MenuGroupUserFireStoreDao();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_group_list_with_item_icons, container, false);
        MenuCardViewGroupListFragment frag = new MenuCardViewGroupListFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        frag.setGroupMenuId(menuTypeId);
        frag.setContainer(this);

        ft.add(R.id.groupListWithItemIcons_groupList_container, frag);
        ft.commit();

        setFields();
        return inflatedView;
    }

    private void setFields() {
        MenuTypeUserDaoI menuTypeUserDao = new MenuTypeUserFireStoreDao();
        menuTypeUserDao.getMenuType_u(menuTypeId, new OnResultListener<MenuType>() {
            @Override
            public void onCallback(MenuType menuTypeFromDB) {
                if (menuTypeFromDB != null) {
                    setMenuTypeName(menuTypeFromDB);
                    setMenuTypeDescription(menuTypeFromDB);
                }
            }
        });
    }

    private void setMenuTypeName(MenuType menuType) {
        TextView menuTypeName = (TextView) inflatedView.findViewById(R.id.groupListWithItemIconsMenuTypeName);
        menuTypeName.setText(TextUtils.getUnderlinesString(menuType.getName()));
    }

    private void setMenuTypeDescription(MenuType menuType) {
        TextView menuTypeDescription = (TextView) inflatedView.findViewById(R.id.groupListWithItemIconsMenuTypeDesc);
        menuTypeDescription.setText(menuType.getDescription());
    }

    @Override
    public void onMenuCardGroupListWithIconsGroupClickListener(final RestaurantItem item) {
        final MenuCardViewItemIconListFragment frag = new MenuCardViewItemIconListFragment();
        frag.setContainer(this.getActivity());

        menuGroupUserDao.getItemsInGroup_u(item.getId(), new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> childItems) {
                item.setChildItems(childItems);
                frag.setSelectedGroup(item);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                if (item != null) {
                    ft.replace(R.id.groupListWithItemIcons_item_icons_container, frag);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        });


    }

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

}
