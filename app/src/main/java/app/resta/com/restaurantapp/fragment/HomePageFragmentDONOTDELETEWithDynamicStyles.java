package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.util.StyleUtil;

public class HomePageFragmentDONOTDELETEWithDynamicStyles extends Fragment {
    private long menuCardId;
    AuthenticationController authenticationController;

    public HomePageFragmentDONOTDELETEWithDynamicStyles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String layout = StyleUtil.layOutMap.get("mainPageLayout");
        View inflatedView = null;
        if (layout != null && layout.equalsIgnoreCase("second")) {
            inflatedView = inflater.inflate(R.layout.activity_second, container, false);
        } else {
            inflatedView = inflater.inflate(R.layout.activity_top_level, container, false);
        }
        setStyle();
        authenticationController = new AuthenticationController(getActivity());
        return inflatedView;
    }

    private void setStyle() {
        int layoutID = getResources().getIdentifier("mainlayout", "id", getActivity().getPackageName());
        RelativeLayout mainLayout = (RelativeLayout) getActivity().findViewById(R.id.mainlayout);
        if (StyleUtil.colorMap.get("mainPageBackground") != null && mainLayout != null) {
            mainLayout.setBackgroundColor(StyleUtil.colorMap.get("mainPageBackground"));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setMenuCardId(long menuCardId) {
        this.menuCardId = menuCardId;
    }
}
