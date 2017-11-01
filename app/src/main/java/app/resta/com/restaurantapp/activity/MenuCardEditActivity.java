package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuCardDao;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardPropEnum;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardEditActivity extends BaseActivity {
    private MenuCardDao menuCardDao;
    private MenuCard menuCard;
    private GridLayout mainButtonsGrid = null;
    private GridLayout otherButtonsGrid = null;
    private int clickedIndex = -1;
    private String[] newImagePath = new String[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_edit);
        setToolbar();
        initialize();
        loadIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setFields();
    }

    private void initialize() {
        menuCardDao = new MenuCardDao();

        mainButtonsGrid = (GridLayout) findViewById(R.id.mainButtonsGrid);
        mainButtonsGrid.removeAllViews();

        otherButtonsGrid = (GridLayout) findViewById(R.id.mainButtonsGrid);
        otherButtonsGrid.removeAllViews();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        long menuCardId = intent.getLongExtra("activity_menucardEdit_cardId", 0l);
        menuCard = menuCardDao.getMenuCard(menuCardId);
        if (menuCard == null) {
            menuCard = new MenuCard();
        }
    }

    private void setFields() {

        setName();
        setImages();
        setGreetingText();
        setMainButtons();
        setOtherButtons();
    }

    private void setImages() {
        newImagePath[0] = null;
        newImagePath[1] = null;

        ImageView logoImageBig = (ImageView) findViewById(R.id.logoImageBig);
        ImageView logoImageSmall = (ImageView) findViewById(R.id.logoImageSmall);

        setImage(logoImageBig, menuCard.getProps().get(MenuCardPropEnum.LOGO_BIG_IMAGE_NAME));
        setImage(logoImageSmall, menuCard.getProps().get(MenuCardPropEnum.LOGO_SMALL_IMAGE_NAME));
    }


    private void setImage(ImageView imageView, String imageName) {
        imageView.setImageResource(R.drawable.noimage);
        if (imageName != null && imageName.length() > 0) {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + imageName + ".jpeg";
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bmp);
            }
        }
    }


    private void getFields() {
        getName();
        getModifiedImage();
        getGreetingText();
    }

    private void setMainButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> mainButtons = menuCard.getMainButtons();
        for (MenuCardButtonEnum buttonEnum : mainButtons.keySet()) {
            addMainButton(mainButtons.get(buttonEnum));
        }
    }

    private void setOtherButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> otherButtons = menuCard.getOtherButtons();
        for (MenuCardButtonEnum buttonEnum : otherButtons.keySet()) {
            if (otherButtons.get(buttonEnum) != null) {
                addOtherButton(otherButtons.get(buttonEnum));
            }
        }
    }


    private void addMainButton(MenuCardButton menuCardButton) {
        mainButtonsGrid = (GridLayout) findViewById(R.id.mainButtonsGrid);
        mainButtonsGrid.setColumnCount(2);
        mainButtonsGrid.setRowCount(2);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(40 * 30 / 100, 40 * 30 / 100, 40 * 30 / 100, 40 * 30 / 100);

        Button button = new Button(this);
        button.setTag(menuCardButton.getLocation());
        button.setWidth(350);
        button.setHeight(120);

        //button.setTag(menuCardButton.getLocation().getValue());
        if (menuCardButton.isEnabled()) {
            button.setBackgroundResource(R.drawable.button_green);
        } else {
            button.setBackgroundResource(R.drawable.button_grey);
        }

        String buttonName = menuCardButton.getName();

        if (buttonName == null && menuCardButton.getLocation() != null) {
            buttonName = menuCardButton.getLocation().name();
        }
        button.setText(buttonName);
        button.setTextSize(20);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToButtonEditPage(view);
            }
        });
        mainButtonsGrid.addView(button, layoutParams);
    }


    private void addOtherButton(MenuCardButton menuCardButton) {
        otherButtonsGrid = (GridLayout) findViewById(R.id.otherButtonsGrid);
        otherButtonsGrid.setColumnCount(1);
        //otherButtonsGrid.setRowCount(2);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(40 * 30 / 100, 40 * 30 / 100, 40 * 30 / 100, 40 * 30 / 100);

        Button button = new Button(this);
        button.setTag(menuCardButton.getLocation());
        button.setWidth(350);
        button.setHeight(120);

        //button.setTag(menuCardButton.getLocation().getValue());
        if (menuCardButton.isEnabled()) {
            button.setBackgroundResource(R.drawable.button_green);
        } else {
            button.setBackgroundResource(R.drawable.button_grey);
        }

        String buttonName = menuCardButton.getName();

        if (buttonName == null && menuCardButton.getLocation() != null) {
            buttonName = menuCardButton.getLocation().name();
        }

        button.setText(buttonName);
        button.setTextSize(20);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToButtonEditPage(view);
            }
        });

        otherButtonsGrid.addView(button, layoutParams);
    }

    private void getName() {
        TextView name = (TextView) findViewById(R.id.menuCardName);
        menuCard.setName(name.getText().toString());
    }

    private void setName() {
        TextView name = (TextView) findViewById(R.id.menuCardName);
        name.setText(menuCard.getName());
    }

    private void setGreetingText() {
        TextView greetingMessage = (TextView) findViewById(R.id.menuCardEditGreetingText);
        String greetingText = menuCard.getProps().get(MenuCardPropEnum.GREETING_TEXT);
        if (greetingText == null) greetingText = "";
        greetingMessage.setText(greetingText);
    }


    private void getGreetingText() {
        TextView greetingMessage = (TextView) findViewById(R.id.menuCardEditGreetingText);
        menuCard.getProps().put(MenuCardPropEnum.GREETING_TEXT, greetingMessage.getText().toString());
    }

    public void goToButtonEditPage(View view) {
        if (menuCard != null && menuCard.getId() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("menuCardEditActivity_buttonClicked", view.getTag());
            params.put("menuCardEditActivity_cardId", menuCard.getId());
            authenticationController.goToMenuCardButtonEditPage(params);
        } else {
            Toast.makeText(MyApplication.getAppContext(), "Please save the Menu card before modifying the buttons.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("menuCardEdit_menuCardId", menuCard.getId());
        authenticationController.goToMenuCardSettingsPage(params);
    }

    public void save(View view) {
        getFields();
        menuCardDao.insertOrUpdateCard(menuCard);
        saveImagesToPhone();
        onBackPressed();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void showSelectImageFromDialogForItemEdit(View view) {
        String index = (String) view.getTag();
        clickedIndex = Integer.parseInt(index);
        showSelectImageFromDialog(view);
    }

    public void setNewImagePath(Intent intent, String path) {
        ImageView logoImageBig = (ImageView) findViewById(R.id.logoImageBig);
        ImageView logoImageSmall = (ImageView) findViewById(R.id.logoImageSmall);

        Bitmap bitmapImage = BitmapFactory.decodeFile(path);

        if (clickedIndex == 1) {
            newImagePath[0] = path;
            logoImageBig.setImageBitmap(bitmapImage);
        } else if (clickedIndex == 2) {
            newImagePath[1] = path;
            logoImageSmall.setImageBitmap(bitmapImage);
        }
    }


    public void deleteSelectedImage(View view) {
        String index = (String) view.getTag();
        clickedIndex = Integer.parseInt(index);

        ImageView logoImageBig = (ImageView) findViewById(R.id.logoImageBig);
        ImageView logoImageSmall = (ImageView) findViewById(R.id.logoImageSmall);
        if (clickedIndex == 1) {
            newImagePath[0] = "noImage";
            logoImageBig.setImageResource(R.drawable.noimage);
        } else if (clickedIndex == 2) {
            newImagePath[1] = "noImage";
            logoImageSmall.setImageResource(R.drawable.noimage);
        }
    }


    private void getModifiedImage(int index) {
        String selectedImageName = newImagePath[index];
        if (selectedImageName != null && selectedImageName.length() > 0) {
            if (selectedImageName.equals("noImage")) {
                updateImageNameParam(index, null);
            } else {
                String newImageName = menuCard.getName() + "_logo_" + index;
                newImageName = newImageName.replaceAll(" ", "_");
                Bitmap mp = BitmapFactory.decodeFile(newImagePath[index]);
                saveImageLater(mp, newImageName);
                updateImageNameParam(index, newImageName);
            }
        }
    }

    private void updateImageNameParam(int index, String imageName) {
        if (index == 0) {
            menuCard.getProps().put(MenuCardPropEnum.LOGO_BIG_IMAGE_NAME, imageName);
        } else {
            menuCard.getProps().put(MenuCardPropEnum.LOGO_SMALL_IMAGE_NAME, imageName);
        }
    }

    private void getModifiedImage() {
        getModifiedImage(0);
        getModifiedImage(1);
    }

    Map<Bitmap, List<String>> imagesToSave = new HashMap<>();

    private void saveImageLater(Bitmap mp, String imageName) {
        List<String> imageNames = imagesToSave.get(mp);
        if (imageNames == null) {
            imageNames = new ArrayList<>();
        }
        imageNames.add(imageName);
        imagesToSave.put(mp, imageNames);
    }


    private void saveImagesToPhone() {
        ImageSaver saver = new ImageSaver(this);
        for (Bitmap mp : imagesToSave.keySet()) {
            List<String> imageNames = imagesToSave.get(mp);
            if (imageNames != null) {
                for (String imageName : imageNames) {
                    saver.saveImageToAppFolder(mp, imageName);
                }
            }
        }
    }

}
