package app.resta.com.restaurantapp.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ScrollView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuButtonActionDao;
import app.resta.com.restaurantapp.fragment.ExpandableMenuWithDetailsFragment;
import app.resta.com.restaurantapp.fragment.MenuCardItemNameWithDescriptionFragment;
import app.resta.com.restaurantapp.fragment.MenuCardItemWithoutDescriptionMultiColumnFragment;
import app.resta.com.restaurantapp.fragment.MenuCardViewGroupListWithItemIconsFragment;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardLayoutEnum;

public class MultipleMenuCardDataActivity extends BaseActivity {

    private MenuButtonActionDao menuButtonActionDao = new MenuButtonActionDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_menu_card_data);
        setToolbar();
        long buttonId = getIntent().getLongExtra("menuCardView_buttonId", 0l);
        addFragments(menuButtonActionDao.getActions(buttonId));
        scrollToView();
    }

    private Fragment getLayoutFragment(MenuCardAction menuCardAction) {
        Fragment fragment = null;
        int layoutId = menuCardAction.getLayoutId();
        long menuTypeId = menuCardAction.getMenuTypeId();
        MenuCardLayoutEnum layoutEnum = MenuCardLayoutEnum.of(layoutId);
        if (layoutEnum == MenuCardLayoutEnum.Expandable_Menu_With_Details) {
            ExpandableMenuWithDetailsFragment fragment1 = new ExpandableMenuWithDetailsFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Group_list_and_Items_With_Image_Icons) {
            MenuCardViewGroupListWithItemIconsFragment fragment1 = new MenuCardViewGroupListWithItemIconsFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Name_With_Description) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(true);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_single_row) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(false);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_two_rows) {
            MenuCardItemWithoutDescriptionMultiColumnFragment fragment1 = new MenuCardItemWithoutDescriptionMultiColumnFragment();
            fragment1.setMenuTypeId(menuTypeId);

            fragment = fragment1;
        }
        return fragment;
    }

    private void addFragments(List<MenuCardAction> actionList) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (MenuCardAction menuCardAction : actionList) {
            ft = getFragmentManager().beginTransaction();
            Fragment f = getLayoutFragment(menuCardAction);
            ft.add(R.id.multipleFragmentContainer, f, menuCardAction.getPosition() + " Tag").commit();
        }
    }

    public void scrollToView() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.multipleMenuCardsScroll);
        scrollView.smoothScrollTo(0, 0);
    }

}
