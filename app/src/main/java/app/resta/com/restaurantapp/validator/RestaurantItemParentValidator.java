package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 18/06/2017.
 */
public class RestaurantItemParentValidator extends ItemValidator {
    private RestaurantItem item;

    public RestaurantItemParentValidator(Activity activity, RestaurantItem item) {
        super(activity);
        this.item = item;
    }


    public boolean validate(List<RestaurantItem> groupsInMenuType) {
        boolean goAhead = true;
        String errorText = "";
        errorText = validateGroupName(groupsInMenuType);

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


    private String validateGroupName(List<RestaurantItem> groupsInMenuType) {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.groupNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.editGroupName);

        if (item.getName() == null || item.getName().trim().length() == 0) {
            nameError = "Please enter a name for the group.";
        } else {
            if (groupsInMenuType != null) {
                for (RestaurantItem group : groupsInMenuType) {
                    if (group.getName().equalsIgnoreCase(item.getName().trim())) {
                        if (!group.getId().equals(item.getId())) {
                            nameError = "Group with the name " + item.getName() + " already exists.Please choose a different name.";
                        }
                    }
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


}
