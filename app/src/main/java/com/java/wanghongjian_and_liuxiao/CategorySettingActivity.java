package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.nex3z.togglebuttongroup.button.ToggleButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class CategorySettingActivity extends AppCompatActivity {

    private SlidrConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_setting);

        mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .velocityThreshold(2400)
                .scrimStartAlpha(0).scrimEndAlpha(0)
                .build();

        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this, mConfig);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        /* hide bar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        TextView categoriesText = findViewById(R.id.categories_text);
        categoriesText.setTypeface(MainActivity.ubuntuMidItalic);

        /* get screen resolution to adjust text size */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_width = displayMetrics.widthPixels;

        categoriesText.setTextSize (screen_width / 63f);

        final MultiSelectToggleGroup multi = findViewById(R.id.group_topics);

        for (int i = 0; i < multi.getChildCount(); i++) {
            LabelToggle child = (LabelToggle) multi.getChildAt(i);

            child.getTextView().setPadding(0, 0, 0, 0);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        //MainActivity.dimmer.setVisibility(View.INVISIBLE);
    }
}
