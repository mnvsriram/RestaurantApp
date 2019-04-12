package app.resta.com.restaurantapp.db.dao.user.button;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCardAction;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuCardButtonUserDaoI {

    void getActions_u(String menuCardId, String buttonId, final OnResultListener<List<MenuCardAction>> listener);
}
