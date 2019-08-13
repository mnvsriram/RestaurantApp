package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.AddItemToGroupActivity;
import app.resta.com.restaurantapp.activity.AdminLauncherActivity;
import app.resta.com.restaurantapp.activity.AdminSettingsActivity;
import app.resta.com.restaurantapp.activity.HorizontalMenuActivity;
import app.resta.com.restaurantapp.activity.IngredientsActivity;
import app.resta.com.restaurantapp.activity.ItemEditActivity;
import app.resta.com.restaurantapp.activity.ItemReviewDetailActivity;
import app.resta.com.restaurantapp.activity.LowTopRatedItemsActivity;
import app.resta.com.restaurantapp.activity.MenuButtonEditActivity;
import app.resta.com.restaurantapp.activity.MenuCardEditActivity;
import app.resta.com.restaurantapp.activity.MenuCardSettingsActivity;
import app.resta.com.restaurantapp.activity.MenuTypeAddActivity;
import app.resta.com.restaurantapp.activity.MenuTypeSettingsActivity;
import app.resta.com.restaurantapp.activity.MultipleMenuCardDataActivity;
import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.activity.OrderActivity;
import app.resta.com.restaurantapp.activity.OrderSummaryViewActivity;
import app.resta.com.restaurantapp.activity.PerformanceGraphsActivity;
import app.resta.com.restaurantapp.activity.RefreshDataActivity;
import app.resta.com.restaurantapp.activity.ReviewMainActivity;
import app.resta.com.restaurantapp.activity.ReviewerLauncherActivity;
import app.resta.com.restaurantapp.activity.TagsActivity;
import app.resta.com.restaurantapp.activity.TopLevelActivity;
import app.resta.com.restaurantapp.activity.UpdateClusterSettingsActivity;
import app.resta.com.restaurantapp.activity.UpdateDeviceActivity;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

/**
 * Created by Sriram on 18/06/2017.
 */
public class AuthenticationController {
    private Activity activity;
    private LoginController loginController;

    public AuthenticationController(Activity activity) {
        this.activity = activity;
        loginController = LoginController.getInstance();
    }


    public void login(final MenuItem item) {
        LayoutInflater li = LayoutInflater.from(activity);
        final View promptsView = li.inflate(R.layout.admin_login_dialog, null);
        if (loginController.isAdminLoggedIn() || loginController.isReviewAdminLoggedIn()) {
            loginController.logout();
            if (item != null) {
                item.setIcon(R.drawable.login);
            }
            goToHomePage();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText password = (EditText) promptsView.findViewById(R.id.password);
                                String passwordText = password.getText().toString();

                                RadioGroup whoIsLoggingIn = promptsView.findViewById(R.id.whoIsLoggingIn);
                                int selectedId = whoIsLoggingIn.getCheckedRadioButtonId();

                                RadioButton selectedButton = promptsView.findViewById(selectedId);
                                if ("Admin".equalsIgnoreCase(selectedButton.getText().toString())) {
                                    loginController.adminLogin(passwordText, AuthenticationController.this);
                                } else {
                                    loginController.waiterLogin(passwordText, AuthenticationController.this);
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void goToMenuPage(Map<String, Object> params) {
        Intent intent = null;
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        } else {
            String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
            if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
                intent = new Intent(MyApplication.getAppContext(), NarrowMenuActivity.class);
            } else {
                intent = new Intent(MyApplication.getAppContext(), HorizontalMenuActivity.class);
            }
        }
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToReviewerMenuPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToItemReviewDetailsPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), ItemReviewDetailActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToPerformanceGraphsPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), PerformanceGraphsActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToReviewerMenuPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), OrderActivity.class);
        intent.putExtra("modifiedItemId", -1l);
        activity.startActivity(intent);
    }


    public void goToAddItemToGroupActivityPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), AddItemToGroupActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToMultipleMenuCardPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MultipleMenuCardDataActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToHomePage() {
        if (LoginController.getInstance().isAdminLoggedIn()) {
            goToAdminLaunchPage();
        } else {
            goToTopLevelPage();
        }
    }


    public void goBackFromMenuPage(Map<String, Object> params, String groupMenuId) {
        if (LoginController.getInstance().isAdminLoggedIn()) {
            if (groupMenuId != null) {
                goToMenuTypeAddPage(params);
            } else {
                goToSettingsPage();
            }
        } else {
            goToTopLevelPage();
        }
    }


    private void goToTopLevelPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), TopLevelActivity.class);
        activity.startActivity(intent);
    }

    public void goToSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), AdminSettingsActivity.class);
        activity.startActivity(intent);
    }


    public void goToAdminLaunchPage() {
        Intent intent = new Intent(MyApplication.getAppContext(), AdminLauncherActivity.class);
        activity.startActivity(intent);
    }


    public void goToReviewerLaunchPage() {
        Intent intent = new Intent(MyApplication.getAppContext(), ReviewerLauncherActivity.class);
        activity.startActivity(intent);
    }

    public void goToTagsSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), TagsActivity.class);
        activity.startActivity(intent);
    }


    public void goToMenuCardSettingsPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MenuCardSettingsActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToDeviceUpdatePage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), UpdateDeviceActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToRefreshDataPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), RefreshDataActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToMenuClusterNetworkSettingsPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), UpdateClusterSettingsActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToMenuEditPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MenuCardEditActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToMenuCardButtonEditPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MenuButtonEditActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToMenuTypeSettingsPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MenuTypeSettingsActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }


    public void goToMenuTypeAddPage(Map<String, Object> params) {
        Intent intent = new Intent(MyApplication.getAppContext(), MenuTypeAddActivity.class);
        insertIntentParams(intent, params);
        activity.startActivity(intent);
    }

    public void goToOrderSummaryPage() {
        Intent intent = new Intent(MyApplication.getAppContext(), OrderSummaryViewActivity.class);
        activity.startActivity(intent);
    }

    public void goToReviewsPage(Map<String, Object> intentParameters) {
        Intent intent = new Intent(MyApplication.getAppContext(), ReviewMainActivity.class);
        insertIntentParams(intent, intentParameters);
        activity.startActivity(intent);
    }


    public void goToLowTopRatedItemsPage(Map<String, Object> intentParameters) {
        Intent intent = new Intent(MyApplication.getAppContext(), LowTopRatedItemsActivity.class);
        insertIntentParams(intent, intentParameters);
        activity.startActivity(intent);
    }


    private void insertIntentParams(Intent intent, Map<String, Object> intentParameters) {
        if (intentParameters != null) {
            for (String key : intentParameters.keySet()) {
                Object value = intentParameters.get(key);
                if (value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                } else if (value instanceof Long) {
                    intent.putExtra(key, (Long) value);
                } else if (value instanceof ArrayList) {
                    intent.putExtra(key, (ArrayList) value);
                } else if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof RestaurantItem) {
                    intent.putExtra(key, (RestaurantItem) value);
                } else if (value instanceof MenuType) {
                    intent.putExtra(key, (MenuType) value);
                } else if (value instanceof MenuCardButtonEnum) {
                    intent.putExtra(key, (MenuCardButtonEnum) value);
                } else if (value instanceof DeviceInfo) {
                    intent.putExtra(key, (DeviceInfo) value);
                }
            }
        }
    }

    public void goToOrderSummaryPage(Map<String, Object> intentParameters) {
        Intent intent = new Intent(MyApplication.getAppContext(), OrderSummaryViewActivity.class);
        insertIntentParams(intent, intentParameters);
        activity.startActivity(intent);
    }

    public void goToItemEditPage(Map<String, Object> intentParameters) {
        Intent intent = new Intent(MyApplication.getAppContext(), ItemEditActivity.class);
        if (!intent.hasExtra("item_obj")) {
            intentParameters.put("item_obj", new RestaurantItem());
        }
        insertIntentParams(intent, intentParameters);
        activity.startActivity(intent);
    }

    public void goToIngredientsSettingsPage() {
        Intent intent = null;
        intent = new Intent(MyApplication.getAppContext(), IngredientsActivity.class);
        activity.startActivity(intent);
    }
}
