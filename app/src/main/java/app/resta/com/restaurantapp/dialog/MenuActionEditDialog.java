package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.AppFontEnum;
import app.resta.com.restaurantapp.model.ColorCodeEnum;
import app.resta.com.restaurantapp.model.FontFaceEnum;
import app.resta.com.restaurantapp.model.FontSizeEnum;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardActionStyle;

/**
 * Created by Sriram on 23/01/2018.
 */

public class MenuActionEditDialog extends Dialog {
    public Activity c;
    public Dialog d;
    AdapterView.OnItemSelectedListener fontSpinnerListener;
    AdapterView.OnItemSelectedListener fontSizeSpinnerListener;
    AdapterView.OnItemSelectedListener fontFaceSpinnerListener;
    AdapterView.OnItemSelectedListener fontColorSpinnerListener;

    private MenuCardAction dataObject;

    public MenuActionEditDialog(Activity activity, MenuCardAction item) {
        super(activity);
        this.c = activity;
        this.dataObject = item;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_menu_action_edit);

        setFontSpinners();
        setFontSizeSpinners();
        setFontFaceSpinners();
        setFontColorSpinners();

        setMenuNameSpinnerValues();
        setMenuDescSpinnerValues();
        setGroupNameSpinnerValues();
        setItemNameSpinnerValues();
        setItemDescSpinnerValues();
    }

    private void setNoneFont(Spinner fontSpinner, ArrayAdapter adapter) {
        fontSpinner.setSelection(adapter.getPosition(AppFontEnum.None.name()));
    }


    private void setNoneFontColor(Spinner fontColorSpinner, ArrayAdapter adapter) {
        fontColorSpinner.setSelection(adapter.getPosition(ColorCodeEnum.None.name()));
    }

    private void setNoneFontSize(Spinner fontSizeSpinner, ArrayAdapter adapter) {
        fontSizeSpinner.setSelection(adapter.getPosition(FontSizeEnum.None.name()));
    }


    private void setNoneFontFace(Spinner fontFaceSpinner, ArrayAdapter adapter) {
        fontFaceSpinner.setSelection(adapter.getPosition(FontFaceEnum.None.name()));
    }

    private void setMenuNameSpinnerValues() {
        MenuCardActionStyle menuNameStyle = dataObject.getMenuNameStyle();
        Spinner fontSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontSpinner);
        ArrayAdapter menuNameAdapter = (ArrayAdapter) fontSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuNameStyle != null && menuNameStyle.getFont() != null) {
            fontSpinner.setSelection(menuNameAdapter.getPosition(menuNameStyle.getFont()));
        } else {
            setNoneFont(fontSpinner, menuNameAdapter);
        }

        Spinner fontSizeSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontSizeSpinner);
        ArrayAdapter fontSizeAdapter = (ArrayAdapter) fontSizeSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuNameStyle != null && menuNameStyle.getFontSize() != null) {
            fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(menuNameStyle.getFontSize()));
        } else {
            setNoneFontSize(fontSizeSpinner, fontSizeAdapter);
        }
        Spinner fontFaceSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontFaceSpinner);
        ArrayAdapter fontFaceAdapter = (ArrayAdapter) fontFaceSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuNameStyle != null && menuNameStyle.getFontFace() != null) {
            fontFaceSpinner.setSelection(fontFaceAdapter.getPosition(menuNameStyle.getFontFace()));
        } else {
            setNoneFontFace(fontFaceSpinner, fontFaceAdapter);
        }
        Spinner fontColorSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontColorSpinner);
        ArrayAdapter fontColorAdapter = (ArrayAdapter) fontColorSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuNameStyle != null && menuNameStyle.getFontColor() != null) {
            fontColorSpinner.setSelection(fontColorAdapter.getPosition(menuNameStyle.getFontColor()));
        } else {
            setNoneFontColor(fontColorSpinner, fontColorAdapter);
        }
    }


    private void setMenuDescSpinnerValues() {
        MenuCardActionStyle menuDescStyle = dataObject.getMenuDescStyle();
        Spinner fontSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontSpinner);
        ArrayAdapter menuNameAdapter = (ArrayAdapter) fontSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuDescStyle != null && menuDescStyle.getFont() != null) {
            fontSpinner.setSelection(menuNameAdapter.getPosition(menuDescStyle.getFont()));
        } else {
            setNoneFont(fontSpinner, menuNameAdapter);
        }
        Spinner fontSizeSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontSizeSpinner);
        ArrayAdapter fontSizeAdapter = (ArrayAdapter) fontSizeSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuDescStyle != null && menuDescStyle.getFontSize() != null) {
            fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(menuDescStyle.getFontSize()));
        } else {
            setNoneFontSize(fontSizeSpinner, fontSizeAdapter);
        }
        Spinner fontFaceSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontFaceSpinner);
        ArrayAdapter fontFaceAdapter = (ArrayAdapter) fontFaceSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuDescStyle != null && menuDescStyle.getFontFace() != null) {
            fontFaceSpinner.setSelection(fontFaceAdapter.getPosition(menuDescStyle.getFontFace()));
        } else {
            setNoneFontFace(fontFaceSpinner, fontFaceAdapter);
        }

        Spinner fontColorSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontColorSpinner);
        ArrayAdapter fontColorAdapter = (ArrayAdapter) fontColorSpinner.getAdapter(); //cast to an ArrayAdapter
        if (menuDescStyle != null && menuDescStyle.getFontColor() != null) {
            fontColorSpinner.setSelection(fontColorAdapter.getPosition(menuDescStyle.getFontColor()));
        } else {
            setNoneFontColor(fontColorSpinner, fontColorAdapter);
        }
    }


    private void setGroupNameSpinnerValues() {
        MenuCardActionStyle groupNameStyle = dataObject.getGroupNameStyle();
        Spinner fontSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontSpinner);
        ArrayAdapter menuNameAdapter = (ArrayAdapter) fontSpinner.getAdapter(); //cast to an ArrayAdapter
        if (groupNameStyle != null && groupNameStyle.getFont() != null) {
            fontSpinner.setSelection(menuNameAdapter.getPosition(groupNameStyle.getFont()));
        } else {
            setNoneFont(fontSpinner, menuNameAdapter);
        }
        Spinner fontSizeSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontSizeSpinner);
        ArrayAdapter fontSizeAdapter = (ArrayAdapter) fontSizeSpinner.getAdapter(); //cast to an ArrayAdapter
        if (groupNameStyle != null && groupNameStyle.getFontSize() != null) {
            fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(groupNameStyle.getFontSize()));
        } else {
            setNoneFontSize(fontSizeSpinner, fontSizeAdapter);
        }

        Spinner fontFaceSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontFaceSpinner);
        ArrayAdapter fontFaceAdapter = (ArrayAdapter) fontFaceSpinner.getAdapter(); //cast to an ArrayAdapter
        if (groupNameStyle != null && groupNameStyle.getFontFace() != null) {
            fontFaceSpinner.setSelection(fontFaceAdapter.getPosition(groupNameStyle.getFontFace()));
        } else {
            setNoneFontFace(fontFaceSpinner, fontFaceAdapter);
        }

        Spinner fontColorSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontColorSpinner);
        ArrayAdapter fontColorAdapter = (ArrayAdapter) fontColorSpinner.getAdapter(); //cast to an ArrayAdapter
        if (groupNameStyle != null && groupNameStyle.getFontColor() != null) {
            fontColorSpinner.setSelection(fontColorAdapter.getPosition(groupNameStyle.getFontColor()));
        } else {
            setNoneFontColor(fontColorSpinner, fontColorAdapter);
        }
    }

    private void setItemNameSpinnerValues() {
        MenuCardActionStyle itemNameStyle = dataObject.getItemNameStyle();
        Spinner fontSpinner = (Spinner) findViewById(R.id.editActionItemNameFontSpinner);
        ArrayAdapter menuNameAdapter = (ArrayAdapter) fontSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemNameStyle != null && itemNameStyle.getFont() != null) {
            fontSpinner.setSelection(menuNameAdapter.getPosition(itemNameStyle.getFont()));
        } else {
            setNoneFont(fontSpinner, menuNameAdapter);
        }

        Spinner fontSizeSpinner = (Spinner) findViewById(R.id.editActionItemNameFontSizeSpinner);
        ArrayAdapter fontSizeAdapter = (ArrayAdapter) fontSizeSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemNameStyle != null && itemNameStyle.getFontSize() != null) {
            fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(itemNameStyle.getFontSize()));
        } else {
            setNoneFontSize(fontSizeSpinner, fontSizeAdapter);
        }

        Spinner fontFaceSpinner = (Spinner) findViewById(R.id.editActionItemNameFontFaceSpinner);
        ArrayAdapter fontFaceAdapter = (ArrayAdapter) fontFaceSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemNameStyle != null && itemNameStyle.getFontFace() != null) {
            fontFaceSpinner.setSelection(fontFaceAdapter.getPosition(itemNameStyle.getFontFace()));
        } else {
            setNoneFontFace(fontFaceSpinner, fontFaceAdapter);
        }

        Spinner fontColorSpinner = (Spinner) findViewById(R.id.editActionItemNameFontColorSpinner);
        ArrayAdapter fontColorAdapter = (ArrayAdapter) fontColorSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemNameStyle != null && itemNameStyle.getFontColor() != null) {
            fontColorSpinner.setSelection(fontColorAdapter.getPosition(itemNameStyle.getFontColor()));
        } else {
            setNoneFontColor(fontColorSpinner, fontColorAdapter);
        }
    }

    private void setItemDescSpinnerValues() {
        MenuCardActionStyle itemDescStyle = dataObject.getItemDescStyle();
        Spinner fontSpinner = (Spinner) findViewById(R.id.editActionItemDescFontSpinner);
        ArrayAdapter menuNameAdapter = (ArrayAdapter) fontSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemDescStyle != null && itemDescStyle.getFont() != null) {
            fontSpinner.setSelection(menuNameAdapter.getPosition(itemDescStyle.getFont()));
        } else {
            setNoneFont(fontSpinner, menuNameAdapter);
        }
        Spinner fontSizeSpinner = (Spinner) findViewById(R.id.editActionItemDescFontSizeSpinner);
        ArrayAdapter fontSizeAdapter = (ArrayAdapter) fontSizeSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemDescStyle != null && itemDescStyle.getFontSize() != null) {
            fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(itemDescStyle.getFontSize()));
        } else {
            setNoneFontSize(fontSizeSpinner, fontSizeAdapter);
        }

        Spinner fontFaceSpinner = (Spinner) findViewById(R.id.editActionItemDescFontFaceSpinner);
        ArrayAdapter fontFaceAdapter = (ArrayAdapter) fontFaceSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemDescStyle != null && itemDescStyle.getFontFace() != null) {
            fontFaceSpinner.setSelection(fontFaceAdapter.getPosition(itemDescStyle.getFontFace()));
        } else {
            setNoneFontFace(fontFaceSpinner, fontFaceAdapter);
        }

        Spinner fontColorSpinner = (Spinner) findViewById(R.id.editActionItemDescFontColorSpinner);
        ArrayAdapter fontColorAdapter = (ArrayAdapter) fontColorSpinner.getAdapter(); //cast to an ArrayAdapter
        if (itemDescStyle != null && itemDescStyle.getFontColor() != null) {
            fontColorSpinner.setSelection(fontColorAdapter.getPosition(itemDescStyle.getFontColor()));
        } else {
            setNoneFontColor(fontColorSpinner, fontColorAdapter);
        }
    }

    private void setFontListener() {
        fontSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (parent.getId() == R.id.editActionMenuNameFontSpinner) {
                    MenuCardActionStyle menuNameStyle = dataObject.getMenuNameStyle();
                    if (menuNameStyle == null) {
                        menuNameStyle = new MenuCardActionStyle();
                        dataObject.setMenuNameStyle(menuNameStyle);
                    }
                    menuNameStyle.setFont(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionMenuDescFontSpinner) {
                    MenuCardActionStyle menuDescStyle = dataObject.getMenuDescStyle();
                    if (menuDescStyle == null) {
                        menuDescStyle = new MenuCardActionStyle();
                        dataObject.setMenuDescStyle(menuDescStyle);
                    }
                    menuDescStyle.setFont(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionGroupNameFontSpinner) {
                    MenuCardActionStyle groupNameStyle = dataObject.getGroupNameStyle();
                    if (groupNameStyle == null) {
                        groupNameStyle = new MenuCardActionStyle();
                        dataObject.setGroupNameStyle(groupNameStyle);
                    }
                    groupNameStyle.setFont(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemNameFontSpinner) {
                    MenuCardActionStyle itemNameStyle = dataObject.getItemNameStyle();
                    if (itemNameStyle == null) {
                        itemNameStyle = new MenuCardActionStyle();
                        dataObject.setItemNameStyle(itemNameStyle);
                    }
                    itemNameStyle.setFont(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemDescFontSpinner) {
                    MenuCardActionStyle itemDescStyle = dataObject.getItemDescStyle();
                    if (itemDescStyle == null) {
                        itemDescStyle = new MenuCardActionStyle();
                        dataObject.setItemDescStyle(itemDescStyle);
                    }
                    itemDescStyle.setFont(parent.getItemAtPosition(pos).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }


    private void setFontSizeSpinnerListener() {
        fontSizeSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (parent.getId() == R.id.editActionMenuNameFontSizeSpinner) {
                    MenuCardActionStyle menuNameStyle = dataObject.getMenuNameStyle();
                    if (menuNameStyle == null) {
                        menuNameStyle = new MenuCardActionStyle();
                        dataObject.setMenuNameStyle(menuNameStyle);
                    }
                    menuNameStyle.setFontSize(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionMenuDescFontSizeSpinner) {
                    MenuCardActionStyle menuDescStyle = dataObject.getMenuDescStyle();
                    if (menuDescStyle == null) {
                        menuDescStyle = new MenuCardActionStyle();
                        dataObject.setMenuDescStyle(menuDescStyle);
                    }
                    menuDescStyle.setFontSize(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionGroupNameFontSizeSpinner) {
                    MenuCardActionStyle groupNameStyle = dataObject.getGroupNameStyle();
                    if (groupNameStyle == null) {
                        groupNameStyle = new MenuCardActionStyle();
                        dataObject.setGroupNameStyle(groupNameStyle);
                    }
                    groupNameStyle.setFontSize(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemNameFontSizeSpinner) {
                    MenuCardActionStyle itemNameStyle = dataObject.getItemNameStyle();
                    if (itemNameStyle == null) {
                        itemNameStyle = new MenuCardActionStyle();
                        dataObject.setItemNameStyle(itemNameStyle);
                    }
                    itemNameStyle.setFontSize(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemDescFontSizeSpinner) {
                    MenuCardActionStyle itemDescStyle = dataObject.getItemDescStyle();
                    if (itemDescStyle == null) {
                        itemDescStyle = new MenuCardActionStyle();
                        dataObject.setItemDescStyle(itemDescStyle);
                    }
                    itemDescStyle.setFontSize(parent.getItemAtPosition(pos).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setFontFaceSpinnerListener() {
        fontFaceSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (parent.getId() == R.id.editActionMenuNameFontFaceSpinner) {
                    MenuCardActionStyle menuNameStyle = dataObject.getMenuNameStyle();
                    if (menuNameStyle == null) {
                        menuNameStyle = new MenuCardActionStyle();
                        dataObject.setMenuNameStyle(menuNameStyle);
                    }
                    menuNameStyle.setFontFace(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionMenuDescFontFaceSpinner) {
                    MenuCardActionStyle menuDescStyle = dataObject.getMenuDescStyle();
                    if (menuDescStyle == null) {
                        menuDescStyle = new MenuCardActionStyle();
                        dataObject.setMenuDescStyle(menuDescStyle);
                    }
                    menuDescStyle.setFontFace(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionGroupNameFontFaceSpinner) {
                    MenuCardActionStyle groupNameStyle = dataObject.getGroupNameStyle();
                    if (groupNameStyle == null) {
                        groupNameStyle = new MenuCardActionStyle();
                        dataObject.setGroupNameStyle(groupNameStyle);
                    }
                    groupNameStyle.setFontFace(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemNameFontFaceSpinner) {
                    MenuCardActionStyle itemNameStyle = dataObject.getItemNameStyle();
                    if (itemNameStyle == null) {
                        itemNameStyle = new MenuCardActionStyle();
                        dataObject.setItemNameStyle(itemNameStyle);
                    }
                    itemNameStyle.setFontFace(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemDescFontFaceSpinner) {
                    MenuCardActionStyle itemDescStyle = dataObject.getItemDescStyle();
                    if (itemDescStyle == null) {
                        itemDescStyle = new MenuCardActionStyle();
                        dataObject.setItemDescStyle(itemDescStyle);
                    }
                    itemDescStyle.setFontFace(parent.getItemAtPosition(pos).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setFontColorSpinnerListener() {
        fontColorSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (parent.getId() == R.id.editActionMenuNameFontColorSpinner) {
                    MenuCardActionStyle menuNameStyle = dataObject.getMenuNameStyle();
                    if (menuNameStyle == null) {
                        menuNameStyle = new MenuCardActionStyle();
                        dataObject.setMenuNameStyle(menuNameStyle);
                    }
                    menuNameStyle.setFontColor(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionMenuDescFontColorSpinner) {
                    MenuCardActionStyle menuDescStyle = dataObject.getMenuDescStyle();
                    if (menuDescStyle == null) {
                        menuDescStyle = new MenuCardActionStyle();
                        dataObject.setMenuDescStyle(menuDescStyle);
                    }
                    menuDescStyle.setFontColor(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionGroupNameFontColorSpinner) {
                    MenuCardActionStyle groupNameStyle = dataObject.getGroupNameStyle();
                    if (groupNameStyle == null) {
                        groupNameStyle = new MenuCardActionStyle();
                        dataObject.setGroupNameStyle(groupNameStyle);
                    }
                    groupNameStyle.setFontColor(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemNameFontColorSpinner) {
                    MenuCardActionStyle itemNameStyle = dataObject.getItemNameStyle();
                    if (itemNameStyle == null) {
                        itemNameStyle = new MenuCardActionStyle();
                        dataObject.setItemNameStyle(itemNameStyle);
                    }
                    itemNameStyle.setFontColor(parent.getItemAtPosition(pos).toString());
                } else if (parent.getId() == R.id.editActionItemDescFontColorSpinner) {
                    MenuCardActionStyle itemDescStyle = dataObject.getItemDescStyle();
                    if (itemDescStyle == null) {
                        itemDescStyle = new MenuCardActionStyle();
                        dataObject.setItemDescStyle(itemDescStyle);
                    }
                    itemDescStyle.setFontColor(parent.getItemAtPosition(pos).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setFontSpinners() {
        setFontListener();
        String[] fonts = Arrays.toString(AppFontEnum.values()).replaceAll("^.|.$", "").split(", ");

        Spinner menuNameFontSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontSpinner);
        menuNameFontSpinner.setOnItemSelectedListener(fontSpinnerListener);
        ArrayAdapter menuNameFontAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        menuNameFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuNameFontSpinner.setAdapter(menuNameFontAdapter);


        Spinner editActionMenuDescFontSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontSpinner);
        editActionMenuDescFontSpinner.setOnItemSelectedListener(fontSpinnerListener);
        ArrayAdapter menuDescFontAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        menuDescFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editActionMenuDescFontSpinner.setAdapter(menuDescFontAdapter);

        Spinner editActionGroupNameFontSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontSpinner);
        editActionGroupNameFontSpinner.setOnItemSelectedListener(fontSpinnerListener);
        ArrayAdapter groupNameFontAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        groupNameFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editActionGroupNameFontSpinner.setAdapter(groupNameFontAdapter);

        Spinner editActionItemNameFontSpinner = (Spinner) findViewById(R.id.editActionItemNameFontSpinner);
        editActionItemNameFontSpinner.setOnItemSelectedListener(fontSpinnerListener);
        ArrayAdapter ItemNameFontAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        ItemNameFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editActionItemNameFontSpinner.setAdapter(ItemNameFontAdapter);

        Spinner editActionItemDescFontSpinner = (Spinner) findViewById(R.id.editActionItemDescFontSpinner);
        editActionItemDescFontSpinner.setOnItemSelectedListener(fontSpinnerListener);
        ArrayAdapter ItemDescFontAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fonts));
        ItemDescFontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editActionItemDescFontSpinner.setAdapter(ItemDescFontAdapter);
    }


    private void setFontSizeSpinners() {
        setFontSizeSpinnerListener();
        String[] sizes = Arrays.toString(FontSizeEnum.values()).replaceAll("^.|.$", "").split(", ");

        Spinner menuNameSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontSizeSpinner);
        menuNameSpinner.setOnItemSelectedListener(fontSizeSpinnerListener);
        ArrayAdapter menuNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(sizes));
        menuNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuNameSpinner.setAdapter(menuNameAdapter);

        Spinner menuDescSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontSizeSpinner);
        menuDescSpinner.setOnItemSelectedListener(fontSizeSpinnerListener);
        ArrayAdapter menuDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(sizes));
        menuDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuDescSpinner.setAdapter(menuDescAdapter);

        Spinner groupNameSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontSizeSpinner);
        groupNameSpinner.setOnItemSelectedListener(fontSizeSpinnerListener);
        ArrayAdapter groupNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(sizes));
        groupNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSpinner.setAdapter(groupNameAdapter);

        Spinner itemNameSpinner = (Spinner) findViewById(R.id.editActionItemNameFontSizeSpinner);
        itemNameSpinner.setOnItemSelectedListener(fontSizeSpinnerListener);
        ArrayAdapter itemNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(sizes));
        itemNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemNameSpinner.setAdapter(itemNameAdapter);

        Spinner itemDescSpinner = (Spinner) findViewById(R.id.editActionItemDescFontSizeSpinner);
        itemDescSpinner.setOnItemSelectedListener(fontSizeSpinnerListener);
        ArrayAdapter itemDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(sizes));
        itemDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemDescSpinner.setAdapter(itemDescAdapter);
    }


    private void setFontFaceSpinners() {
        setFontFaceSpinnerListener();
        String[] fontFaces = Arrays.toString(FontFaceEnum.values()).replaceAll("^.|.$", "").split(", ");

        Spinner menuNameSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontFaceSpinner);
        menuNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter menuNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fontFaces));
        menuNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuNameSpinner.setAdapter(menuNameAdapter);

        Spinner menuDescSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontFaceSpinner);
        menuDescSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter menuDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fontFaces));
        menuDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuDescSpinner.setAdapter(menuDescAdapter);

        Spinner groupNameSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontFaceSpinner);
        groupNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter groupNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fontFaces));
        groupNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSpinner.setAdapter(groupNameAdapter);

        Spinner itemNameSpinner = (Spinner) findViewById(R.id.editActionItemNameFontFaceSpinner);
        itemNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter itemNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fontFaces));
        itemNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemNameSpinner.setAdapter(itemNameAdapter);

        Spinner itemDescSpinner = (Spinner) findViewById(R.id.editActionItemDescFontFaceSpinner);
        itemDescSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter itemDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(fontFaces));
        itemDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemDescSpinner.setAdapter(itemDescAdapter);

    }

    private void setFontColorSpinners() {
        setFontColorSpinnerListener();
        String[] colors = Arrays.toString(ColorCodeEnum.values()).replaceAll("^.|.$", "").split(", ");

        Spinner menuNameSpinner = (Spinner) findViewById(R.id.editActionMenuNameFontColorSpinner);
        menuNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter menuNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        menuNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuNameSpinner.setAdapter(menuNameAdapter);
        menuNameSpinner.setOnItemSelectedListener(fontColorSpinnerListener);

        Spinner menuDescSpinner = (Spinner) findViewById(R.id.editActionMenuDescFontColorSpinner);
        menuDescSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter menuDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        menuDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuDescSpinner.setAdapter(menuDescAdapter);
        menuDescSpinner.setOnItemSelectedListener(fontColorSpinnerListener);

        Spinner groupNameSpinner = (Spinner) findViewById(R.id.editActionGroupNameFontColorSpinner);
        groupNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter groupNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        groupNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSpinner.setAdapter(groupNameAdapter);
        groupNameSpinner.setOnItemSelectedListener(fontColorSpinnerListener);

        Spinner itemNameSpinner = (Spinner) findViewById(R.id.editActionItemNameFontColorSpinner);
        itemNameSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter itemNameAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        itemNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemNameSpinner.setAdapter(itemNameAdapter);
        itemNameSpinner.setOnItemSelectedListener(fontColorSpinnerListener);

        Spinner itemDescSpinner = (Spinner) findViewById(R.id.editActionItemDescFontColorSpinner);
        itemDescSpinner.setOnItemSelectedListener(fontFaceSpinnerListener);
        ArrayAdapter itemDescAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, Arrays.asList(colors));
        itemDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemDescSpinner.setAdapter(itemDescAdapter);
        itemDescSpinner.setOnItemSelectedListener(fontColorSpinnerListener);
    }

}
