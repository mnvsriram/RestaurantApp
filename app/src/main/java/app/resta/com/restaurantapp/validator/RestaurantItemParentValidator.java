package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 18/06/2017.
 */
public class RestaurantItemParentValidator extends ItemValidator {
    private RestaurantItem item;
    private boolean goAhead = true;
    private MenuItemDao menuItemDao = new MenuItemDao();

    public RestaurantItemParentValidator(Activity activity, RestaurantItem item) {
        super(activity);
        this.item = item;
    }


    public boolean validate() {
        goAhead = true;
        String errorText = "";
        errorText = validateGroupName();
        errorText += validateGroupMenu();

        TextView errorBlock = (TextView) activity.findViewById(R.id.groupNameValidationBlock);
        if (errorText.length() > 0) {
            goAhead = false;
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.VISIBLE);
        } else {
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.GONE);
        }
        return goAhead;
    }


    private String validateGroupName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.groupNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.editGroupName);

        if (item.getName() == null || item.getName().trim().length() == 0) {
            nameError = "Please enter a name for the group.";
        } else {

            List<String> parents = menuItemDao.getParentNamesForSelectedMenuType();
            for (String parent : parents) {
                if (parent.equalsIgnoreCase(item.getName())) {
                    nameError = "A group already exists with this name. Please select a different name.";
                }
            }

        }

        if (nameError.length() > 0) {
            nameError = "\n" + nameError;
            nameLabel.setTextColor(errorColor);
            userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            nameLabel.setTextColor(greenColor);
            userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        }
        return nameError;
    }

    private String validateGroupMenu() {
        String groupMenuError = "";
        TextView label = (TextView) activity.findViewById(R.id.groupMenuLabel);
        Spinner spinner = (Spinner) activity.findViewById(R.id.groupMenuSpinner);

        if (item.getMenuTypeId() <= 0) {
            groupMenuError = "Please select a valid Menu Group.";
        }
        if (groupMenuError.length() > 0) {
            groupMenuError = "\n" + groupMenuError;
            label.setTextColor(errorColor);
            spinner.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            label.setTextColor(greenColor);
            spinner.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        }
        return groupMenuError;
    }


}
