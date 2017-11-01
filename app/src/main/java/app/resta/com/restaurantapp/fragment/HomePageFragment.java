package app.resta.com.restaurantapp.fragment;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.MenuCardDao;
import app.resta.com.restaurantapp.model.ColorCodeEnum;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonPropEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonShapeEnum;
import app.resta.com.restaurantapp.model.MenuCardPropEnum;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public class HomePageFragment extends Fragment {
    private long menuCardId;
    private MenuCard menuCard;
    private boolean enableAll = true;
    private MenuCardDao menuCardDao;
    AuthenticationController authenticationController;
    View inflatedView = null;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadIntentParams();
        initialize();
        String layout = menuCard.getProps().get(MenuCardPropEnum.LAYOUT);
        if (layout != null && layout.equalsIgnoreCase("second")) {
            inflatedView = inflater.inflate(R.layout.activity_second, container, false);
        } else {
            inflatedView = inflater.inflate(R.layout.activity_top_level, container, false);
        }
        setStyle();
        setFields();

        return inflatedView;
    }

    private void loadIntentParams() {
        Intent intent = getActivity().getIntent();
        menuCardId = intent.getLongExtra("activity_menucardEdit_cardId", 1l);
    }

    private void setFields() {
        if (menuCard.getId() > 0) {
            setMainLogoImage();
            setMainButtons();
            setOtherButtons();
        }
        setGreetingText();
    }

    private void setMainLogoImage() {
        ImageView logoImage = (ImageView) inflatedView.findViewById(R.id.mainlogo);
        String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
        String imageName = menuCard.getProps().get(MenuCardPropEnum.LOGO_BIG_IMAGE_NAME);
        if (imageName == null) {
            imageName = "noImage";
        }
        String filePath = path + imageName + ".jpeg";
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        Drawable d = new BitmapDrawable(getResources(), bmp);
        logoImage.setBackground(d);

    }

    private void setGreetingText() {
        TextView greetingTextView = (TextView) inflatedView.findViewById(R.id.greetingText);
        if (menuCard.getId() == 0) {
            greetingTextView.setText("Please login and go to Settings to create a Menu card");
        } else {
            String greetingText = menuCard.getProps().get(MenuCardPropEnum.GREETING_TEXT);
            if (greetingText != null && greetingTextView != null) {
                greetingTextView.setText(greetingText);
            }
        }
    }

    private void setMainButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> mainButtons = menuCard.getMainButtons();
        for (MenuCardButtonEnum menuCardButtonEnum : mainButtons.keySet()) {
            setMainButton(menuCardButtonEnum, mainButtons.get(menuCardButtonEnum));
        }
    }

    private void setOtherButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> mainButtons = menuCard.getOtherButtons();
        for (MenuCardButtonEnum menuCardButtonEnum : mainButtons.keySet()) {
            setOtherButton(menuCardButtonEnum, mainButtons.get(menuCardButtonEnum));
        }
    }


    private void setMainButton(MenuCardButtonEnum menuCardButtonEnum, MenuCardButton menuCardButton) {
        Button button = getMainButton(menuCardButtonEnum);
        if (button != null && menuCardButton != null) {
            button.setText(menuCardButton.getName());
            button.setEnabled(enableAll);
            button.setVisibility(menuCardButton.isEnabled() ? View.VISIBLE : View.INVISIBLE);
            setButtonShape(menuCardButton, button);
            setButtonColor(menuCardButton, button);
            setButtonTextColor(menuCardButton, button);
            setButtonAnimation(menuCardButton, button);
            setRouteToPage(menuCardButton, button);
        }
    }


    private void setOtherButton(MenuCardButtonEnum menuCardButtonEnum, MenuCardButton menuCardButton) {
        Button button = getOtherButton(menuCardButtonEnum);
        if (button != null && menuCardButton != null) {
            button.setText(menuCardButton.getName());
            button.setEnabled(enableAll);
            button.setVisibility(menuCardButton.isEnabled() ? View.VISIBLE : View.INVISIBLE);
            setButtonShape(menuCardButton, button, true);
            setButtonColor(menuCardButton, button);
            setButtonTextColor(menuCardButton, button);
            setButtonAnimation(menuCardButton, button);
            setRouteToPage(menuCardButton, button);
            button.setTextSize(dpToPx(10));
        }
    }

    public void setRouteToPage(final MenuCardButton menuCardButton, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<String, Object> params = new HashMap<>();
                params.put("menuCardView_buttonId", menuCardButton.getId());
                authenticationController.goToMultipleMenuCardPage(params);
            }
        });

    }

    private void setButtonColor(MenuCardButton menuCardButton, Button button) {
        String buttonColor = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_COLOR);
        int selectedColor = Color.parseColor(getColor(buttonColor, ColorCodeEnum.White).name().toLowerCase());
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(selectedColor);
    }

    private void setButtonTextColor(MenuCardButton menuCardButton, Button button) {
        String selectedTextColor = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_TEXT_COLOR);
        int textColor = Color.parseColor(getColor(selectedTextColor, ColorCodeEnum.Black).name().toLowerCase());
        button.setTextColor(textColor);
    }


    private ColorCodeEnum getColor(String selectedColor, ColorCodeEnum defaultColor) {
        if (selectedColor == null || ColorCodeEnum.of(selectedColor) == null) {
            selectedColor = defaultColor.getValue() + "";
        }
        return ColorCodeEnum.of(selectedColor);
    }

    private void setButtonAnimation(MenuCardButton menuCardButton, Button button) {
        if (menuCardButton.blink()) {
            String selectedTextColor = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_TEXT_COLOR);
            int textColor = Color.parseColor(getColor(selectedTextColor, ColorCodeEnum.Black).name().toLowerCase());
            final ObjectAnimator colorAnim = ObjectAnimator.ofInt(button, "textColor", textColor, Color.TRANSPARENT); //you can change colors
            colorAnim.setDuration(500);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }
    }


    private void setButtonShape(MenuCardButton menuCardButton, Button button) {
        setButtonShape(menuCardButton, button, false);
    }

    private void setButtonShape(MenuCardButton menuCardButton, Button button, boolean isSmall) {
        String shapeId = menuCardButton.getProps().get(MenuCardButtonPropEnum.BUTTON_SHAPE);
        int shapeIdInt = 0;
        if (shapeId == null) {
            shapeIdInt = MenuCardButtonShapeEnum.RECTANGLE.getValue();
        } else {
            shapeIdInt = Integer.parseInt(shapeId);
        }

        ViewGroup.LayoutParams params = button.getLayoutParams();
        if (shapeIdInt == MenuCardButtonShapeEnum.RECTANGLE.getValue()) {
            button.setBackgroundResource(R.drawable.button_menu);
            if (isSmall) {
                params.height = dpToPx(50);
                params.width = dpToPx(100);
            } else {
                params.height = dpToPx(80);
                params.width = dpToPx(250);
            }

        } else if (shapeIdInt == MenuCardButtonShapeEnum.OVAL.getValue()) {
            button.setBackgroundResource(R.drawable.button_oval);
            if (isSmall) {
                params.height = dpToPx(50);
                params.width = dpToPx(100);
            } else {
                params.height = dpToPx(80);
                params.width = dpToPx(250);
            }
        } else if (shapeIdInt == MenuCardButtonShapeEnum.ROUND.getValue()) {
            button.setBackgroundResource(R.drawable.button_round);
            if (isSmall) {
                params.height = dpToPx(75);
                params.width = dpToPx(75);
            } else {
                params.height = dpToPx(100);
                params.width = dpToPx(100);
            }
        } else if (shapeIdInt == MenuCardButtonShapeEnum.SQUARE.getValue()) {
            button.setBackgroundResource(R.drawable.button_menu);
            if (isSmall) {
                params.height = dpToPx(100);
                params.width = dpToPx(100);
            } else {
                params.height = dpToPx(150);
                params.width = dpToPx(150);
            }
        }
        button.setLayoutParams(params);

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    Button getMainButton(MenuCardButtonEnum menuCardButtonEnum) {
        Button mainButton = null;
        if (menuCardButtonEnum == MenuCardButtonEnum.MAIN_1) {
            mainButton = (Button) inflatedView.findViewById(R.id.mainButton1);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MAIN_2) {
            mainButton = (Button) inflatedView.findViewById(R.id.mainButton2);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MAIN_3) {
            mainButton = (Button) inflatedView.findViewById(R.id.mainButton3);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MAIN_4) {
            mainButton = (Button) inflatedView.findViewById(R.id.mainButton4);
        }
        return mainButton;
    }


    Button getOtherButton(MenuCardButtonEnum menuCardButtonEnum) {
        Button otherButton = null;
        if (menuCardButtonEnum == MenuCardButtonEnum.TOP_LEFT) {
            otherButton = (Button) inflatedView.findViewById(R.id.topleft);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.TOP_CENTER) {
            otherButton = (Button) inflatedView.findViewById(R.id.topCenter);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.TOP_RIGHT) {
            otherButton = (Button) inflatedView.findViewById(R.id.topRight);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MIDDLE_LEFT) {
            otherButton = (Button) inflatedView.findViewById(R.id.centerLeft);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MIDDLE_CENTER) {
            otherButton = (Button) inflatedView.findViewById(R.id.centerMid);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.MIDDLE_RIGHT) {
            otherButton = (Button) inflatedView.findViewById(R.id.centerRight);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.BOTTOM_LEFT) {
            otherButton = (Button) inflatedView.findViewById(R.id.bottomleft);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.BOTTOM_CENTER) {
            otherButton = (Button) inflatedView.findViewById(R.id.bottomCenter);
        } else if (menuCardButtonEnum == MenuCardButtonEnum.BOTTOM_RIGHT) {
            otherButton = (Button) inflatedView.findViewById(R.id.bottomRight);
        }
        return otherButton;
    }

    private void initialize() {
        menuCardDao = new MenuCardDao();
        menuCard = menuCardDao.getMenuCard(menuCardId);
        if (menuCard == null) {
            menuCard = new MenuCard();
            Toast.makeText(MyApplication.getAppContext(), "Please login and go to Settings to create a Menu card", Toast.LENGTH_LONG);
        }
        authenticationController = new AuthenticationController(getActivity());
    }


    private void setStyle() {
        String backGroundColor = menuCard.getProps().get(MenuCardPropEnum.BACKGROUND_COLOR);
        if (backGroundColor == null) {
            backGroundColor = "#000000";
        }
        int backGroundColorInt = Color.parseColor(backGroundColor);
        RelativeLayout mainLayout = (RelativeLayout) getActivity().findViewById(R.id.mainlayout);
        if (StyleUtil.colorMap.get("mainPageBackground") != null && mainLayout != null) {
            mainLayout.setBackgroundColor(backGroundColorInt);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setMenuCardId(long menuCardId) {
        this.menuCardId = menuCardId;
    }

    public boolean isEnableAll() {
        return enableAll;
    }

    public void setEnableAll(boolean enableAll) {
        this.enableAll = enableAll;
    }
}
