package app.resta.com.restaurantapp.db.dao.user.menuCard;

import com.google.firebase.firestore.Source;

import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;

public class MenuCardUserFireStoreDao extends MenuCardAdminFireStoreDao implements MenuCardUserDaoI {
    private static final String TAG = "MenuCardAdminDao";

    public void getCardWithButtonsAndActions_u(final String cardId, final OnResultListener<MenuCard> listener) {
        getCardWithButtonsAndActions(cardId, Source.CACHE, listener);
    }

    @Override
    public void getDefaultCardWithButtonsAndActions_u(final OnResultListener<MenuCard> listener) {
        getDefaultCardWithButtonsAndActions(Source.CACHE, listener);
    }

}
