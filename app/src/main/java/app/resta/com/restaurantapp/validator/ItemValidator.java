package app.resta.com.restaurantapp.validator;

import android.app.Activity;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 18/06/2017.
 */
public class ItemValidator {
    protected Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;
    protected boolean goAhead = true;

    public ItemValidator(Activity activity) {
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
    }


}
