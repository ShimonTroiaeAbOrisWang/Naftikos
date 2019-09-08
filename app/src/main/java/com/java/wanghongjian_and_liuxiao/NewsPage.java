package com.java.wanghongjian_and_liuxiao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;
import com.snatik.storage.Storage;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;


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
    Animation rotateOnce;
    TextView textContent;
    static final int KEY_HIDE_ID_OFFSET = 0xE29000;

    final Stack<Bitmap> shareBitmap = new Stack<>();

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
        textContent = findViewById(R.id.news_text);
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
            /* images cycler */
            countDownTimer = new CountDownTimer(2500*100, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Glide.with(coverImage).load (news.imageURLs.elementAt(displayImage)).into(coverImage);
                    displayImage = (displayImage + 1) % imageTotal;
                }
                @Override
                public void onFinish() { }
            }.start();


            /* save the last image to external storage for sharing */
            String imgURL = news.imageURLs.elementAt(news.imageURLs.size() - 1);
            Glide.with(this)
                    .asBitmap()
                    .load(imgURL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            shareBitmap.push(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


        } else {
            coverImage.setImageDrawable(getDrawable(R.drawable.testnewsbg));
        }

        /* prepare video (if any) */
        FloatingActionButton videoButton = findViewById(R.id.news_play_video);
        if (news.videoURL == null) {
            videoButton.hide();
        } else if (news.videoURL.length() < 5) {
            videoButton.hide();
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            videoButton.startAnimation(shake);
        }

        rotateOnce = AnimationUtils.loadAnimation(this, R.anim.rotate_once);


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

        /* toggle night shift */
        if (MainActivity.nightShift) {
            textContent.setBackgroundColor(Color.BLACK);
            textContent.setTextColor(Color.rgb(0xAD, 0xAD, 0xAD));
            CoordinatorLayout layout = findViewById(R.id.news_page_content_container);
            layout.setBackgroundColor(Color.BLACK);

            LinearLayout card = findViewById(R.id.sub_content_card);
            card.setBackgroundColor(Color.rgb(0x3D, 0x3D, 0x3D));
        }
    }

    public void toggleFavourite (View view) {

        addedToFavourite = !addedToFavourite;
        if (addedToFavourite) {
            news.setCollection();
        } else {
            news.deleteCollection();
        }
        FloatingActionButton button = findViewById(R.id.news_favourite);
        button.startAnimation(rotateOnce);

        button.hide();
        button.setImageDrawable(addedToFavourite ? added_icon: not_added_icon);
        button.show();

        Snackbar.make(view, addedToFavourite ? "News added to your Bookmarks." : "News removed from your Bookmarks.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void playVideo (View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(MainActivity.EXTRA_VIDEO_URL, news.videoURL);
        startActivity(intent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out); // transition
    }

    public void share (View view) {

        String shareText = "I'm reading a Ναυτικός news!\n";
        String shareURL = news.url;


        if (news.imageURLs.size() > 0) {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            /* Save the image to external storage so as to share it */
            String imgURL = news.imageURLs.elementAt(0);
            // init
            Storage storage = new Storage(getApplicationContext());
            // get external storage
            String externPath = storage.getExternalStorageDirectory() + File.separator + "Naftikos";
            // new dir
            if (!storage.isDirectoryExists(externPath)) {
                storage.createDirectory(externPath);
            }

            if (imgURL.length() < 7) {
                return;
            }
            String imagePath = externPath + File.separator + imgURL.substring(imgURL.length() - 7);

            try {
                storage.createFile(imagePath, shareBitmap.pop());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm reading a Ναυτικός news!\n\n" + news.title + "\n\nLink: " + shareURL);
            try {
                System.out.println(imagePath);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                sendIntent.setType("image/*");
                sendIntent.putExtra("Kdescription", "I'm reading a Ναυτικός news!\n\n" + news.title + "\n\nLink: " + shareURL);

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(Intent.createChooser(shareIntent, "Share"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm reading a Ναυτικός news!\n\n" + news.title + "\n\nLink: " + shareURL);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

    }


    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }


    public void doNotLikeIt (View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose keywords to hide");


        final MultiSelectToggleGroup multi = new MultiSelectToggleGroup(this);
        multi.setRowSpacing(0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(9, 10, 9, 0);

        multi.setLayoutParams(params);
        final HashSet<String> toHide = new HashSet<>();

        for (int i = 0; i < news.keywords.size(); i += 1) {
            LabelToggle toggle = new LabelToggle(this);
            toggle.setLayoutParams(params);
            toggle.setText(news.keywords.elementAt(i));

            int color = ContextCompat.getColor(this, R.color.colorSecondary);
            // Background for unchecked state
            GradientDrawable unchecked = new GradientDrawable();
            unchecked.setCornerRadius(40);
            unchecked.setStroke(2, color);
            toggle.getTextView().setBackgroundDrawable(unchecked);

            // Background for checked state
            GradientDrawable checked = new GradientDrawable();
            checked.setColor(color);
            checked.setCornerRadius(32);
            checked.setStroke(2, color);
            toggle.setCheckedImageDrawable(checked);

            toggle.setId(KEY_HIDE_ID_OFFSET + i);
            multi.addView(toggle);
        }

        multi.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    toHide.add(news.keywords.elementAt(checkedId - KEY_HIDE_ID_OFFSET));
                } else {
                    toHide.remove(news.keywords.elementAt(checkedId - KEY_HIDE_ID_OFFSET));
                }
            }
        });

        dialogBuilder.setView(multi);

        dialogBuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* add those key words to a set */
                MainActivity.keywordsBlacklist.addAll(toHide);
                Snackbar.make(textContent, toHide.size() + " key word(s) added to your blacklist.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        dialogBuilder.create().show();

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
