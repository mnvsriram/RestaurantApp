package app.resta.com.restaurantapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.RestaurantItemExtraDataController;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
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

    List<String> tags = new ArrayList<>();
    List<String> ingredients = new ArrayList<>();

    private static String newImagePath = "";

    private final static int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private final static int RESULT_LOAD_IMAGE_FROM_APP = 2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        restaurantItemExtraDataController = new RestaurantItemExtraDataController();
        gl = (GridLayout) findViewById(R.id.ggwItemsGrid);
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
    }

    public void goBack(View view) {
        onBackPressed();
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
        String suggestion = button.getTag().toString().toLowerCase();
        restaurantItemExtraDataController.deleteTagItem(suggestion);
        tags.remove(suggestion);
    }


    public void removeFromIngredients(View view) {
        Button button = (Button) view;
        ((ViewGroup) view.getParent()).removeView(view);

        String suggestion = button.getTag().toString().toLowerCase();
        restaurantItemExtraDataController.deleteIngredientItem(suggestion);

        ingredients.remove(suggestion);
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
        String presentImageName = item.getImage();
        ImageView imageView = (ImageView) findViewById(R.id.itemImage);

        if (item.getImage() == null) {
            imageView.setImageResource(R.drawable.noimage);
        } else {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + presentImageName + ".jpeg";
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bmp);
        }
    }


    private void setParentSpinner() {
        Spinner parentSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> parents = new ArrayList<String>();
        parents.add("Select Parent");
        parents.addAll(MenuExpandableListAdapter.getHeaderItems());
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

    private void setAutoCompleteField() {
        //String[] dishes = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
        Map<String, RestaurantItem> dishes = MenuItemDao.getDishes();
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


    private void setTags() {
        if (!newItemCreation) {
            tags = TagsDao.getTags(item.getId());
            for (String tag : tags) {
                addTagsButton(tag);
            }
        }
    }

    private void setIngredients() {
        if (!newItemCreation) {
            ingredients = IngredientDao.getIngredients(item.getId());
            for (String ingredient : ingredients) {
                addIngredientsButton(ingredient);
            }
        }
    }


    private void addTagsButton(String item) {

        GridLayout gl = (GridLayout) findViewById(R.id.tagsItemsGrid);
        Button ggwItemButton = new Button(MyApplication.getAppContext());
        ggwItemButton.setClickable(true);
        ggwItemButton.setText(item);
        ggwItemButton.setTag(item);

        ggwItemButton.setMaxHeight(10);
        ggwItemButton.setMaxWidth(20);
        //ggwItemButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
        ggwItemButton.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.colorAccent));
        //ggwItemButton.setBackgroundResource(R.drawable.edit);
        ggwItemButton.setOnClickListener(tagButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gl.addView(ggwItemButton, lp);
    }

    private void addIngredientsButton(String ingredient) {
        GridLayout gl = (GridLayout) findViewById(R.id.ingredientItemsGrid);
        Button ggwItemButton = new Button(MyApplication.getAppContext());
        ggwItemButton.setClickable(true);
        ggwItemButton.setText(ingredient);
        ggwItemButton.setTag(ingredient);

        ggwItemButton.setMaxHeight(10);
        ggwItemButton.setMaxWidth(20);
        //ggwItemButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
        ggwItemButton.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.colorAccent));
        //ggwItemButton.setBackgroundResource(R.drawable.edit);
        ggwItemButton.setOnClickListener(ingredientButtonOnClickListener);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gl.addView(ggwItemButton, lp);
    }

    public void addToGoesGreatWith(View view) {
        AutoCompleteTextView goesGreatWithText = (AutoCompleteTextView) findViewById(R.id.goesGreatWithSuggestion);
        String suggestion = goesGreatWithText.getText().toString();
        RestaurantItem itemSuggested = MenuItemDao.getDishes().get(suggestion);
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
        String suggestion = tagsWithText.getText().toString().toLowerCase();

        TextView tagsErrors = (TextView) findViewById(R.id.tagsValidationBlock);

        if (tags.contains(suggestion)) {
            Toast.makeText(this, suggestion + " is already present.", Toast.LENGTH_LONG);
            tagsErrors.setText(suggestion + " is already present.");
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

        restaurantItemExtraDataController.addTagItem(suggestion);
        tagsWithText.setText("");
        tags.add(suggestion);
        addTagsButton(suggestion);
    }


    public void addToIngredients(View view) {
        AutoCompleteTextView ingrdientsWithText = (AutoCompleteTextView) findViewById(R.id.ingredientsSuggestion);
        String suggestion = ingrdientsWithText.getText().toString().toLowerCase();
        TextView ingredientsErrors = (TextView) findViewById(R.id.ingredientsValidationBlock);

        if (ingredients.contains(suggestion)) {
            Toast.makeText(this, suggestion + " is already present.", Toast.LENGTH_LONG);
            ingredientsErrors.setText(suggestion + " is already present.");
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

        restaurantItemExtraDataController.addIngredientItem(suggestion);
        ingrdientsWithText.setText("");
        addIngredientsButton(suggestion);
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
        setAutoCompleteField();
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

    private void getModifiedParent() {
        Spinner parentSpinner = (Spinner) findViewById(R.id.spinner);

        String modifiedParent = parentSpinner.getSelectedItem().toString();
        RestaurantItem parent = MenuExpandableListAdapter.getHeaderMap().get(modifiedParent);
        if (parent != null) {
            Long modifiedParentId = parent.getId();
            item.setParentId(modifiedParentId);
        } else {
            item.setParentId(-1);
        }
    }

    private void getModifiedImage() {
        String newImageName = item.getName() + item.getParentItem().getName();
        String oldImageName = item.getImage();
        ImageSaver imageSaver = new ImageSaver(this);

        if (newImagePath != null && newImagePath.length() > 0) {
            item.setImage(newImageName);
            Bitmap mp = BitmapFactory.decodeFile(newImagePath);
            imageSaver.deleteImage(oldImageName);
            imageSaver.saveImageToAppFolder(mp, item.getImage());
        } else {
            if (newItemCreation) {
                item.setImage(newImageName);
                Bitmap noImage = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),
                        R.drawable.noimage);
                imageSaver.saveImageToAppFolder(noImage, item.getImage());

            } else {
                if (!newImageName.equalsIgnoreCase(oldImageName)) {
                    //name of the item is modified. so the image have to be changed and the old image deleted.
                    String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
                    String filePath = path + oldImageName + ".jpeg";
                    Bitmap oldImage = BitmapFactory.decodeFile(filePath);
                    item.setImage(newImageName);
                    imageSaver.saveImageToAppFolder(oldImage, newImageName);
                    imageSaver.deleteImage(oldImageName);
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

    public void save(View view) {

        getModifiedItemName();
        getModifiedPrice();
        getModifiedStatus();
        getModifiedParent();
        getModifiedDescription();
        if (validateInput()) {
            getModifiedImage();
            MenuItemDao.insertOrUpdateMenuItem(item);
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

    public void loadImagesFromGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView itemImage = (ImageView) findViewById(R.id.itemImage);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE_FROM_APP) {
                if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                    File selectedFile = new File
                            (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                    newImagePath = selectedFile.getAbsolutePath();
                    itemImage.setImageBitmap(BitmapFactory.decodeFile(newImagePath));
                }
            } else if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                newImagePath = picturePath;
                itemImage.setImageBitmap(BitmapFactory.decodeFile(newImagePath));
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}