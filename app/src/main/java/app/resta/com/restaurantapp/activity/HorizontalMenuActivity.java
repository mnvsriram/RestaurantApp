package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.LazyAdapterToDelete;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public class HorizontalMenuActivity extends BaseActivity {

    ListView list;
    LazyAdapterToDelete adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_menu);


        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", "id1");
        map.put("title", "title1");
        map.put("artist", "artist1");
        map.put("duration", "duration1");


        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("id", "id1");
        map2.put("title", "title1");
        map2.put("artist", "artist1");
        map2.put("duration", "duration1");


        // adding HashList to ArrayList
        songsList.add(map);
        songsList.add(map2);


        list = (ListView) findViewById(R.id.list);

        // Getting adapter by passing xml data ArrayList
        adapter = new LazyAdapterToDelete(this, songsList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast toast = Toast.makeText(MyApplication.getAppContext(), position + "", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        setStyle();
    }


    private void setStyle() {
        int layoutID = getResources().getIdentifier("horizontalMenuLayout", "id", getPackageName());
        RelativeLayout mainLayout = (RelativeLayout) findViewById(layoutID);
        mainLayout.setBackgroundColor(StyleUtil.colorMap.get("horizontalMenuBackground"));
    }

}
