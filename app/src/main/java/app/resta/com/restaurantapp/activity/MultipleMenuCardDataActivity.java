package app.resta.com.restaurantapp.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.db.dao.user.button.MenuCardButtonUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.button.MenuCardButtonUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.dialog.MenuItemDetailDialog;
import app.resta.com.restaurantapp.fragment.ExpandableMenuWithDetailsFragment;
import app.resta.com.restaurantapp.fragment.MenuCardItemNameWithDescriptionFragment;
import app.resta.com.restaurantapp.fragment.MenuCardItemWithoutDescriptionMultiColumnFragment;
import app.resta.com.restaurantapp.fragment.MenuCardViewGroupListWithItemIconsFragment;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardActionStyle;
import app.resta.com.restaurantapp.model.MenuCardLayoutEnum;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.StyleUtil;

public class MultipleMenuCardDataActivity extends BaseActivity {
    private MenuCardButtonUserDaoI buttonUserDao = new MenuCardButtonUserFireStoreDao();
    private StyleController styleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_menu_card_data);
        setToolbar();
        String buttonId = getIntent().getStringExtra("menuCardView_buttonId");
        String cardId = getIntent().getStringExtra("menuCardView_cardId");
        if (getIntent().hasExtra("menuCardView_button_content_styles")) {
            styleController = (StyleController) getIntent().getSerializableExtra("menuCardView_button_content_styles");
        }
        addFragments(cardId, buttonId);
        scrollToView();

        ViewGroup mainLayout = findViewById(R.id.multipleMenuCardDataLayout);
        StyleUtil.setStyle(mainLayout, styleController);
    }

    private Fragment getLayoutFragment(MenuCardAction menuCardAction) {
        Fragment fragment = null;
        long layoutId = menuCardAction.getLayoutId();
        String menuTypeId = menuCardAction.getMenuTypeId();
        MenuCardLayoutEnum layoutEnum = MenuCardLayoutEnum.of(layoutId);
        StyleController enrichedStyleController = enrichStyleController(menuCardAction);
        if (layoutEnum == MenuCardLayoutEnum.Expandable_Menu_With_Details) {
            ExpandableMenuWithDetailsFragment fragment1 = new ExpandableMenuWithDetailsFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Group_list_and_Items_With_Image_Icons) {
            MenuCardViewGroupListWithItemIconsFragment fragment1 = new MenuCardViewGroupListWithItemIconsFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Name_With_Description) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(true);
            fragment1.setDetailsPopup(false);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Name_With_Description_WithDetailPopup) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(true);
            fragment1.setDetailsPopup(true);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_single_row) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(false);
            fragment1.setDetailsPopup(false);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_single_row_WithDetailPopup) {
            MenuCardItemNameWithDescriptionFragment fragment1 = new MenuCardItemNameWithDescriptionFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDescription(false);
            fragment1.setStyleController(enrichedStyleController);
            fragment1.setDetailsPopup(true);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_two_rows) {
            MenuCardItemWithoutDescriptionMultiColumnFragment fragment1 = new MenuCardItemWithoutDescriptionMultiColumnFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDetailsPopup(false);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        } else if (layoutEnum == MenuCardLayoutEnum.Item_Without_description_in_two_rows_WithDetailPopup) {
            MenuCardItemWithoutDescriptionMultiColumnFragment fragment1 = new MenuCardItemWithoutDescriptionMultiColumnFragment();
            fragment1.setMenuTypeId(menuTypeId);
            fragment1.setShowDetailsPopup(true);
            fragment1.setStyleController(enrichedStyleController);
            fragment = fragment1;
        }
        return fragment;
    }

    private StyleController enrichStyleController(MenuCardAction action) {
        StyleController styleControllerEnriched = new StyleController();
        styleControllerEnriched.setAppFontEnum(styleController.getAppFontEnum());
        styleControllerEnriched.setBackgroundColor(styleController.getBackgroundColor());
        styleControllerEnriched.setContentColor(styleController.getContentColor());

        styleControllerEnriched.setItemDescStyle(setDefaults(action.getItemDescStyle()));
        styleControllerEnriched.setItemStyle(setDefaults(action.getItemNameStyle()));
        styleControllerEnriched.setGroupNameStyle(setDefaults(action.getGroupNameStyle()));
        styleControllerEnriched.setMenuNameStyle(setDefaults(action.getMenuNameStyle()));
        styleControllerEnriched.setMenuDescStyle(setDefaults(action.getMenuDescStyle()));
        return styleControllerEnriched;
    }

    private MenuCardActionStyle setDefaults(MenuCardActionStyle menuCardActionStyle) {
        if (menuCardActionStyle.getFont() == null || menuCardActionStyle.getFont().trim().length() == 0) {
            menuCardActionStyle.setFont(styleController.getAppFontEnum().name());
        }

        if (menuCardActionStyle.getFontColor() == null || menuCardActionStyle.getFontColor().trim().length() == 0) {
            menuCardActionStyle.setFontColor(styleController.getContentColor());
        }
        return menuCardActionStyle;
    }

    private void addFragments(String cardId, String buttonId) {
        buttonUserDao.getActions_u(cardId, buttonId, new OnResultListener<List<MenuCardAction>>() {
            @Override
            public void onCallback(List<MenuCardAction> actions) {
                FragmentTransaction ft;
                for (MenuCardAction menuCardAction : actions) {
                    ft = getFragmentManager().beginTransaction();
                    Fragment f = getLayoutFragment(menuCardAction);
                    ft.add(R.id.multipleFragmentContainer, f, menuCardAction.getPosition() + " Tag").commit();
                }
            }
        });
    }

    public void scrollToView() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.multipleMenuCardsScroll);
        scrollView.smoothScrollTo(0, 0);
    }

    public void showDetailsPopup(View view) {
        MenuItemDetailDialog cdd = new MenuItemDetailDialog(this, (RestaurantItem) view.getTag(), styleController);
        cdd.show();
    }
}
