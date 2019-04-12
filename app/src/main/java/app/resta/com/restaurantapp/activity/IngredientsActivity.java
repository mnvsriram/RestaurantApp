package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.IngredientArrayAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ingredient.IngredientAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.IngredientNameComparator;
import app.resta.com.restaurantapp.util.MyApplication;

public class IngredientsActivity extends BaseActivity {
    private final String TAG = "IngredientsActivity";
    String newImagePath = "";
    AuthenticationController authenticationController;
    IngredientAdminDaoI ingredientAdminDao;
    GridView ingredientsGrid = null;
    StorageReference storageRef;
    IngredientArrayAdapter adapter;
    List<Ingredient> ingredientsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_ingredients);
        newImagePath = "";
        authenticationController = new AuthenticationController(this);
        ingredientAdminDao = new IngredientAdminFireStoreDao();
        ingredientsGrid = findViewById(R.id.ingredientsItemsGrid);
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();

        adapter = new IngredientArrayAdapter(IngredientsActivity.this, android.R.layout.simple_list_item_1, convertListToArray(ingredientsData), buttonOnClickDelete);
        ingredientsGrid.setAdapter(adapter);
        setToolbar();
        setIngredientsGrid();
    }

    private void setIngredientsGrid() {
        ingredientAdminDao.getIngredients(new OnResultListener<List<Ingredient>>() {
            @Override
            public void onCallback(List<Ingredient> ingredients) {
                findViewById(R.id.ingredientsGridOnloadProgressBar).setVisibility(View.GONE);
                for (Ingredient ingredient : ingredients) {
                    addToGrid(ingredient);
                }
            }
        });
    }


    public Ingredient[] convertListToArray(List<Ingredient> ingredientList) {
        Collections.sort(ingredientList, new IngredientNameComparator());
        Object[] itemObjectArr = ingredientList.toArray();
        return Arrays.copyOf(itemObjectArr, itemObjectArr.length, Ingredient[].class);
    }

    public void addToGrid(Ingredient item) {
        ingredientsData.add(item);
        adapter.setData(convertListToArray(ingredientsData));
        adapter.notifyDataSetChanged();
    }

    private void deleteImage(final Ingredient ingredient) {
        Log.i(TAG, "Deleting image for ingredient" + ingredient.getName());
        StorageReference ingredientImageRef = storageRef.child(FireBaseStorageLocation.getIngredientImagesLocation() + ingredient.getId() + ".jpg");
        ingredientImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.i(TAG, "Successfully deleted image for  " + ingredient.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e(TAG, "onFailure: Not able to delete the image for " + ingredient.getName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

    public void goBack(View view) {
        onBackPressed();
    }


    private void getIngredientName(Ingredient ingredient) {
        EditText userInput = findViewById(R.id.ingredientNameSettings);
        String modifiedName = userInput.getText().toString();
        ingredient.setName(modifiedName);
    }


    public void save(View view) {
        Log.i(TAG, "Saving Ingredient");
        final Ingredient newIngredient = new Ingredient();
        getIngredientName(newIngredient);
        if (newIngredient.getName() == null || newIngredient.getName().trim().length() == 0) {
            String errorMessage = "Please enter a name for the Ingredient.";
            markNameFieldAsError(errorMessage);
        } else {
            ingredientAdminDao.getIngredients(new OnResultListener<List<Ingredient>>() {
                @Override
                public void onCallback(List<Ingredient> ingredients) {
                    boolean exists = false;
                    for (Ingredient ingredient : ingredients) {
                        if (ingredient.getName().equalsIgnoreCase(newIngredient.getName())) {
                            exists = true;
                        }
                    }
                    if (exists) {
                        String error = "A Ingredient already exists with this name. Please choose a different name.";
                        markNameFieldAsError(error);
                    } else {
                        markNameFieldAsSuccess();
                        Log.i(TAG, "Saving ingredient " + newIngredient.getName());
                        ingredientAdminDao.insertIngredient(newIngredient, new OnResultListener<Ingredient>() {
                            @Override
                            public void onCallback(Ingredient ingredient) {
                                saveImage(ingredient);
                            }
                        });
                    }
                }
            });

        }

    }

    private void markNameFieldAsSuccess() {
        final TextView nameLabel = findViewById(R.id.editIngredientsNameLabel);
        final EditText userInput = findViewById(R.id.ingredientNameSettings);
        final int greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        final int greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
        final TextView errorBlock = findViewById(R.id.ingredientNameValidationBlock);

        nameLabel.setTextColor(greenColor);
        userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        errorBlock.setText("");
        errorBlock.setVisibility(View.GONE);
    }

    private void markNameFieldAsError(String error) {
        final TextView nameLabel = findViewById(R.id.editIngredientsNameLabel);
        final EditText userInput = findViewById(R.id.ingredientNameSettings);
        final int errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        final TextView errorBlock = findViewById(R.id.ingredientNameValidationBlock);
        errorBlock.setText(error);
        errorBlock.setVisibility(View.VISIBLE);
        nameLabel.setTextColor(errorColor);
        userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
    }

    void saveImage(final Ingredient ingredient) {
        Log.i(TAG, "Saving image for ingredient " + ingredient.getName() + " and id " + ingredient.getId());
        if (ingredient.getId() != null && ingredient.getId().length() > 0 && newImagePath != null && newImagePath.length() > 0) {
            Uri uri = Uri.fromFile(new File(newImagePath));
            StorageReference storageReference = storageRef.child(FireBaseStorageLocation.getIngredientImagesLocation() + ingredient.getId() + ".jpg");
            ImageUtil.createImage(storageReference, uri, Ingredient.FIRESTORE_IMAGE_URL, new OnResultListener<String>() {
                @Override
                public void onCallback(String path) {
                    ingredientAdminDao.updateImageUrl(ingredient, path, new OnResultListener<String>() {
                        @Override
                        public void onCallback(String path) {
                            authenticationController.goToIngredientsSettingsPage();
                        }
                    });
                }
            });
        } else {
            authenticationController.goToIngredientsSettingsPage();
        }

    }


    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Ingredient ingredient) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage("Do you really want to delete the Ingredient - " + ingredient.getName() + "?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Deleting Ingredient " + ingredient.getName());
                                ingredientAdminDao.deleteIngredient(ingredient.getId());
                                deleteImage(ingredient);
                                Toast.makeText(IngredientsActivity.this, "Ingredient " + ingredient.getName() + " deleted.", Toast.LENGTH_LONG).show();
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


    public void setNewImagePath(Intent intent, String path) {
        newImagePath = path;
        ImageView ingredientsImageView = findViewById(R.id.ingredientsSettingsImage);
        Bitmap bitmapImage = BitmapFactory.decodeFile(path);
        ingredientsImageView.setImageBitmap(bitmapImage);
    }


    View.OnClickListener buttonOnClickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Ingredient ingredient = (Ingredient) v.getTag();
            if (ingredient != null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        IngredientsActivity.this);
                buildAlertWindow(alertDialogBuilder, ingredient);
            }
        }
    };
}
