package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.fragment.MenuDetailFragment;
import app.resta.com.restaurantapp.fragment.MenuListFragment;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;

public class NarrowMenuActivity extends BaseActivity implements MenuListFragment.OnMenuItemSelectedListener, MenuListFragment.OnMenuTypeChanged {
    private TagAdminDaoI tagAdminDao = new TagAdminFireStoreDao();
    private MenuItemAdminDaoI menuItemAdminDao = new MenuItemAdminFireStoreDao();
    private String menuTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrow_menu);
        setToolbar();
        menuTypeId = getIntent().getStringExtra("groupMenuId");
//        if (menuTypeId == null) {
//            menuTypeId = "1";
//        }
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("activityMenuTypeAdd_menuTypeId", menuTypeId);
        if (menuTypeId != null) {
            authenticationController.goToMenuTypeSettingsPage(null);
        } else {
            authenticationController.goBackFromMenuPage(params, menuTypeId);
        }
    }


    @Override
    public void onMenuTypeChanged(String groupMenuId) {
        menuTypeId = groupMenuId;
    }


    @Override
    public void onRestaurantItemClicked(RestaurantItem item) {

        final MenuDetailFragment frag = new MenuDetailFragment();
        frag.setSelectedItem(item);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (item != null) {


            menuItemAdminDao.getTagsForItem(item.getId() + "", new OnResultListener<List<String>>() {
                @Override
                public void onCallback(List<String> tagIds) {
                    final List<Tag> tagList = new ArrayList<>();
                    for (String tagId : tagIds) {
                        tagAdminDao.getTag(tagId, new OnResultListener<Tag>() {
                            @Override
                            public void onCallback(Tag tag) {
                                tagList.add(tag);
                            }
                        });
                    }
                    frag.setTagList(tagList);
                    ft.replace(R.id.fragment_container, frag);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            });
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


    public void showGoesGreatWithItems(View view) {
        final String itemId = (String) view.getTag();

        menuItemAdminDao.getItem(itemId, new OnResultListener<RestaurantItem>() {
            @Override
            public void onCallback(final RestaurantItem restaurantItem) {

                menuItemAdminDao.getGGWsForItem(restaurantItem.getId() + "", new OnResultListener<List<String>>() {
                    @Override
                    public void onCallback(List<String> ggwIds) {
                        final List<RestaurantItem> ggws = new ArrayList<>();
                        for (String ggwId : ggwIds) {
                            menuItemAdminDao.getItem(ggwId, new OnResultListener<RestaurantItem>() {
                                @Override
                                public void onCallback(RestaurantItem restaurantItem) {
                                    ggws.add(restaurantItem);
                                }
                            });
                        }
                        restaurantItem.setGgwItems(ggws);

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(NarrowMenuActivity.this);
                        builderSingle.setIcon(R.drawable.edit);
                        builderSingle.setTitle(restaurantItem.getName() + " goes great along with:");
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(NarrowMenuActivity.this, android.R.layout.select_dialog_singlechoice);

                        List<RestaurantItem> ggwItems = restaurantItem.getGgwItems();
                        if (ggwItems != null) {
                            for (RestaurantItem ggwItem : ggwItems) {
                                arrayAdapter.add(ggwItem.getName());
                            }
                        }
                        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builderSingle.show();
                    }
                });
            }
        });
    }

}
