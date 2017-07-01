package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.util.MyApplication;

public class IngredientsValidator {
    private Ingredient Ingredient;
    private Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;
    private boolean goAhead = true;
    private IngredientDao ingredientDao;

    public IngredientsValidator(Activity activity, Ingredient Ingredient) {
        this.Ingredient = Ingredient;
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
        this.ingredientDao = new IngredientDao();
    }


    public boolean validate() {
        goAhead = true;
        validateIngredientRefDataAdd();
        return goAhead;
    }

    private void validateIngredientRefDataAdd() {
        String errorText = "";
        errorText = validateName();
        TextView errorBlock = (TextView) activity.findViewById(R.id.ingredientNameValidationBlock);
        if (errorText.length() > 0) {
            goAhead = false;
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.VISIBLE);
        } else {
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.GONE);
        }
    }

    private String validateName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.editIngredientsNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.ingredientNameSettings);

        if (Ingredient.getName() == null || Ingredient.getName().trim().length() == 0) {
            nameError = "Please enter a name for the Ingredient.";
        } else {
            //1==1 write the below method in Ingredientsdao and see if an item exists with the same name...if exists, then throw and error..
            Ingredient foundItem = ingredientDao.getIngredientRefData(Ingredient.getName(), -1);
            if (foundItem != null) {
                nameError = "A Ingredient already exists with this name. Please choose a different name.";
            }
        }

        if (nameError.length() > 0) {
            nameLabel.setTextColor(errorColor);
            userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            nameLabel.setTextColor(greenColor);
            userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);

        }
        return nameError;
    }
}
