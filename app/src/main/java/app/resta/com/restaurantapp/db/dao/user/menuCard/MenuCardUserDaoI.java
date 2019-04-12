package app.resta.com.restaurantapp.db.dao.user.menuCard;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardButton;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuCardUserDaoI {
//    void getCard_u(String cardId, final OnResultListener<MenuCard> listener);

    void getCardWithButtonsAndActions_u(String cardId, final OnResultListener<MenuCard> listener);

//    void getButtonsInCard_u(final String menuCardId, final OnResultListener<List<MenuCardButton>> listener);

    void getDefaultCardWithButtonsAndActions_u(final OnResultListener<MenuCard> listener);
}
