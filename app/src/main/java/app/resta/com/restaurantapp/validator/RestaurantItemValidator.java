package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.StringTokenizer;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

import static java.lang.Long.parseLong;

/**
 * Created by Sriram on 18/06/2017.
 */
public class RestaurantItemValidator extends ItemValidator {
    private ScrollView scrollView;
    private RestaurantItem item;

    public RestaurantItemValidator(Activity activity, RestaurantItem item) {
        super(activity);
        this.item = item;
        scrollView = activity.findViewById(R.id.editPageScrollView);
    }


    public boolean validate() {
        goAhead = true;
        validateNamePriceParentStatus();
        validateDescription();
        return goAhead;
    }

    private String validateDescription() {
        String descError = "";
        TextView descLabel = activity.findViewById(R.id.descriptionLabel);
        EditText descInput = activity.findViewById(R.id.editItemDescription);
        TextView descErrorBlock = activity.findViewById(R.id.descriptionValidationBlock);
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

    private void validateNamePriceParentStatus() {
        String errorText = validatePrice();
        errorText += validateName();


        TextView errorBlock = activity.findViewById(R.id.namePriceParentStatusValidationBlock);
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
        TextView nameLabel = activity.findViewById(R.id.nameLabel);
        EditText userInput = activity.findViewById(R.id.editItemName);

        if (item.getName() == null || item.getName().trim().length() == 0) {
            nameError = "Please enter a name for the item.";
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

    private void scrollToView(final View view, final boolean top) {

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
        EditText priceText = activity.findViewById(R.id.editItemPrice);
        TextView priceLabel = activity.findViewById(R.id.priceLabel);

        if (item.getPrice() == null || item.getPrice().trim().length() == 0) {
            priceErrorText += "Please enter the price of the item.";
        } else {
            String price = item.getPrice();
            StringTokenizer tokenizer = new StringTokenizer(price, ".");

            if (tokenizer.countTokens() > 2) {
                priceErrorText += "Please enter a valid price of the item.";
            } else {
                try {
                    parseLong(tokenizer.nextToken());
                    if (tokenizer.hasMoreTokens()) {
                        parseLong(tokenizer.nextToken());
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
