package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
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
    int imageTotal;
    int currentLoadedImages;
    int displayImage = 0;
    boolean [] imageLoaded;

    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        /* hide default bar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        not_added_icon = getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp);
        added_icon = getResources().getDrawable(R.drawable.ic_bookmark_black_24dp);

        /* get news */
        news = MainActivity.newsToDisplay;
        TextView textContent = findViewById(R.id.news_text);
        textContent.setText(news.content);
        TextView textTitle = findViewById(R.id.news_page_title);
        textTitle.setText(news.title);
        if (news.publisher != null) {
            TextView textPublisher = findViewById(R.id.news_page_publisher);
            textPublisher.setText(news.publisher);
        }

        /* load image */
        imageTotal = news.image.size();
        currentLoadedImages = 0;
        final AppCompatImageView coverImage = findViewById(R.id.news_cover_img);
        if (imageTotal != 0) {
            for (Image img: news.image) {
                boolean firstFlag = true;
                if (img.getImage() != null) { // this is needed: every image must be iterated first, otherwise it does not display
                    if (firstFlag) {
                        coverImage.setImageBitmap(img.getImage());
                        currentLoadedImages = 1;
                    }
                    firstFlag = false;
                }
            }

            /* images cycler */
            countDownTimer = new CountDownTimer(2000*100, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    /* load image */
                    if (currentLoadedImages < imageTotal) {
                        Image img = news.image.elementAt(currentLoadedImages);
                        if (img.hasImage()) {
                            if (img.unsafeURL) {
                                news.image.remove(img);
                                imageTotal -= 1;
                                if (imageTotal == 0) {
                                    // non of the images is safe
                                    coverImage.setImageDrawable(getDrawable(R.drawable.testnewsbg));
                                }
                            } else {
                                coverImage.setImageBitmap(img.getImage());
                                currentLoadedImages += 1;
                            }
                        }
                    } else if (imageTotal > 0) {
                        Image img = news.image.elementAt(displayImage);
                        if (img.hasImage()) {
                            coverImage.setImageBitmap(img.getImage());
                        }
                        displayImage = (displayImage + 1) % imageTotal;
                    }
                }
                @Override
                public void onFinish() { }
            }.start();

        } else {
            coverImage.setImageDrawable(getDrawable(R.drawable.testnewsbg));
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
                    collapsingToolbarLayout.setTitle(" "); // maybe time and location?
                    isShow = false;
                }
            }
        });

        /* bookmarked? */
        addedToFavourite = (news.collection == "1");
        FloatingActionButton button = findViewById(R.id.news_favourite);
        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();
    }

    public void toggleFavourite (View view) {
        addedToFavourite = !addedToFavourite;
        if (addedToFavourite) {
            //news.setCollection();
        } else {
            //news.deleteCollection();
        }
        FloatingActionButton button = findViewById(R.id.news_favourite);

        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();

        Snackbar.make(view, addedToFavourite ? "News added to your Bookmarks." : "News removed from your Bookmarks.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

}
