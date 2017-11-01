package app.resta.com.restaurantapp.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuButtonActionDao;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
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

    }

    private Fragment getLayoutFragment(int layoutId) {
        Fragment fragment = null;
        MenuCardLayoutEnum layoutEnum = MenuCardLayoutEnum.of(layoutId);
        if (layoutEnum == MenuCardLayoutEnum.Expandable_Menu_List) {
            fragment = new MenuDetailFragment();
        }
        return fragment;
    }

    private void addFragments(List<MenuCardAction> actionList) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (MenuCardAction menuCardAction : actionList) {
            ft = getFragmentManager().beginTransaction();
            Fragment f = getLayoutFragment(menuCardAction.getLayoutId());
            ft.add(R.id.multipleFragmentContainer, f).commit();
        }
    }

}
