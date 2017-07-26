package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.RestaurantItemExtraDataController;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.Paths;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.validator.RestaurantItemValidator;

public class ItemEditActivity extends BaseActivity {

    RestaurantItem item = null;
    int groupPosition = 0;
    int childPosition = 0;
    boolean newItemCreation = false;
    private RestaurantItemExtraDataController restaurantItemExtraDataController;
    List<RestaurantItem> ggwItems = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    List<Ingredient> ingredients = new ArrayList<>();
    private int clickedIndex = -1;
    private TagsDao tagsDao;
    private IngredientDao ingredientDao;
    private String[] newImagePath = new String[3];
    private final static int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private final static int RESULT_LOAD_IMAGE_FROM_APP = 2;
    Map<String, Ingredient> ingredientsRefDataMap = new HashMap<>();
    Map<String, Tag> tagsRefDataMap = new HashMap<>();


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

    GridLayout gl = null;
    GridLayout tagsGrid = null;
    GridLayout ingredientsGrid = null;

    private void loadTagsRefData() {
        List<Tag> tagsRefDataObj = tagsDao.getTagsRefData();
        for (Tag tag : tagsRefDataObj) {
            tagsRefDataMap.put(tag.getName().toLowerCase(), tag);
        }
    }

    private void loadIngredientsRefData() {
        List<Ingredient> ingredientsRefDataObj = ingredientDao.getIngredientsRefData();
        for (Ingredient ingredient : ingredientsRefDataObj) {
            ingredientsRefDataMap.put(ingredient.getName().toLowerCase(), ingredient);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        restaurantItemExtraDataController = new RestaurantItemExtraDataController();
        tagsDao = new TagsDao();
        ingredientDao = new IngredientDao();
        gl = (GridLayout) findViewById(R.id.ggwItemsGrid);
        tagsGrid = (GridLayout) findViewById(R.id.tagsItemsGrid);
        ingredientsGrid = (GridLayout) findViewById(R.id.ingredientItemsGrid);

        Intent intent = getIntent();
        if (intent.hasExtra("item_obj")) {
            item = (RestaurantItem) intent.getSerializableExtra("item_obj");
        }
        groupPosition = intent.getIntExtra("item_group_position", 0);
        childPosition = intent.getIntExtra("item_child_position", 0);


        setFieldValues();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadIngredientsRefData();
        loadTagsRefData();
        setImageIcons();
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
        authenticationController.goToMenuPage();
    }

    public void removeFromGoesGreatWith(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        Long id = Long.parseLong(button.getTag().toString());
        restaurantItemExtraDataController.deleteGGWItem(id);


        RestaurantItem deletedItem = new RestaurantItem();
        deletedItem.setId(id);
        ggwItems.remove(deletedItem);
    }


    public void removeFromTags(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        Long tagSelectedForDeletion = (Long) button.getTag();

        restaurantItemExtraDataController.deleteTagItem(tagSelectedForDeletion);
        tags.remove(tagSelectedForDeletion);
    }


    public void removeFromIngredients(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);
        Long itemSelectedForDeletion = (Long) button.getTag();
        restaurantItemExtraDataController.deleteIngredientItem(itemSelectedForDeletion);
        ingredients.remove(itemSelectedForDeletion);
    }

    private void setItemName() {
        EditText userInput = (EditText) findViewById(R.id.editItemName);
        if (item.getName() != null) {
            userInput.setText(item.getName());
        }
    }

    private void setPriceName() {
        EditText price = (EditText) findViewById(R.id.editItemPrice);
        if (item.getPrice() != null) {
            price.setText(item.getPrice());
        }

    }

    private void setDescription() {
        EditText description = (EditText) findViewById(R.id.editItemDescription);
        if (item.getDescription() != null) {

            description.setText(item.getDescription());
        }
    }

    private void setImage() {
        ImageView itemImageOne = (ImageView) findViewById(R.id.itemImageFirst);
        ImageView itemImageTwo = (ImageView) findViewById(R.id.itemImageSecond);
        ImageView itemImageThree = (ImageView) findViewById(R.id.itemImageThird);
        RestaurantImage[] images = item.getImages();

        if (images != null) {
            setImage(itemImageOne, images[0]);
            setImage(itemImageTwo, images[1]);
            setImage(itemImageThree, images[2]);
        }
    }


    private void setImage(ImageView imageView, RestaurantImage image) {
        imageView.setImageResource(R.drawable.noimage);
        if (image != null && image.getName() != null) {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + image.getName() + ".jpeg";
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bmp);
            }
        }
    }


    private void setParentSpinner() {
        Spinner parentSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> parents = new ArrayList<String>();
        parents.add("Select Parent");
        parents.addAll(MenuItemDao.getAllParentItemsByName().keySet());
        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MyApplication.getAppContext(), android.R.layout.simple_spinner_item, parents);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        parentSpinner.setAdapter(dataAdapter);
        RestaurantItem parent = item.getParentItem();
        if (item.getParentItem() == null) {
            parent = item;
        }
        parentSpinner.setSelection(dataAdapter.getPosition(parent.getName()));

    }

    private void setStatus() {
        ToggleButton status = (ToggleButton) findViewById(R.id.editItemToggleActive);
               /* if (item.getActive() != null) {
                    status.setText(item.getActive());
                    if (item.getActive().equalsIgnoreCase("Y")) {
                        status.setChecked(true);
                    }
                }*/

        if (item.getActive() == null || item.getActive().equalsIgnoreCase("Y")) {
            status.setText("Y");
            status.setChecked(true);
        }
    }

    private void setGoesGreatWith() {
        //String[] dishes = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
        Map<String, RestaurantItem> dishes = MenuItemDao.getAllChildItemsByName();
        String[] dishesArray = dishes.keySet().toArray(new String[dishes.keySet().size()]);

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, dishesArray);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.goesGreatWithSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setGGWItems() {
        if (!newItemCreation) {
            ggwItems = GGWDao.getGGWMappings(item.getId());
            item.setGgwItems(ggwItems);
            gl.removeAllViews();
            gl.setColumnCount(4);
            gl.setRowCount(3);
            for (RestaurantItem ggwItem : ggwItems) {
                addGGWButton(ggwItem);
            }
        }
    }

    private void addGGWButton(RestaurantItem item) {

        Button ggwItemButton = new Button(MyApplication.getAppContext());
        ggwItemButton.setClickable(true);
        ggwItemButton.setText(item.getName());
        ggwItemButton.setTag(item.getId());

        ggwItemButton.setMaxHeight(10);
        ggwItemButton.setMaxWidth(20);
        //ggwItemButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
        ggwItemButton.setTextColor(Color.RED);
        ggwItemButton.setOnClickListener(ggwButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gl.addView(ggwItemButton, lp);

    }

    private void setAutoCompleteTags() {
        List<Tag> tags = tagsDao.getTagsRefData();
        String[] tagArr = new String[tags.size()];
        int i = 0;
        for (Tag tag : tags) {
            tagArr[i++] = tag.getName();
        }
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, tagArr);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.tagsSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setTags() {
        if (!newItemCreation) {
            tags = TagsDao.getTagsForItem(item.getId());
            for (Tag tag : tags) {
                addTagsButton(tag);
            }
        }
    }

    private void setAutoCompleteIngredients() {
        ingredients = ingredientDao.getIngredientsRefData();
        String[] ingredientArr = new String[ingredients.size()];
        int i = 0;
        for (Ingredient ingredient : ingredients) {
            ingredientArr[i++] = ingredient.getName();
        }
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, ingredientArr);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.ingredientsSuggestion);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
    }

    private void setIngredients() {
        if (!newItemCreation) {
            List<Ingredient> ingredients = IngredientDao.getIngredientsForItem(item.getId());
            for (Ingredient ingredient : ingredients) {
                addIngredientsButton(ingredient);
            }
        }
    }

    private void addTagsButton(Tag tag) {
        Button tagButton = new Button(MyApplication.getAppContext());
        tagButton.setClickable(true);
        tagButton.setText(tag.getName());
        tagButton.setTag(tag.getId());

        tagButton.setMaxHeight(10);
        tagButton.setMaxWidth(20);
        //ggwItemButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
        tagButton.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.colorAccent));
        //ggwItemButton.setBackgroundResource(R.drawable.edit);
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
        AutoCompleteTextView goesGreatWithText = (AutoCompleteTextView) findViewById(R.id.goesGreatWithSuggestion);
        String suggestion = goesGreatWithText.getText().toString();
        RestaurantItem itemSuggested = MenuItemDao.getAllChildItemsByName().get(suggestion);
        List<RestaurantItem> ggwItemsFromDB = item.getGgwItems();

        TextView ggwErrors = (TextView) findViewById(R.id.ggwValidationBlock);

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

            restaurantItemExtraDataController.addGGWItem(itemSuggested.getId());
            goesGreatWithText.setText("");
            ggwItems.add(itemSuggested);
            addGGWButton(itemSuggested);
        }
    }
    public void addToTags(View view) {
        AutoCompleteTextView tagsWithText = (AutoCompleteTextView) findViewById(R.id.tagsSuggestion);
        String suggestion = tagsWithText.getText().toString().trim().toLowerCase();
        TextView tagsErrors = (TextView) findViewById(R.id.tagsValidationBlock);

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
            } else if (tags.size() >= Paths.MAX_GGW_ITEMS) {
                Toast.makeText(this, "Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.", Toast.LENGTH_LONG);
                tagsErrors.setText("Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.");
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
        AutoCompleteTextView ingrdientsWithText = (AutoCompleteTextView) findViewById(R.id.ingredientsSuggestion);
        String suggestion = ingrdientsWithText.getText().toString().trim().toLowerCase();
        TextView ingredientsErrors = (TextView) findViewById(R.id.ingredientsValidationBlock);
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
            } else if (ingredients.size() >= Paths.MAX_GGW_ITEMS) {
                Toast.makeText(this, "Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.", Toast.LENGTH_LONG);
                ingredientsErrors.setText("Cannot add more than " + Paths.MAX_GGW_ITEMS + " items.");
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

    private void setFieldValues() {
        if (item.getId() == 0) {
            newItemCreation = true;
        }
        setItemName();
        setPriceName();
        setDescription();
        setImage();
        setParentSpinner();
        setStatus();
        setGoesGreatWith();
        setGGWItems();
        setAutoCompleteTags();
        setTags();
        setAutoCompleteIngredients();
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

    private void getModifiedParent() {
        Spinner parentSpinner = (Spinner) findViewById(R.id.spinner);
        String modifiedParent = parentSpinner.getSelectedItem().toString();
        RestaurantItem parent = MenuItemDao.getAllParentItemsByName().get(modifiedParent);
        if (parent != null) {
            Long modifiedParentId = parent.getId();
            item.setParentId(modifiedParentId);
            item.setMenuGroupId(parent.getMenuGroupId());
        } else {
            item.setParentId(-1);
        }
    }

    private void getModifiedImage() {
        getModifiedImage(0, item.getImage(0));
        getModifiedImage(1, item.getImage(1));
        getModifiedImage(2, item.getImage(2));
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

    private void getModifiedImage(int index, String oldImageName) {
        String newImageName = item.getName() + "_" + item.getParentItem().getName() + "_" + index;
        newImageName = newImageName.replaceAll(" ", "_");
        //ImageSaver imageSaver = new ImageSaver(this);

        if (newImagePath[index] != null && newImagePath[index].length() > 0) {
            if (item.getImages() == null) {
                item.setImages(new RestaurantImage[3]);
            }
            RestaurantImage image = item.getImages()[index];
            if (image == null) {
                image = new RestaurantImage(item.getId(), newImageName, null);
                item.setImage(index, image);
            } else {
                item.getImages()[index].setName(newImageName);
            }

            Bitmap mp = BitmapFactory.decodeFile(newImagePath[index]);


            String name = item.getImage(index);
            if (newImagePath[index].equalsIgnoreCase("noImage")) {
                item.setImage(index, null);
            } else {
                //imageSaver.deleteImage(oldImageName);
                saveImageLater(mp, item.getImage(index));
                //imageSaver.saveImageToAppFolder(mp, item.getImage(index));
            }

        } else {
            if (newItemCreation) {
                //  item.setImage(index, new RestaurantImage(item.getId(), newImageName));
                //Bitmap noImage = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),
                //      R.drawable.noimage);
                //imageSaver.saveImageToAppFolder(noImage, item.getImage(index));
            } else {
                if (oldImageName != null && !newImageName.equalsIgnoreCase(oldImageName)) {
                    //name of the item is modified. so the image have to be changed and the old image deleted.
                    String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
                    String filePath = path + oldImageName + ".jpeg";
                    File imageFile = new File(filePath);
                    item.setImage(index, new RestaurantImage(item.getId(), "noImage"));
                    item.getImages()[index].setName("noImage");
                    if (imageFile.exists()) {
                        Bitmap oldImage = BitmapFactory.decodeFile(filePath);
                        item.setImage(index, new RestaurantImage(item.getId(), newImageName));

                        saveImageLater(oldImage, newImageName);
                        //imageSaver.saveImageToAppFolder(oldImage, newImageName);
                        //      imageSaver.deleteImage(oldImageName);
                    }

                }
                //image is not modified
            }
        }
    }

    private void saveGGWMappings() {
        if (restaurantItemExtraDataController.getGgwItemsAdded().size() > 0) {
            GGWDao.insertGGWItems(item.getId(), restaurantItemExtraDataController.getGgwItemsAdded());
        }
        if (restaurantItemExtraDataController.getGgwItemsDeleted().size() > 0) {
            GGWDao.deleteGGWItems(item.getId(), restaurantItemExtraDataController.getGgwItemsDeleted());
        }
    }


    private void saveTags() {
        if (restaurantItemExtraDataController.getTagsAdded().size() > 0) {
            TagsDao.insertTags(item.getId(), restaurantItemExtraDataController.getTagsAdded());
        }
        if (restaurantItemExtraDataController.getTagsDeleted().size() > 0) {
            TagsDao.deleteTags(item.getId(), restaurantItemExtraDataController.getTagsDeleted());
        }
    }

    private void saveIngredients() {
        if (restaurantItemExtraDataController.getIngredientsAdded().size() > 0) {
            IngredientDao.insertIngredients(item.getId(), restaurantItemExtraDataController.getIngredientsAdded());
        }
        if (restaurantItemExtraDataController.getIngredientsDeleted().size() > 0) {
            IngredientDao.deleteIngredients(item.getId(), restaurantItemExtraDataController.getIngredientsDeleted());
        }
    }

    private boolean validateInput() {
        RestaurantItemValidator validator = new RestaurantItemValidator(this, item);
        return validator.validate();
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

    public void save(View view) {

        getModifiedItemName();
        getModifiedPrice();
        getModifiedStatus();
        getModifiedParent();
        getModifiedDescription();
        if (validateInput()) {
            getModifiedImage();
            MenuItemDao.insertOrUpdateMenuItem(item);
            saveImagesToPhone();
            saveGGWMappings();
            saveIngredients();
            saveTags();
            dispatchToMenuPage();
        }
    }

    private void dispatchToMenuPage() {
        Intent intent = null;
        String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");
        if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
            intent = new Intent(this, NarrowMenuActivity.class);
        } else {
            intent = new Intent(this, HorizontalMenuActivity.class);
        }
        //intent.putExtra("test", "hello");
        intent.putExtra("groupToOpen", item.getParentId());
        intent.putExtra("modifiedItemGroupPosition", groupPosition);
        intent.putExtra("modifiedItemChildPosition", childPosition);
        intent.putExtra("modifiedItemId", item.getId());

        startActivity(intent);
    }


    public void loadOtherAppImages(View view) {
        Intent intent = new Intent(this, FilePicker.class);
        intent.putExtra(FilePicker.IMAGE_ONLY_PICKER, "true");
        startActivityForResult(intent, RESULT_LOAD_IMAGE_FROM_APP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void showSelectImageFromDialogForItemEdit(View view) {
        clickedIndex = (Integer) view.getTag();
        showSelectImageFromDialog(view);
    }

    public void setNewImagePath(Intent intent, String path) {
        ImageView itemImageOne = (ImageView) findViewById(R.id.itemImageFirst);
        ImageView itemImageTwo = (ImageView) findViewById(R.id.itemImageSecond);
        ImageView itemImageThree = (ImageView) findViewById(R.id.itemImageThird);
        Bitmap bitmapImage = BitmapFactory.decodeFile(path);

        if (clickedIndex == 1) {
            newImagePath[0] = path;
            itemImageOne.setImageBitmap(bitmapImage);
        } else if (clickedIndex == 2) {
            newImagePath[1] = path;
            itemImageTwo.setImageBitmap(bitmapImage);
        } else if (clickedIndex == 3) {
            newImagePath[2] = path;
            itemImageThree.setImageBitmap(bitmapImage);
        }
    }


    public void deleteSelectedImage(View view) {
        clickedIndex = (Integer) view.getTag();

        ImageView itemImageOne = (ImageView) findViewById(R.id.itemImageFirst);
        ImageView itemImageTwo = (ImageView) findViewById(R.id.itemImageSecond);
        ImageView itemImageThree = (ImageView) findViewById(R.id.itemImageThird);
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