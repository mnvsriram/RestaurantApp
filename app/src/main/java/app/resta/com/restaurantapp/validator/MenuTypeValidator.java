package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.StringTokenizer;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.MenuType;

/**
 * Created by Sriram on 18/06/2017.
 */
public class MenuTypeValidator extends ItemValidator {
    private MenuType menuType;
    private boolean goAhead = true;

    public MenuTypeValidator(Activity activity, MenuType menuType) {
        super(activity);
        this.menuType = menuType;
    }


    public boolean validate(List<MenuType> menuTypeList) {
        goAhead = true;
        String errorText = "";
        errorText = validateMenuName(menuTypeList);
        errorText += validatePrice();

        TextView errorBlock = activity.findViewById(R.id.namePriceMenuTypeValidationBlock);
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


    private String validateMenuName(List<MenuType> menuTypeList) {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.menuTypeAddNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.menuTypeAddName);

        if (menuType.getName() == null || menuType.getName().trim().length() == 0) {
            nameError = "Please enter a menu name.";
        } else {
            if (menuTypeList != null) {
                for (MenuType mt : menuTypeList) {
                    if (mt.getName().equalsIgnoreCase(menuType.getName().trim())) {
                        if (!mt.getId().equals(menuType.getId())) {
                            nameError = "Menu with the name" + menuType.getName() + " already exists.Please choose a different name.";
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

    private String validatePrice() {
        String priceErrorText = "";
        EditText priceText = (EditText) activity.findViewById(R.id.menuTypeAddPrice);
        TextView priceLabel = (TextView) activity.findViewById(R.id.menuTypeAddPriceLabel);

        if (!menuType.isShowPriceOfChildren() && (menuType.getPrice() == null || menuType.getPrice().trim().length() == 0)) {
            priceErrorText += "Please enter the price of the item.";
        }

        if (menuType.getPrice() != null && menuType.getPrice().trim().length() > 0) {
            String price = menuType.getPrice();
            StringTokenizer tokenizer = new StringTokenizer(price, ".");

            if (tokenizer.countTokens() > 2) {
                priceErrorText += "Please enter a valid price for the menu.";
            } else {
                try {
                    Long.parseLong(tokenizer.nextToken());
                    if (tokenizer.hasMoreTokens()) {
                        Long.parseLong(tokenizer.nextToken());
                    }

                } catch (NumberFormatException nfe) {
                    priceErrorText += "Please enter a valid price for the menu.";
                }
            }
        }

        if (priceErrorText.length() > 0) {
            priceText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            priceLabel.setTextColor(errorColor);
        } else {
            priceLabel.setTextColor(greenColor);
            priceText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        }
        return priceErrorText;
    }

}
