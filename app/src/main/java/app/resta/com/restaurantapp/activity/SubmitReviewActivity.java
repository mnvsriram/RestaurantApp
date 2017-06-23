package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ReviewAdapter;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class SubmitReviewActivity extends BaseActivity {

    OrderListFragment frag = new OrderListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submit_menu);

        ListView listView = (ListView) findViewById(R.id.reviewlist);

        ReviewForOrder reviewForOrder = null;
        Intent intent = getIntent();
        if (intent.hasExtra("ordered_items")) {
            reviewForOrder = (ReviewForOrder) intent.getSerializableExtra("ordered_items");
        }
        if (reviewForOrder != null) {
            ReviewAdapter adapter = new ReviewAdapter(reviewForOrder.getDishes(), getApplicationContext());
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

}
