package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.StringTokenizer;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 18/06/2017.
 */
public class RestaurantItemValidator {
    private RestaurantItem item;
    private Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;
    private ScrollView scrollView;
    private boolean goAhead = true;

    public RestaurantItemValidator(Activity activity, RestaurantItem item) {
        this.item = item;
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
        scrollView = (ScrollView) activity.findViewById(R.id.editPageScrollView);
    }


    public boolean validate() {
        goAhead = true;
        validateNamePriceParentStatus();
        validateDescription();

        return goAhead;
    }

    private String validateDescription() {
        String descError = "";
        TextView descLabel = (TextView) activity.findViewById(R.id.descriptionLabel);
        EditText descInput = (EditText) activity.findViewById(R.id.editItemDescription);
        TextView descErrorBlock = (TextView) activity.findViewById(R.id.descriptionValidationBlock);
        String description = descInput.getText().toString();
        if (description != null && description.trim().length() > 300) {
            goAhead = false;
            descError = "\n The description is too long(" + description.length() + "). The maximum length for the description is 300";

            descErrorBlock.setText(descError);
            descErrorBlock.setVisibility(View.VISIBLE);
            descLabel.setTextColor(errorColor);
            descInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            scrollToView(descInput, true);

        } else {
            descLabel.setTextColor(greenColor);
            descInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);

            descErrorBlock.setText("");
            descErrorBlock.setVisibility(View.GONE);
        }
        return descError;
    }


    public boolean validateGroup() {
        goAhead = true;
        String errorText = "";
        errorText = validateGroupName();

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


    private void validateNamePriceParentStatus() {
        String errorText = "";
        errorText = validateParent();
        if (errorText.length() == 0) {
            errorText += validatePrice();
            errorText += validateName();
        }


        TextView errorBlock = (TextView) activity.findViewById(R.id.namePriceParentStatusValidationBlock);
        if (errorText.length() > 0) {
            goAhead = false;
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.VISIBLE);
        } else {
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.GONE);
        }
    }

    private String validateParent() {
        String parentError = "";
        TextView parentLabel = (TextView) activity.findViewById(R.id.parentLabel);
        Spinner spinner = (Spinner) activity.findViewById(R.id.spinner);

        if (item.getParentId() <= 0) {
            parentError = "Please select a valid parent.";
        }
        if (parentError.length() > 0) {
            parentError = "\n" + parentError;

            parentLabel.setTextColor(errorColor);
            spinner.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            scrollToView(spinner, true);

        } else {
            parentLabel.setTextColor(greenColor);
            spinner.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        }
        return parentError;
    }

    private String validateName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.nameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.editItemName);

        if (item.getName() == null || item.getName().trim().length() == 0) {
            nameError = "Please enter a name for the item.";
        } else {
            RestaurantItem foundItem = MenuItemDao.getItem(item.getName(), item.getParentId(), item.getId());
            if (foundItem != null) {
                nameError = "A Item already exists with this name under the same type. Please select a different name or type.";
            }
        }

        if (nameError.length() > 0) {
            nameError = "\n" + nameError;

            nameLabel.setTextColor(errorColor);
            userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            scrollToView(userInput, true);

        } else {
            nameLabel.setTextColor(greenColor);
            userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);

        }
        return nameError;
    }


    private String validateGroupName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.groupNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.editGroupName);

        if (item.getName() == null || item.getName().trim().length() == 0) {
            nameError = "Please enter a name for the group.";
        } else {
            RestaurantItem foundItem = MenuItemDao.getItem(item.getName(), -1, item.getId());
            if (foundItem != null) {
                nameError = "A group already exists with this name. Please select a different name.";
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

    public void scrollToView(final View view, final boolean top) {

        // View needs a focus
        view.requestFocus();

        // Determine if scroll needs to happen
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (!view.getLocalVisibleRect(scrollBounds)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (top) {
                        scrollView.smoothScrollTo(0, 0);
                    } else {
                        scrollView.smoothScrollTo(0, view.getBottom());
                    }

                }
            });
        }

    }

    private String validatePrice() {
        String priceErrorText = "";
        EditText priceText = (EditText) activity.findViewById(R.id.editItemPrice);
        TextView priceLabel = (TextView) activity.findViewById(R.id.priceLabel);

        if (item.getPrice() == null || item.getPrice().trim().length() == 0) {
            priceErrorText += "Please enter the price of the item.";
        } else {
            String price = item.getPrice();
            StringTokenizer tokenizer = new StringTokenizer(price, ":");

            if (tokenizer.countTokens() > 2) {
                priceErrorText += "Please enter a valid price of the item.";
            } else {
                try {
                    Long.parseLong(tokenizer.nextToken());
                    if (tokenizer.hasMoreTokens()) {
                        Long.parseLong(tokenizer.nextToken());
                    }

                } catch (NumberFormatException nfe) {
                    priceErrorText += "Please enter a valid price of the item.";
                }
            }
        }


        if (priceErrorText.length() > 0) {
            priceText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            priceLabel.setTextColor(errorColor);
            scrollToView(priceText, true);
        } else {
            priceLabel.setTextColor(greenColor);
            priceText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        }
        return priceErrorText;
    }

}
