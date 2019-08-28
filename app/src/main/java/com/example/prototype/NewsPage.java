package com.example.prototype;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewsPage extends AppCompatActivity {

    boolean addedToFavourite = false;
    Drawable not_added_icon;
    Drawable added_icon;
    String newsTitle;
    String newsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        /* hide default bar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        not_added_icon = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        added_icon = getResources().getDrawable(R.drawable.ic_star_black_24dp);

        Intent intent = getIntent();
        newsTitle = intent.getStringExtra(MainActivity.EXTRA_NEWS_TITLE);
        newsText = intent.getStringExtra(MainActivity.EXTRA_NEWS_TEXT);

        CollapsingToolbarLayout newsLayout = findViewById(R.id.news_layout);
        newsLayout.setTitle(newsTitle);
        TextView textView = findViewById(R.id.news_text);
        textView.setText(newsText);


    }

    public void toggleFavourite (View view) {
        addedToFavourite = !addedToFavourite;
        FloatingActionButton button = findViewById(R.id.news_favourite);

        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();

    }
}
