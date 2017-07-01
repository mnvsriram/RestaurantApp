package app.resta.com.restaurantapp.validator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.MyApplication;

public class TagsValidator {
    private Tag tag;
    private Activity activity;
    int errorColor;
    int greenColor;
    int greyColor;
    private boolean goAhead = true;
    private TagsDao tagsDao;

    public TagsValidator(Activity activity, Tag tag) {
        this.tag = tag;
        this.activity = activity;
        this.errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        this.greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        this.greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
        this.tagsDao = new TagsDao();
    }


    public boolean validate() {
        goAhead = true;
        validateTagRefDataAdd();
        return goAhead;
    }

    private void validateTagRefDataAdd() {
        String errorText = "";
        errorText = validateName();
        TextView errorBlock = (TextView) activity.findViewById(R.id.tagNameValidationBlock);
        if (errorText.length() > 0) {
            goAhead = false;
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.VISIBLE);
        } else {
            errorBlock.setText(errorText);
            errorBlock.setVisibility(View.GONE);
        }
    }

    private String validateName() {
        String nameError = "";
        TextView nameLabel = (TextView) activity.findViewById(R.id.editTagsNameLabel);
        EditText userInput = (EditText) activity.findViewById(R.id.tagNameSettings);

        if (tag.getName() == null || tag.getName().trim().length() == 0) {
            nameError = "Please enter a name for the Tag.";
        } else {
            //1==1 write the below method in tagsdao and see if an item exists with the same name...if exists, then throw and error..
            Tag foundItem = tagsDao.getTagRefData(tag.getName(), -1);
            if (foundItem != null) {
                nameError = "A Tag already exists with this name. Please choose a different name.";
            }
        }

        if (nameError.length() > 0) {
            nameLabel.setTextColor(errorColor);
            userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            nameLabel.setTextColor(greenColor);
            userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);

        }
        return nameError;
    }
}
