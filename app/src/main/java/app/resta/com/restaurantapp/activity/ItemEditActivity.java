package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.RestaurantItemExtraDataController;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.ItemParentMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.Paths;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.validator.RestaurantItemValidator;

public class ItemEditActivity extends BaseActivity {

    //Data
    RestaurantItem item = null;
    RestaurantItem parentItem = null;
    private RestaurantItemExtraDataController restaurantItemExtraDataController;
    List<RestaurantItem> ggwItems = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    List<Ingredient> ingredients = new ArrayList<>();
    private String[] newImagePath = new String[3];
    Map<String, RestaurantItem> itemsByName = new HashMap<>();
    Map<String, Ingredient> ingredientsRefDataMap = new HashMap<>();
    Map<String, Tag> tagsRefDataMap = new HashMap<>();

    //Flags
    boolean newItemCreation = false;
    private static int clickedIndex = -1;

    //Grids
    GridLayout gl = null;
    GridLayout tagsGrid = null;
    GridLayout ingredientsGrid = null;

    //DAO
    private MenuGroupAdminDaoI menuGroupAdminDaoI;
    private IngredientAdminDaoI ingredientAdminDao;
    private TagAdminDaoI tagAdminDao;
    private MenuItemAdminDaoI menuItemAdminDao;

    private ImageView itemImageOne;
    private ImageView itemImageTwo;
    private ImageView itemImageThree;


    StorageReference storageRef;
    private final static String TAG = "ItemEditActivity";
    View.OnClickListener ggwButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeFromGoesGreatWith(v);
        }
    };
    View.OnClickListener tagButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeFromTags(v);
        }
    };
    View.OnClickListener ingredientButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeFromIngredients(v);
        }
    };


    private void loadTagsRefData() {
        tagAdminDao.getTags(new OnResultListener<List<Tag>>() {
            @Override
            public void onCallback(List<Tag> tags) {
                for (Tag tag : tags) {
                    tagsRefDataMap.put(tag.getName().toLowerCase(), tag);
                }
                setAutoCompleteTags();
            }
        });
    }

    private void loadIngredientsRefData() {
        ingredientAdminDao.getIngredients(new OnResultListener<List<Ingredient>>() {
            @Override
            public void onCallback(List<Ingredient> ingredients) {
                for (Ingredient ingredient : ingredients) {
                    ingredientsRefDataMap.put(ingredient.getName().toLowerCase(), ingredient);
                }
                setAutoCompleteIngredients();
            }
        });
    }

    private void initialize() {
        itemImageOne = findViewById(R.id.itemImageFirst);
        itemImageTwo = findViewById(R.id.itemImageSecond);
        itemImageThree = findViewById(R.id.itemImageThird);
        restaurantItemExtraDataController = new RestaurantItemExtraDataController();
        menuItemAdminDao = new MenuItemAdminFireStoreDao();
        ingredientAdminDao = new IngredientAdminFireStoreDao();
        tagAdminDao = new TagAdminFireStoreDao();
        menuGroupAdminDaoI = new MenuGroupAdminFireStoreDao();
        gl = findViewById(R.id.ggwItemsGrid);
        tagsGrid = findViewById(R.id.tagsItemsGrid);
        ingredientsGrid = findViewById(R.id.ingredientItemsGrid);
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        if (intent.hasExtra("item_obj")) {
            item = (RestaurantItem) intent.getSerializableExtra("item_obj");
        }
        if (intent.hasExtra("itemEditActivity_parentItem")) {
            parentItem = (RestaurantItem) intent.getSerializableExtra("itemEditActivity_parentItem");
        }
        if (parentItem == null) {
            parentItem = item.getParent();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        initialize();
        loadIntentParams();
        loadItems();
        loadIngredientsRefData();
        loadTagsRefData();
        setFieldValues();
        setToolbar();
        setImageIcons();
//        loadFromSavedInstanceState(savedInstanceState);
    }

//    private void loadFromSavedInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            Bitmap bitmap = savedInstanceState.getParcelable("image1");
//            if (bitmap != null) {
//                itemImageOne.setImageBitmap(bitmap);
//            }
//
//            Bitmap bitmap2 = savedInstanceState.getParcelable("image2");
//            if (bitmap2 != null) {
//                itemImageTwo.setImageBitmap(bitmap2);
//
//            }
//            Bitmap bitmap3 = savedInstanceState.getParcelable("image3");
//            if (bitmap3 != null) {
//                itemImageThree.setImageBitmap(bitmap3);
//            }
//        }
//
//    }

    private void loadItems() {
        menuItemAdminDao.getAllItems(new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                for (RestaurantItem rItem : items) {
                    itemsByName.put(rItem.getName(), rItem);
                }
                setGoesGreatWith();
            }
        });
    }

    private void setEditImageIcons() {
        View itemImageOne = findViewById(R.id.editFirstImage);
        View itemImageTwo = findViewById(R.id.editSecondImage);
        View itemImageThree = findViewById(R.id.editThirdImage);
        itemImageOne.setTag(1);
        itemImageTwo.setTag(2);
        itemImageThree.setTag(3);
    }

    private void setDeleteImageIcons() {
        View deleteImageOne = findViewById(R.id.deleteFirstImage);
        View deleteImageTwo = findViewById(R.id.deleteSecondImage);
        View deleteImageThree = findViewById(R.id.deleteThirdmage);
        deleteImageOne.setTag(1);
        deleteImageTwo.setTag(2);
        deleteImageThree.setTag(3);
    }

    private void setImageIcons() {
        setEditImageIcons();
        setDeleteImageIcons();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        if (parentItem == null || parentItem.getId() == null) {
//            params.put("groupMenuId", -1L);
        } else {
            params.put("groupMenuId", parentItem.getMenuTypeId());
            params.put("groupToOpen", parentItem.getId());
        }
        authenticationController.goToMenuPage(params);
    }

    public void removeFromGoesGreatWith(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        String id = button.getTag().toString();
        restaurantItemExtraDataController.deleteGGWItem(id + "");


        RestaurantItem deletedItem = new RestaurantItem();
        deletedItem.setId(id);
        ggwItems.remove(deletedItem);
    }


    public void removeFromTags(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        String tagSelectedForDeletion = (String) button.getTag();

        restaurantItemExtraDataController.deleteTagItem(tagSelectedForDeletion);
        tags.remove(tagSelectedForDeletion);
    }


    public void removeFromIngredients(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        String itemSelectedForDeletion = (String) button.getTag();
        restaurantItemExtraDataController.deleteIngredientItem(itemSelectedForDeletion);
        ingredients.remove(itemSelectedForDeletion);
    }

    private void setItemName() {
        EditText userInput = findViewById(R.id.editItemName);
        if (item.getName() != null) {
            userInput.setText(item.getName());
        }
    }

    private void setPriceName() {
        EditText price;
        price = findViewById(R.id.editItemPrice);
        if (parentItem != null) {
            ItemParentMapping mapping = item.getMappingForParent(parentItem.getId());
            if (mapping != null && mapping.getPrice() != null && mapping.getPrice().trim().length() > 0) {
                item.setPrice(mapping.getPrice());
            }

        }
        price.setText(item.getPrice());
    }

    private void setDescription() {
        EditText description = findViewById(R.id.editItemDescription);
        if (item.getDescription() != null) {

            description.setText(item.getDescription());
        }
    }

    private void setImage() {
        if (item != null && item.getId() != null && item.getId().length() > 0) {

            if (item.getItemImage1() != null) {
                StorageReference firstImageReference = storageRef.child(item.getItemImage1().getStorageUrl());
                ImageUtil.loadImageFromStorageBySkippingCache(this, firstImageReference, "1st image for " + item.getName(), itemImageOne);
            }
            if (item.getItemImage2() != null) {
                StorageReference secondImageReference = storageRef.child(item.getItemImage2().getStorageUrl());
                ImageUtil.loadImageFromStorageBySkippingCache(this, secondImageReference, "2nd image for " + item.getName(), itemImageTwo);
            }
            if (item.getItemImage3() != null) {
                StorageReference thirdImageReference = storageRef.child(item.getItemImage3().getStorageUrl());
                ImageUtil.loadImageFromStorageBySkippingCache(this, thirdImageReference, "3rd image for " + item.getName(), itemImageThree);
            }

        }
    }

//
//    private void setImage(ImageView imageView, RestaurantImage image) {
//        imageView.setImageResource(R.drawable.noimage);
//        if (image != null && image.getName() != null) {
//            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
//            String filePath = path + image.getName() + ".jpeg";
//            File file = new File(filePath);
//            if (file.exists()) {
//                Bitmap bmp = BitmapFactory.decodeFile(filePath);
//                imageView.setImageBitmap(bmp);
//            }
//        }
//    }

    private void setStatus() {
        ToggleButton status = findViewById(R.id.editItemToggleActive);
        if (item.getActive() == null || item.getActive().equalsIgnoreCase("Y")) {
            status.setText("Y");
            status.setChecked(true);
        }
    }

    private void setParentName() {
        TextView headerLabel = findViewById(R.id.itemEditHeader);
        if (parentItem == null) {
            headerLabel.setText("Add Item:");
        } else {
            headerLabel.setText("Add Item to " + parentItem.getName() + ":");
        }
    }

    private void setGoesGreatWith() {
        Set<String> itemNames = itemsByName.keySet();
        String[] dishesArray = itemNames.toArray(new String[itemNames.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, dishesArray);
        AutoCompleteTextView actv = findViewById(R.id.goesGreatWithSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setGGWItems() {
        if (!newItemCreation) {
            menuItemAdminDao.getGGWsForItem(item.getId() + "", new OnResultListener<List<String>>() {
                @Override
                public void onCallback(List<String> ggwIds) {
                    gl.removeAllViews();
                    gl.setColumnCount(4);
                    gl.setRowCount(3);
                    for (String ggwId : ggwIds) {
                        menuItemAdminDao.getItem(ggwId, new OnResultListener<RestaurantItem>() {
                            @Override
                            public void onCallback(RestaurantItem restaurantItem) {
                                if (restaurantItem != null) {
                                    ggwItems.add(restaurantItem);
                                    item.setGgwItems(ggwItems);
                                    addGGWButton(restaurantItem);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void addGGWButton(RestaurantItem item) {
        Button ggwItemButton = new Button(MyApplication.getAppContext());
        ggwItemButton.setClickable(true);
        ggwItemButton.setText(item.getName());
        ggwItemButton.setTag(item.getId());

        ggwItemButton.setMaxHeight(10);
        ggwItemButton.setMaxWidth(20);
        ggwItemButton.setTextColor(Color.RED);
        ggwItemButton.setOnClickListener(ggwButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gl.addView(ggwItemButton, lp);

    }

    private void setAutoCompleteTags() {
        List<Tag> allTags = new ArrayList<>(tagsRefDataMap.values());
        String[] tagArr = new String[allTags.size()];
        int i = 0;
        for (Tag tag : allTags) {
            tagArr[i++] = tag.getName();
        }
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, tagArr);
        AutoCompleteTextView actv = findViewById(R.id.tagsSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setTags() {
        if (!newItemCreation) {
            menuItemAdminDao.getTagsForItem(item.getId() + "", new OnResultListener<List<String>>() {
                @Override
                public void onCallback(List<String> tagIds) {
                    for (String tagId : tagIds) {

                        tagAdminDao.getTag(tagId, new OnResultListener<Tag>() {
                            @Override
                            public void onCallback(Tag tag) {
                                if (tag != null) {
                                    tags.add(tag);
                                    addTagsButton(tag);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void setAutoCompleteIngredients() {
        List<Ingredient> allIngredients = new ArrayList<>(ingredientsRefDataMap.values());
        String[] ingredientArr = new String[allIngredients.size()];
        int i = 0;
        for (Ingredient ingredient : allIngredients) {
            ingredientArr[i++] = ingredient.getName();
        }
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, ingredientArr);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = findViewById(R.id.ingredientsSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setIngredients() {
        if (!newItemCreation) {
            menuItemAdminDao.getIngredientsForItem(item.getId() + "", new OnResultListener<List<String>>() {
                @Override
                public void onCallback(List<String> ingredientIds) {
                    for (String ingredientId : ingredientIds) {
                        ingredientAdminDao.getIngredient(ingredientId, new OnResultListener<Ingredient>() {
                            @Override
                            public void onCallback(Ingredient ingredient) {
                                if (ingredient != null) {
                                    addIngredientsButton(ingredient);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void addTagsButton(Tag tag) {
        Button tagButton = new Button(MyApplication.getAppContext());
        tagButton.setClickable(true);
        tagButton.setText(tag.getName());
        tagButton.setTag(tag.getId());

        tagButton.setMaxHeight(10);
        tagButton.setMaxWidth(20);
        tagButton.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.colorAccent));
        tagButton.setOnClickListener(tagButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsGrid.addView(tagButton, lp);
    }

    private void addIngredientsButton(Ingredient ingredient) {
        Button ingredientButton = new Button(MyApplication.getAppContext());
        ingredientButton.setClickable(true);
        ingredientButton.setText(ingredient.getName());
        ingredientButton.setTag(ingredient.getId());

        ingredientButton.setMaxHeight(10);
        ingredientButton.setMaxWidth(20);
        ingredientButton.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.colorAccent));
        ingredientButton.setOnClickListener(ingredientButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ingredientsGrid.addView(ingredientButton, lp);
    }

    public void addToGoesGreatWith(View view) {
        AutoCompleteTextView goesGreatWithText = findViewById(R.id.goesGreatWithSuggestion);
        String suggestion = goesGreatWithText.getText().toString();

        RestaurantItem itemSuggested = itemsByName.get(suggestion);
        TextView ggwErrors = findViewById(R.id.ggwValidationBlock);

        if (itemSuggested == null) {
            Toast.makeText(this, "Not a valid item", Toast.LENGTH_LONG);
            ggwErrors.setText("Not a valid item");
            ggwErrors.setVisibility(View.VISIBLE);

        } else {
            if (ggwItems.contains(itemSuggested)) {
                Toast.makeText(this, itemSuggested.getName() + " is already present.", Toast.LENGTH_LONG);
                ggwErrors.setText(itemSuggested.getName() + " is already present.");
                ggwErrors.setVisibility(View.VISIBLE);
                return;
            } else if (ggwItems.size() >= Paths.MAX_GGW_ITEMS) {
                Toast.makeText(this, "Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.", Toast.LENGTH_LONG);
                ggwErrors.setText("Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.");
                ggwErrors.setVisibility(View.VISIBLE);
                return;
            } else {
                ggwErrors.setText("");
                ggwErrors.setVisibility(View.GONE);
            }

            restaurantItemExtraDataController.addGGWItem(itemSuggested.getId() + "");
            goesGreatWithText.setText("");
            ggwItems.add(itemSuggested);
            addGGWButton(itemSuggested);
        }
    }

    public void addToTags(View view) {
        AutoCompleteTextView tagsWithText = findViewById(R.id.tagsSuggestion);
        String suggestion = tagsWithText.getText().toString().trim().toLowerCase();
        TextView tagsErrors = findViewById(R.id.tagsValidationBlock);

        Tag itemSuggested = tagsRefDataMap.get(suggestion);
        if (itemSuggested == null) {
            Toast.makeText(this, "Not a valid item", Toast.LENGTH_LONG);
            tagsErrors.setText("Not a valid item");
            tagsErrors.setVisibility(View.VISIBLE);
        } else {
            if (tags.contains(itemSuggested)) {
                Toast.makeText(this, itemSuggested.getName() + " is already present.", Toast.LENGTH_LONG);
                tagsErrors.setText(itemSuggested.getName() + " is already present.");
                tagsErrors.setVisibility(View.VISIBLE);
                return;
            } else if (tags.size() >= Paths.MAX_TAG_ITEMS) {
                Toast.makeText(this, "Cannot add more than " + Paths.MAX_TAG_ITEMS + " items.", Toast.LENGTH_LONG);
                tagsErrors.setText("Cannot add more than " + Paths.MAX_TAG_ITEMS + " items.");
                tagsErrors.setVisibility(View.VISIBLE);
                return;
            } else {
                tagsErrors.setText("");
                tagsErrors.setVisibility(View.GONE);
            }

            restaurantItemExtraDataController.addTagItem(itemSuggested.getId());
            tagsWithText.setText("");
            tags.add(itemSuggested);
            addTagsButton(itemSuggested);
        }

    }


    public void addToIngredients(View view) {
        AutoCompleteTextView ingrdientsWithText = findViewById(R.id.ingredientsSuggestion);
        String suggestion = ingrdientsWithText.getText().toString().trim().toLowerCase();
        TextView ingredientsErrors = findViewById(R.id.ingredientsValidationBlock);
        Ingredient itemSuggested = ingredientsRefDataMap.get(suggestion);
        if (itemSuggested == null) {
            Toast.makeText(this, "Not a valid item", Toast.LENGTH_LONG);
            ingredientsErrors.setText("Not a valid item");
            ingredientsErrors.setVisibility(View.VISIBLE);
        } else {
            if (ingredients.contains(itemSuggested)) {
                Toast.makeText(this, itemSuggested.getName() + " is already present.", Toast.LENGTH_LONG);
                ingredientsErrors.setText(itemSuggested.getName() + " is already present.");
                ingredientsErrors.setVisibility(View.VISIBLE);
                return;
            } else if (ingredients.size() >= Paths.MAX_INGREDIENT_ITEMS) {
                Toast.makeText(this, "Cannot add more than " + Paths.MAX_INGREDIENT_ITEMS + " items.", Toast.LENGTH_LONG);
                ingredientsErrors.setText("Cannot add more than " + Paths.MAX_INGREDIENT_ITEMS + " items.");
                ingredientsErrors.setVisibility(View.VISIBLE);
                return;
            } else {
                ingredientsErrors.setText("");
                ingredientsErrors.setVisibility(View.GONE);
            }
            restaurantItemExtraDataController.addIngredientItem(itemSuggested.getId());
            ingrdientsWithText.setText("");
            ingredients.add(itemSuggested);
            addIngredientsButton(itemSuggested);
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void disableFields() {
        if (item.getParent() != null && item.getParent().getId() != null) {
            EditText userInput = (EditText) findViewById(R.id.editItemName);
            EditText description = (EditText) findViewById(R.id.editItemDescription);
            ToggleButton status = (ToggleButton) findViewById(R.id.editItemToggleActive);

            status.setEnabled(false);
            disableEditText(userInput);
            disableEditText(description);

            ImageButton editFirstImage = (ImageButton) findViewById(R.id.editFirstImage);
            ImageButton deleteFirstImage = (ImageButton) findViewById(R.id.deleteFirstImage);
            ImageButton editSecondImage = (ImageButton) findViewById(R.id.editSecondImage);
            ImageButton deleteSecondImage = (ImageButton) findViewById(R.id.deleteSecondImage);
            ImageButton editThirdImage = (ImageButton) findViewById(R.id.editThirdImage);
            ImageButton deleteThirdImage = (ImageButton) findViewById(R.id.deleteThirdmage);
            Button addTagButton = (Button) findViewById(R.id.addTagButton);
            Button addIgredientButton = (Button) findViewById(R.id.addIngredientsButton);
            Button addGoesGreat = (Button) findViewById(R.id.addGoesGreat);


            editFirstImage.setVisibility(View.GONE);
            deleteFirstImage.setVisibility(View.GONE);
            editSecondImage.setVisibility(View.GONE);
            deleteSecondImage.setVisibility(View.GONE);
            editThirdImage.setVisibility(View.GONE);
            deleteThirdImage.setVisibility(View.GONE);
            addTagButton.setVisibility(View.GONE);
            addIgredientButton.setVisibility(View.GONE);
            addGoesGreat.setVisibility(View.GONE);

        }
    }

    private void setFieldValues() {
        if (item.getId() == null) {
            newItemCreation = true;
        }
        setItemName();
        setPriceName();
        setDescription();
        setImage();
        setParentName();
        disableFields();
        setStatus();
        setGGWItems();
        setTags();
        setIngredients();
    }

    private void getModifiedItemName() {
        EditText userInput = (EditText) findViewById(R.id.editItemName);
        String modifiedName = userInput.getText().toString();
        item.setName(modifiedName);
    }

    private void getModifiedDescription() {
        EditText description = (EditText) findViewById(R.id.editItemDescription);
        String modifiedDescription = description.getText().toString();
        item.setDescription(modifiedDescription);
    }

    private void getModifiedPrice() {
        EditText price = (EditText) findViewById(R.id.editItemPrice);
        String modifiedPrice = price.getText().toString();
        item.setPrice(modifiedPrice);
    }

    private void getModifiedStatus() {
        ToggleButton status = (ToggleButton) findViewById(R.id.editItemToggleActive);
        String activeStatus = status.getText().toString();
        if (activeStatus.equalsIgnoreCase("on")) {
            activeStatus = "Y";
        } else {
            activeStatus = "N";
        }
        item.setActive(activeStatus);
    }

//    private void getModifiedImage() {
//        getModifiedImage(0, item.getImage(0));
//        getModifiedImage(1, item.getImage(1));
//        getModifiedImage(2, item.getImage(2));
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
//
//    private void getModifiedImage(int index, String oldImageName) {
//        String newImageName = item.getName() + "_" + index;
//        newImageName = newImageName.replaceAll(" ", "_");
//        if (newImagePath[index] != null && newImagePath[index].length() > 0) {
//            if (item.getImages() == null) {
//                item.setImages(new RestaurantImage[3]);
//            }
//            RestaurantImage image = item.getImages()[index];
//            if (image == null) {
//                image = new RestaurantImage(item.getId(), newImageName, null);
//                item.setImage(index, image);
//            } else {
//                item.getImages()[index].setName(newImageName);
//            }
//
//            Bitmap mp = BitmapFactory.decodeFile(newImagePath[index]);
//            if (newImagePath[index].equalsIgnoreCase("noImage")) {
//                item.setImage(index, null);
//            } else {
//                //imageSaver.deleteImage(oldImageName);
//                saveImageLater(mp, item.getImage(index));
//                //imageSaver.saveImageToAppFolder(mp, item.getImage(index));
//            }
//
//        } else {
//            if (newItemCreation) {
//                //  item.setImage(index, new RestaurantImage(item.getId(), newImageName));
//                //Bitmap noImage = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),
//                //      R.drawable.noimage);
//                //imageSaver.saveImageToAppFolder(noImage, item.getImage(index));
//            } else {
//                if (oldImageName != null && !newImageName.equalsIgnoreCase(oldImageName)) {
//                    //name of the item is modified. so the image have to be changed and the old image deleted.
//                    String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
//                    String filePath = path + oldImageName + ".jpeg";
//                    File imageFile = new File(filePath);
//                    item.setImage(index, new RestaurantImage(item.getId(), "noImage"));
//                    item.getImages()[index].setName("noImage");
//                    if (imageFile.exists()) {
//                        Bitmap oldImage = BitmapFactory.decodeFile(filePath);
//                        item.setImage(index, new RestaurantImage(item.getId(), newImageName));
//
//                        saveImageLater(oldImage, newImageName);
//                        //imageSaver.saveImageToAppFolder(oldImage, newImageName);
//                        //      imageSaver.deleteImage(oldImageName);
//                    }
//
//                }
//                //image is not modified
//            }
//        }
//    }

    private void saveGGWMappings() {
        if (restaurantItemExtraDataController.getGgwItemsAdded().size() > 0) {
            menuItemAdminDao.addGGWsToItem(item.getId() + "", restaurantItemExtraDataController.getGgwItemsAdded());
        }
        if (restaurantItemExtraDataController.getGgwItemsDeleted().size() > 0) {
            menuItemAdminDao.removeGGWsFromItem(item.getId() + "", restaurantItemExtraDataController.getGgwItemsDeleted());
        }
    }


    private void saveTags() {
        if (restaurantItemExtraDataController.getTagsAdded().size() > 0) {
            menuItemAdminDao.addTagsToItem(item.getId() + "", restaurantItemExtraDataController.getTagsAdded());
        }
        if (restaurantItemExtraDataController.getTagsDeleted().size() > 0) {
            menuItemAdminDao.removeTagsFromItem(item.getId() + "", restaurantItemExtraDataController.getTagsDeleted());
        }
    }

    private void saveIngredients() {
        if (restaurantItemExtraDataController.getIngredientsAdded().size() > 0) {
            //1==1 change to item.getId()
            menuItemAdminDao.addIngredientsToItem(item.getId() + "", restaurantItemExtraDataController.getIngredientsAdded());
        }
        //1==1 change to item.getId()
        if (restaurantItemExtraDataController.getIngredientsDeleted().size() > 0) {
            menuItemAdminDao.removeIngredientsFromItem(item.getId() + "", restaurantItemExtraDataController.getIngredientsDeleted());
        }
    }

    private boolean validateInput() {
        RestaurantItemValidator validator = new RestaurantItemValidator(this, item);
        return validator.validate();
    }

    private void saveImages() {
        StorageReference firstImageReference = storageRef.child(FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_1.jpg");
        StorageReference secondImageReference = storageRef.child(FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_2.jpg");
        StorageReference thirdImageReference = storageRef.child(FireBaseStorageLocation.getItemImagesLocation() + item.getId() + "/" + item.getId() + "_3.jpg");
        saveImage(0, firstImageReference, item.getId() + "_1.jpg");
        saveImage(1, secondImageReference, item.getId() + "_2.jpg");
        saveImage(2, thirdImageReference, item.getId() + "_3.jpg");
    }

    void saveImage(final int index, StorageReference imageReference, String imageName) {
        if (newImagePath[index] == null) {
            Log.i(TAG, "No Change to the image" + (index + 1));
        } else if (newImagePath[index].equalsIgnoreCase("noImage")) {
            Log.i(TAG, "Image " + (index + 1) + " deleted by the user");
            ImageUtil.deleteImage(imageReference, imageName, new OnResultListener<Object>() {
                @Override
                public void onCallback(Object object) {
                   // menuItemAdminDao.updateImageUrl(item, (index + 1) + "", "", "");
                }
            });

        } else {
            Log.i(TAG, "Image " + (index + 1) + " changed by the user");
            Uri uri = Uri.fromFile(new File(newImagePath[index]));
            Log.i(TAG, "Deleting the old image " + (index + 1));
            deleteExistingAndSaveNewImageInFireStorage(index, imageReference, imageName, uri);
        }
    }

    void deleteExistingAndSaveNewImageInFireStorage(final int index, final StorageReference storageReference, final String imageName, final Uri uri) {
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uriForExistingImage) {
                        ImageUtil.deleteImage(storageReference, imageName, new OnResultListener<Object>() {
                            @Override
                            public void onCallback(Object object) {
                                Log.i(TAG, "Deleted image from storage");
                                createImageInFiresStorage(index, storageReference, uri, imageName);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        int errorCode = ((StorageException) exception).getErrorCode();
                        if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            createImageInFiresStorage(index, storageReference, uri, imageName);
                        }

                    }
                });
    }

    private void createImageInFiresStorage(final int index, StorageReference storageReference, Uri uri, String imageName) {
        Log.i(TAG, "Creating new image " + (index + 1));
        ImageUtil.createImage(storageReference, uri, imageName, new OnResultListener<String>() {
            @Override
            public void onCallback(String path) {
//                menuItemAdminDao.updateImageUrl(item, (index + 1) + "", "", path);
            }
        });
    }

    public void save(View view) {
        getModifiedItemName();
        getModifiedPrice();
        getModifiedStatus();
        getModifiedDescription();
        if (validateInput()) {
            menuItemAdminDao.insertOrUpdateMenuItem(item, new OnResultListener<RestaurantItem>() {
                @Override
                public void onCallback(RestaurantItem item) {
                    if (parentItem != null && parentItem.getId() != null) {
                        if (newItemCreation) {
                            insertParentChildMapping(item);
                        }
                    }
                    saveImages();
                    saveGGWMappings();
                    saveIngredients();
                    saveTags();
                    dispatchToMenuPage();
                }
            });

        }
    }

    private void insertParentChildMapping(RestaurantItem item) {
        GroupAndItemMapping mapping = new GroupAndItemMapping();
        mapping.setItemId(item.getId());
        mapping.setGroupId(parentItem.getId());
        mapping.setItemPosition(-1);
        mapping.setItemPrice("-1");
        menuGroupAdminDaoI.addItemToGroup(mapping);
    }

    private void dispatchToMenuPage() {
        Intent intent;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(this, NarrowMenuActivity.class);
        } else {
            intent = new Intent(this, HorizontalMenuActivity.class);
        }
        intent.putExtra("modifiedItemId", item.getId());

        if (parentItem == null || parentItem.getId() == null) {
//            intent.putExtra("groupMenuId", -1l);
        } else {
            intent.putExtra("groupMenuId", parentItem.getMenuTypeId());
            intent.putExtra("groupToOpen", parentItem.getId());
        }


        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        BitmapDrawable drawable = (BitmapDrawable) itemImageOne.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//
//        drawable = (BitmapDrawable) itemImageTwo.getDrawable();
//        Bitmap bitmap2 = drawable.getBitmap();
//
//
//        drawable = (BitmapDrawable) itemImageThree.getDrawable();
//        Bitmap bitmap3 = drawable.getBitmap();
//
//
//        outState.putParcelable("image1", bitmap);
//        outState.putParcelable("image2", bitmap2);
//        outState.putParcelable("image3", bitmap3);
//        super.onSaveInstanceState(outState);
//    }

    public void showSelectImageFromDialogForItemEdit(View view) {
        clickedIndex = (Integer) view.getTag();
        showSelectImageFromDialog(view);

    }

//    public void setNewImagePath(Intent intent, String path) {
//        ImageView itemImageOne = findViewById(R.id.itemImageFirst);
//        ImageView itemImageTwo = findViewById(R.id.itemImageSecond);
//        ImageView itemImageThree = findViewById(R.id.itemImageThird);
//        Bitmap bitmapImage = BitmapFactory.decodeFile(path);
//
//        if (clickedIndex == 1) {
//            newImagePath[0] = path;
//            itemImageOne.setImageBitmap(bitmapImage);
//        } else if (clickedIndex == 2) {
//            newImagePath[1] = path;
//            itemImageTwo.setImageBitmap(bitmapImage);
//        } else if (clickedIndex == 3) {
//            newImagePath[2] = path;
//            itemImageThree.setImageBitmap(bitmapImage);
//        }
//    }


    @Override
    public void setNewImagePath(Uri uri, String path) {
        if (clickedIndex == 1) {
            newImagePath[0] = path;
            itemImageOne.setImageURI(uri);
        } else if (clickedIndex == 2) {
            newImagePath[1] = path;
            itemImageTwo.setImageURI(uri);
        } else if (clickedIndex == 3) {
            newImagePath[2] = path;
            itemImageThree.setImageURI(uri);
        }
    }


    public void deleteSelectedImage(View view) {
        clickedIndex = (Integer) view.getTag();

        if (clickedIndex == 1) {
            newImagePath[0] = "noImage";
            itemImageOne.setImageResource(R.drawable.noimage);
        } else if (clickedIndex == 2) {
            newImagePath[1] = "noImage";
            itemImageTwo.setImageResource(R.drawable.noimage);
        } else if (clickedIndex == 3) {
            newImagePath[2] = "noImage";
            itemImageThree.setImageResource(R.drawable.noimage);
        }
    }
}