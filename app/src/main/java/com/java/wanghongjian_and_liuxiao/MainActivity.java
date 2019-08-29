package com.java.wanghongjian_and_liuxiao;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import com.java.wanghongjian_and_liuxiao.ui.login.LoginActivity;
import com.google.android.material.card.MaterialCardView;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* constants strings as keys in key-value pairs */
    public static final String EXTRA_SEARCH_WORDS = "com.naftikos.SEARCH_WORDS";
    public static final String EXTRA_NEWS_SERIAL = "com.naftikos.NEWS_SERIAL";

    public static Context context;
    TextView title;
    String homePageMode;
    Typeface ubuntuMidItalic;
    boolean isAfterSearch;
    String searchString;
    AlertDialog searchDialog;
    TextInputEditText searchDialogText;
    MaterialCardView newsCardModelFirst, newsCardModel;

    Vector<News> newsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Work in progress.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        newsList = new Vector<News>();
        context = this;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        title = findViewById(R.id.home_title);

        /* add fonts */
        ubuntuMidItalic = Typeface.createFromAsset(getAssets(), "Ubuntu-MediumItalic.ttf");
        title.setTypeface(ubuntuMidItalic);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SEARCH_WORDS)) {
            // this means this page is created after search
            isAfterSearch = true;
            searchString = intent.getStringExtra(EXTRA_SEARCH_WORDS);
            homePageMode = "";
            title.setText ("Search: " + searchString + ' ');

            // TODO: 19.8.12 dynamically change the size of the title bar w.r.t the length of the title string

        } else {
            isAfterSearch = false;
            searchString = "";
            homePageMode = "Latest";
            title.setText (homePageMode + ' ');
        }


        MaterialCardView titleCard = findViewById(R.id.title_material_card);

        titleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                /* when title is clicked, open the navigation drawer */
                DrawerLayout _drawer = findViewById(R.id.drawer_layout);
                _drawer.openDrawer(GravityCompat.START);
            }
        });

        /* build search dialog */

        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);
        searchDialogBuilder.setTitle("News Search");

        searchDialogText = new TextInputEditText (this);
        searchDialogText.setHint("Find news with...");

        FrameLayout searchContainer = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 30, 30, 20);
        searchDialogText.setSingleLine();
        searchDialogText.setLayoutParams(params);
        searchContainer.addView(searchDialogText);

        searchDialogBuilder.setView(searchContainer);
        searchDialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* search */
                searchString = searchDialogText.getText().toString();
                if (searchString.isEmpty()) {
                    return;
                }
                isAfterSearch = true;
                homePageMode = "";
                title.setText ("Search: " + searchString + ' ');
                getNewsFromServer();
                loadNews();
            }
        });

        searchDialogBuilder.setNegativeButton("Advanced", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                advancedSearch ();
            }
        });

        searchDialog = searchDialogBuilder.create();

        /* set the style of a news card for future creation */
        newsCardModelFirst = findViewById(R.id.news_card_01);
        newsCardModel = findViewById(R.id.news_card_02);


        getNewsFromServer();
        loadNews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_customization) {
            /* customise navigation (clicking "+" icon) */
            NavigationView v = findViewById(R.id.nav_view);
            Menu nav_menu = v.getMenu();

            /* the following lines are only a demo */
            MenuItem sport_item = nav_menu.findItem(R.id.nav_sports);
            sport_item.setVisible(!sport_item.isVisible());
            MenuItem tech_item = nav_menu.findItem(R.id.nav_technology);
            tech_item.setVisible(!tech_item.isVisible());
            MenuItem autos_item = nav_menu.findItem(R.id.nav_autos);
            autos_item.setVisible(!autos_item.isVisible());

            // TODO: 19.8.12 redesign the function of navigation menu customization

        } else if (id == R.id.nav_preferences) {
            /* preferences page */
        } else if (id == R.id.nav_about) {
            /* about page */
        } else {
            if (id == R.id.nav_latest) {
                homePageMode = "Latest";
            } else if (id == R.id.nav_random) {
                homePageMode = "Random";
            } else if (id == R.id.nav_personal) {
                homePageMode = "Personal Feeds";
            } else if (id == R.id.nav_finance) {
                homePageMode = "Finance";
            } else if (id == R.id.nav_education) {
                homePageMode = "Education";
            } else if (id == R.id.nav_entertainment) {
                homePageMode = "Entertainment";
            } else if (id == R.id.nav_sports) {
                homePageMode = "Sports";
            } else if (id == R.id.nav_technology) {
                homePageMode = "Technology";
            } else if (id == R.id.nav_autos) {
                homePageMode = "Autos";
            }

            /* change home page according to mode */
            TextView title = findViewById(R.id.home_title);
            title.setText (homePageMode + ' ');

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            isAfterSearch = false;
            getNewsFromServer();
            loadNews();

        }

        return true;
    }

    public void showSearchPage (View view) {
        /* search button pressed */
        searchDialog.show();
        return;
    }

    public void advancedSearch () {
        Intent intent = new Intent (this, SearchActivity.class);
        startActivity(intent);
    }

    public void showLogin (View view) {
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
    }

    public void getNewsFromServer () {
        // TODO: 19.8.15  fill newsList with news according to homePageMode, searchString, etc.
        newsList.clear();
        /* demo */
        News news = null;
        for (int i = 0; i < 50; i += 1) {
            if (isAfterSearch) {
                news = new News(null, "Search Result #" + (i + 1) + "  我们需要兼容更长的标题，比如这个字符串", getString(R.string.sample_news_text), null, null, null);
            } else {
                news = new News(null,homePageMode + " News #" + (i + 1), getString(R.string.sample_news_text), null, null, null);
            }
            newsList.add(news);
        }

    }

    public void loadNews () {
        /* load news into home screen */
        LinearLayout newsContainer = findViewById(R.id.home_news_container);
        newsContainer.removeAllViews();

        boolean firstFlag = true;
        int newsCounter = 0;
        for (News newsItem: newsList) {
            newsContainer.addView(generateHomeNewsCard(newsCounter, newsItem));
            newsCounter += 1;
            // TODO: 19.8.15 specify params for news cards
        }

    }

    private MaterialCardView generateHomeNewsCard (final int newsCounter, News newsItem) {
        MaterialCardView newCard = new MaterialCardView(this);
        // modelCard: pre-drawn news card (created with XML but removed in run time)
        MaterialCardView modelCard = (newsCounter == 0) ? newsCardModelFirst : newsCardModel;

        /* it is fucking stupid that Android view does not support cloning! */
        newCard.setLayoutParams(modelCard.getLayoutParams());
        newCard.setCardElevation(modelCard.getCardElevation());
        newCard.setCardBackgroundColor(modelCard.getCardBackgroundColor());
        newCard.setBackground(modelCard.getBackground());
        newCard.setForegroundGravity(modelCard.getForegroundGravity());

        TextView textInCard = new TextView(this);
        // TODO: 19.8.15 put content, image, etc. to the card
        textInCard.setText(newsItem.getTitle());
        newCard.addView(textInCard);

        newCard.setClickable(true);
        newCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewsByNumber(newsCounter, view);
            }
        });
        return newCard;
    }

    /* open news page according to the number of the news (in newsList) */
    private void openNewsByNumber (int newsNumber, View view) {
        Intent intent = new Intent (this, NewsPage.class);
        intent.putExtra (EXTRA_NEWS_SERIAL, newsList.elementAt(newsNumber));
        startActivity(intent);
    }

    public static Context getContext() {
        return context;
    }
}
