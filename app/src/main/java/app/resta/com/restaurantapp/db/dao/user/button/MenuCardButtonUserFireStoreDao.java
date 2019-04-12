package app.resta.com.restaurantapp.db.dao.user.button;

import android.util.Log;

import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.dao.admin.button.MenuCardButtonAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCardAction;

public class MenuCardButtonUserFireStoreDao extends MenuCardButtonAdminFireStoreDao implements MenuCardButtonUserDaoI {
    private static final String TAG = "MenuButtonUserDao";

    @Override
    public void getActions_u(final String menuCardId, final String buttonId, final OnResultListener<List<MenuCardAction>> listener) {
        Log.i(TAG, "Getting list of actions for the button" + buttonId + " in the card " + menuCardId);
        getActions(menuCardId, buttonId, Source.CACHE, listener);
    }
}
