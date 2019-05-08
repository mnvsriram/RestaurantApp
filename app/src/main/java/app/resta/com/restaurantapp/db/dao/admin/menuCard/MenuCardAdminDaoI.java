package app.resta.com.restaurantapp.db.dao.admin.menuCard;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuCardAdminDaoI {
    void insertOrUpdateCard(final MenuCard card, final OnResultListener<MenuCard> listener);

    void getCard(String cardId, final OnResultListener<MenuCard> listener);

    void getCardWithButtonsAndActions(String cardId, final OnResultListener<MenuCard> listener);

    void getDefaultCard(final OnResultListener<MenuCard> listener);

    void getButtonInCard(final String menuCardId, final MenuCardButtonEnum buttonType, final OnResultListener<MenuCardButton> listener);

    void getButtonsInCard(final String menuCardId, final OnResultListener<List<MenuCardButton>> listener);

//    void updateImageUrl(final MenuCard menuCard, String imageNameKey, String imageStorageUrl);

    void getDefaultCardWithButtonsAndActions(final OnResultListener<MenuCard> listener);
}
