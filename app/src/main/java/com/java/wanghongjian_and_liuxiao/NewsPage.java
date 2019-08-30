package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class NewsPage extends AppCompatActivity {

    boolean addedToFavourite = false;
    Drawable not_added_icon;
    Drawable added_icon;
    News news;


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

        /* get news */
        news = MainActivity.newsToDisplay;
        TextView textView = findViewById(R.id.news_text);
        textView.setText(news.content);

        /* load image */
        if (!news.image.isEmpty()) {
            AppCompatImageView coverImage = findViewById(R.id.news_cover_img);
            coverImage.setImageBitmap(news.image.elementAt(0).getImage());
        }

        /* hide title in expanded view */
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.news_layout);
        AppBarLayout appBarLayout = findViewById(R.id.news_page_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(news.title);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(news.publishTime); // maybe time and location?
                    isShow = false;
                }
            }
        });


    }

    public void toggleFavourite (View view) {
        addedToFavourite = !addedToFavourite;
        FloatingActionButton button = findViewById(R.id.news_favourite);

        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();

        Snackbar.make(view, addedToFavourite ? "Added to favourite." : "Removed from favourite.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }
}
