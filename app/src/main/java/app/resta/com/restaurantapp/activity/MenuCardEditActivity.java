package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardEditActivity extends BaseActivity {
    private MenuCardAdminDaoI menuCardAdminDao;
    private MenuCard menuCard = new MenuCard();
    private GridLayout mainButtonsGrid = null;
    private GridLayout otherButtonsGrid = null;
    private int clickedIndex = -1;
    private String[] newImagePath = new String[2];
    StorageReference storageRef;
    private String TAG = "MenuCardEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_edit);
        setToolbar();
        initialize();
        loadIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initialize() {
        menuCardAdminDao = new MenuCardAdminFireStoreDao();
        mainButtonsGrid = findViewById(R.id.mainButtonsGrid);
        mainButtonsGrid.removeAllViews();

        otherButtonsGrid = findViewById(R.id.mainButtonsGrid);
        otherButtonsGrid.removeAllViews();
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        final String menuCardId = intent.getStringExtra("activity_menucardEdit_cardId");
        if (menuCardId != null) {
            menuCardAdminDao.getCard(menuCardId, new OnResultListener<MenuCard>() {
                @Override
                public void onCallback(final MenuCard menuCardFromDB) {
                    menuCard = menuCardFromDB;
                    menuCardAdminDao.getButtonsInCard(menuCardId, new OnResultListener<List<MenuCardButton>>() {
                        @Override
                        public void onCallback(List<MenuCardButton> buttonsForThisCard) {
                            Map<MenuCardButtonEnum, MenuCardButton> cardButtons = new HashMap<>();
                            for (MenuCardButton button : buttonsForThisCard) {
                                cardButtons.put(button.getLocation(), button);
                            }
                            menuCard.setButtons(cardButtons);
                            setFields();
                        }
                    });
                }
            });
        }
    }

    private void setFields() {
        Log.i(TAG, "setFields");
        setName();
        setImages();
        setGreetingText();
        setBGColorCode();
        setMainButtons();
        setOtherButtons();
    }

    private void setImages() {
        Log.i(TAG, "SetImages");
        newImagePath[0] = null;
        newImagePath[1] = null;

        ImageView logoImageBig = findViewById(R.id.logoImageBig);
        ImageView logoImageSmall = findViewById(R.id.logoImageSmall);

        StorageReference bigImageReference = storageRef.child(menuCard.getLogoBigImageUrl());
        StorageReference smallImageReference = storageRef.child(menuCard.getLogoSmallImageUrl());
        if (newImagePath[0] == null && menuCard.getLogoBigImageUrl() != null) {
            ImageUtil.loadImageFromStorageBySkippingCache(this, bigImageReference, MenuCard.FIRESTORE_LOGO_IMABE_BIG_URL, logoImageBig);
            Log.i(TAG, "newImagePath[0]" + newImagePath[0]);
        }

        if (newImagePath[1] == null && menuCard.getLogoSmallImageUrl() != null) {
            ImageUtil.loadImageFromStorageBySkippingCache(this, smallImageReference, MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL, logoImageSmall);
            Log.i(TAG, "newImagePath[1]" + newImagePath[1]);
        }
    }

    private void getFields() {
        getName();
        getGreetingText();
        getBGColor();
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
        mainButtonsGrid = findViewById(R.id.mainButtonsGrid);
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
        otherButtonsGrid = findViewById(R.id.otherButtonsGrid);
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
        TextView name = findViewById(R.id.menuCardName);
        menuCard.setName(name.getText().toString());
    }

    private void setName() {
        TextView name = findViewById(R.id.menuCardName);
        name.setText(menuCard.getName());
    }

    private void setGreetingText() {
        TextView greetingMessage;
        greetingMessage = findViewById(R.id.menuCardEditGreetingText);
        String greetingText = menuCard.getGreetingText();
        if (greetingText == null) greetingText = "";
        greetingMessage.setText(greetingText);
    }

    private void setBGColorCode() {
        TextView cardBgColor = findViewById(R.id.cardBgColorText);
        String bgColor = menuCard.getBackgroundColor();
        if (bgColor == null) bgColor = "";
        cardBgColor.setText(bgColor);
    }


    private void getGreetingText() {
        TextView greetingMessage = findViewById(R.id.menuCardEditGreetingText);
        menuCard.setGreetingText(greetingMessage.getText().toString());
    }

    private void getBGColor() {
        TextView bgColor = findViewById(R.id.cardBgColorText);
        menuCard.setBackgroundColor(bgColor.getText().toString());
    }

    public void goToButtonEditPage(View view) {
        if (menuCard != null && menuCard.getId() != null && menuCard.getId().length() > 0) {
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
        //TODO As there is only one Menu card. Setting this as true. If we choose to have more than one menu card. then, we need to give the option to the user to choose the default one.
        menuCard.setDefault(true);
        menuCardAdminDao.insertOrUpdateCard(menuCard, new OnResultListener<MenuCard>() {
            @Override
            public void onCallback(MenuCard menuCard) {
                saveImages();
                onBackPressed();
            }
        });
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void showSelectImageFromDialogForItemEdit(View view) {
        String index = (String) view.getTag();
        clickedIndex = Integer.parseInt(index);
        showSelectImageFromDialog(view);
    }


    @Override
    public void setNewImagePath(Uri uri, String path) {
        ImageView logoImageBig = findViewById(R.id.logoImageBig);
        ImageView logoImageSmall = findViewById(R.id.logoImageSmall);

        if (clickedIndex == 1) {
            newImagePath[0] = path;
            logoImageBig.setImageURI(uri);
        } else if (clickedIndex == 2) {
            newImagePath[1] = path;
            logoImageSmall.setImageURI(uri);
        }
    }

    public void deleteSelectedImage(View view) {
        String index = (String) view.getTag();
        clickedIndex = Integer.parseInt(index);

        ImageView logoImageBig = findViewById(R.id.logoImageBig);
        ImageView logoImageSmall;
        logoImageSmall = findViewById(R.id.logoImageSmall);
        if (clickedIndex == 1) {
            newImagePath[0] = "noImage";
            logoImageBig.setImageResource(R.drawable.noimage);
        } else if (clickedIndex == 2) {
            newImagePath[1] = "noImage";
            logoImageSmall.setImageResource(R.drawable.noimage);
        }
    }

//
//    private void getModifiedImage(int index) {
//        String selectedImageName = newImagePath[index];
//        if (selectedImageName != null && selectedImageName.length() > 0) {
//            if (selectedImageName.equals("noImage")) {
//                updateImageNameParam(index, null);
//            } else {
//                String newImageName = menuCard.getName() + "_logo_" + index;
//                newImageName = newImageName.replaceAll(" ", "_");
//                Bitmap mp = BitmapFactory.decodeFile(newImagePath[index]);
//                saveImageLater(mp, newImageName);
//                updateImageNameParam(index, newImageName);
//            }
//        }
//    }
//
//    private void updateImageNameParam(int index, String imageName) {
//        if (index == 0) {
////            menuCard.getProps().put(MenuCardPropEnum.LOGO_BIG_IMAGE_NAME, imageName);
//        } else {
////            menuCard.getProps().put(MenuCardPropEnum.LOGO_SMALL_IMAGE_NAME, imageName);
//        }
//    }
//
//    private void getModifiedImage() {
//        getModifiedImage(0);
//        getModifiedImage(1);
//    }

//    Map<Bitmap, List<String>> imagesToSave = new HashMap<>();
//
//    private void saveImageLater(Bitmap mp, String imageName) {
//        List<String> imageNames = imagesToSave.get(mp);
//        if (imageNames == null) {
//            imageNames = new ArrayList<>();
//        }
//        imageNames.add(imageName);
//        imagesToSave.put(mp, imageNames);
//    }


    private void saveImages() {
        StorageReference bigImageRef = storageRef.child(FireBaseStorageLocation.getMenuCardImagesLocation() + menuCard.getId() + "/" + menuCard.getId() + "_" + MenuCard.FIRESTORE_LOGO_IMABE_BIG_URL + ".jpg");
        StorageReference smallImageRef = storageRef.child(FireBaseStorageLocation.getMenuCardImagesLocation() + menuCard.getId() + "/" + menuCard.getId() + "_" + MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL + ".jpg");
        saveImage(bigImageRef, MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL, newImagePath[0]);
        saveImage(smallImageRef, MenuCard.FIRESTORE_LOGO_IMABE_SMALL_URL, newImagePath[1]);
    }

    void saveImage(StorageReference imageReference, final String imageNameKey, String modifiedPath) {
        if (modifiedPath == null) {
            Log.i(TAG, "No Change to the image" + imageNameKey);
        } else if (modifiedPath.equalsIgnoreCase("noImage")) {
            Log.i(TAG, "Image " + imageNameKey + " deleted by the user");
            ImageUtil.deleteImage(imageReference, imageNameKey, new OnResultListener<Object>() {
                @Override
                public void onCallback(Object object) {
//                    menuCardAdminDao.updateImageUrl(menuCard, imageNameKey, "");
                }
            });

        } else {
            Log.i(TAG, "Image " + imageNameKey + " changed by the user");
            Uri uri = Uri.fromFile(new File(modifiedPath));
            Log.i(TAG, "Deleting the old image " + imageNameKey);
            ImageUtil.deleteImage(imageReference, imageNameKey, new OnResultListener<Object>() {
                @Override
                public void onCallback(Object object) {
                    Log.i(TAG, "Deleted image from storage");
                }
            });
            Log.i(TAG, "Creating new image " + imageNameKey);
            ImageUtil.createImage(imageReference, uri, imageNameKey, new OnResultListener<String>() {
                @Override
                public void onCallback(String path) {
//                    menuCardAdminDao.updateImageUrl(menuCard, imageNameKey, path);
                }
            });
        }
    }

}
