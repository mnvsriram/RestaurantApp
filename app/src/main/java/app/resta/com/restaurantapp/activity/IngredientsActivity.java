package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.validator.IngredientsValidator;

public class IngredientsActivity extends BaseActivity {
    String newImagePath = "";
    AuthenticationController authenticationController;
    IngredientDao ingredientDao;
    GridLayout IngredientsGrid = null;
    IngredientsValidator ingredientsValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_ingredients);
        ingredientsValidator = null;
        newImagePath = "";
        authenticationController = new AuthenticationController(this);
        ingredientDao = new IngredientDao();
        IngredientsGrid = (GridLayout) findViewById(R.id.ingredientsItemsGrid);
        IngredientsGrid.removeAllViews();
        IngredientsGrid.setColumnCount(3);

        setToolbar();
        setIngredientsGrid();
    }

    private void setIngredientsGrid() {
        List<Ingredient> ingredients = ingredientDao.getIngredientsRefData();
        for (Ingredient ingredient : ingredients) {
            addIngredientsRow(ingredient);
        }
    }

    private void addIngredientsRow(Ingredient ingredient) {
        showIngredientNameInGrid(ingredient.getName());
        showIngredientsImageInGrid(ingredient.getImage());
        showDeleteIngredientsButtonInImageInGrid(ingredient);
    }

    private void showIngredientNameInGrid(String item) {
        TextView IngredientName = new TextView(MyApplication.getAppContext());
        IngredientName.setText(item);
        IngredientName.setWidth(100);
        IngredientName.setTextColor(Color.BLACK);
        IngredientName.setTextSize(20);
        //ggwItemButton.setIngredient(item);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        IngredientsGrid.addView(IngredientName, lp);
    }


    private void showIngredientsImageInGrid(String imageName) {
        ImageButton ggwItemButton = new ImageButton(MyApplication.getAppContext());
        if (imageName == null || imageName.length() == 0 || imageName.equalsIgnoreCase("noImage")) {
            ggwItemButton.setBackgroundResource(R.drawable.noimage);
        } else {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + imageName + ".jpeg";
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Drawable d = new BitmapDrawable(getResources(), bmp);
            ggwItemButton.setBackground(d);
        }
        ggwItemButton.setClickable(true);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(60, 60);
        IngredientsGrid.addView(ggwItemButton, lp);
    }

    private void showDeleteIngredientsButtonInImageInGrid(final Ingredient Ingredient) {
        ImageButton ggwItemButton = new ImageButton(MyApplication.getAppContext());
        ggwItemButton.setBackgroundResource(R.drawable.deletered);
        ggwItemButton.setClickable(true);
        //ggwItemButton.setIngredient(Ingredient.getId());

        ggwItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        IngredientsActivity.this);
                buildAlertWindow(alertDialogBuilder, Ingredient);
            }
        });


        // 1==1 dialog box to confirm deletion is required here
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(60, 60);
        IngredientsGrid.addView(ggwItemButton, lp);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void selectImageForIngredient(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.edit);
        builderSingle.setTitle("Select Image From:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Gallery");
        arrayAdapter.add("Camera");
        arrayAdapter.add("All Folders");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                if (strName.equalsIgnoreCase("camera")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                } else if (strName.equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                } else if (strName.equals("All Folders")) {
                    Intent intent = new Intent(IngredientsActivity.this, FilePicker.class);
                    intent.putExtra(FilePicker.IMAGE_ONLY_PICKER, "true");
                    startActivityForResult(intent, 2);
                }
            }
        });
        builderSingle.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView IngredientsImageView = (ImageView) findViewById(R.id.ingredientsSettingsImage);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    IngredientsImageView.setImageURI(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    newImagePath = picturePath;
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    IngredientsImageView.setImageURI(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    newImagePath = picturePath;
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    if (imageReturnedIntent.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        File selectedFile = new File
                                (imageReturnedIntent.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        newImagePath = selectedFile.getAbsolutePath();
                        IngredientsImageView.setImageBitmap(BitmapFactory.decodeFile(newImagePath));
                    }
                }
                break;
        }
    }

    private void getIngredientName(Ingredient Ingredient) {
        EditText userInput = (EditText) findViewById(R.id.ingredientNameSettings);
        String modifiedName = userInput.getText().toString();
        Ingredient.setName(modifiedName);
    }


    private void getIngredientImage(Ingredient Ingredient) {
        String newImageName = Ingredient.getName() + "Ingredient";
        String oldImageName = Ingredient.getImage();
        ImageSaver imageSaver = new ImageSaver(this);
        if (newImagePath != null && newImagePath.length() > 0) {
            Ingredient.setImage(newImageName);
            Bitmap mp = BitmapFactory.decodeFile(newImagePath);
            imageSaver.deleteImage(oldImageName);
            imageSaver.saveImageToAppFolder(mp, Ingredient.getImage());
        } else {
            Ingredient.setImage("noImage");
        }
    }

    public void save(View view) {
        Ingredient newIngredient = new Ingredient();
        getIngredientName(newIngredient);
        getIngredientImage(newIngredient);
        ingredientsValidator = new IngredientsValidator(this, newIngredient);
        if (ingredientsValidator.validate()) {
            ingredientDao.insertIngredientRefData(newIngredient);
            authenticationController.goToIngredientsSettingsPage();
        }
    }

    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Ingredient Ingredient) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage("Do you really want to delete the Ingredient - " + Ingredient.getName() + "?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ingredientDao.deleteIngredientRefData(Ingredient.getAppId());
                                authenticationController.goToIngredientsSettingsPage();
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

}
