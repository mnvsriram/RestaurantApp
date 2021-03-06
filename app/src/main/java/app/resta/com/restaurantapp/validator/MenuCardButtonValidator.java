package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 18/06/2017.
 */
public class MenuCardButtonValidator {
    private MenuCardButton menuCardButton;
    private boolean goAhead = true;
    protected Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;

    public MenuCardButtonValidator(Activity activity, MenuCardButton menuCardButton) {
        this.menuCardButton = menuCardButton;
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
    }


    public boolean validate() {
        goAhead = true;
        validateButtonName();
        validateLocation();
        return goAhead;
    }

    private void validateButtonName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.menuButtonNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.menuButtonName);

        if (menuCardButton.getName() == null || menuCardButton.getName().trim().length() == 0) {
            nameError = "Please enter a menu name.";
        }
        TextView errorBlock = (TextView) activity.findViewById(R.id.buttonNameValidationBlock);

        if (nameError.length() > 0) {
            nameError = "\n" + nameError;
            nameLabel.setTextColor(errorColor);
            userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);

            goAhead = false;
            errorBlock.setText(nameError);
            errorBlock.setVisibility(View.VISIBLE);

        } else {
            nameLabel.setTextColor(greenColor);
            userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
            errorBlock.setVisibility(View.GONE);
        }
    }


    private void validateLocation() {
        String error = "";
        TextView parentLabel = (TextView) activity.findViewById(R.id.menuButtonLocationLabel);
        Spinner spinner = (Spinner) activity.findViewById(R.id.menuButtonLocationSpinner);

        if (menuCardButton.getLocation() == null) {
            error = "Please select a valid Location.";
        }
        TextView errorBlock = (TextView) activity.findViewById(R.id.locationValidationBlock);

        if (error.length() > 0) {
            error = "\n" + error;

            parentLabel.setTextColor(errorColor);
            spinner.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);

            goAhead = false;
            errorBlock.setText(error);
            errorBlock.setVisibility(View.VISIBLE);

        } else {
            parentLabel.setTextColor(greenColor);
            spinner.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
            errorBlock.setVisibility(View.GONE);
        }
    }


}
