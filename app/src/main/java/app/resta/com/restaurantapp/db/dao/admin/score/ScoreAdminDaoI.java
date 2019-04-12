package app.resta.com.restaurantapp.db.dao.admin.score;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 06/03/2019.
 */

public interface ScoreAdminDaoI {
    void modifyScores(List<ReviewForDish> reviewForDishes);

    void modifyScore(ReviewEnum reviewEnum, final int incrementBy, final OnResultListener<String> listener);
}
