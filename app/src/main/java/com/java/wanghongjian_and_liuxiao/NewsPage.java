package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

public class NewsPage extends FragmentActivity /*AppCompatActivity*/ {

    boolean addedToFavourite = false;
    Drawable not_added_icon;
    Drawable added_icon;
    News news;
    int imageTotal;
    int currentLoadedImages;
    int displayImage = 0;
    AppCompatImageView coverImage;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        Slidr.attach(this);

        /* hide default bar */
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }*/

        not_added_icon = getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp);
        added_icon = getResources().getDrawable(R.drawable.ic_bookmark_black_24dp);

        /* get news */
        news = MainActivity.newsToDisplay;
        TextView textContent = findViewById(R.id.news_text);
        textContent.setText(news.content);
        final TextView textTitle = findViewById(R.id.news_page_title);
        textTitle.setText(news.title);
        if (news.publisher != null) {
            TextView textPublisher = findViewById(R.id.news_page_publisher);
            textPublisher.setText(news.publisher);
        }

        /* load image */
        imageTotal = news.imageURLs.size();
        currentLoadedImages = 0;

        coverImage = findViewById(R.id.news_cover_img);
        if (imageTotal != 0) {
            /*
            for (Image img: news.image) {
                boolean firstFlag = true;
                if (img.getImage() != null) {
                    if (firstFlag) {
                        coverImage.setImageBitmap(img.getImage());
                        currentLoadedImages = 1;
                    }
                    firstFlag = false;
                }
            } */
            //Glide.with(coverImage).load (news.imageURLs.elementAt(displayImage)).into(coverImage);
            /* images cycler */
            countDownTimer = new CountDownTimer(2500*100, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    /* load image */
                    /*
                    if (currentLoadedImages < imageTotal) {
                        Image img = news.image.elementAt(currentLoadedImages);
                        if (img.hasImage()) {
                            if (img.unsafeURL) {
                                news.image.remove(img);
                                imageTotal -= 1;
                                if (imageTotal == 0) {
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
                    */
                    Glide.with(coverImage).load (news.imageURLs.elementAt(displayImage)).into(coverImage);
                    displayImage = (displayImage + 1) % imageTotal;
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
        addedToFavourite = (news.getCollectionStatus().equals("1"));
        FloatingActionButton button = findViewById(R.id.news_favourite);
        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();
    }

    public void toggleFavourite (View view) {
        addedToFavourite = !addedToFavourite;
        if (addedToFavourite) {
            news.setCollection();
        } else {
            news.deleteCollection();
        }
        FloatingActionButton button = findViewById(R.id.news_favourite);

        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();

        Snackbar.make(view, addedToFavourite ? "News added to your Bookmarks." : "News removed from your Bookmarks.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void share (View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm reading a Ναυτικός news!\n" + news.title);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


    @Override
    public void finish() {
        Glide.with(coverImage).clear(coverImage);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
