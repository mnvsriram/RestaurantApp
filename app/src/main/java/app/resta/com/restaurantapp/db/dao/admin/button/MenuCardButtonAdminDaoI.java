package app.resta.com.restaurantapp.db.dao.admin.button;


import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuCardButtonAdminDaoI {

    void getActions(String menuCardId, String buttonId, Source source, final OnResultListener<List<MenuCardAction>> listener);

    void deleteAndInsertAllActionsForButton(final String menuCardId, final String buttonId, final List<MenuCardAction> actionsToBeCreated, final OnResultListener<List<MenuCardAction>> listener);

    void insertOrUpdateButton(MenuCardButton menuCardButton, String oldLocation, final OnResultListener<MenuCardButton> listener);

    void deleteButton(String id, String cardId, final OnResultListener<String> listener);

}
