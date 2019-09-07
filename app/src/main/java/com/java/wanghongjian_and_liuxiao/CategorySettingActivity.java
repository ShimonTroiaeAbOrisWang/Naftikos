package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

import java.util.Vector;

public class CategorySettingActivity extends AppCompatActivity {

    private SlidrConfig mConfig;
    Intent resultIntent;
    boolean[] isPreferredTopic;
    private final static int TOPIC_ID_OFFSET = 0x5E000;

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

        resultIntent = new Intent();
        Intent intent = getIntent();
        isPreferredTopic = intent.getBooleanArrayExtra(MainActivity.EXTRA_PREFERRED);

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

        multi.removeAllViews();
        String[] dummyText = getResources().getStringArray(R.array.topics_array);
        int topicIndex = MainActivity.HOME_FINANCE;
        for (String text : dummyText) {
            LabelToggle toggle = new LabelToggle(this);
            toggle.setText(text);
            toggle.setTextColor(getResources().getColor(android.R.color.white));

            int color = ContextCompat.getColor(this, R.color.colorCategoryButton);
            // Background for unchecked state
            GradientDrawable unchecked = new GradientDrawable();
            unchecked.setCornerRadius(42);
            unchecked.setStroke(2, color);
            toggle.getTextView().setBackgroundDrawable(unchecked);

            // Background for checked state
            GradientDrawable checked = new GradientDrawable();
            checked.setColor(color);
            checked.setCornerRadius(42);
            checked.setStroke(2, color);
            toggle.setCheckedImageDrawable(checked);
            toggle.setPadding(6, 0, 6, 0);

            toggle.getTextView().setPadding(22, 0, 22, 0);
            toggle.getTextView().setTextSize(12);
            toggle.setChecked(isPreferredTopic[topicIndex]);
            toggle.setId(TOPIC_ID_OFFSET + topicIndex);
            multi.addView(toggle);

            topicIndex += 1;
        }

        multi.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {

                isPreferredTopic[checkedId - TOPIC_ID_OFFSET] = isChecked; // this is strange...
            }
        });

    }


    @Override
    public void finish() {
        resultIntent.putExtra(MainActivity.EXTRA_PREFERRED_II, isPreferredTopic);
        setResult(RESULT_OK, resultIntent);
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
        //MainActivity.dimmer.setVisibility(View.INVISIBLE);
    }
}
