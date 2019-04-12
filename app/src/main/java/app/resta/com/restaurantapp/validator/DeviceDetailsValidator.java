package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.MyApplication;

public class DeviceDetailsValidator {
    private Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;
    private boolean goAhead = true;

    public DeviceDetailsValidator(Activity activity) {
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
    }


    public boolean validate(DeviceInfo deviceInfo) {
//        boolean validId = validateRestaurantId(deviceInfo);
//        boolean validateAddress = validateAddress(deviceInfo);
  //      boolean validateRestaurantName = validateRestaurantName(deviceInfo);
        boolean validateUsername = validateUsername(deviceInfo);
    //    boolean validateVerificationCode = true;
//        if (registration) {
      //      validateVerificationCode = validateVerificationCode(deviceInfo);
//        }
        if (validateUsername ) {
            return true;
        } else {
            return false;
        }
    }

//
//    private boolean validateAddress(DeviceInfo deviceInfo) {
//        boolean isValid = false;
//        String addressError = "";
//        TextView addressLabel = (TextView) activity.findViewById(R.id.addressLineOne);
//        EditText addressText = (EditText) activity.findViewById(R.id.addressLineOneText);
//
//        if (deviceInfo.getAddress() == null || deviceInfo.getAddress().trim().length() == 0) {
//            addressError = "Please enter the address of your restaurant.";
//        } else if (deviceInfo.getAddress().length() > 100) {
//            addressError = "Address cannot be more than 100 characters in length. Please enter a shorter address.";
//        }
//
//        TextView errorBlock = (TextView) activity.findViewById(R.id.addressErrorBlock);
//
//        if (addressError.length() > 0) {
//            addressLabel.setTextColor(errorColor);
//            addressText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(addressError);
//            errorBlock.setVisibility(View.VISIBLE);
//
//        } else {
//            addressLabel.setTextColor(greenColor);
//            addressText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(addressError);
//            errorBlock.setVisibility(View.GONE);
//            isValid = true;
//
//        }
//        return isValid;
//    }

//
//    private boolean validateRestaurantId(DeviceInfo deviceInfo) {
//        boolean isValid = false;
//        String restIdError = "";
//        TextView restIdLabel = (TextView) activity.findViewById(R.id.restaurantId);
//        EditText restIdText = (EditText) activity.findViewById(R.id.restaurantIdText);
//
//        if (deviceInfo.getRestaurantId() == null || deviceInfo.getRestaurantId().trim().length() == 0) {
//            restIdError = "Please enter the ID of your restaurant.";
//        } else if (deviceInfo.getAddress().length() > 20) {
//            restIdError = "ID cannot be more than 20 characters in length. Please enter a shorter ID.";
//        }
//
//        TextView errorBlock = (TextView) activity.findViewById(R.id.restaurantIdErrorBlock);
//
//        if (restIdError.length() > 0) {
//            restIdLabel.setTextColor(errorColor);
//            restIdText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(restIdError);
//            errorBlock.setVisibility(View.VISIBLE);
//
//        } else {
//            restIdLabel.setTextColor(greenColor);
//            restIdText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(restIdError);
//            errorBlock.setVisibility(View.GONE);
//            isValid = true;
//
//        }
//        return isValid;
//    }
//
//
    private boolean validateUsername(DeviceInfo deviceInfo) {
        boolean isValid = false;
        String usernameError = "";
        TextView usernameLabel = (TextView) activity.findViewById(R.id.username);
        EditText usernameText = (EditText) activity.findViewById(R.id.usernameText);

        if (deviceInfo.getUsername() == null || deviceInfo.getUsername().trim().length() == 0) {
            usernameError = "Please enter the username.";
        } else if (deviceInfo.getUsername().length() > 20) {
            usernameError = "Username cannot be more than 20 characters in length. Please enter a shorter username.";
        }

        TextView errorBlock = (TextView) activity.findViewById(R.id.usernameErrorBlock);

        if (usernameError.length() > 0) {
            usernameLabel.setTextColor(errorColor);
            usernameText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
            errorBlock.setText(usernameError);
            errorBlock.setVisibility(View.VISIBLE);

        } else {
            usernameLabel.setTextColor(greenColor);
            usernameText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
            errorBlock.setText(usernameError);
            errorBlock.setVisibility(View.GONE);
            isValid = true;

        }
        return isValid;
    }


//    private boolean validateRestaurantName(DeviceInfo deviceInfo) {
//        boolean isValid = false;
//        String restaurantError = "";
//        TextView restaurantLabel = (TextView) activity.findViewById(R.id.restaurant);
//        EditText restaurantText = (EditText) activity.findViewById(R.id.restaurantText);
//
//        if (deviceInfo.getRestaurantName() == null || deviceInfo.getRestaurantName().trim().length() == 0) {
//            restaurantError = "Please enter the Restaurant name.";
//        } else if (deviceInfo.getRestaurantName().length() > 20) {
//            restaurantError = "Restaurant name cannot be more than 20 characters in length. Please enter a shorter name.";
//        }
//
//        TextView errorBlock = (TextView) activity.findViewById(R.id.restaurantNameErrorBlock);
//
//        if (restaurantError.length() > 0) {
//            restaurantLabel.setTextColor(errorColor);
//            restaurantText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(restaurantError);
//            errorBlock.setVisibility(View.VISIBLE);
//
//        } else {
//            restaurantLabel.setTextColor(greenColor);
//            restaurantText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(restaurantError);
//            errorBlock.setVisibility(View.GONE);
//            isValid = true;
//
//        }
//        return isValid;
//    }
//
//    private boolean validateVerificationCode(DeviceInfo deviceInfo) {
//        boolean isValid = false;
//        String verificationCodeError = "";
//        TextView verificationCodeLabel = (TextView) activity.findViewById(R.id.verificationCode);
//        EditText verificationCodeText = (EditText) activity.findViewById(R.id.verificationCodeText);
//
//        if (deviceInfo.getVerificationCode() == null || deviceInfo.getVerificationCode().trim().length() == 0) {
//            verificationCodeError = "Please enter the verificationCode name.";
//        } else if (!deviceInfo.getVerificationCode().equals("1")) {
//            verificationCodeError = "Invalid verification code. Please contact support at mnvsriram@gmail.com to get a verification code.";
//        }
//
//        TextView errorBlock = (TextView) activity.findViewById(R.id.verificationCodeErrorBlock);
//
//        if (verificationCodeError.length() > 0) {
//            verificationCodeLabel.setTextColor(errorColor);
//            verificationCodeText.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(verificationCodeError);
//            errorBlock.setVisibility(View.VISIBLE);
//
//        } else {
//            verificationCodeLabel.setTextColor(greenColor);
//            verificationCodeText.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
//            errorBlock.setText(verificationCodeError);
//            errorBlock.setVisibility(View.GONE);
//            isValid = true;
//
//        }
//        return isValid;
//    }

}
